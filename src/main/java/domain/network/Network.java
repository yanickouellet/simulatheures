package domain.network;

import domain.Coordinate;
import domain.NodeVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Network {
    private ArrayList<Node> nodes;
    private ArrayList<BusRoute> routes;
    private HashMap<NodeVector, Segment> segments;

    public Network() {
        nodes = new ArrayList<>();
        segments = new HashMap<>();
        routes = new ArrayList<>();
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

    public boolean addRoute(BusRoute route) {
        routes.add(route);
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

    public ArrayList<Segment> getSegmentOnCoords(Coordinate coords) {
        ArrayList<Segment> list = new ArrayList<>();

        for (Segment s : segments.values()) {
            if (s.isOnCoordinate(coords))
                list.add(s);
        }

        return list;
    }

    public NetworkElement getElementOnCoords(Coordinate coords) {
        NetworkElement elem = getNodeOnCoords(coords);

        if (elem != null)
            return elem;

        ArrayList<Segment> list = getSegmentOnCoords(coords);
        if (list.size() > 0)
            return list.get(0);

        return null;
    }

    public void deleteElement(NetworkElement elem) {
        if (elem instanceof Segment) {
            Segment segment = (Segment) elem;
            deleteRoutesForSegments(segment);
            segments.remove(segment.getVector());
        } else if (elem instanceof Node) {
            Node node = (Node) elem;
            deleteSegmentsForNode(node);
            nodes.remove(node);
        }
    }

    private void deleteSegmentsForNode(Node node) {
        Iterator i = segments.entrySet().iterator();

        while (i.hasNext()) {
            Segment s = (Segment) ((Map.Entry)i.next()).getValue();
            if (s.getSource() == node || s.getDestination() == node) {
                deleteRoutesForSegments(s);
                i.remove();
            }
        }
    }

    private void deleteRoutesForSegments(Segment segment) {
        Iterator i = routes.iterator();

        while (i.hasNext()) {
            BusRoute r = (BusRoute)i.next();
            if (r.getSegments().contains(segment))
                i.remove();
        }
    }

    public ArrayList<BusRoute> getRoutes() {
        return routes;
    }
}
