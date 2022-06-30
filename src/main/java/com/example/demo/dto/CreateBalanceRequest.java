package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateBalanceRequest {
    private Long accountId;
    private Long balance;
}
