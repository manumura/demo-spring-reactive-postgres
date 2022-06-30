package com.example.demo.exception;

import com.example.demo.error.ErrorCode;
import com.example.demo.error.ErrorResponse;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.time.Instant;

@Slf4j
@GrpcAdvice
// https://stackoverflow.com/questions/48748745/pattern-for-rich-error-handling-in-grpc
public class CustomExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleException(Exception exception) {

        log.error(exception.getMessage(), exception);

        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();

        ErrorResponse errorResponse =
                ErrorResponse.newBuilder()
                        .setErrorCode(ErrorCode.UNKNOWN)
                        .setTimestamp(timestamp)
                        .build();

        var status =
                com.google.rpc.Status.newBuilder()
                        .setCode(Code.INTERNAL.getNumber())
                        .setMessage("Internal server error")
                        .addDetails(Any.pack(errorResponse))
                        .build();

        return StatusProto.toStatusRuntimeException(status);
    }
}