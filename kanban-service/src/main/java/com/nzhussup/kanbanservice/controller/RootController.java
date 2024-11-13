package com.nzhussup.kanbanservice.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.cache.annotation.Cacheable;

@Controller
@RequestMapping("/")
public class RootController {


    private final Counter rootCallsCounter;

    @Autowired
    public RootController(MeterRegistry registry) {
        this.rootCallsCounter = registry.counter("root_calls_total");
    }

    @GetMapping
    @Cacheable("swagger")
    public String redirectToSwaggerUI(Authentication authentication) {

        rootCallsCounter.increment();
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/swagger-ui/index.html?url=/v3/api-docs/";
        }
        return "forward:/index.html";
    }
}
