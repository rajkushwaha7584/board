package com.whiteboard.app.dto;

import com.whiteboard.app.domain.Point;

/**
 * DTO for a 2D point in stroke data.
 */
public record PointDto(double x, double y) {

    public static PointDto from(Point point) {
        return new PointDto(point.getX(), point.getY());
    }

    public Point toEntity() {
        return new Point(x, y);
    }
}
