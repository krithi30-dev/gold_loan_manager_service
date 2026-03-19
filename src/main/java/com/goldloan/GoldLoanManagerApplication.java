package com.goldloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GoldLoanManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoldLoanManagerApplication.class, args);
    }
}
