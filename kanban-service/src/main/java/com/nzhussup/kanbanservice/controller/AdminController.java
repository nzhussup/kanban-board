package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.CardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.exception.UserNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddAdminRequest;
import com.nzhussup.kanbanservice.model.requestModels.card.CardRequest;
import com.nzhussup.kanbanservice.model.requestModels.list.ListRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.nzhussup.kanbanservice.service.BoardService;
import com.nzhussup.kanbanservice.service.UserService;
import com.nzhussup.kanbanservice.service.CardService;
import com.nzhussup.kanbanservice.service.ListService;


@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    public final UserService userService;
    public final BoardService boardService;
    public final CardService cardService;
    public final ListService listService;

    @PostMapping("/dropAllCache")
    @CacheEvict(value = {"users", "boards", "lists", "cards", "swagger"}, allEntries = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> dropAllCache() {
        return ResponseEntity.status(HttpStatus.OK).body("All caches have been cleared.");
    }

    @PostMapping("/board")
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

    @PutMapping("/board/{id}")
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

    @DeleteMapping("/board/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAdminBoard(@PathVariable long id, Authentication authentication) {
        return boardService.deleteBoardAdmin(id, authentication);
    }


    @PostMapping("/card")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCardAdmin(@RequestBody CardRequest request, Authentication authentication) {
        Card card;
        try {
            card = cardService.saveCard(request, authentication, "admin");
        } catch (ListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @PutMapping("/card/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateCardAdmin(@PathVariable Long id, @RequestBody CardRequest request, Authentication authentication) {
        Card card;
        try {
            card = cardService.updateCard(id, request, authentication, "admin");
        } catch (CardNotFoundException | ListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @DeleteMapping("/card/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCardAdmin(@PathVariable Long id, Authentication authentication) {
        try {
            cardService.deleteCard(id, authentication, "admin");
        } catch (CardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Card successfully deleted");
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addAdminList(@RequestBody ListRequest listRequest, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.addListAdmin(listRequest, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @PutMapping("/list/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateAdminList(@RequestBody ListRequest listRequest,@PathVariable long id, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.updateListAdmin(listRequest, id, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied!");
        } catch (ListNotFoundException | BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @DeleteMapping("/list/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteAdminList(@PathVariable long id, Authentication authentication) {
        return listService.deleteList(id, authentication, "admin");
    }



}

