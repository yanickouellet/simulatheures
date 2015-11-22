package ui;

import domain.*;
import domain.network.Node;
import domain.network.Segment;
import util.CoordinateConverter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

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
    private Color arrowColor;

    public MapDrawer(ApplicationState state) {
        this.state = state;

        defaultColor = Color.black;
        arrowColor = Color.gray;
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
        Segment[] segments = state.getPlane().getSegments().values().toArray(new Segment[0]);
        ArrayList<int[]> segmentPoints = new ArrayList<>();

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

            segmentPoints.add(new int[]{source.x, source.y, destination.x, destination.y});
        }

        // We must draw arrow after segments
        for (int i = 0; i < segmentPoints.size(); i++) {
            if (segments[i] == state.getSelectedElement() || state.isSegmentOnCurrentRoute(segments[i]))
                g.setColor(selectedColor);
            else if (segments[i].isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);
            else
                g.setColor(defaultColor);

            int[] s = segmentPoints.get(i);
            g.drawLine(s[0], s[1], s[2], s[3]);
        }

        for (int [] s : segmentPoints) {
            drawArrow(g, s[0], s[1], s[2], s[3], baseStroke);
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

            if (n == state.getSelectedElement() || state.isNodeStationOnCurrentRoute(n))
                g.setColor(selectedColor);
            else if (n.isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);

            g.fill(new Ellipse2D.Float(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
        }
    }

    // Inspired by http://stackoverflow.com/a/4112875/3757513
    private void drawArrow(Graphics2D g1, int x1, int y1, int x2, int y2, int width) {
        Graphics2D g = (Graphics2D) g1.create();
        g.setColor(arrowColor);

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        g.fillPolygon(new int[] {len, len-width, len-width, len},
                      new int[] {0, -width, width, 0}, 4);

        g.setStroke(new BasicStroke(1));
        g.setColor(defaultColor);
        g.drawPolygon(new int[] {len, len-width, len-width, len},
                new int[] {0, -width, width, 0}, 4);

        g.dispose();
    }
}
