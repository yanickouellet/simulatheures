package ui;

import domain.ApplicationState;
import domain.EditionMode;
import domain.Node;
import util.CoordinateConverter;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MapDrawer {
    private ApplicationState state;

    public MapDrawer(ApplicationState state) {
        this.state = state;
    }

    public void draw(Graphics2D g, int maxWidth, int maxHeight) {
        float zoom = state.getZoomRatio();
        int baseStroke = Math.round(10 * zoom);
        baseStroke = Math.max(2, baseStroke);
        int halfStroke = baseStroke / 2;

        Point p;
        int x, y;

        if (state.getCurrentMode() == EditionMode.AddNode) {
            p = CoordinateConverter.CoordinateToPoint(
                    state.getCurrentPosition(),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom);
            x = (int) p.getX();
            y = (int) p.getY();
            g.fill(new Ellipse2D.Float(x - halfStroke, y - halfStroke, baseStroke, baseStroke));
        }

        g.setColor(Color.black);
        for (Node n : state.getPlane().getNodes()) {
            p = CoordinateConverter.CoordinateToPoint(
                    n.getCoordinate(),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom);
            x = (int) p.getX();
            y = (int) p.getY();

            g.fill(new Ellipse2D.Float(x - halfStroke, y - halfStroke, baseStroke, baseStroke));
        }

    }
}
