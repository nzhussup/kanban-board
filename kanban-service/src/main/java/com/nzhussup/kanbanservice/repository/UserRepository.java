package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<List<User>> findUserByRole(String role);
}
