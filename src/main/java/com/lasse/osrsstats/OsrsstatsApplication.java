package com.lasse.osrsstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // skrur på støtte for tidsstyrte oppgaver
public class OsrsstatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OsrsstatsApplication.class, args);
    }
}