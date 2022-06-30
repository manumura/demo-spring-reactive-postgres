package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Balance {

    @Id
    private UUID id;
    // https://github.com/spring-projects/spring-data-r2dbc/issues/356
    @Column("account_id")
    private Long accountId;
    private Long balance;
    @CreatedBy
    @Column("created_by")
    private String createdBy;
    @CreatedDate
    @Column("created_date")
    private LocalDateTime createdDate;
}
