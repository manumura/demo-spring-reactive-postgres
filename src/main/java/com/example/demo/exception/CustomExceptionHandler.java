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
        var status = StatusBuilder.buildStatus(ErrorCode.UNKNOWN,
                Code.INTERNAL,
                "Internal server error: " + exception.getMessage());
        return StatusProto.toStatusRuntimeException(status);
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public StatusRuntimeException handleException(BadRequestException exception) {
        log.error(exception.getMessage(), exception);
        var status = StatusBuilder.buildStatus(ErrorCode.BAD_REQUEST,
                Code.FAILED_PRECONDITION,
                "Bad request error: " + exception.getMessage());
        return StatusProto.toStatusRuntimeException(status);
    }

    @GrpcExceptionHandler(NotFoundException.class)
    public StatusRuntimeException handleException(NotFoundException exception) {
        log.error(exception.getMessage(), exception);
        var status = StatusBuilder.buildStatus(ErrorCode.NOT_FOUND,
                Code.NOT_FOUND,
                "Not found error: " + exception.getMessage());
        return StatusProto.toStatusRuntimeException(status);
    }
}