package com.nzhussup.kanbanservice.service;
import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.requestModels.list.ListRequest;
import com.nzhussup.kanbanservice.repository.BoardRepository;
import com.nzhussup.kanbanservice.repository.ListRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListServiceTest extends TestDataSetup {

    @InjectMocks
    private ListService listService;

    @Mock
    private ListRepository listRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private Authentication authentication;

    // Assuming you have test data objects: user, list1, board1, etc.

    @Test
    public void listService_getAllLists_AdminUser_returnsEmptyList() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(listRepository.findAll()).thenReturn(Collections.emptyList());

        List<ListModel> lists = listService.getAllLists(authentication);

        assert lists.size() == 0;
    }

    @Test
    public void listService_getAllLists_NonAdminUser_returnsEmptyList() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(listRepository.findByOwnerId(user.getId())).thenReturn(Collections.emptyList());

        List<ListModel> lists = listService.getAllLists(authentication);

        assert lists.size() == 0;
    }

    @Test
    public void listService_getListById_AdminUser_returnsList() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(listRepository.findById(1L)).thenReturn(Optional.of(list1));

        ListModel list = listService.getListById(1L, authentication);

        assert list != null;
        assert list.getId() == list1.getId();
    }

    @Test
    public void listService_getListById_NonAdminUser_returnsList() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(listRepository.findById(list1.getId())).thenReturn(Optional.empty());

        assertThrows(ListNotFoundException.class, () -> listService.getListById(list1.getId(), authentication));
    }

    @Test
    public void listService_addList_throwsAccessDeniedException() {

        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));

        assertThrows(AccessDeniedException.class, () -> {
           listService.addList(ListRequest.builder().boardId(1L).position(2).name("new list").build(), authentication);
        });

    }

    @Test
    public void listService_deleteList_AdminUser_returnsSuccess() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(listRepository.findById(1L)).thenReturn(Optional.of(new ListModel()));

        ResponseEntity<?> response = listService.deleteList(1L, authentication, "admin");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void listService_deleteList_returnsListNotFoundException() {
        when(listRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ListNotFoundException.class, () -> {
            listService.deleteList(1L, authentication, "user");
        });
    }

    @Test
    public void listService_addListAdmin_returnAccessDeniedException() {
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);

        assertThrows(AccessDeniedException.class, () -> {
            listService.addListAdmin(ListRequest.builder().boardId(1L).position(2).name("new list").build(), authentication);
        });

    }

}
