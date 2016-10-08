package me.hao0.diablo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "me.hao0.diablo.server.dao",
        "me.hao0.diablo.server.support",
        "me.hao0.diablo.server.service",
        "me.hao0.diablo.server.event",
        "me.hao0.diablo.server.cluster",
        "me.hao0.diablo.server.config"})
public class DiabloServerTest {

    public static void main(String[] args) {
        SpringApplication.run(DiabloServerTest.class, args);
    }
}
