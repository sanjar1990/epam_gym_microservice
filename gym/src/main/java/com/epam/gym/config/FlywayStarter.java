package com.epam.gym.config;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
@RequiredArgsConstructor
public class FlywayStarter implements CommandLineRunner {
    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        Flyway.configure().baselineOnMigrate(true).dataSource(dataSource).load().migrate();
    }

}
