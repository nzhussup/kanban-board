package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CardRepositoryTest extends TestDataSetup {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ListRepository listRepository; // Add ListRepository
    @Autowired
    private BoardRepository boardRepository; // Add BoardRepository

    @BeforeEach
    public void setup() {

        boardRepository.save(board1);
        boardRepository.save(board2);

        listRepository.save(list1);
        listRepository.save(list2);
        listRepository.save(list3);

        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);
    }

    @Test
    public void cardRepository_save_returnSavedCard() {
        Card savedCard = cardRepository.save(card1);
        assert savedCard.getId().equals(card1.getId());
        assert savedCard.getTitle().equals(card1.getTitle());
    }

    @Test
    public void cardRepository_findByOwnerId_returnCardsByOwnerId() {
        List<Card> cards = cardRepository.findByOwnerId(list1.getBoard().getOwnerId());
        assert cards.size() == 3;
        assert cards.stream().anyMatch(card -> card.getId().equals(card1.getId()));
        assert cards.stream().anyMatch(card -> card.getId().equals(card2.getId()));
    }

    @Test
    public void cardRepository_findByTitleContaining_returnCardsByTitleContaining() {
        List<Card> cards = cardRepository.findByTitleContaining("Card");
        assert cards.size() == 3;
    }

    @Test
    public void setCardRepository_findAll_returnAllCards() {
        List<Card> cards = cardRepository.findAll();
        assert cards.size() == 3;
    }


    @Test
    public void cardRepository_delete_returnVoid() {
        cardRepository.delete(card1);
        List<Card> remainingCards = cardRepository.findAll();
        assert remainingCards.size() == 2;
    }
}
