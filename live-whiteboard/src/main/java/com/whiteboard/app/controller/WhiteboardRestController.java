package com.whiteboard.app.controller;

import com.whiteboard.app.dto.CreateWhiteboardRequest;
import com.whiteboard.app.dto.WhiteboardDto;
import com.whiteboard.app.service.WhiteboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API for whiteboard CRUD and listing.
 */
@RestController
@RequestMapping("/api/whiteboards")
public class WhiteboardRestController {

    private final WhiteboardService whiteboardService;
    
    public WhiteboardRestController(WhiteboardService whiteboardService) {
        this.whiteboardService = whiteboardService;
    }

    @GetMapping
    public List<WhiteboardDto> list() {
        return whiteboardService.listWhiteboards();
    }

    @GetMapping("/{id}")
    public WhiteboardDto get(@PathVariable UUID id) {
        return whiteboardService.getWhiteboard(id);
    }

    @PostMapping
    public ResponseEntity<WhiteboardDto> create(@RequestBody CreateWhiteboardRequest request) {
        WhiteboardDto created = whiteboardService.createWhiteboard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
