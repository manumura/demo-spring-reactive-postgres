package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Builder
@Data
public class Account {

    @Id
    private Long id;
    private String name;
}
