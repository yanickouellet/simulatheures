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
        Coordinate source = vector.getSource().getCoordinate();
        Coordinate destination = vector.getDestination().getCoordinate();

        float maxDelta = 5;

        float dx = source.getX() - destination.getX();
        float dy = source.getY() - destination.getY();

        float a = dy / dx;
        float b = destination.getY() - destination.getX() * a;

        float y = coords.getX() * a + b;
        float delta = Math.abs(y - coords.getY());

        return delta < 1;
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
