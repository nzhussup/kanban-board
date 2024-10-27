package com.nzhussup.kanbanservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class KanbanServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanServiceApplication.class, args);
    }

}
