package com.nzhussup.kanbanservice.controller;

import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.exception.ListNotFoundException;
import com.nzhussup.kanbanservice.model.ListModel;
import com.nzhussup.kanbanservice.model.requestModels.list.ListByNameRequest;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/list")
public class ListController {

    private final ListService listService;


    @GetMapping("/all")
    public ResponseEntity<List<ListModel>> getAllLists(Authentication authentication) {
        List<ListModel> lists = listService.getAllLists(authentication);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/byName")
    public ResponseEntity<?> getListByName(@RequestBody ListByNameRequest request, Authentication authentication) {
        List<ListModel> lists = listService.getListsByName(request.getName(), authentication);
        if (lists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List is empty");
        }
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

    @PostMapping("/add")
    public ResponseEntity<?> addList(@RequestBody ListRequest listRequest, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.addList(listRequest, authentication);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addAdminList(@RequestBody ListRequest listRequest, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.addListAdmin(listRequest, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @PutMapping("/update/{id}")
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

    @PutMapping("/admin/update/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> updateAdminList(@RequestBody ListRequest listRequest,@PathVariable long id, Authentication authentication) {
        ListModel listModel;
        try {
            listModel = listService.updateListAdmin(listRequest, id, authentication);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied!");
        } catch (ListNotFoundException | BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(listModel);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteList(@PathVariable long id, Authentication authentication) {
        return listService.deleteList(id, authentication, "user");
    }

    @DeleteMapping("/admin/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteAdminList(@PathVariable long id, Authentication authentication) {
        return listService.deleteList(id, authentication, "admin");
    }


}
