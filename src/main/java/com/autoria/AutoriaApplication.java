package com.autoria;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AutoriaApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .filename(".env") // назва може бути змінена, якщо файл має іншу назву
                .ignoreIfMissing()
                .load();

        // Встановлюємо значення як системні змінні
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });

        SpringApplication.run(AutoriaApplication.class, args);
    }

}
