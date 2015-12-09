package domain.pathfinding;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import domain.network.BusRoute;
import domain.network.Network;
import domain.network.Node;
import domain.network.Segment;

import java.util.ArrayList;

public class Pathfinder {
    private Network network;
    private Node source;
    private Node dest;

    private ArrayList<GraphNode> nodes;

    public Pathfinder(Network network, Node source, Node dest) {
        this.network = network;
        this.source = source;
        this.dest = dest;
        nodes = new ArrayList<>();

        createNode(source);
    }

    private void createNode(Node node) {
        if (network.getBusRoutesWithStation(node).size() == 0)
            throw new IllegalArgumentException("Must be a valid station");

        for (BusRoute r : network.getBusRoutesWithStation(node)) {
            GraphNode gNode = createNode(node, r);
        }
    }

    private GraphNode createNode(Node node, BusRoute route) {
        GraphNode gNode = new GraphNode(node, route);
        findDest(gNode);
        nodes.add(gNode);
        return gNode;
    }

    private void findDest(GraphNode node) {
        BusRoute route = node.getRoute();
        ArrayList<Node> stations = route.getStations();
        ArrayList<Segment> segments = route.getSegmentsBetweenNodes(node.getNode(), stations.get(stations.size()-1));
        double cost = 0;
        GraphNode last = null;

        for (Segment s : segments) {
            cost += s.getDistribution().getAverageValue();

            if (stations.contains(s.getDestination())) {
                ArrayList<BusRoute> routes = network.getBusRoutesWithStation(s.getDestination());
                for (BusRoute r : routes) {
                    GraphNode dest = createNode(s.getDestination(), r);
                    node.getNexts().add(new Path(node, dest, cost));
                }
            }
        }
    }
}
