package com.example.demo.exception;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.ErrorResponse;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;

import java.time.Instant;

public class StatusBuilder {

    private StatusBuilder() {}

    public static com.google.rpc.Status buildStatus(ErrorCode errorCode, Code code, String message) {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();

        ErrorResponse errorResponse =
                ErrorResponse.newBuilder()
                        .setErrorCode(errorCode)
                        .setTimestamp(timestamp)
                        .build();

        return com.google.rpc.Status.newBuilder()
                .setCode(code.getNumber())
                .setMessage(message)
                .addDetails(Any.pack(errorResponse))
                .build();
    }
}
