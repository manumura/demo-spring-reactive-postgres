package com.example.demo.facade;

import com.example.demo.dto.CreateTransactionalRequest;
import com.example.demo.dto.CreateTransactionalResponse;
import com.example.demo.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionalFacade {

    private final TransactionalService transactionalService;

    @Transactional
    public Mono<CreateTransactionalResponse> doTransaction(CreateTransactionalRequest request) {
        return transactionalService.doTransaction(request);
    }
}
