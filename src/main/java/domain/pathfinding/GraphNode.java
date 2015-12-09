package domain.pathfinding;

import domain.network.BusRoute;
import domain.network.Node;

import java.util.LinkedList;

public class GraphNode {
    Node node;
    BusRoute route;
    LinkedList<Path> nexts;

    public GraphNode(Node node, BusRoute route) {
        this.node = node;
        this.route = route;
        nexts = new LinkedList<>();
    }

    public Node getNode() {
        return node;
    }

    public BusRoute getRoute() {
        return route;
    }

    public LinkedList<Path> getNexts() {
        return nexts;
    }

    @Override
    public String toString() {
        return "Node : " + node.getName() + ", " + route.getName();
    }
}
