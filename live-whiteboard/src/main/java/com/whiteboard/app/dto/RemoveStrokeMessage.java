package com.whiteboard.app.dto;

import java.util.UUID;

/**
 * WebSocket message payload for removing a stroke (undo).
 */
public record RemoveStrokeMessage(UUID strokeId) {
}
