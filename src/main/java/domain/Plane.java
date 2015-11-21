package domain;

import java.util.ArrayList;

public class Plane {
    private ArrayList<Node> nodes;
    private ArrayList<Segment> segments;

    public Plane() {
        nodes = new ArrayList<>();
        segments = new ArrayList<>();
    }

    public void addNode(Coordinate coords) {
        nodes.add(new Node(coords));
    }

    public void addSegment(Node source, Node destination) {
        segments.add(new Segment(source, destination));
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public Node getHoveredNode(Coordinate coords) {
        for (Node n : nodes) {
            if (n.isOnCoordinate(coords)) {
                return n;
            }
        }
        return null;
    }
}
