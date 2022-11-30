package com.nttdata.bc39.grupo04.composite;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.nttdata.bc39.grupo04"})
public class CompositeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompositeServiceApplication.class, args);
    }
}