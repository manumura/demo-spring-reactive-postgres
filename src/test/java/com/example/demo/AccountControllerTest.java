package com.example.demo;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;


@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles(profiles = "test")
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${spring.r2dbc.url}")
    private String dbUrl;

    private static final List<String> NAMES = List.of("John", "Bob", "Jack", "Michael");

    @BeforeEach
    public void setup() {
        initializeDatabase();
        insertData();
    }

    private void initializeDatabase() {
        ConnectionFactory connectionFactory = ConnectionFactories.get(dbUrl);
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        initializer.setDatabasePopulator(resource);
    }

    private void insertData() {
        Flux<Account> accountFlux = Flux.just(
                Account.builder().name("John").createdBy("SYSTEM").build(),
                Account.builder().name("Bob").createdBy("SYSTEM").build(),
                Account.builder().name("Jack").createdBy("SYSTEM").build(),
                Account.builder().name("Michael").createdBy("SYSTEM").build()
        );
        accountRepository.deleteAll()
                .thenMany(accountFlux)
                .flatMap(accountRepository::save)
                .doOnNext(account -> log.info("inserted {}", account))
                .blockLast();
    }

    @Test
    void getAll() {
        var response = webTestClient.get()
                .uri("/api/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Account.class)
                .hasSize(4)
                .returnResult();

        List<Account> accounts = response.getResponseBody();
        assert accounts != null;

        accounts.forEach(account -> {
            Assertions.assertTrue(NAMES.contains(account.getName()));
        });
    }

    @Test
    void getOne() {
        webTestClient.get()
                .uri("/api/accounts/Bob")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Bob")
                .jsonPath("$.id")
                .isNumber();
    }
}
