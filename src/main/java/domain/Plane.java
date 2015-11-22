package domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Plane {
    private ArrayList<Node> nodes;
    private HashMap<NodeVector, Segment> segments;

    public Plane() {
        nodes = new ArrayList<>();
        segments = new HashMap<>();
    }

    public boolean addNode(Coordinate coords) {
        if (getNodeOnCoords(coords) != null)
            return false;

        nodes.add(new Node(coords));
        return true;
    }

    public boolean addSegment(Node source, Node destination) {
        NodeVector vector = new NodeVector(source, destination);

        if (segments.containsKey(vector))
            return false;

        segments.put(vector, new Segment(vector));
        return true;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public HashMap<NodeVector, Segment> getSegments() {
        return segments;
    }

    public Node getNodeOnCoords(Coordinate coords) {
        for (Node n : nodes) {
            if (n.isOnCoordinate(coords)) {
                return n;
            }
        }
        return null;
    }
}
