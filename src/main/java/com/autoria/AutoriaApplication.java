package com.autoria;

import com.autoria.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AutoriaApplication {

    public static void main(String[] args) {

        EnvLoader.loadEnv();  // Завантажуємо .env до старту Spring


        SpringApplication.run(AutoriaApplication.class, args);
    }

}
