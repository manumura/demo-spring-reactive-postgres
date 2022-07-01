package com.example.demo.controller;

import com.example.demo.dto.CreateTransactionalRequest;
import com.example.demo.exception.NotFoundException;
import com.example.demo.facade.TransactionalFacade;
import com.example.demo.mapper.Mapper;
import com.example.demo.service.TransactionalService;
import com.example.demo.transaction.CreateTransactionRequest;
import com.example.demo.transaction.CreateTransactionResponse;
import com.example.demo.transaction.TransactionServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class TransactionService extends TransactionServiceGrpc.TransactionServiceImplBase {

    private final TransactionalFacade transactionalFacade;

    @Override
    public void createTransaction(CreateTransactionRequest request, StreamObserver<CreateTransactionResponse> responseObserver) {
        CreateTransactionalRequest r = CreateTransactionalRequest.builder()
                .from(request.getFrom())
                .to(request.getTo())
                .amount(request.getAmount())
                .build();

        transactionalFacade.doTransaction(r)
                .map(response -> CreateTransactionResponse.newBuilder()
                        .setFrom(Mapper.buildBalanceResponse(response.getFrom()))
                        .setTo(Mapper.buildBalanceResponse(response.getTo()))
                        .build())
                .onErrorResume(
                        NotFoundException.class,
                        e -> Mono.error(new NotFoundException(e.getMessage()))
                )
                .subscribe(responseObserver::onNext, responseObserver::onError, responseObserver::onCompleted);
    }
}
