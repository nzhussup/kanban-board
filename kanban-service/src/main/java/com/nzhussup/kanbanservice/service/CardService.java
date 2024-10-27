package com.nzhussup.kanbanservice.service;

import static com.nzhussup.kanbanservice.service.helpers.Helper.checkIsAdmin;

import com.nzhussup.kanbanservice.exception.CardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.card.CardRequest;
import com.nzhussup.kanbanservice.repository.CardRepository;
import com.nzhussup.kanbanservice.repository.ListRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ListRepository listRepository;

    @Cacheable(value = "cards", key = "'allCards_' + #authentication.name")
    public List<Card> getAllCards(Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return cardRepository.findAll();
        }
        User user = userRepository.findByUsername(authentication.getName());
        return cardRepository.findByOwnerId(user.getId());
    }

    @Cacheable(value = "cards", key = "'title_' + #title + '_' + #authentication.name", unless = "#result == null")
    public List<Card> getCardsByTitle(String title, Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return cardRepository.findByTitleContaining(title);
        }
        User user = userRepository.findByUsername(authentication.getName());
        return cardRepository.findByTitleContainingAndOwnerId(title, user.getId());
    }

    @Cacheable(value = "cards", key = "'card_' + #id + '_' + #authentication.name", unless = "#result == null")
    public Card getCardById(Long id, Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("No such card!"));
        }
        User user = userRepository.findByUsername(authentication.getName());
        Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("No such card!"));

        if (!card.getListModel().getBoard().getOwnerId().equals(user.getId())) {
            throw new AccessDeniedException("User does not have permission to access this card!");
        }

        return card;
    }

    @Transactional
    @CachePut(value = "cards", key = "'card_' + #result.id")
    public Card saveCard(CardRequest card, Authentication authentication, String role) {

        ListModel listModel = listRepository.findById(card.getListId())
                .orElseThrow(() -> new ListNotFoundException("No such list!"));

        User user = null;
        if ("user".equals(role)) {
            user = userRepository.findByUsername(authentication.getName());
            if (!listModel.getBoard().getOwnerId().equals(user.getId())) {
                throw new AccessDeniedException("User does not have permission to access this list!");
            }
        }

        Card toAdd = new Card();
        toAdd.setTitle(card.getTitle());
        toAdd.setPosition(card.getPosition());
        toAdd.setDescription(card.getDescription());
        toAdd.setListModel(listModel);

        return cardRepository.save(toAdd);
    }

    @Transactional
    @CachePut(value = "cards", key = "'card_' + #result.id")
    public Card updateCard(Long id, CardRequest cardRequest, Authentication authentication, String role) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("No such card!"));
        ListModel listModel = listRepository.findById(cardRequest.getListId())
                .orElseThrow(() -> new ListNotFoundException("No such list!"));

        User user = null;
        if ("user".equals(role)) {
            user = userRepository.findByUsername(authentication.getName());
            if (!listModel.getBoard().getOwnerId().equals(user.getId())) {
                throw new AccessDeniedException("User does not have permission to access this card!");
            }
        }

        card.setTitle(cardRequest.getTitle());
        card.setPosition(cardRequest.getPosition());
        card.setDescription(cardRequest.getDescription());
        card.setListModel(listModel);

        return cardRepository.save(card);
    }

    @Transactional
    @CacheEvict(value = "cards", key = "'card_' + #id")
    public void deleteCard(Long id, Authentication authentication, String role) {

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("No such card!"));

        User user = null;
        if ("user".equals(role)) {
            user = userRepository.findByUsername(authentication.getName());
            if (!card.getListModel().getBoard().getOwnerId().equals(user.getId())) {
                throw new AccessDeniedException("User does not have permission to delete this card!");
            }
        }
        cardRepository.delete(card);
    }
}

