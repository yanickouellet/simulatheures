package domain;

import domain.network.*;

public class ApplicationState {
    private Coordinate currentPosition;
    private Coordinate centerCoordinate;
    private EditionMode currentMode;
    private Plane plane;
    private NetworkElement selectedElement;
    private BusRoute currentBusRoute;
    private int zoomLevel;
    private String message;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        plane = new Plane();
        zoomLevel = 175;
        centerCoordinate = new Coordinate();
        message = "";
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

    public NetworkElement getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(NetworkElement selectedElement) {
        this.selectedElement = selectedElement;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusRoute getCurrentBusRoute() {
        return currentBusRoute;
    }

    public void setCurrentBusRoute(BusRoute currentBusRoute) {
        this.currentBusRoute = currentBusRoute;
    }

    public boolean isNodeStationOnCurrentRoute(Node node) {
        return currentBusRoute != null && currentBusRoute.isNodeStation(node);
    }

    public boolean isSegmentOnCurrentRoute(Segment segment) {
        return currentBusRoute != null && currentBusRoute.isSegmentOnRoute(segment);
    }
}