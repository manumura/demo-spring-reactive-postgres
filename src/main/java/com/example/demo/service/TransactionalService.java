package com.example.demo.service;

import com.example.demo.dto.CreateTransactionRequest;
import com.example.demo.dto.CreateTransactionResponse;
import com.example.demo.entity.Balance;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Random;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionalService {

    private final BalanceRepository balanceRepository;
    private static final Random random = new Random();

    @Transactional
    public Mono<CreateTransactionResponse> doTransaction(CreateTransactionRequest request) {
        Long amount = request.getAmount();

        return Mono.zip(balanceRepository.findFirstByAccountIdOrderByCreatedDateDesc(request.getFrom())
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Balance not found !!!")))),
                        balanceRepository.findFirstByAccountIdOrderByCreatedDateDesc(request.getTo())
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Balance not found !!!")))))
                .flatMap(balanceTuple -> executeTransaction(balanceTuple, amount));
    }

    private Mono<CreateTransactionResponse> executeTransaction(Tuple2<Balance, Balance> balanceTuple, Long amount) {
        Balance fromBalance = balanceTuple.getT1();
        Balance toBalance = balanceTuple.getT2();

        return Mono.zip(deductBalance(fromBalance, amount),
                        increaseBalance(toBalance, amount))
                .map(newBalanceTuple -> CreateTransactionResponse.builder()
                        .from(newBalanceTuple.getT1())
                        .to(newBalanceTuple.getT2())
                        .build());
    }

    private Mono<Balance> deductBalance(Balance fromBalance, Long amount) {
        Balance newFromBalance = Balance.builder()
                .accountId(fromBalance.getAccountId())
                .balance(fromBalance.getBalance() - amount)
                .createdBy("TRANSACTION")
                .build();
        return balanceRepository.save(newFromBalance);
    }

    private Mono<Balance> increaseBalance(Balance toBalance, Long amount) {
        return Mono.fromSupplier(random::nextDouble)
                .flatMap(randomValue -> increaseBalanceOrException(toBalance, amount, randomValue));
    }

    private Mono<Balance> increaseBalanceOrException(Balance toBalance, Long amount, Double randomValue) {
        log.info("random value {}", randomValue);
        if (randomValue > 0.5) {
            Balance newToBalance = Balance.builder()
                    .accountId(toBalance.getAccountId())
                    .balance(toBalance.getBalance() + amount)
                    .createdBy("TRANSACTION")
                    .build();
            return balanceRepository.save(newToBalance);
        }
        return Mono.error(new RuntimeException("random exception"));
    }

}
