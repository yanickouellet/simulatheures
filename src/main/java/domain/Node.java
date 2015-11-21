package domain;

public class Node extends NetworkElement {
    private Coordinate coordinate;

    public Node(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public boolean isOnCoordinate(Coordinate coords) {
        float maxDelta = 5;
        float deltaX = Math.abs(coords.getX() - coordinate.getX());
        float deltaY = Math.abs(coords.getY() - coordinate.getY());

        return deltaX  < maxDelta && deltaY < maxDelta;
    }
}
