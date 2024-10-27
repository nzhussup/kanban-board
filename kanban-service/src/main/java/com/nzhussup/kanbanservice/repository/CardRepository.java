package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT c FROM Card c WHERE c.listModel.board.ownerId = :ownerId")
    List<Card> findByOwnerId(Long ownerId);

    List<Card> findByTitleContaining(String name);

    @Query("SELECT c FROM Card c WHERE c.title LIKE %:title% AND c.listModel.board.ownerId = :ownerId")
    List<Card> findByTitleContainingAndOwnerId(@Param("title") String title, @Param("ownerId") Long ownerId);

}
