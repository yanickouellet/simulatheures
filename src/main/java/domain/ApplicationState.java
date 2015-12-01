package domain;

import domain.network.*;
import domain.simulation.Simulation;

public class ApplicationState {
    private Coordinate currentPosition;
    private Coordinate centerCoordinate;
    private EditionMode currentMode;
    private Network network;
    private NetworkElement selectedElement;
    private BusRoute currentBusRoute;
    private int zoomLevel;
    private String message;
    private Simulation simulation;
    private double currentMinute;

    public ApplicationState() {
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        network = new Network();
        zoomLevel = 175;
        centerCoordinate = new Coordinate();
        message = "";
        currentMinute = 0;
    }

    public void startSimulation(Simulation simulation) {
        this.simulation = simulation;
        currentMinute = 0;
        currentMode = EditionMode.Simulation;
        currentBusRoute = null;
        selectedElement = null;
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

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Coordinate getCenterCoordinate() {
        return centerCoordinate;
    }

    public void setCenterCoordinate(Coordinate centerCoordinate) {
        this.centerCoordinate = centerCoordinate;
    }

    public double getZoomRatio() {
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

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public double getCurrentMinute() {
        return currentMinute;
    }

    public void setCurrentMinute(double currentMinute) {
        this.currentMinute = currentMinute;
    }
}