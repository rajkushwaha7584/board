package com.whiteboard.app.websocket;

import com.whiteboard.app.dto.*;
import com.whiteboard.app.service.WhiteboardService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.UUID;

/**
 * WebSocket controller for real-time whiteboard collaboration.
 * <p>
 * Receives drawing events (strokes) from clients and broadcasts them to all users
 * connected to the same board. Clients must subscribe to the board topic to receive
 * events; everyone subscribed to that topic gets every drawing event for that board.
 * </p>
 * <ul>
 *   <li>Send drawing: /app/whiteboard/{whiteboardId}/stroke</li>
 *   <li>Subscribe for events: /topic/whiteboard/{whiteboardId}</li>
 * </ul>
 */
@Controller
public class WhiteboardWebSocketController {

    /** Topic prefix for a board; all users on the same board subscribe here. */
    public static final String BOARD_TOPIC_PREFIX = "/topic/whiteboard/";

    private final WhiteboardService whiteboardService;
    private final SimpMessagingTemplate messagingTemplate;

    public WhiteboardWebSocketController(WhiteboardService whiteboardService,
                                         SimpMessagingTemplate messagingTemplate) {
        this.whiteboardService = whiteboardService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Receives a stroke (drawing event), persists it, and broadcasts it to all users
     * connected to this board.
     */
    @MessageMapping("/whiteboard/{whiteboardId}/stroke")
    public void handleStroke(@DestinationVariable String whiteboardId, @Payload StrokeMessage message) {
        StrokeMessage withBoardId = mapWhiteboardId(message, whiteboardId);
        StrokeDto stroke = whiteboardService.addStroke(withBoardId);
        broadcastToBoard(whiteboardId, WebSocketMessage.TYPE_STROKE_ADDED, stroke);
    }

    /**
     * Removes a stroke (undo) and broadcasts to all users on the board.
     */
    // @MessageMapping("/whiteboard/{whiteboardId}/stroke/remove")
    // public void handleRemoveStroke(@DestinationVariable String whiteboardId, @Payload RemoveStrokeMessage message) {
    //     // Safeguard: if there is no stroke id, do nothing (avoid null id error)
    //     if (message == null || message.strokeId() == null) {
    //         return;
    //     }
    //     java.util.UUID boardUuid = java.util.UUID.fromString(whiteboardId);
    //     java.util.UUID removedId = whiteboardService.removeStroke(boardUuid, message.strokeId());
    //     broadcastToBoard(whiteboardId, WebSocketMessage.TYPE_STROKE_REMOVED, removedId.toString());
    // }

@MessageMapping("/whiteboard/{whiteboardId}/stroke/remove")
public void handleRemoveStroke(
        @DestinationVariable String whiteboardId,
        @Payload RemoveStrokeMessage message
) {
    if (message == null || message.strokeId() == null) {
        return;
    }

    UUID boardUuid = UUID.fromString(whiteboardId);
    UUID removedId = whiteboardService.removeStroke(boardUuid, message.strokeId());

    if (removedId != null) {
        broadcastToBoard(
                whiteboardId,
                WebSocketMessage.TYPE_STROKE_REMOVED,
                removedId.toString()
        );
    }
}

    /**
     * Clears all strokes from the board and broadcasts to all users.
     */
    @MessageMapping("/whiteboard/{whiteboardId}/clear")
    public void handleClear(@DestinationVariable String whiteboardId) {
        java.util.UUID boardUuid = java.util.UUID.fromString(whiteboardId);
        whiteboardService.clearStrokes(boardUuid);
        broadcastToBoard(whiteboardId, WebSocketMessage.TYPE_BOARD_CLEARED, null);
    }

    /**
     * Broadcasts a message to every client subscribed to the given board.
     */
    private void broadcastToBoard(String whiteboardId, String eventType, Object payload) {
        String topic = boardTopic(whiteboardId);
        messagingTemplate.convertAndSend(topic, WebSocketMessage.of(eventType, payload));
    }

    /**
     * Topic destination for a board; all users on the same board subscribe here.
     */
    public static String boardTopic(String whiteboardId) {
        return BOARD_TOPIC_PREFIX + whiteboardId;
    }

    private static StrokeMessage mapWhiteboardId(StrokeMessage message, String whiteboardId) {
        return new StrokeMessage(
                java.util.UUID.fromString(whiteboardId),
                message.color(),
                message.lineWidth(),
                message.shapeType(),
                message.points(),
                message.authorId()
        );
    }
}
