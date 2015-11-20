package ui;

import domain.ApplicationState;
import util.CoordinateConverter;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MapDrawer {
    private ApplicationState state;

    public MapDrawer(ApplicationState state) {
        this.state = state;
    }

    public void draw(Graphics2D g, int maxWidth, int maxHeight) {
        Stroke nodeStroke = new BasicStroke(5);

        Point p = CoordinateConverter.CoordinateToPoint(state.getCurrentPosition(), maxWidth, maxHeight, 1);
        int x = (int) p.getX();
        int y = (int) p.getY();

        g.fill(new Ellipse2D.Float(x-5, y-5, 10, 10));
    }
}
