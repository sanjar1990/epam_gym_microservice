package com.epam.gym.workload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@Slf4j
@EnableJms
@SpringBootApplication
public class WorkloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkloadApplication.class, args);
    }

}
