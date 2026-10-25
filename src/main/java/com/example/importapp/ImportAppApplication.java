package com.example.importapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ImportAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportAppApplication.class, args);
    }
}
