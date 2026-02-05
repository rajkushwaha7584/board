package com.whiteboard.app.service;


import com.whiteboard.app.domain.Stroke;
import com.whiteboard.app.domain.Whiteboard;
import com.whiteboard.app.dto.*;
import com.whiteboard.app.repository.StrokeRepository;
import com.whiteboard.app.repository.WhiteboardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final StrokeRepository strokeRepository;

    public WhiteboardService(WhiteboardRepository whiteboardRepository, StrokeRepository strokeRepository) {
        this.whiteboardRepository = whiteboardRepository;
        this.strokeRepository = strokeRepository;
    }

    @Transactional(readOnly = true)
    public List<WhiteboardDto> listWhiteboards() {
        return whiteboardRepository.findAll().stream()
                .map(WhiteboardDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WhiteboardDto getWhiteboard(UUID id) {
        Whiteboard whiteboard = whiteboardRepository.findById(id)
                .orElseThrow(() -> new WhiteboardNotFoundException(id));
        return WhiteboardDto.from(whiteboard);
    }

    @Transactional
    public WhiteboardDto createWhiteboard(CreateWhiteboardRequest request) {
        Whiteboard whiteboard = new Whiteboard();
        whiteboard.setName(request.name() != null && !request.name().isBlank() ? request.name() : "Untitled Board");
        whiteboard = whiteboardRepository.save(whiteboard);
        return WhiteboardDto.from(whiteboard);
    }

    @Transactional
    public StrokeDto addStroke(StrokeMessage message) {
    
        if ("eraser".equalsIgnoreCase(message.shapeType())) {
            return null; // eraser is visual-only
        }
    
        Whiteboard whiteboard = whiteboardRepository.findById(message.whiteboardId())
                .orElseThrow(() -> new WhiteboardNotFoundException(message.whiteboardId()));
    
        Stroke stroke = new Stroke();
        stroke.setWhiteboard(whiteboard);
        stroke.setColor(message.color() != null ? message.color() : "#000000");
        stroke.setLineWidth(message.lineWidth() > 0 ? message.lineWidth() : 2.0);
        stroke.setShapeType(
                message.shapeType() != null && !message.shapeType().isBlank()
                        ? message.shapeType()
                        : "freehand"
        );
        stroke.setAuthorId(message.authorId());
    
        if (message.points() != null) {
            stroke.setPoints(
                message.points().stream()
                    .map(PointDto::toEntity)
                    .toList()
            );
        }
        
    
        whiteboard.getStrokes().add(stroke);
        Stroke saved = strokeRepository.save(stroke);
    
        return StrokeDto.from(saved);
    }
    @Transactional
public UUID removeStroke(UUID whiteboardId, UUID strokeId) {

    if (strokeId == null) {
        // second undo / invalid request → ignore safely
        return null;
    }

    Stroke stroke = strokeRepository.findById(strokeId)
            .orElse(null);

    if (stroke == null) {
        return null;
    }

    if (!stroke.getWhiteboard().getId().equals(whiteboardId)) {
        return null;
    }

    stroke.getWhiteboard().getStrokes().remove(stroke);
    strokeRepository.delete(stroke);

    return strokeId;
}


    // @Transactional
    // public UUID removeStroke(UUID whiteboardId, UUID strokeId) {
    //     Stroke stroke = strokeRepository.findById(strokeId)
    //             .orElseThrow(() -> new StrokeNotFoundException(strokeId));
    //     if (!stroke.getWhiteboard().getId().equals(whiteboardId)) {
    //         throw new StrokeNotFoundException(strokeId);
    //     }
    //     stroke.getWhiteboard().getStrokes().remove(stroke);
    //     strokeRepository.delete(stroke);
    //     return strokeId;
    // }

    @Transactional
    public void clearStrokes(UUID whiteboardId) {
        Whiteboard whiteboard = whiteboardRepository.findById(whiteboardId)
                .orElseThrow(() -> new WhiteboardNotFoundException(whiteboardId));
        strokeRepository.deleteAll(whiteboard.getStrokes());
        whiteboard.getStrokes().clear();
    }
}
