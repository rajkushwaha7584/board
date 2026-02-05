package com.whiteboard.app.service;

import java.util.UUID;

public class StrokeNotFoundException extends RuntimeException {

    public StrokeNotFoundException(UUID id) {
        super("Stroke not found: " + id);
    }
}
