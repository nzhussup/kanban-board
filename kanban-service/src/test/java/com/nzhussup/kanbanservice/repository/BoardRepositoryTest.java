package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.model.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testFindById() {
        Optional<Board> boardOpt = boardRepository.findById(1L);
        assert boardOpt.isPresent();

        Board board = boardOpt.get();

        assert board.getId() == 1L;
    }

}
