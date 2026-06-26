package com.taskboard.service;

import com.taskboard.dto.CreateBoardRequest;
import com.taskboard.model.Board;
import com.taskboard.model.User;
import com.taskboard.repository.BoardRepository;
import com.taskboard.repository.BoardColumnRepository;
import com.taskboard.repository.TaskRepository;
import com.taskboard.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock private BoardRepository boardRepository;
    @Mock private BoardColumnRepository columnRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    void getMyBoards_shouldReturnUserBoards() {
        User user = new User("alice", "a@test.com", "pass");
        user.setId(1L);
        Board board = new Board("My Board", user);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(boardRepository.findByOwner(user)).thenReturn(List.of(board));

        List<Board> result = boardService.getMyBoards("alice");

        assertEquals(1, result.size());
        assertEquals("My Board", result.get(0).getName());
    }

    @Test
    void createBoard_shouldCreateBoardWithDefaultColumns() {
        User user = new User("bob", "b@test.com", "pass");
        user.setId(2L);
        CreateBoardRequest req = new CreateBoardRequest();
        req.setName("Project X");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(boardRepository.save(any(Board.class))).thenAnswer(i -> {
            Board b = i.getArgument(0);
            b.setId(10L);
            return b;
        });
        when(boardRepository.findById(10L)).thenAnswer(i -> {
            Board b = new Board("Project X", user);
            b.setId(10L);
            return Optional.of(b);
        });

        Board result = boardService.createBoard("bob", req);

        assertEquals("Project X", result.getName());
        verify(columnRepository, times(3)).save(any());
    }

    @Test
    void getBoard_shouldThrowWhenNotFound() {
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> boardService.getBoard(999L));
    }

    @Test
    void deleteTask_shouldCallRepository() {
        boardService.deleteTask(5L);
        verify(taskRepository).deleteById(5L);
    }
}
