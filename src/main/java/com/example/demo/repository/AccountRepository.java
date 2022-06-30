package com.example.demo.repository;

import com.example.demo.entity.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends R2dbcRepository<Account, Long> {
    Mono<Account> findByName(String name);
}
