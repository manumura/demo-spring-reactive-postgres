package com.example.demo;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;


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

  @BeforeEach
  public void setup() {
    initializeDatabase();
    insertData();
  }

  private void initializeDatabase() {
    ConnectionFactory connectionFactory = ConnectionFactories.get(dbUrl);
    R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
    String query = "CREATE TABLE IF NOT EXISTS account (id SERIAL PRIMARY KEY, name TEXT NOT NULL);";
    template.getDatabaseClient().sql(query).fetch().rowsUpdated().block();
  }

  private void insertData() {
    Flux<Account> accountFlux = Flux.just(
        Account.builder().name("ani").build(),
        Account.builder().name("budi").build(),
        Account.builder().name("cep").build(),
        Account.builder().name("dod").build()
    );
    accountRepository.deleteAll()
        .thenMany(accountFlux)
        .flatMap(accountRepository::save)
        .doOnNext(account -> log.info("inserted {}", account))
        .blockLast();
  }

  @Test
  public void getAll() {
    webTestClient.get()
        .uri("/api/account")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isArray()
        .jsonPath("$[0].name")
        .isEqualTo("ani")
        .jsonPath("$[1].name")
        .isEqualTo("budi")
        .jsonPath("$[2].name")
        .isEqualTo("cep")
        .jsonPath("$[3].name")
        .isEqualTo("dod");
  }

  @Test
  public void getOne() {
    webTestClient.get()
        .uri("/api/account/any")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("any")
        .jsonPath("$.id")
        .isNumber();
  }
}
