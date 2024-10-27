package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.UserNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddAdminRequest;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddRequest;
import com.nzhussup.kanbanservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<Board>> getAllBoards(Authentication authentication) {
        List<Board> boards = boardService.getAllBoards(authentication);
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id, Authentication authentication) {
        Board board = boardService.getById(id, authentication);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(board);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Board> addBoard(@RequestBody BoardAddRequest board, Authentication authentication) {
        Board addedBoard = boardService.addBoard(board, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedBoard);
    }

    @PostMapping("/admin/add")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addAdminBoard(@RequestBody BoardAddAdminRequest board, Authentication authentication) {
        Board addedBoard;
        try {
            addedBoard = boardService.addBoardAdmin(board, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addedBoard);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateBoard(@PathVariable long id, @RequestBody BoardAddRequest board, Authentication authentication) {
        Board updatedBoard;
        try {
            updatedBoard = boardService.updateBoard(id, board, authentication);
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedBoard);
    }

    @PutMapping("/admin/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateAdminBoard(@PathVariable long id, @RequestBody BoardAddAdminRequest board, Authentication authentication) {
        Board updatedBoard;
        try {
            updatedBoard = boardService.updateBoardAdmin(id, board);
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(updatedBoard);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteBoard(@PathVariable long id, Authentication authentication) {
        return boardService.deleteBoard(id, authentication);
    }

    @DeleteMapping("/admin/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAdminBoard(@PathVariable long id, Authentication authentication) {
        return boardService.deleteBoardAdmin(id, authentication);
    }



}
