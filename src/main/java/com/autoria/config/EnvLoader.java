package com.autoria.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            // Якщо змінної немає у системних перемінних, встановлюємо її як System Property
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}
