package ui;

import domain.*;
import util.CoordinateConverter;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MapDrawer {
    private ApplicationState state;
    private float zoom;
    private int baseStroke;
    private int halfStroke;
    private int maxWidth;
    private int maxHeight;

    private Color defaultColor;
    private Color hoverColor;
    private Color selectedColor;

    public MapDrawer(ApplicationState state) {
        this.state = state;

        defaultColor = Color.black;
        hoverColor = Color.red;
        selectedColor = Color.orange;
    }

    public void draw(Graphics2D g, int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        zoom = state.getZoomRatio();
        baseStroke = Math.round(10 * zoom);
        baseStroke = Math.max(2, baseStroke);
        halfStroke = baseStroke / 2;

        if (state.getCurrentMode() == EditionMode.AddNode)
            drawCurrentNode(g);
        drawSegments(g);
        drawNodes(g);
    }

    private void drawCurrentNode(Graphics2D g) {
        Point p = CoordinateConverter.CoordinateToPoint(
                state.getCurrentPosition(),
                maxWidth,
                maxHeight,
                state.getCenterCoordinate(),
                zoom);
        g.fill(new Ellipse2D.Float(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
    }

    private void drawSegments(Graphics2D g) {
        g.setStroke(new BasicStroke(halfStroke));
        for (Segment s :state.getPlane().getSegments().values()) {
            g.setColor(defaultColor);

            Point source = CoordinateConverter.CoordinateToPoint(
                    s.getSource().getCoordinate(),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom
            );
            Point destination = CoordinateConverter.CoordinateToPoint(
                    s.getDestination().getCoordinate(),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom
            );

            g.drawLine(source.x, source.y, destination.x, destination.y);
        }

    }

    private void drawNodes(Graphics2D g) {
        for (Node n : state.getPlane().getNodes()) {
            g.setColor(defaultColor);

            Point p = CoordinateConverter.CoordinateToPoint(
                    n.getCoordinate(),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom);

            if (n == state.getSelectedNode())
                g.setColor(selectedColor);
            else if (n.isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);

            g.fill(new Ellipse2D.Float(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
        }
    }
}
