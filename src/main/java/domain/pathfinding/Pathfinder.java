package domain.pathfinding;

import domain.network.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

public class Pathfinder {
    private Network network;
    private Node source;
    private Node dest;

    private ArrayList<GraphNode> nodes;
    private ArrayList<GraphNode> begin;
    private ArrayList<GraphNode> end;
    private HashSet<Node> marked;

    public Pathfinder(Network network, Node source, Node dest) {
        this.network = network;
        this.source = source;
        this.dest = dest;
        nodes = new ArrayList<>();
        begin = new ArrayList<>();
        end = new ArrayList<>();
        marked = new HashSet<>();

        createNode(source);
    }

    public PassengerRoute find() {
        double bestCost = Double.MAX_VALUE;
        ArrayList<GraphNode> path = new ArrayList<>();
        ArrayList<GraphNode> bestPath = null;


        for (GraphNode b : begin) {
            for (GraphNode e : end) {
                path = new ArrayList<>();
                double cost = dijkstra(b, e, path);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestPath = path;
                }
            }
        }

        if (bestPath == null)
            return null;

        PassengerRoute route = new PassengerRoute();
        for (int i = 0; i < bestPath.size() - 1; i++) {
            GraphNode node = bestPath.get(i);
            GraphNode next = bestPath.get(i + 1);
            route.addFragment(new PassengerRouteFragment(node.getNode(), next.getNode(), node.getRoute()));
        }

        return route;
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


        if (gNode.getNode().equals(source))
            begin.add(gNode);
        if (gNode.getNode().equals(dest))
            end.add(gNode);

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
                if (marked.contains(s.getDestination()))
                    continue;
                ArrayList<BusRoute> routes = network.getBusRoutesWithStation(s.getDestination());

                marked.add(s.getDestination());
                for (BusRoute r : routes) {
                    GraphNode dest = createNode(s.getDestination(), r);
                    node.getNexts().add(new Path(node, dest, cost));
                }
            }
        }
    }

    private double dijkstra(GraphNode source, GraphNode dest, ArrayList<GraphNode> bestPath) {
        int sourceIdx = nodes.indexOf(source);
        int destIdx = nodes.indexOf(dest);

        int n = nodes.size();
        double[] d = new double[n];
        int[] p = new int[n];
        boolean[] solved = new boolean[n];
        PriorityQueue<Pair> queue = new PriorityQueue<>();

        for (int i = 0; i < n; i++) {
            d[i] = Integer.MAX_VALUE;
            p[i] = Integer.MAX_VALUE;
            solved[i] = false;
        }

        d[sourceIdx] = 0;
        queue.add(new Pair(sourceIdx, 0));

        while (!queue.isEmpty()) {
            int uStar = queue.poll().i;
            solved[uStar] = true;

            if (d[uStar] == Integer.MAX_VALUE)
                break;
            if (uStar == destIdx)
                break;

            for(Path path : nodes.get(uStar).getNexts()) {
                int u = nodes.indexOf(path.getDest());
                double temp = d[uStar] + path.cost;
                if (temp < d[u]) {
                    d[u] = temp;
                    p[u] = uStar;
                    queue.add(new Pair(u, temp));
                }
            }
        }

        if (p[destIdx] == Integer.MAX_VALUE)
            return p[destIdx];

        bestPath.clear();
        Stack<Integer> stack = new Stack<>();
        int num = destIdx;
        while (p[num] != Integer.MAX_VALUE) {
            stack.push(num);
            num = p[num];
        }
        stack.push(sourceIdx);
        while (!stack.isEmpty()) {
            bestPath.add(nodes.get(stack.pop()));
        }

        return d[destIdx];
    }

    private class Pair implements Comparable<Pair> {
        public int i;
        public double cost;
        public Pair(int i, double cost) {
            this.i = i;
            this.cost = cost;
        }

        @Override
        public int compareTo(Pair o) {
            if (cost > o.cost)
                return -1;
            if (cost < o.cost)
                return 1;
            return 0;
        }
    }
}
