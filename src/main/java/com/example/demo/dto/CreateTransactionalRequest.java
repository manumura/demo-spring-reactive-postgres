package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateTransactionalRequest {
    private Long from;
    private Long to;
    private Long amount;
}