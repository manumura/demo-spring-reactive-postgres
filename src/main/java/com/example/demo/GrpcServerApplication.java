package com.example.demo;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.io.IOException;

@SpringBootApplication
@EnableR2dbcAuditing
public class GrpcServerApplication {

    public static void main(String[] args) {
//        final String path = args.length == 0 ? "certificates/server.crt" : args[0];
//        final DefaultResourceLoader loader = new DefaultResourceLoader();
//        System.out.println(toString(loader.getResource("classpath:" + path)));
//        System.out.println(toString(loader.getResource("classpath:/" + path)));
//        System.out.println(toString(loader.getResource("file:" + path)));
//        System.out.println(toString(loader.getResource("file:/" + path)));
//        System.out.println(toString(loader.getResource(path)));
//        System.out.println(toString(loader.getResource("/" + path)));

        SpringApplication.run(GrpcServerApplication.class, args);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        initializer.setDatabasePopulator(resource);
        return initializer;
    }

//    public static String toString(final Resource resource) throws IOException {
//        if (resource.exists()) {
//            return resource.getFile().getAbsolutePath();
//        } else {
//            return "File Not Found";
//        }
//    }
}
