package domain;

public class Segment extends NetworkElement {
    private NodeVector vector;

    public Segment(NodeVector vector) {
        this.vector = vector;
    }

    public Node getSource() {
        return vector.getSource();
    }

    public Node getDestination() {
        return vector.getDestination();
    }

    public NodeVector getVector() {
        return vector;
    }

    @Override
    public boolean isOnCoordinate(Coordinate coords) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        return !(vector != null ? !vector.equals(segment.vector) : segment.vector != null);

    }

    @Override
    public int hashCode() {
        return vector != null ? vector.hashCode() : 0;
    }
}
