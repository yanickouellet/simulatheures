package domain.network;

import domain.Coordinate;
import domain.NodeVector;
import domain.TriangularDistribution;

public class Segment extends NetworkElement {
    private NodeVector vector;
    private TriangularDistribution distribution;

    public Segment(NodeVector vector) {
        this.vector = vector;
        //TODO Change that to use user input
        distribution = new TriangularDistribution(5, 10, 15);
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

        float arrond = 10;

        float dx = source.getX() - destination.getX();
        float dy = source.getY() - destination.getY();

        float a = dy / dx;
        float b = destination.getY() - destination.getX() * a;

        float yMin = (coords.getX() - arrond) * a + b;
        float yMax = (coords.getX() + arrond) * a + b;

        float y = coords.getY();

        return y >= yMin && y <= yMax;
    }

    public TriangularDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(TriangularDistribution distribution) {
        this.distribution = distribution;
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
