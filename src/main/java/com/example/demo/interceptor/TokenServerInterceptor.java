package com.example.demo.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@GrpcGlobalServerInterceptor
public class TokenServerInterceptor implements ServerInterceptor {

    private static final String X_BEARER_TOKEN = "x-bearer-token";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        log.info("Sever interceptor: method called {}", serverCall.getMethodDescriptor());

        Metadata.Key<String> tokenMetadata = Metadata.Key.of(X_BEARER_TOKEN, Metadata.ASCII_STRING_MARSHALLER);
        String token = metadata.get(tokenMetadata);
        log.info("Token from client {}", token);

        if (StringUtils.equals(token, "my-secret-token")) {
            return serverCallHandler.startCall(serverCall, metadata);
        }

        Status status = Status.UNAUTHENTICATED.withDescription("Invalid token");
        serverCall.close(status, metadata);
        return new ServerCall.Listener<>() {};
    }
}
