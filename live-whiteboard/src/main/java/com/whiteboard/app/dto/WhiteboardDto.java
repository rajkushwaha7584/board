package com.whiteboard.app.dto;

import com.whiteboard.app.domain.Whiteboard;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for whiteboard summary (list/detail).
 */
public record WhiteboardDto(
        UUID id,
        String name,
        Instant createdAt,
        List<StrokeDto> strokes
) {

    public static WhiteboardDto from(Whiteboard whiteboard) {
        List<StrokeDto> strokes = whiteboard.getStrokes().stream()
                .map(StrokeDto::from)
                .toList();
        return new WhiteboardDto(
                whiteboard.getId(),
                whiteboard.getName(),
                whiteboard.getCreatedAt(),
                strokes
        );
    }
}
