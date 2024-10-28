package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest extends TestDataSetup {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void userRepository_save_returnSavedUser() {

        User savedUser = userRepository.save(test1);

        assert savedUser.getUsername().equals(test1.getUsername());
        assert savedUser.getRole().equals(test1.getRole());
        assert savedUser.getPassword().equals(test1.getPassword());
    }


    @Test
    public void userRepository_findById_returnUserById() {

        User savedUser = userRepository.save(test2);

        Optional<User> foundUserOpt = userRepository.findById(savedUser.getId());

        assert foundUserOpt.isPresent();
        User foundUser = foundUserOpt.get();

        assert foundUser.getUsername().equals(test2.getUsername());
        assert foundUser.getRole().equals(test2.getRole());
        assert foundUser.getPassword().equals(test2.getPassword());
    }

    @Test
    public void userRepository_findByUsername_returnUserByUsername() {

        userRepository.save(test1);

        User foundUser = userRepository.findByUsername("test1");

        assert foundUser != null;
        assert foundUser.getUsername().equals(test1.getUsername());
        assert foundUser.getRole().equals(test1.getRole());
        assert foundUser.getPassword().equals(test1.getPassword());
    }

    @Test
    public void userRepository_findByUsername_returnEmptyUser() {

        User emptyUser = userRepository.findByUsername("notexistinguser");
        assert emptyUser == null;
    }

    @Test
    public void userRepository_findByAll_returnListOfUser() {
        userRepository.save(test1);
        userRepository.save(test2);

        List<User> users = userRepository.findAll();

        assert users.size() == 2;
        assert users.get(0).getUsername().equals("test1");
        assert users.get(1).getRole().equals("ROLE_USER");
    }
}
