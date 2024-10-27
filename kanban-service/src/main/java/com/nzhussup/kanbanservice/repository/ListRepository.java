package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.model.ListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ListRepository extends JpaRepository<ListModel, Long> {
    List<ListModel> findByBoardId(Long boardId);

    @Query("SELECT l FROM ListModel l WHERE l.board.ownerId = :ownerId")
    List<ListModel> findByOwnerId(@Param("ownerId") Long ownerId);

    List<ListModel> findByNameContaining(String name);

    @Query("SELECT l FROM ListModel l WHERE l.name LIKE %:name% AND l.board.ownerId = :ownerId")
    List<ListModel> findByNameContainingAndOwnerId(@Param("name") String name, @Param("ownerId") Long ownerId);

}
