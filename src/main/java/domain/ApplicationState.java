package domain;

public class ApplicationState {
    private Coordinate currentPosition;
    private EditionMode currentMode;
    private Plane plane;
    private int zoomLevel;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        plane = new Plane();
        zoomLevel = 100;
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

    public float getZoomRatio() {
        return zoomLevel / 100f;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }
}