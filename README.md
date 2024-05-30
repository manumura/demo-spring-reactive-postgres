# Demo Spring Boot Reactive PostgreSQL

Switch to `http` branch for http instead of gRPC

## Generate grpc services

`mvn compile`

## Building Docker Image

Run spring-boot build image plugin

`mvn spring-boot:build-image -DskipTests`

[![Push Image to Container Registry](https://github.com/manumura/demo-spring-reactive-postgres/actions/workflows/push-to-cr.yml/badge.svg)](https://github.com/manumura/demo-spring-reactive-postgres/actions/workflows/push-to-cr.yml)
