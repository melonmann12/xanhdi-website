package com.xanhdi.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class XanhDiApplication {
    public static void main(String[] args) {
        SpringApplication.run(XanhDiApplication.class, args);
    }
}
