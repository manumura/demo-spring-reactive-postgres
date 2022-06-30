package com.example.demo.controller;

import com.example.demo.dto.CreateBalanceRequest;
import com.example.demo.entity.Balance;
import com.example.demo.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/balance")
@RequiredArgsConstructor
@Slf4j
public class BalanceController {

    private final BalanceRepository balanceRepository;

    @PostMapping
    public Mono<ResponseEntity<Balance>> createBalance(@RequestBody CreateBalanceRequest request) {
        final Balance balance = Balance.builder()
                .balance(request.getBalance())
                .accountId(request.getAccountId())
                .createdBy("SYSTEM")
                .build();
        return balanceRepository.save(balance)
                .map(ResponseEntity::ok);
    }

}
