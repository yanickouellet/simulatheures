package ui;

import domain.*;
import domain.network.Node;
import domain.network.Segment;
import domain.simulation.Simulation;
import domain.simulation.Vehicle;
import util.CoordinateConverter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class MapDrawer {
    private ApplicationState state;
    private double zoom;
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
        baseStroke = (int) Math.round(10 * zoom);
        baseStroke = Math.max(2, baseStroke);
        halfStroke = baseStroke / 2;

        if (state.getCurrentMode() == EditionMode.AddNode)
            drawCurrentNode(g);
        drawSegments(g);
        drawNodes(g);

        if (state.getCurrentMode() == EditionMode.Simulation)
            drawVehicles(g);
    }

    private void drawCurrentNode(Graphics2D g) {
        Point p = CoordinateConverter.CoordinateToPoint(
                state.getCurrentPosition(),
                maxWidth,
                maxHeight,
                state.getCenterCoordinate(),
                zoom);
        g.fill(new Ellipse2D.Double(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
    }

    private void drawSegments(Graphics2D g) {
        Segment[] segments = state.getNetwork().getSegments().values().toArray(new Segment[0]);
        ArrayList<int[]> segmentPoints = new ArrayList<>();

        g.setStroke(new BasicStroke(halfStroke / 2));
        for (Segment s :state.getNetwork().getSegments().values()) {
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

            double delta = halfStroke;
            double x1 = source.x;
            double x2 = destination.x;
            double y1 = source.y;
            double y2 = destination.y;
            double dirX = Math.signum(s.getDestination().getCoordinate().getX() - s.getSource().getCoordinate().getX());
            double dirY = Math.signum(s.getDestination().getCoordinate().getY() - s.getSource().getCoordinate().getY());
            double angle = s.getVector().computeAngle();

            double sin, cos, sin2, cos2;
            double pi4 = Math.PI / 2;
            if (dirX > 0) {
                cos = Math.cos(angle + pi4);
            } else {
                cos = Math.cos(angle - pi4);
            }

            if (dirY > 0) {
                sin = Math.sin(angle + pi4);
            } else {
                sin= Math.sin(angle - pi4);
            }



            x1 += cos * delta;
            y1 += sin * delta;
            x2 += cos * delta;
            y2 += sin * delta;

            segmentPoints.add(new int[]{(int)x1, (int)y1, (int)x2, (int)y2, (int) dirX, (int)dirY});
        }

        // We must draw arrow after segments
        for (int i = 0; i < segmentPoints.size(); i++) {
            int[] s = segmentPoints.get(i);

            if (segments[i] == state.getSelectedElement() || state.isSegmentOnCurrentRoute(segments[i]))
                g.setColor(selectedColor);
            else if (segments[i].isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);
            else
                g.setColor(defaultColor);

            g.drawLine(s[0], s[1], s[2], s[3]);
        }

        for (int i = 0; i < segmentPoints.size(); i++) {
            int[] s = segmentPoints.get(i);
            double ratio = s[3] - s[1] < 5 || s[2] - s[0] < 5 ? 0.90 : 0.95;
            Coordinate c = segments[i].getVector().computeNewCoordinate(ratio);
            Point p = CoordinateConverter.CoordinateToPoint(c, maxWidth, maxHeight, state.getCenterCoordinate(), zoom);

            drawArrow(g, s[0], s[1], p.x, p.y, halfStroke);
        }

    }

    private void drawNodes(Graphics2D g) {
        for (Node n : state.getNetwork().getNodes()) {
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

            g.fill(new Ellipse2D.Double(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
        }
    }

    private void drawVehicles(Graphics2D g) {
        Simulation sim = state.getSimulation();
        double minute = state.getCurrentMinute();

        for (Vehicle v : sim.getVehicles()) {
            Coordinate c = sim.computePosition(v, minute);
            if (c == null)
                continue;

            Point p = CoordinateConverter.CoordinateToPoint(
                    sim.computePosition(v, minute),
                    maxWidth,
                    maxHeight,
                    state.getCenterCoordinate(),
                    zoom);

            g.setColor(v.getRoute().getColor());
            g.fill(new Ellipse2D.Double(p.x - halfStroke, p.y - halfStroke, baseStroke, baseStroke));
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
