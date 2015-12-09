package domain.network;

import domain.IDistributableElement;
import domain.TriangularDistribution;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class BusRoute implements IDistributableElement, Serializable{
    private Node routeSource;
    private String name;
    private boolean isLoop;
    private Source busSource;
    private ArrayList<Segment> segments;
    private ArrayList<Node> stations;
    private TriangularDistribution distribution;
    private Color color;

    private static int i = 1;

    public BusRoute(Node routeSource, Color color) {
        name = "Circuit " + Integer.toString(i++);
        isLoop = false;

        this.routeSource = routeSource;
        this.color = color;
        segments = new ArrayList<>();
        stations = new ArrayList<>();

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
            if (node != busSource.getNode())
                stations.remove(node);
        } else {
            stations.add(node);
        }
        sortStations();
    }

    public boolean isConsecutive(Segment segment) {
        if (segment == null || segments.contains(segment))
            return false;

        if (segments.size() == 0)
            return segment.getSource() == routeSource;

        Segment s = segments.get(segments.size()-1);
        return s.getDestination() == segment.getSource() && s.getSource() != segment.getDestination();
    }

    public boolean isNodeStation(Node node) {
        return stations.contains(node);
    }

    public boolean isNodeOnRoute(Node node) {
        if (node == routeSource)
            return true;

        for (Segment s : segments) {
            if (s.getSource() == node || s.getDestination() == node) {
                return true;
            }
        }

        return false;
    }

    public boolean isNodeSource(Node node) {
        return routeSource == node;
    }

    public boolean isLoopable() {
        return segments.size() >= 2 && segments.get(0).getSource() == segments.get(segments.size()-1).getDestination();
    }

    public boolean isSegmentOnRoute(Segment segment) {
        return segments.contains(segment);
    }

    public Source getBusSource() {
        return busSource;
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

    public Color getColor() {
        return color;
    }

    public void setBusSource(Source busSource) {
        if (this.busSource != null)
            throw new IllegalArgumentException();
        this.busSource = busSource;
        this.stations.add(busSource.getNode());
    }

    public Node getRouteSource() {
        return routeSource;
    }

    public int getStationPosition(Node node) {
        return stations.indexOf(node);
    }

    public boolean isLastStation(Node node) {
        if (isLoop)
            return false;
        return stations.indexOf(node) == stations.size() - 1;
    }

    public ArrayList<Segment> getSegmentsBetweenNodes(Node first, Node last) {
        ArrayList<Segment> list = new ArrayList<>();
        boolean begin = false;

        for (Segment s : segments) {
            if (s.getSource().equals(first))
                begin = true;
            if (begin)
                list.add(s);
            if (s.getDestination().equals(last))
                break;
        }

        return list;
    }

    @Override
    public double generate() {
        return busSource.getDistribution().generate();
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

        if (name != null ? !name.equals(busRoute.name) : busRoute.name != null) return false;
        if (busSource != null ? !busSource.equals(busRoute.busSource) : busRoute.busSource != null) return false;
        if (segments != null ? !segments.equals(busRoute.segments) : busRoute.segments != null) return false;
        if (stations != null ? !stations.equals(busRoute.stations) : busRoute.stations != null) return false;
        return !(distribution != null ? !distribution.equals(busRoute.distribution) : busRoute.distribution != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (busSource != null ? busSource.hashCode() : 0);
        result = 31 * result + (segments != null ? segments.hashCode() : 0);
        result = 31 * result + (stations != null ? stations.hashCode() : 0);
        result = 31 * result + (distribution != null ? distribution.hashCode() : 0);
        return result;
    }

    private void sortStations() {
        if (segments.size() <= 1) return;

        int i = 0;
        int j = stations.indexOf(segments.get(0).getSource());
        if (j != -1)
            swapStations(i++, j);

        for(Segment s : segments) {
            j = stations.indexOf(s.getDestination());
            if (j != -1 && j < stations.size() - 1)
                swapStations(i++, j);
        }
    }

    private void swapStations(int i, int j) {
        Node tmp = stations.get(i);
        stations.set(i, stations.get(j));
        stations.set(j, tmp);
    }
}
