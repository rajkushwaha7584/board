package com.whiteboard.app.service;

import java.util.UUID;

public class WhiteboardNotFoundException extends RuntimeException {

    public WhiteboardNotFoundException(UUID id) {
        super("Whiteboard not found: " + id);
    }
}
