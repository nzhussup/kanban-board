package com.nzhussup.kanbanservice.service;

import static com.nzhussup.kanbanservice.service.helpers.Helper.checkIsAdmin;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nzhussup.kanbanservice.config.TestDataSetup;
import com.nzhussup.kanbanservice.exception.BoardNotFoundException;
import com.nzhussup.kanbanservice.model.Board;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddAdminRequest;
import com.nzhussup.kanbanservice.model.requestModels.board.BoardAddRequest;
import com.nzhussup.kanbanservice.repository.BoardRepository;
import com.nzhussup.kanbanservice.repository.UserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest extends TestDataSetup {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BoardService boardService;

    @Test
    public void boardService_getAllBoards_Admin_ReturnsAllBoards() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(boardRepository.findAll()).thenReturn(List.of(board1, board2));

        List<Board> boards = boardService.getAllBoards(authentication);

        assert boards.size() == 2;
        assert boards.get(0).getOwnerId() == 1L;
        assert boards.get(1).getOwnerId() == 2L;
    }

    @Test
    public void boardService_getAllBoards_User_ReturnsUserBoards() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(boardRepository.findByOwnerId(user.getId())).thenReturn(List.of(board2));

        List<Board> boards = boardService.getAllBoards(authentication);

        assert boards.size() == 1;
        assert boards.get(0).getOwnerId().equals(user.getId());
    }


    @Test
    public void boardService_getById_Admin_ReturnsBoard() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(boardRepository.findById(board1.getId())).thenReturn(Optional.of(board1));

        Board foundBoard = boardService.getById(board1.getId(), authentication);

        assert foundBoard != null;
        assert foundBoard.getId() == board1.getId();
        assert foundBoard.getOwnerId() == board1.getOwnerId();
    }

    @Test
    public void boardService_getById_User_ReturnsUserBoard() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(userRepository.findByUsername(authentication.getName())).thenReturn(user);
        when(boardRepository.findByIdAndOwnerId(board2.getId(), user.getId())).thenReturn(Optional.of(board2));

        Board foundBoard = boardService.getById(board2.getId(), authentication);

        assert foundBoard != null;
        assert foundBoard.getId() == board2.getId();
        assert foundBoard.getOwnerId() == user.getId();
    }

    @Test
    public void boardService_addBoard_ReturnsAddedBoard() {

        BoardAddRequest boardAddRequest = BoardAddRequest.builder()
                .name("To add board")
                .build();
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(boardRepository.save(any(Board.class))).thenReturn(Board.builder().ownerId(user.getId()).name(boardAddRequest.getName()).build());

        Board result = boardService.addBoard(boardAddRequest, authentication);

        assert result != null;
        assert result.getOwnerId().equals(user.getId());
        assert result.getName().equals(boardAddRequest.getName());
    }


    @Test
    public void boardService_addBoardAdmin_ThrowsAccessDeniedException_WhenNotAdmin() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);

        assertThrows(AccessDeniedException.class, () -> {
            boardService.addBoardAdmin(new BoardAddAdminRequest("Admin Board", user.getId()), authentication);
        });
    }


    @Test
    public void boardService_updateBoardAdmin_ThrowsBoardNotFoundException() {

        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> {
            boardService.updateBoardAdmin(1L, new BoardAddAdminRequest("New board", 1L));
        });
    }

    @Test
    public void boardService_deleteBoard_ReturnsOk_WhenBoardDeleted() {

        when(authentication.getName()).thenReturn(user.getUsername());
        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(boardRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(board1));

        ResponseEntity<?> response = boardService.deleteBoard(1L, authentication);

        assert HttpStatus.OK.equals(response.getStatusCode());
        verify(boardRepository).delete(board1);
    }

    @Test
    public void boardService_deleteBoard_ReturnsNotFound_WhenBoardDoesNotExist() {

        when(authentication.getName()).thenReturn(user.getUsername());
        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(boardRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.empty());

        ResponseEntity<?> response = boardService.deleteBoard(1L, authentication);

        assert HttpStatus.NOT_FOUND.equals(response.getStatusCode());
        assert response.getBody().equals("Board not found!");
    }

    @Test
    public void boardService_deleteBoardAdmin_ReturnsOk_WhenAdminDeletesBoard() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));

        ResponseEntity<?> response = boardService.deleteBoardAdmin(1L, authentication);

        assert HttpStatus.OK.equals(response.getStatusCode());
        verify(boardRepository).delete(board1);
    }

    @Test
    public void boardService_deleteBoardAdmin_ReturnsNotFound_WhenBoardDoesNotExist() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(boardRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = boardService.deleteBoardAdmin(1L, authentication);

        assert HttpStatus.NOT_FOUND.equals(response.getStatusCode());
        assert response.getBody().equals("Board not found!");
    }

    @Test
    public void boardService_deleteBoardAdmin_ReturnsForbidden_WhenNotAdmin() {

        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);

        ResponseEntity<?> response = boardService.deleteBoardAdmin(1L, authentication);

        assert HttpStatus.FORBIDDEN.equals(response.getStatusCode());
    }
}
