package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.exception.CardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.requestModels.card.CardByTitleRequest;
import com.nzhussup.kanbanservice.model.requestModels.card.CardRequest;
import com.nzhussup.kanbanservice.service.CardService;
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
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllCards(Authentication authentication) {
        List<Card> cards;
        try {
            cards = cardService.getAllCards(authentication);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/byTitle")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCardsByTitle(@RequestBody CardByTitleRequest request, Authentication authentication) {
        List<Card> cards;
        try {
            cards = cardService.getCardsByTitle(request.getTitle(), authentication);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getCardById(@PathVariable Long id, Authentication authentication) {
        Card card;
        try {
            card = cardService.getCardById(id, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        return ResponseEntity.ok(card);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addCard(@RequestBody CardRequest request, Authentication authentication) {
        Card card;
        try {
            card = cardService.saveCard(request, authentication, "user");
        } catch (ListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }


    @PostMapping("/admin/add")
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

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateCard(@PathVariable Long id, @RequestBody CardRequest request, Authentication authentication) {
        Card card;
        try {
            card = cardService.updateCard(id, request, authentication, "user");
        } catch (CardNotFoundException | ListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @PutMapping("/admin/update/{id}")
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

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteCard(@PathVariable Long id, Authentication authentication) {
        try {
            cardService.deleteCard(id, authentication, "user");
        } catch (CardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Card successfully deleted");
    }

    @DeleteMapping("/admin/delete/{id}")
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



}
