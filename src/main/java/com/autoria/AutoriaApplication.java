package com.autoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutoriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoriaApplication.class, args);
    }

}
