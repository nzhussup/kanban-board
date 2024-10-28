package com.nzhussup.kanbanservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.model.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BoardRepositoryTest extends TestDataSetup {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void boardRepository_save_returnSavedBoard() {
        Board board = boardRepository.save(board1);

        assert board.getName().equals(board1.getName());
        assert board.getOwnerId().equals(board1.getOwnerId());

        assert board.getCreatedAt().isEqual(board1.getCreatedAt()) || board.getCreatedAt().isAfter(board1.getCreatedAt());
        assert board.getUpdatedAt().isEqual(board1.getUpdatedAt()) || board.getUpdatedAt().isAfter(board1.getUpdatedAt());
    }

    @Test
    public void boardRepository_findById_returnBoardById() {
        Board savedBoard = boardRepository.save(board1);

        Optional<Board> optionalBoard = boardRepository.findById(savedBoard.getId());
        assert optionalBoard.isPresent();

        Board board = optionalBoard.get();
        assert board.getId().equals(savedBoard.getId());
        assert board.getName().equals(board1.getName());
        assert board.getOwnerId().equals(board1.getOwnerId());
    }

    @Test
    public void boardRepository_findAll_returnAllBoards() {
        boardRepository.save(board1);
        boardRepository.save(board2);

        List<Board> allBoards = boardRepository.findAll();

        assert allBoards.size() == 2;
        assert allBoards.stream().anyMatch(b -> b.getName().equals(board1.getName()) && b.getOwnerId().equals(board1.getOwnerId()));
        assert allBoards.stream().anyMatch(b -> b.getName().equals(board2.getName()) && b.getOwnerId().equals(board2.getOwnerId()));
    }

    @Test
    public void boardRepository_delete_returnVoid() {
        boardRepository.save(board1);
        boardRepository.delete(board1);

        Optional<Board> optionalBoard = boardRepository.findById(board1.getId());
        assert optionalBoard.isEmpty();
    }

    @Test
    public void boardRepository_update_returnUpdatedBoard() {
        boardRepository.save(board1);

        Board board = boardRepository.findById(board1.getId()).orElseThrow(null);
        board.setName("updated name");
        boardRepository.save(board);

        Board newBoard = boardRepository.findById(board1.getId()).orElseThrow(null);
        assert newBoard != null;
        assert newBoard.getName().equals("updated name");
    }
}
