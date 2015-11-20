package util;

import domain.Coordinate;

import java.awt.*;

public class CoordinateConverter {
    public static Coordinate PointToCoordinate(Point point, int maxWidth, int maxHeigh, Coordinate center, float zoomRatio) {
        float dx = maxWidth / 2;
        float dy = maxHeigh / 2;

        float x = ((float) point.getX()) -  dx + center.getX();
        float y = ((float) point.getY()) - dy + center.getY();

        x /= zoomRatio;
        y /= zoomRatio;

        return new Coordinate(x, y);
    }

    public static Point CoordinateToPoint(Coordinate coords, int maxWidth, int maxHeight, Coordinate center, float zoomRatio) {
        float dx = maxWidth / 2;
        float dy = maxHeight / 2;

        float x = zoomRatio * coords.getX() + dx - center.getX();
        float y = zoomRatio * coords.getY() + dy - center.getY();

        return new Point((int)x, (int)y);
    }
}
