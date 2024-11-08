package com.nzhussup.kanbanservice.service;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.card.CardRequest;
import com.nzhussup.kanbanservice.repository.CardRepository;
import com.nzhussup.kanbanservice.repository.ListRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

// In this testing I decided to use junit's and mockito's static methods for assertion
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CardServiceTest extends TestDataSetup {
    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ListRepository listRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Card card;

    @Mock User user;

    @Mock
    private ListModel listModel;

    @Mock
    private Board board;

    @Test
    public void cardService_getAllCards_AsAdmin_ReturnsAllCards() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(cardRepository.findAll()).thenReturn(List.of(card));

        List<Card> cards = cardService.getAllCards(authentication);

        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals(card, cards.get(0));
        verify(cardRepository, times(1)).findAll();
    }

    @Test
    public void cardService_getAllCards_AsUser_ReturnsUserCards() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cardRepository.findByOwnerId(1L)).thenReturn(List.of(card));

        List<Card> cards = cardService.getAllCards(authentication);

        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals(card, cards.get(0));
        verify(cardRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    public void cardService_getCardsByTitle_AsAdmin_ReturnsMatchingCards() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(cardRepository.findByTitleContaining("test")).thenReturn(List.of(card));

        List<Card> cards = cardService.getCardsByTitle("test", authentication);

        assertNotNull(cards);
        assertEquals(1, cards.size());
        assertEquals(card, cards.get(0));
        verify(cardRepository, times(1)).findByTitleContaining("test");
    }

    @Test
    public void cardService_getCardById_AsUserWithAccess_ReturnsCard() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(card.getListModel()).thenReturn(listModel);
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(1L);

        Card result = cardService.getCardById(1L, authentication);

        assertNotNull(result);
        assertEquals(card, result);
    }

    @Test
    public void cardService_getCardById_AsUserWithoutAccess_ThrowsAccessDeniedException() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(card.getListModel()).thenReturn(listModel);
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(2L);

        assertThrows(AccessDeniedException.class, () -> cardService.getCardById(1L, authentication));
    }

    @Test
    public void cardService_saveCard_AsUserWithAccess_SavesCard() {

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(listRepository.findById(1L)).thenReturn(Optional.of(listModel));
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(1L);

        CardRequest cardRequest = new CardRequest("New Card", "Description", 1L, 1);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card result = cardService.saveCard(cardRequest, authentication, "user");

        assertNotNull(result);
        assertEquals(card, result);
    }

    @Test
    public void cardService_saveCard_AsUserWithoutAccess_ThrowsAccessDeniedException() {

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(listRepository.findById(1L)).thenReturn(Optional.of(listModel));
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(2L);

        CardRequest cardRequest = new CardRequest("New Card", "Description", 1L, 1);

        assertThrows(AccessDeniedException.class, () -> cardService.saveCard(cardRequest, authentication, "user"));
    }

    @Test
    public void cardService_updateCard_AsUserWithAccess_UpdatesCard() {

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(listRepository.findById(1L)).thenReturn(Optional.of(listModel));
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(1L);

        CardRequest cardRequest = new CardRequest("Updated Card", "Updated Description", 1L, 1);
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.updateCard(1L, cardRequest, authentication, "user");

        assertNotNull(result);
        assertEquals(card, result);
    }

    @Test
    public void cardService_updateCard_AsUserWithoutAccess_ThrowsAccessDeniedException() {

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(listRepository.findById(1L)).thenReturn(Optional.of(listModel));
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(2L);

        CardRequest cardRequest = new CardRequest("Updated Card", "Updated Description", 1L, 1);

        assertThrows(AccessDeniedException.class, () -> cardService.updateCard(1L, cardRequest, authentication, "user"));
    }


    @Test
    public void cardService_deleteCard_AsAdmin_DeletesCard() {

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L, authentication, "admin");

        verify(cardRepository, times(1)).delete(card);
    }

    @Test
    void cardService_deleteCard_AsUserWithoutAccess_ThrowsAccessDeniedException() {

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        when(card.getListModel()).thenReturn(listModel);
        when(listModel.getBoard()).thenReturn(board);
        when(board.getOwnerId()).thenReturn(3L);

        assertThrows(AccessDeniedException.class, () -> cardService.deleteCard(1L, authentication, "user"));

        verify(cardRepository, never()).delete(any(Card.class));
    }

}

