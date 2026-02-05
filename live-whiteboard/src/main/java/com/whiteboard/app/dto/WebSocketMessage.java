package com.whiteboard.app.dto;

import java.util.UUID;

/**
 * Generic WebSocket message wrapper for real-time events.
 */
public record WebSocketMessage<T>(
        String type,
        T payload
) {

    public static <T> WebSocketMessage<T> of(String type, T payload) {
        return new WebSocketMessage<>(type, payload);
    }

    public static final String TYPE_STROKE_ADDED = "STROKE_ADDED";
    public static final String TYPE_STROKE_REMOVED = "STROKE_REMOVED";
    public static final String TYPE_BOARD_CLEARED = "BOARD_CLEARED";
}
