package com.example.demo.controller;

import com.example.demo.account.*;
import com.example.demo.entity.Account;
import com.example.demo.error.ErrorCode;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.StatusBuilder;
import com.example.demo.mapper.Mapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.ConvertionUtils;
import com.google.rpc.Code;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.RandomStringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@GrpcService
// https://stackoverflow.com/a/46812593
public class AccountService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountRepository accountRepository;

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<com.example.demo.account.Account> responseObserver) {

        final Account account = Account.builder()
                .name(request.getName())
                .createdBy("SYSTEM")
                .build();

        accountRepository.findByName(request.getName())
                .flatMap(accountExisting -> Mono.error(new BadRequestException("Account name already exists !!!")))
                .switchIfEmpty(Mono.defer(() -> accountRepository.save(account)))
                .cast(Account.class)
                .map(Mapper::buildAccountResponse)
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }

    @Override
    public void createAccounts(CreateAccountsRequest request, StreamObserver<com.example.demo.account.Account> responseObserver) {
        if (request.getNumber() <= 0) {
            var status = StatusBuilder.buildStatus(ErrorCode.BAD_REQUEST,
                    Code.FAILED_PRECONDITION,
                    "Bad request error: Number of accounts to create should be positive !!!");
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
        generateRandomAccount(request.getNumber())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(a -> {
                    log.info("Find all on next: {}", a);
                    responseObserver.onNext(Mapper.buildAccountResponse(a));
                })
                .doOnError(responseObserver::onError)
                .doOnComplete(responseObserver::onCompleted)
                .subscribe();
    }

    private Flux<Account> generateRandomAccount(int number) {
        return Mono.fromSupplier(
                        () -> Account.builder()
                                .name(RandomStringUtils.randomAlphabetic(5))
                                .createdBy("SYSTEM")
                                .build())
                .flatMap(accountRepository::save)
                .repeat(number - 1L);
    }

    @Override
    public void getOneByName(GetOneByNameRequest request, StreamObserver<com.example.demo.account.Account> responseObserver) {
        accountRepository.findByName(request.getName())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Account not found !!!"))))
                .map(a -> {
                            log.info("Account found: {}", a);
                            return Mapper.buildAccountResponse(a);
                        }
                )
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }

    @Override
    public void getAll(GetAllRequest request, StreamObserver<com.example.demo.account.Account> responseObserver) {
        accountRepository.findAll()
                .doOnNext(a -> {
                    log.info("Find all on next: {}", a);
                    responseObserver.onNext(Mapper.buildAccountResponse(a));
                })
                .doOnError(responseObserver::onError)
                .doOnComplete(responseObserver::onCompleted)
                .subscribe();
    }

    @Override
    public void updateAccount(UpdateAccountRequest request, StreamObserver<com.example.demo.account.Account> responseObserver) {
        // TODO check name does not exist
        accountRepository
                .findById(request.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Account not found !!!"))))
                .flatMap(account -> {
                    account.setName(request.getName());
                    account.setLastModifiedBy("SYSTEM");
                    return accountRepository.save(account);
                })
                .map(Mapper::buildAccountResponse)
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }

    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<com.example.demo.account.DeleteAccountResponse> responseObserver) {
        accountRepository
                .findById(request.getId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Account not found !!!"))))
                .flatMap(account -> accountRepository.deleteById(request.getId()))
                .map(v -> DeleteAccountResponse.newBuilder().build())
                .doOnError(responseObserver::onError)
                .doOnSuccess(r -> {
                    responseObserver.onNext(r);
                    responseObserver.onCompleted();
                })
                .subscribe();
    }
}
