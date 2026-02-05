package com.whiteboard.app.repository;

import com.whiteboard.app.domain.Whiteboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WhiteboardRepository extends JpaRepository<Whiteboard, UUID> {
}
