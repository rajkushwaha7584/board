package com.whiteboard.app.controller;

import com.whiteboard.app.service.StrokeNotFoundException;
import com.whiteboard.app.service.WhiteboardNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handling for whiteboard API.
 */
@RestControllerAdvice
public class WhiteboardExceptionHandler {

    @ExceptionHandler(WhiteboardNotFoundException.class)
    public ProblemDetail handleNotFound(WhiteboardNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(StrokeNotFoundException.class)
    public ProblemDetail handleStrokeNotFound(StrokeNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
