package com.nzhussup.kanbanservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class RootController {

    @GetMapping
    @Cacheable("swagger")
    public ModelAndView redirectToSwaggerUI(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
                return new ModelAndView("redirect:/swagger-ui/index.html?url=/v3/api-docs/");
        }
        return new ModelAndView("forward:/index.html");
    }
}

