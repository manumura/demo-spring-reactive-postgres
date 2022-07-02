package com.example.demo.controller;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.UpdateAccountRequest;
import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(value = "/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountRepository accountRepository;

    // https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-ann-responseentity
    @GetMapping
    public ResponseEntity<Flux<Account>> getAll() {
        return ResponseEntity.ok(accountRepository.findAll());
    }

    @GetMapping(value = "/{name}")
    public Mono<ResponseEntity<Account>> getOne(@PathVariable String name) {
        return accountRepository.findByName(name)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found !!!"))))
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Account>> createAccount(@RequestBody CreateAccountRequest request) {
        final Account account = Account.builder()
                .name(request.getName())
                .createdBy("SYSTEM")
                .build();
        return accountRepository.findByName(request.getName())
                .flatMap(accountExisting -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account name already exists !!!")))
                .switchIfEmpty(Mono.defer(() -> accountRepository.save(account)))
                .cast(Account.class)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/{number}")
    public ResponseEntity<Flux<Account>> createAccounts(@PathVariable int number) {
        if (number <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Number of accounts to create should be positive !!!");
        }
        return ResponseEntity.ok(generateRandomAccount(number).subscribeOn(Schedulers.boundedElastic()));
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

    @PutMapping(value = "/{id}")
    public Mono<ResponseEntity<Account>> updateAccount(@PathVariable Long id, @RequestBody UpdateAccountRequest request) {
        return accountRepository
                .findByNameAndIdNot(request.getName(), id)
                .flatMap(accountExisting -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account name already exists !!!")))
                .switchIfEmpty(Mono.defer(() -> accountRepository.findById(id)))
                .cast(Account.class)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found !!!"))))
                .flatMap(account -> {
                    account.setName(request.getName());
                    account.setLastModifiedBy("SYSTEM");
                    return accountRepository.save(account);
                })
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable Long id) {
        return accountRepository
                .findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found !!!"))))
                .flatMap(account -> accountRepository.deleteById(id))
                .map(ResponseEntity::ok);
    }
}
