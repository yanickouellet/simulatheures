package util;

import domain.Coordinate;

import java.awt.*;

public class CoordinateConverter {
    public static Coordinate PointToCoordinate(Point point, int maxWidth, int maxHeigh, float zoomRatio) {
        float dx = maxWidth / 2;
        float dy = maxHeigh / 2;

        float x = ((float) point.getX()) -  dx;
        float y = ((float) point.getY()) - dy;

        x /= zoomRatio;
        y /= zoomRatio;

        return new Coordinate(x, y);
    }

    public static Point CoordinateToPoint(Coordinate coords, int maxWidth, int maxHeight, float zoomRatio) {
        float dx = maxWidth / 2;
        float dy = maxHeight / 2;

        float x = zoomRatio * coords.getX() + dx;
        float y = zoomRatio * coords.getY() + dy;

        return new Point((int)x, (int)y);
    }
}
