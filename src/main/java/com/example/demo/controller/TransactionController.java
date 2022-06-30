package com.example.demo.controller;

import com.example.demo.dto.CreateTransactionRequest;
import com.example.demo.dto.CreateTransactionResponse;
import com.example.demo.entity.Balance;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionalService transactionalService;

    @PostMapping
    @Transactional
    public Mono<ResponseEntity<CreateTransactionResponse>> createTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionalService.doTransaction(request)
                .map(ResponseEntity::ok)
                .onErrorResume(
                        NotFoundException.class,
                        e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()))
                );
    }
}
