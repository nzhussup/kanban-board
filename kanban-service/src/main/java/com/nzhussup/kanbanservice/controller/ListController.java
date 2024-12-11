package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.requestModels.list.ListRequest;
import com.nzhussup.kanbanservice.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/list")
public class ListController {

    private final ListService listService;


    @GetMapping()
    public ResponseEntity<?> getAllLists(Authentication authentication,
                                         @RequestParam Optional<String> name) {

        if (name.isPresent()) {
            List<ListModel> lists = listService.getListsByName(name.get(), authentication);
            if (lists.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List is empty");
            }
            return ResponseEntity.ok(lists);
        }
        List<ListModel> lists = listService.getAllLists(authentication);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getListById(@PathVariable Long id, Authentication authentication) {
        ListModel list;
        try {
            list = listService.getListById(id, authentication);
        } catch (ListNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping()
    public ResponseEntity<?> addList(@RequestBody ListRequest listRequest, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.addList(listRequest, authentication);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateList(@RequestBody ListRequest listRequest,@PathVariable long id, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.updateList(listRequest, id, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ListNotFoundException | BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteList(@PathVariable long id, Authentication authentication) {
        return listService.deleteList(id, authentication, "user");
    }

}
