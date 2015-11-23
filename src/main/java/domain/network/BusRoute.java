package domain.network;

import domain.IDistributableElement;
import domain.TriangularDistribution;

import java.awt.*;
import java.util.ArrayList;

public class BusRoute implements IDistributableElement {
    private String name;
    private boolean isLoop;
    private int startAt;
    private Source source;
    private ArrayList<Segment> segments;
    private ArrayList<Node> stations;
    private TriangularDistribution distribution;
    private Color color;

    private static int i = 1;

    public BusRoute(Node source, Color color) {
        startAt = 0;
        name = "Circuit " + Integer.toString(i++);
        isLoop = false;

        this.source = new Source(source);
        this.color = color;
        segments = new ArrayList<>();
        stations = new ArrayList<>();
        stations.add(source);

        distribution = new TriangularDistribution(5, 10, 15);
    }

    public void addSegment(Segment segment) {
        if (segment == null || !isConsecutive(segment))
            throw new IllegalArgumentException();

        segments.add(segment);
    }

    public void toggleStation(Node node) {
        if (!isNodeOnRoute(node))
            throw new IllegalArgumentException();

        if (stations.contains(node)) {
            if (node != source.getNode())
                stations.remove(node);
        } else {
            stations.add(node);
        }
    }

    public boolean isConsecutive(Segment segment) {
        if (segment == null)
            return false;

        if (segments.size() == 0)
            return segment.getSource() == source.getNode();

        return segments.get(segments.size() - 1).getDestination() == segment.getSource();
    }

    public boolean isNodeStation(Node node) {
        return stations.contains(node);
    }

    public boolean isNodeOnRoute(Node node) {
        if (node == source.getNode())
            return true;

        for (Segment s : segments) {
            if (s.getSource() == node || s.getDestination() == node) {
                return true;
            }
        }

        return false;
    }

    public boolean isSegmentOnRoute(Segment segment) {
        return segments.contains(segment);
    }

    public Source getSource() {
        return source;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public ArrayList<Node> getStations() {
        return stations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsLoop() {
        return isLoop;
    }

    public void setIsLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public int getStartAt() {
        return startAt;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public double generate() {
        return source.getDistribution().generate();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusRoute busRoute = (BusRoute) o;

        if (startAt != busRoute.startAt) return false;
        if (name != null ? !name.equals(busRoute.name) : busRoute.name != null) return false;
        if (source != null ? !source.equals(busRoute.source) : busRoute.source != null) return false;
        if (segments != null ? !segments.equals(busRoute.segments) : busRoute.segments != null) return false;
        if (stations != null ? !stations.equals(busRoute.stations) : busRoute.stations != null) return false;
        return !(distribution != null ? !distribution.equals(busRoute.distribution) : busRoute.distribution != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + startAt;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (segments != null ? segments.hashCode() : 0);
        result = 31 * result + (stations != null ? stations.hashCode() : 0);
        result = 31 * result + (distribution != null ? distribution.hashCode() : 0);
        return result;
    }
}
