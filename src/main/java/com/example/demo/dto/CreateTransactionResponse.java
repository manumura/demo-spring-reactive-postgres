package com.example.demo.dto;

import com.example.demo.entity.Balance;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTransactionResponse {
    private Balance from;
    private Balance to;
}