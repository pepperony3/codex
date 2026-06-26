package com.taskboard.service;

import com.taskboard.dto.CreateBoardRequest;
import com.taskboard.dto.CreateTaskRequest;
import com.taskboard.dto.MoveTaskRequest;
import com.taskboard.model.*;
import com.taskboard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository columnRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public BoardService(BoardRepository boardRepository,
                        BoardColumnRepository columnRepository,
                        TaskRepository taskRepository,
                        UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Board> getMyBoards(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return boardRepository.findByOwner(user);
    }

    @Transactional
    public Board createBoard(String username, CreateBoardRequest req) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Board board = new Board(req.getName(), user);
        board = boardRepository.save(board);

        // 默认创建三列：待办、进行中、已完成
        columnRepository.save(new BoardColumn("待办", board, 0));
        columnRepository.save(new BoardColumn("进行中", board, 1));
        columnRepository.save(new BoardColumn("已完成", board, 2));

        return boardRepository.findById(board.getId())
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    @Transactional
    public BoardColumn createColumn(Long boardId, String name) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        int maxOrder = board.getColumns().stream()
                .mapToInt(bc -> bc.getDisplayOrder()).max().orElse(-1);
        BoardColumn column = new BoardColumn(name, board, maxOrder + 1);
        return columnRepository.save(column);
    }

    @Transactional
    public Task createTask(Long columnId, CreateTaskRequest req) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));
        Task task = new Task(req.getTitle(), req.getDescription(), column);
        if (req.getAssigneeId() != null) {
            User assignee = userRepository.findById(req.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task moveTask(Long taskId, MoveTaskRequest req) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (req.getTargetColumnId() != null) {
            BoardColumn targetColumn = columnRepository.findById(req.getTargetColumnId())
                    .orElseThrow(() -> new RuntimeException("Target column not found"));
            task.setColumn(targetColumn);
        }
        if (req.getTargetSortOrder() != null) {
            task.setSortOrder(req.getTargetSortOrder());
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
