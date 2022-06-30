package com.example.demo.util;

import com.google.protobuf.Timestamp;
import com.google.type.Date;
import com.google.type.Money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ConvertionUtils {

    private ConvertionUtils() {}

    public static Timestamp toGoogleTimestampUTC(final LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC))
                .setNanos(localDateTime.getNano())
                .build();
    }

    public static LocalDateTime fromGoogleTimestampUTC(final Timestamp googleTimestamp) {
        return Instant.ofEpochSecond(googleTimestamp.getSeconds(), googleTimestamp.getNanos())
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public static Date toGoogleDate(final LocalDate localDate) {
        return Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonth().getValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }

    public static LocalDate fromGoogleDate(final Date googleDate) {
        return LocalDate.of(googleDate.getYear(), googleDate.getMonth(), googleDate.getDay());
    }

    public static Money toGoogleMoney(final BigDecimal decimal, final String currency) {
        return Money.newBuilder()
                .setCurrencyCode(currency)
                .setUnits(decimal.longValue())
                .setNanos(decimal.remainder(BigDecimal.ONE).movePointRight(decimal.scale()).intValue())
                .build();
    }

    public static BigDecimal fromGoogleMoney(final Money googleMoney) {
        return new BigDecimal(googleMoney.getUnits())
                .add(new BigDecimal(googleMoney.getNanos(), new MathContext(9)));
    }
}
