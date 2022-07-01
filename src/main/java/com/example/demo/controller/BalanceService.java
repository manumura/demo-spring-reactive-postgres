package com.example.demo.controller;

import com.example.demo.balance.BalanceServiceGrpc;
import com.example.demo.balance.CreateBalanceRequest;
import com.example.demo.entity.Balance;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.Mapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BalanceRepository;
import com.example.demo.util.ConvertionUtils;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@GrpcService
// https://stackoverflow.com/a/46812593
public class BalanceService extends BalanceServiceGrpc.BalanceServiceImplBase {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;

    @Override
    public void createBalance(CreateBalanceRequest request, StreamObserver<com.example.demo.balance.Balance> responseObserver) {

        final Balance balance = Balance.builder()
                .balance(request.getBalance())
                .accountId(request.getAccountId())
                .createdBy("SYSTEM")
                .build();

        // TODO master + create accounts created by
        accountRepository.findById(request.getAccountId())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Account not found !!!"))))
                .flatMap(a -> balanceRepository.save(balance))
                .map(Mapper::buildBalanceResponse)
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }
}
