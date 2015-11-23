package util;

import domain.Coordinate;

import java.awt.*;

public class CoordinateConverter {
    public static Coordinate PointToCoordinate(Point point, int maxWidth, int maxHeigh, Coordinate center, double zoomRatio) {
        double dx = maxWidth / 2;
        double dy = maxHeigh / 2;

        double x = point.getX() -  dx + center.getX();
        double y = point.getY() - dy + center.getY();

        x /= zoomRatio;
        y /= -zoomRatio;

        return new Coordinate(x, y);
    }

    public static Point CoordinateToPoint(Coordinate coords, int maxWidth, int maxHeight, Coordinate center, double zoomRatio) {
        double dx = maxWidth / 2;
        double dy = maxHeight / 2;

        double x = zoomRatio * coords.getX() + dx - center.getX();
        double y = -zoomRatio * coords.getY() + dy - center.getY();

        return new Point((int)x, (int)y);
    }
}
