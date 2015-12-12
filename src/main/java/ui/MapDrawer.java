package ui;

import domain.*;
import domain.network.BusRoute;
import domain.network.Node;
import domain.network.PassengerRoute;
import domain.network.Segment;
import domain.simulation.Simulation;
import domain.simulation.Vehicle;
import util.CoordinateConverter;

import javax.imageio.ImageIO;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private Color sourceColor;
    private Color stationColor;

    private Font defaultFont;

    public MapDrawer(ApplicationState state) {
        this.state = state;

        defaultColor = Color.black;
        arrowColor = Color.gray;
        hoverColor = Color.red;
        selectedColor = new Color(255, 254, 21);
        sourceColor = new Color(254, 255, 164);
        stationColor = new Color(255, 255, 50);
        defaultFont = new Font("Comic Sans MS", Font.BOLD, 12);
    }

    public void draw(Graphics2D g, int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        zoom = state.getZoomRatio();
        baseStroke = (int) Math.round(10 * zoom);
        baseStroke = Math.max(2, baseStroke);
        halfStroke = baseStroke / 2;

        drawBackgroundImage(g);

        if (state.getCurrentMode() == EditionMode.AddNode)
            drawCurrentNode(g);
        drawSegments(g);
        drawNodes(g);

        if (state.getCurrentMode() == EditionMode.Simulation)
            drawVehicles(g);
    }

    private void drawBackgroundImage(Graphics2D g) {
        BufferedImage image = state.getBackgroundImage();
        if (image != null) {
            int imageHeight = (int)((float)maxWidth / image.getWidth() * image.getHeight());
            g.drawImage(image, 0, 0, maxWidth, imageHeight, null);
        }
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
        ArrayList<Integer> end = new ArrayList<>();
        ArrayList<Tuple<Integer, Color>> colored = new ArrayList<>();

        Segment[] segments = state.getNetwork().getSegments().values().toArray(new Segment[0]);
        ArrayList<int[]> segmentPoints = new ArrayList<>();

        g.setStroke(new BasicStroke(halfStroke));
        int j = 0;
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

            if ((state.isSegmentOnCurrentRoute(s) && state.getCurrentMode() != EditionMode.AddPassengerRoute)
                    || s == state.getSelectedElement()
                    || state.isSegmentOnCurrentPassengerRoute(s))
                end.add(j);

            PassengerRoute pRoute = state.getCurrentPassengerRoute();
            if (pRoute != null && pRoute.isSegmentOnRoute(s)) {
                BusRoute busRoute = pRoute.getBusRouteForSegment(s);
                colored.add(new Tuple<>(j, busRoute.getColor()));
            }

            segmentPoints.add(new int[]{source.x, source.y, destination.x, destination.y});
            j++;
        }

        // We must draw arrow after segments
        for (int i = 0; i < segmentPoints.size(); i++) {
            if (segments[i].isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);
            else
                g.setColor(defaultColor);

            int[] s = segmentPoints.get(i);
            g.drawLine(s[0], s[1], s[2], s[3]);
        }

        for (int [] s : segmentPoints) {
            drawArrow(g, s[0], s[1], s[2], s[3], baseStroke, arrowColor);
        }

        g.setColor(selectedColor);
        for (Integer i : end) {
            int[] s = segmentPoints.get(i);
            g.drawLine(s[0], s[1], s[2], s[3]);
            drawArrow(g, s[0], s[1], s[2], s[3], baseStroke, selectedColor);
        }

        for (Tuple<Integer, Color> t : colored) {
            g.setColor(t.second);
            int[] s = segmentPoints.get(t.first);
            g.drawLine(s[0], s[1], s[2], s[3]);
            drawArrow(g, s[0], s[1], s[2], s[3], baseStroke, t.second);
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

            if (n == state.getSelectedElement())
                g.setColor(selectedColor);
            else if (state.isNodeStationOnCurrentRoute(n))
                g.setColor(stationColor);
            else if (state.isNodeSourceOnCurrentRoute(n))
                g.setColor(sourceColor);
            else if (n.isOnCoordinate(state.getCurrentPosition()))
                g.setColor(hoverColor);

            int nodeStroke = (int)(baseStroke * 1.5);
            int halfNodeStroke = nodeStroke / 2;
            g.fill(new Ellipse2D.Double(p.x - halfNodeStroke, p.y - halfNodeStroke, nodeStroke, nodeStroke));

            Simulation sim = state.getSimulation();
            if (sim != null) {
                g.setFont(defaultFont);
                g.setColor(Color.white);
                Integer passengerCount = sim.computePassengerCountAtStation(n, state.getCurrentMinute());
                if (passengerCount >= 0) {
                    g.drawString(passengerCount.toString(), p.x, p.y);
                }
            }
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

            g.drawImage(getImage(v.getRoute().getColor()), p.x - baseStroke, p.y - baseStroke, null);

            Integer passengerCount = v.getPassengersMap().getPassengerCountAt(minute);
            g.setColor(Color.black);
            g.setFont(defaultFont);
            g.drawString(passengerCount.toString(), p.x, p.y);
        }
    }

    private BufferedImage getImage(Color routeColor){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("src/main/resources/bus-30x37-white.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        int width = image.getWidth();
        int height = image.getHeight();
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                Color originalColor = new Color(image.getRGB(xx, yy), true);

                if (originalColor.equals(Color.BLACK) && originalColor.getAlpha() == 255) {
                    image.setRGB(xx, yy, routeColor.getRGB());
                }
            }
        }
        return image;
    }

    // Inspired by http://stackoverflow.com/a/4112875/3757513
    private void drawArrow(Graphics2D g1, int x1, int y1, int x2, int y2, int width, Color color) {
        Graphics2D g = (Graphics2D) g1.create();
        g.setColor(color);

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

    private class Tuple<T1, T2> {
        public T1 first;
        public T2 second;

        public Tuple(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }
    }
}
