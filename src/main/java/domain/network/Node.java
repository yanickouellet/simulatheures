package domain.network;

import domain.Coordinate;

public class Node extends NetworkElement {
    private Coordinate coordinate;
    private String name;

    public Node(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.name = "";
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isOnCoordinate(Coordinate coords) {
        float maxDelta = 5;
        float deltaX = Math.abs(coords.getX() - coordinate.getX());
        float deltaY = Math.abs(coords.getY() - coordinate.getY());

        return deltaX  < maxDelta && deltaY < maxDelta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return !(coordinate != null ? !coordinate.equals(node.coordinate) : node.coordinate != null);
    }

    @Override
    public int hashCode() {
        return coordinate != null ? coordinate.hashCode() : 0;
    }
}
