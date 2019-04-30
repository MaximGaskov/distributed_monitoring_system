package edu.max.monsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonsysApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonsysApplication.class, args);
    }

}
