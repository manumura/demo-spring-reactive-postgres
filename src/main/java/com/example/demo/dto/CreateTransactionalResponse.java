package com.example.demo.dto;

import com.example.demo.entity.Balance;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTransactionalResponse {
    private Balance from;
    private Balance to;
}