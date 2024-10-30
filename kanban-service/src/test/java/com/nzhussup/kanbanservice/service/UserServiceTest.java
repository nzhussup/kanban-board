package com.nzhussup.kanbanservice.service;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.user.UserRequest;
import com.nzhussup.kanbanservice.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends TestDataSetup {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private Authentication authentication;



    @Test
    public void userService_getAllUsers_returnsAllUsers() {

        when(userRepository.findAll()).thenReturn(List.of(admin, user));

        List<User> result = userService.getAllUsers();

        assert result.size() == 2;
        assert result.contains(admin);
        assert result.contains(user);
    }

    @Test
    public void userService_getById_returnsUserIfExists() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        User result = userService.getById(1L);

        assert result != null;
        assert result.getUsername().equals("admin");
    }

    @Test
    public void userService_getByUsername_returnsUserIfExists() {

        when(userRepository.findByUsername("admin")).thenReturn(admin);

        User result = userService.getByUsername("admin");

        assert result != null;
        assert result.getUsername().equals("admin");
    }

    @Test
    public void userService_saveUser_savesAndReturnsUser() {

        UserRequest userRequest = new UserRequest("newUser", "password123");
        when(userRepository.save(any(User.class))).thenReturn(new User(null, "newUser", "password", "ROLE_USER"));

        User result = userService.save(userRequest, "user");

        assert result != null;
        assert result.getUsername().equals("newUser");
        assert result.getRole().equals("ROLE_USER");
    }

    @Test
    public void userService_deleteUserByUsername_returnsUnauthorizedIfNotSelfOrAdmin() {

        when(authentication.getName()).thenReturn("otherUser");
        Collection authorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);

        ResponseEntity<?> response = userService.deleteUserByUsername("admin", authentication);

        assert response.getStatusCode() == HttpStatus.UNAUTHORIZED;
        assert response.getBody().equals("Can't delete other users.");
    }

    @Test
    public void userService_deleteUserByUsername_returnsConflictIfSingleAdmin() {

        when(authentication.getName()).thenReturn("admin");
        Collection authorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(userRepository.findUserByRole("ROLE_ADMIN")).thenReturn(Optional.of(List.of(admin)));

        ResponseEntity<?> response = userService.deleteUserByUsername("admin", authentication);

        assert response.getStatusCode() == HttpStatus.CONFLICT;
        assert response.getBody().equals("Can't delete single admin");
    }

    @Test
    public void userService_updateUser_updatesAndReturnsUser() {

        UserRequest userRequest = new UserRequest("admin", "newPassword");
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(userRepository.save(any(User.class))).thenReturn(admin);

        User result = userService.updateUser(userRequest, authentication);

        assert result != null;
        assert result.getUsername().equals("admin");
        assert result.getPassword() != null;
    }

    @Test
    public void userService_updateRole_returnsNullIfNotAdmin() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);

        User result = userService.updateRole("user", authentication);

        assert result == null;
    }

    @Test
    public void userService_updateRole_returnsNullIfSingleAdmin() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(userRepository.findUserByRole("ROLE_ADMIN")).thenReturn(Optional.of(List.of(admin)));

        User result = userService.updateRole("admin", authentication);

        assert result == null;
    }
}

