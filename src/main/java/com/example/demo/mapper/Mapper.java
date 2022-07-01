package com.example.demo.mapper;

import com.example.demo.entity.Account;
import com.example.demo.entity.Balance;
import com.example.demo.util.ConvertionUtils;

public class Mapper {

    private Mapper() {}

    public static com.example.demo.account.Account buildAccountResponse(Account a) {
        com.example.demo.account.Account.Builder builder = com.example.demo.account.Account.newBuilder();
        builder.setId(a.getId())
                .setName(a.getName())
                .setCreatedBy(a.getCreatedBy())
                .setCreatedDate(ConvertionUtils.toGoogleTimestampUTC(a.getCreatedDate()));

        if (a.getLastModifiedBy() != null) {
            builder.setLastModifiedBy(a.getLastModifiedBy());
        }
        if (a.getLastModifiedDate() != null) {
            builder.setLastModifiedDate(ConvertionUtils.toGoogleTimestampUTC(a.getLastModifiedDate()));
        }
        return builder.build();
    }

    public static com.example.demo.balance.Balance buildBalanceResponse(Balance b) {
        com.example.demo.balance.Balance.Builder builder = com.example.demo.balance.Balance.newBuilder();
        builder.setId(b.getId().toString())
                .setBalance(b.getBalance())
                .setAccountId(b.getAccountId())
                .setCreatedBy(b.getCreatedBy())
                .setCreatedDate(ConvertionUtils.toGoogleTimestampUTC(b.getCreatedDate()));
        return builder.build();
    }
}
