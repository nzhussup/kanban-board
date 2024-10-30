package com.nzhussup.kanbanservice.config;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.Card;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.User;

import java.time.LocalDateTime;

public class TestDataSetup {

    public User admin = User.builder().id(1L).username("admin")
            .password("$2a$12$9pml//HbMdII7IHKA2xhyOeMnrYj7xgzIyrLEYLYFdFMXDubxgeMW")
            .role("ROLE_ADMIN").build();
    public User user = User.builder().id(2L).username("user")
            .password("$2a$12$9pml//HbMdII7IHKA2xhyOeMnrYj7xgzIyrLEYLYFdFMXDubxgeMW")
            .role("ROLE_USER").build();


    public User test1 = User.builder().id(1L).username("test1")
            .password("$2a$12$9pml//HbMdII7IHKA2xhyOeMnrYj7xgzIyrLEYLYFdFMXDubxgeMW")
            .role("ROLE_ADMIN").build();
    public User test2 = User.builder().id(2L).username("test2")
            .password("$2a$12$9pml//HbMdII7IHKA2xhyOeMnrYj7xgzIyrLEYLYFdFMXDubxgeMW")
            .role("ROLE_USER").build();

    public Board board1 = Board.builder()
            .name("Board 1")
            .ownerId(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public Board board2 = Board.builder()
            .name("Board 2")
            .ownerId(2L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public ListModel list1 = ListModel.builder()
            .name("List 1")
            .position(1)
            .board(board1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public ListModel list2 = ListModel.builder()
            .name("List 2")
            .position(2)
            .board(board1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public ListModel list3 = ListModel.builder()
            .name("Another List")
            .position(1)
            .board(board2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public Card card1 = Card.builder()
            .title("Card 1")
            .description("Description for Card 1")
            .listModel(list1)
            .position(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public Card card2 = Card.builder()
            .title("Card 2")
            .description("Description for Card 2")
            .listModel(list1)
            .position(2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    public Card card3 = Card.builder()
            .title("Card 3")
            .description("Description for Card 3")
            .listModel(list2)
            .position(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
}
