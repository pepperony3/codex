package com.taskboard.controller;

import com.taskboard.dto.CreateBoardRequest;
import com.taskboard.dto.CreateTaskRequest;
import com.taskboard.dto.MoveTaskRequest;
import com.taskboard.model.Board;
import com.taskboard.model.BoardColumn;
import com.taskboard.model.Task;
import com.taskboard.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards")
    public ResponseEntity<List<Board>> getMyBoards(Principal principal) {
        return ResponseEntity.ok(boardService.getMyBoards(principal.getName()));
    }

    @PostMapping("/boards")
    public ResponseEntity<Board> createBoard(Principal principal,
                                             @Valid @RequestBody CreateBoardRequest req) {
        return ResponseEntity.ok(boardService.createBoard(principal.getName(), req));
    }

    @GetMapping("/boards/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    @PostMapping("/boards/{boardId}/columns")
    public ResponseEntity<BoardColumn> createColumn(@PathVariable Long boardId,
                                                    @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(boardService.createColumn(boardId, body.get("name")));
    }

    @PostMapping("/boards/{boardId}/columns/{columnId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long boardId,
                                           @PathVariable Long columnId,
                                           @Valid @RequestBody CreateTaskRequest req) {
        return ResponseEntity.ok(boardService.createTask(columnId, req));
    }

    @PatchMapping("/tasks/{taskId}/move")
    public ResponseEntity<Task> moveTask(@PathVariable Long taskId,
                                         @Valid @RequestBody MoveTaskRequest req) {
        return ResponseEntity.ok(boardService.moveTask(taskId, req));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        boardService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
