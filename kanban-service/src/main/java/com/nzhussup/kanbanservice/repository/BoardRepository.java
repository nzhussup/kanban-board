package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwnerId(Long ownerId);
    Optional<Board> findByIdAndOwnerId(Long id, Long ownerId);
}
