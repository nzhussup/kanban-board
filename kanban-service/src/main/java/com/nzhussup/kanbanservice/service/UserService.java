package com.nzhussup.kanbanservice.service;

import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.user.UserRequest;
import com.nzhussup.kanbanservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final CacheManager cacheManager;

/*    private void doLongRunningTask() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @Cacheable(value = "users", unless="#result == null")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "userById", key = "#id", unless="#result == null")
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "userByUsername", key = "#username", unless="#result == null")
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @CachePut(value = "userByUsername", key = "#userRequest.username")
    @CacheEvict(value = {"users", "boards", "lists", "cards"}, allEntries = true)
    public User save(UserRequest userRequest, String role) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));

        if (role.equals("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @CacheEvict(value = {"users", "boards", "lists", "cards"}, allEntries = true)
    public ResponseEntity<?> deleteUserByUsername(String username, Authentication authentication) {

        boolean isSelf = authentication.getName().equals(username);
        boolean isAuthAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Check if user is neither admin nor self
        if (!isSelf && !isAuthAdmin) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't delete other users.");
        }

        // Check if user is null
        User toDeleteUser = userRepository.findByUsername(username);
        if (toDeleteUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        // Check if user is admin and if it's a single admin
        boolean isUsernameAdmin = toDeleteUser.getRole().equals("ROLE_ADMIN");
        if (isUsernameAdmin) {
            Optional<List<User>> admins = userRepository.findUserByRole("ROLE_ADMIN");
            if ((admins.isPresent()) && (admins.get().size() <= 1)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't delete single admin");
            }
        }

        // If all checks passed, delete
        userRepository.delete(toDeleteUser);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @CachePut(value = "userByUsername", key = "#userRequest.username")
    @CacheEvict(value = {"users", "boards", "lists", "cards"}, allEntries = true)
    public User updateUser(UserRequest userRequest, Authentication authentication) {
        if ((!authentication.getName().equals(userRequest.getUsername())) &&
                (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            return null;
        }

        User user = userRepository.findByUsername(userRequest.getUsername());
        user.setUsername(userRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));

        User userUpdated = userRepository.save(user);

        return userUpdated;
    }

    @CachePut(value = "userByUsername", key = "#username")
    @CacheEvict(value = {"users", "boards", "lists", "cards"}, allEntries = true)
    public User updateRole(String username, Authentication authentication) {
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return null;
        }

        // Check if the role is admin and its a single admin of a person to update
        User user = userRepository.findByUsername(username);
        String role = user.getRole();
        if (role.equals("ROLE_ADMIN")) {
            Optional<List<User>> admins = userRepository.findUserByRole(role);
            if ((admins.isPresent()) && (admins.get().size() <= 1)) {
                return null;
            }
        }

        user.setRole(role.equals("ROLE_ADMIN") ? "ROLE_USER" : "ROLE_ADMIN");
        User userUpdated = userRepository.save(user);
        return userUpdated;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    //UNUSEFUL METHODS

//    @CacheEvict(value = "users", allEntries = true)
//    public void evictAllUserCache() {}
//


/*    // CACHE MANAGER CODE
    private void updateUsersCache(User user, CacheOperation operation) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            List<User> users = cache.get("SimpleKey []", List.class);
            if (users != null) {
                switch (operation) {
                    case ADD -> users.add(user);
                    case UPDATE -> users.replaceAll(u -> u.getUsername().equals(user.getUsername()) ? user : u);
                    case REMOVE -> users.removeIf(u -> u.getUsername().equals(user.getUsername()));
                }
                cache.put("SimpleKey []", users);
            }
        }
    }

    private enum CacheOperation {
        ADD, UPDATE, REMOVE
    }*/

}
