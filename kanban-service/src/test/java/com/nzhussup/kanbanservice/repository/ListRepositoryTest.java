package com.nzhussup.kanbanservice.repository;

import com.nzhussup.kanbanservice.config.TestDataSetup;
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
public class ListRepositoryTest extends TestDataSetup {

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setup() {

        boardRepository.save(board1);
        boardRepository.save(board2);

        listRepository.save(list1);
        listRepository.save(list2);
        listRepository.save(list3);
    }

    @Test
    public void listRepository_save_returnSavedList() {
        ListModel list = listRepository.save(list1);
        assert list.getId().equals(list1.getId());
        assert list.getName().equals(list1.getName());
    }

    @Test
    public void listRepository_findByOwnerId_returnListsByOwnerId() {
        List<ListModel> lists = listRepository.findByOwnerId(list1.getBoard().getOwnerId());
        assert lists.get(0).getId().equals(list1.getId());
    }

    @Test
    public void listRepository_findByNameContaining_returnListsByNameContaining() {
        List<ListModel> lists = listRepository.findByNameContaining(list1.getName());
        assert lists.size() == 1;
        assert lists.get(0).getId().equals(list1.getId());
    }

    @Test
    public void listRepository_findAll_returnLists() {
        List<ListModel> lists = listRepository.findAll();
        assert lists.size() == 3;
    }

    @Test
    public void listRepository_delete_returnVoid() {
        listRepository.delete(list1);

        List<ListModel> lists1 = listRepository.findAll();
        assert lists1.size() == 2;
    }
}
