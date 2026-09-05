package com.fincommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FincommerceApplication {


    public static void main(String[] args) {
        SpringApplication.run(FincommerceApplication.class, args);
    }
}
