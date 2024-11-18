package com.nzhussup.kanbanservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.cache.annotation.Cacheable;

@Controller
@RequestMapping("/")
public class RootController {

    @GetMapping
    @Cacheable("swagger")
    public String redirectToSwaggerUI(Authentication authentication) {

        // INFO: TURNED OFF AUTHENTICATION FOR ROOT ENDPOINT. MIGHT REVERT IT BACK LATER THEREFORE ONLY COMMENTED FOR NOW.
/*        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/swagger-ui/index.html?url=/v3/api-docs/";
        }
        return "forward:/index.html";*/

        return "redirect:/swagger-ui/index.html";
    }
}
