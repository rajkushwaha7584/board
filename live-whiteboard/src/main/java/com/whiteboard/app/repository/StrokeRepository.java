package com.whiteboard.app.repository;

import com.whiteboard.app.domain.Stroke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StrokeRepository extends JpaRepository<Stroke, UUID> {
}
