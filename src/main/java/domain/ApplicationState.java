package domain;

public class ApplicationState {
    private Coordinate currentPosition;
    private EditionMode currentMode;
    private Plane plane;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        plane = new Plane();
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    public EditionMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(EditionMode currentMode) {
        this.currentMode = currentMode;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }
}