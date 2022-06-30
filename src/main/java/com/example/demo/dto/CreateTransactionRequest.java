package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateTransactionRequest {
    private Long from;
    private Long to;
    private Long amount;
}