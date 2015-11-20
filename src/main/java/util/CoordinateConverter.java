package util;

import domain.Coordinate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

public class CoordinateConverter {
    public static Coordinate PointToCoordinate(Point point, int maxWidth, int maxHeigh, float zoomLevel) {
        float dx = maxWidth / 2;
        float dy = maxHeigh / 2;

        float x = ((float) point.getX()) -  dx;
        float y = ((float) point.getY()) - dy;

        return new Coordinate(x, y);
    }

    public static Point CoordinateToPoint(Coordinate coords, float zoomLevel) {
        throw new NotImplementedException();
    }
}
