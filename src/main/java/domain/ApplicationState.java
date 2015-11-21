package domain;

public class ApplicationState {
    private Coordinate currentPosition;
    private Coordinate centerCoordinate;
    private EditionMode currentMode;
    private Plane plane;
    private Node selectedNode;
    private int zoomLevel;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        plane = new Plane();
        zoomLevel = 175;
        centerCoordinate = new Coordinate();
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

    public Coordinate getCenterCoordinate() {
        return centerCoordinate;
    }

    public void setCenterCoordinate(Coordinate centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
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

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }
}