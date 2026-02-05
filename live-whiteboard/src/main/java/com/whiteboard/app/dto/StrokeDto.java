package com.whiteboard.app.dto;

import com.whiteboard.app.domain.Stroke;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for stroke data (REST and WebSocket).
 */
public record StrokeDto(
        UUID id,
        UUID whiteboardId,
        String color,
        double lineWidth,
        String shapeType,
        List<PointDto> points,
        Instant createdAt,
        String authorId
) {

    public static StrokeDto from(Stroke stroke) {
        return new StrokeDto(
                stroke.getId(),
                stroke.getWhiteboard().getId(),
                stroke.getColor(),
                stroke.getLineWidth(),
                stroke.getShapeType() != null ? stroke.getShapeType() : "freehand",
                stroke.getPoints().stream().map(PointDto::from).toList(),
                stroke.getCreatedAt(),
                stroke.getAuthorId()
        );
    }
}
