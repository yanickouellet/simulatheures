package domain.network;

import domain.IDistributableElement;
import domain.TriangularDistribution;

import java.util.ArrayList;

public class BusRoute implements IDistributableElement {
    private String name;
    private Node source;
    private ArrayList<Segment> segments;
    private ArrayList<Node> stations;
    private TriangularDistribution distribution;

    //TODO Remove me!
    private static int i = 0;

    public BusRoute(Node source) {
        name = Integer.toString(i++);
        this.source = source;
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
            if (node != source)
                stations.remove(node);
        } else {
            stations.add(node);
        }
    }

    public boolean isConsecutive(Segment segment) {
        if (segment == null)
            return false;

        if (segments.size() == 0)
            return segment.getSource() == source;

        return segments.get(segments.size() - 1).getDestination() == segment.getSource();
    }

    public boolean isNodeStation(Node node) {
        return stations.contains(node);
    }

    public boolean isNodeOnRoute(Node node) {
        if (node == source)
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

    public Node getSource() {
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

    @Override
    public float generate() {
        return distribution.generate();
    }
}
