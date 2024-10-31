package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.auth.AuthRequest;
import com.nzhussup.kanbanservice.model.requestModels.auth.AuthResponse;
import com.nzhussup.kanbanservice.model.requestModels.user.UserRequest;
import com.nzhussup.kanbanservice.security.JwtUtil;
import com.nzhussup.kanbanservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            if (!bCryptPasswordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            String token = jwtUtil.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {

        if (userService.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        User createdUser = userService.save(userRequest, "user");
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/delete/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication authentication) {
        return userService.deleteUserByUsername(username, authentication);
    }


    @PostMapping("/admin/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRequest userRequest) {

        if (userService.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }

        User createdUser = userService.save(userRequest, "admin");
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, Authentication authentication) {

        if (!userService.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found!");
        }
        return ResponseEntity.ok(userService.updateUser(userRequest, authentication));
    }

    @PutMapping("/admin/update/role/{username}")
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
