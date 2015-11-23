package domain;

import domain.network.Node;

public class NodeVector {
    private Node source;
    private Node destination;

    public NodeVector(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public float getComponentX() {
        return destination.getCoordinate().getX() - source.getCoordinate().getX();
    }

    public float getComponentY() {
        return destination.getCoordinate().getY() - source.getCoordinate().getY();
    }

    public float computeAngle() {
        return (float) Math.atan(getComponentY() / getComponentX());
    }

    public float computeNorm() {
        float x = getComponentX();
        float y = getComponentY();

        return (float) Math.sqrt(x * x + y * y);
    }

    public Coordinate computeNewCoordinate(float ratio) {
        float x = getSource().getCoordinate().getX() + getComponentX() * ratio;
        float y = getSource().getCoordinate().getY() + getComponentY() * ratio;
        return new Coordinate(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeVector that = (NodeVector) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return !(destination != null ? !destination.equals(that.destination) : that.destination != null);

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}
