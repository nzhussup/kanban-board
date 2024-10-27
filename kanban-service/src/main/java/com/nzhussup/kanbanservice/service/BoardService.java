package com.nzhussup.kanbanservice.service;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.UserNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddAdminRequest;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddRequest;
import com.nzhussup.kanbanservice.repository.BoardRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.nzhussup.kanbanservice.service.helpers.Helper.checkIsAdmin;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /*
    INFO:
     Main idea of the board service is that admin can fetch all kind of data
     User can fetch only the data that belongs to this user
    */

/*    private void doLongRunningTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @Cacheable(value = "boards", key = "'allBoards_' + #authentication.name", unless="#result == null")
    public List<Board> getAllBoards(Authentication authentication) {

        if (checkIsAdmin(authentication)) {
            return boardRepository.findAll();
        } else {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            return boardRepository.findByOwnerId(user.getId());
        }
    }

    @Cacheable(value = "boards", key = "'board_' + #authentication.name", unless="#result == null")
    public Board getById(Long id, Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return boardRepository.findById(id).orElse(null);
        } else {
            User user = userRepository.findByUsername(authentication.getName());
            return boardRepository.findByIdAndOwnerId(id, user.getId()).orElse(null);
        }
    }

    @CachePut(value = "boards", key = "#result.id")
    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public Board addBoard(BoardAddRequest board, Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        Long ownerId = user.getId();

        Board toAdd = new Board();
        toAdd.setOwnerId(ownerId);
        toAdd.setName(board.getName());

        return boardRepository.save(toAdd);
    }

    @CachePut(value = "boards", key = "#result.id")
    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public Board addBoardAdmin(BoardAddAdminRequest board, Authentication authentication) {
        if (!checkIsAdmin(authentication)) {
            throw new AccessDeniedException("Access denied");
        }

        User user = userService.getById(board.getOwnerId());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Board toAdd = new Board();
        toAdd.setOwnerId(board.getOwnerId());
        toAdd.setName(board.getName());
        return boardRepository.save(toAdd);
    }

    @CachePut(value = "board", key = "#id")
    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public Board updateBoard(long id, BoardAddRequest board, Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        Board currentBoard = boardRepository.findByIdAndOwnerId(id, user.getId()).orElse(null);
        if (currentBoard == null) {
            throw new BoardNotFoundException("Board not found");
        }

        currentBoard.setName(board.getName());
        return boardRepository.save(currentBoard);
    }

    @CachePut(value = "board", key = "#id")
    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public Board updateBoardAdmin(long id, BoardAddAdminRequest board) {
        Board currentBoard = boardRepository.findById(id).orElse(null);
        if (currentBoard == null) {
            throw new BoardNotFoundException("Board not found");
        }
        currentBoard.setName(board.getName());
        currentBoard.setOwnerId(board.getOwnerId());
        return boardRepository.save(currentBoard);
    }

    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public ResponseEntity<?> deleteBoard(long id, Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        Board currentBoard = boardRepository.findByIdAndOwnerId(id, user.getId()).orElse(null);
        if (currentBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found!");
        }
        boardRepository.delete(currentBoard);
        return ResponseEntity.ok().build();
    }

    @CacheEvict(value = {"boards", "lists", "cards"}, allEntries = true)
    public ResponseEntity<?> deleteBoardAdmin(long id, Authentication authentication) {
        if (!checkIsAdmin(authentication)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Board currentBoard = boardRepository.findById(id).orElse(null);
        if (currentBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found!");
        }
        boardRepository.delete(currentBoard);
        return ResponseEntity.ok().build();
    }

}

