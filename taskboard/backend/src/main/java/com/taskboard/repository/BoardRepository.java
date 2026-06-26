package com.taskboard.repository;

import com.taskboard.model.Board;
import com.taskboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwner(User owner);
}
