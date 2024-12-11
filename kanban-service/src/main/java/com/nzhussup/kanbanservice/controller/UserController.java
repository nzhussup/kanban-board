package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.user.UserRequest;
import com.nzhussup.kanbanservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;


    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserBy(@RequestParam Optional<Long> id,
                                          @RequestParam Optional<String> name) {
        if (id.isPresent()) {
            User user = userService.getById(id.get());
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(user);
        } else if (name.isPresent()) {
            User user = userService.getByUsername(name.get());
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(user);
        } else {
            return getAllUsers();
        }
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication authentication) {
        return userService.deleteUserByUsername(username, authentication);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, Authentication authentication) {

        if (!userService.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found!");
        }
        return ResponseEntity.ok(userService.updateUser(userRequest, authentication));
    }

    @PutMapping("{username}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable String username, Authentication authentication) {

        if (!userService.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found!");
        }

        User updatedUser = userService.updateRole(username, authentication);
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot update single admin to user");
        }
        return ResponseEntity.ok(updatedUser);

    }

}
