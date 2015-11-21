package domain;

public class Segment extends NetworkElement {
    private Node source;
    private Node destination;

    public Segment(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    @Override
    public boolean isOnCoordinate(Coordinate coords) {
        return false;
    }
}
