package com.whiteboard.app.dto;

import java.util.List;
import java.util.UUID;

/**
 * WebSocket message payload for adding a stroke (incoming from client).
 */
public record StrokeMessage(
        UUID whiteboardId,
        String color,
        double lineWidth,
        String shapeType,
        List<PointDto> points,
        String authorId
) {
}
