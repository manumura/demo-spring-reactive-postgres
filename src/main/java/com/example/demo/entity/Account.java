package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;


@Builder
@Data
public class Account {

    @Id
    private Long id;
    private String name;
    @CreatedBy
    @Column("created_by")
    private String createdBy;
    @CreatedDate
    @Column("created_date")
    private LocalDateTime createdDate;
    @LastModifiedBy
    @Column("last_modified_by")
    private String lastModifiedBy;
    @LastModifiedDate
    @Column("last_modified_date")
    private LocalDateTime lastModifiedDate;
}
