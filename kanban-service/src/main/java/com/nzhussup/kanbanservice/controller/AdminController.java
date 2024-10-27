package com.nzhussup.kanbanservice.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/dropAllCache")
    @CacheEvict(value = {"users", "boards", "lists", "cards"}, allEntries = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> dropAllCache() {
        return ResponseEntity.status(HttpStatus.OK).body("All caches have been cleared.");
    }
}

