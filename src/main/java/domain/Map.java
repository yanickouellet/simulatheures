package domain;

public class Map {
    private Coordinate currentPosition;

    public Map() {
        currentPosition = new Coordinate(0, 0);
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }
}
