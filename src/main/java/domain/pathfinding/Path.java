package domain.pathfinding;

public class Path {
    GraphNode source;
    GraphNode dest;
    double cost;

    public Path(GraphNode source, GraphNode dest, double cost) {
        this.source = source;
        this.dest = dest;
        this.cost = cost;
    }

    public GraphNode getSource() {
        return source;
    }

    public GraphNode getDest() {
        return dest;
    }

    public double getCost() {
        return cost;
    }
}
