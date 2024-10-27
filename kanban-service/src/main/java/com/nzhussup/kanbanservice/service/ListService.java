package com.nzhussup.kanbanservice.service;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.User;
import com.nzhussup.kanbanservice.model.requestModels.list.ListRequest;
import com.nzhussup.kanbanservice.repository.BoardRepository;
import com.nzhussup.kanbanservice.repository.ListRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static com.nzhussup.kanbanservice.service.helpers.Helper.checkIsAdmin;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Cacheable(value = "lists", key = "'allLists_' + #authentication.name")
    public List<ListModel> getAllLists(Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return listRepository.findAll();
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        return listRepository.findByOwnerId(user.getId());
    }

    @Cacheable(value = "lists", key = "'name_' + #name + '_' + #authentication.name", unless = "#result == null")
    public List<ListModel> getListsByName(String name, Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return listRepository.findByNameContaining(name);
        }

        User user = userRepository.findByUsername(authentication.getName());
        return listRepository.findByNameContainingAndOwnerId(name, user.getId());
    }

    @Cacheable(value = "lists", key = "'list_' + #listId + '_' + #authentication.name", unless = "#result == null")
    public ListModel getListById(Long listId, Authentication authentication) {
        if (checkIsAdmin(authentication)) {
            return listRepository.findById(listId).orElseThrow(() -> new ListNotFoundException("List not found"));
        }

        User user = userRepository.findByUsername(authentication.getName());
        ListModel list = listRepository.findById(listId).orElseThrow(() -> new ListNotFoundException("List not found"));

        if (!list.getBoard().getOwnerId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return list;
    }

    @Transactional
    @CachePut(value = "lists", key = "#result.id")
    @CacheEvict(value = {"lists", "cards"}, allEntries = true)
    public ListModel addList(ListRequest listRequest, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        Board board = boardRepository.findById(listRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        if (!board.getOwnerId().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized to add a list to this board.");
        }

        ListModel newList = new ListModel();
        newList.setName(listRequest.getName());
        newList.setBoard(board);
        newList.setPosition(listRequest.getPosition());

        return listRepository.save(newList);
    }

    @Transactional
    @CachePut(value = "lists", key = "#result.id")
    @CacheEvict(value = {"lists", "cards"}, allEntries = true)
    public ListModel addListAdmin(ListRequest listRequest, Authentication authentication) {
        if (!checkIsAdmin(authentication)) {
            throw new AccessDeniedException("User is not authorized to perform this action.");
        }

        Board board = boardRepository.findById(listRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        ListModel newList = new ListModel();
        newList.setName(listRequest.getName());
        newList.setBoard(board);
        newList.setPosition(listRequest.getPosition());

        return listRepository.save(newList);
    }

    @Transactional
    @CachePut(value = "lists", key = "#result.id")
    @CacheEvict(value = {"lists", "cards"}, allEntries = true)
    public ListModel updateList(ListRequest listRequest, Long id, Authentication authentication) {
        ListModel currentList = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("List not found."));

        User user = userRepository.findByUsername(authentication.getName());
        Board board = boardRepository.findById(listRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));

        if (!currentList.getBoard().getOwnerId().equals(user.getId())) {
            throw new AccessDeniedException("User is not authorized to update this list.");
        }

        currentList.setName(listRequest.getName());
        currentList.setBoard(board);
        currentList.setPosition(listRequest.getPosition());

        return listRepository.save(currentList);
    }

    @Transactional
    @CachePut(value = "lists", key = "#result.id")
    @CacheEvict(value = {"lists", "cards"}, allEntries = true)
    public ListModel updateListAdmin(ListRequest listRequest, Long id, Authentication authentication) {
        if (!checkIsAdmin(authentication)) {
            throw new AccessDeniedException("User is not authorized to perform this action.");
        }

        ListModel currentList = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("List not found."));
        Board board = boardRepository.findById(listRequest.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException("Board not found."));

        currentList.setName(listRequest.getName());
        currentList.setBoard(board);
        currentList.setPosition(listRequest.getPosition());

        return listRepository.save(currentList);
    }

    @Transactional
    @CacheEvict(value = {"lists", "cards"}, allEntries = true)
    public ResponseEntity<?> deleteList(Long id, Authentication authentication, String role) {
        ListModel currentList = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("List not found."));

        if ("admin".equals(role) && checkIsAdmin(authentication)) {
            listRepository.delete(currentList);
            return ResponseEntity.ok().build();
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (!currentList.getBoard().getOwnerId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }

        listRepository.delete(currentList);
        return ResponseEntity.ok().build();
    }
}
