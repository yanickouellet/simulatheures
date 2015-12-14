package domain;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import domain.network.*;
import domain.simulation.Simulation;
import util.Strings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class ApplicationState implements Serializable {
    private Coordinate currentPosition;
    private Coordinate centerCoordinate;
    private EditionMode currentMode;
    private ControllerMode controllerMode;
    private Network network;
    private NetworkElement selectedElement;
    private BusRoute currentBusRoute;
    private ArrayList<BusRoute> availableBusRoutes;
    private String appTitle = Strings.DefaultAppTitle;

    private transient BufferedImage backgroundImage;
    private transient Stack<ByteInputStream> prevStack;
    private transient Stack<ByteInputStream> nextStack;

    private PassengerRoute currentPassengerRoute;
    private int zoomLevel;
    private String message;
    private Simulation simulation;
    private LinkedList<Simulation> simulations;
    private double currentMinute;
    private int remainingSimulations;

    private OpenedPanel openedPanel;

    public ApplicationState() {
        remainingSimulations = 0;
        currentPosition = new Coordinate();
        currentMode  = EditionMode.None;
        openedPanel = OpenedPanel.None;
        network = new Network();
        zoomLevel = 150;
        centerCoordinate = new Coordinate();
        message = "";
        currentMinute = 0;
        simulations = new LinkedList<>();
        prevStack = new Stack<>();
        nextStack = new Stack<>();
        controllerMode = ControllerMode.Normal;
    }

    public void startSimulation(Simulation simulation) {
        this.simulation = simulation;
        currentMinute = 0;
        currentMode = EditionMode.Simulation;
        currentBusRoute = null;
        selectedElement = null;
    }

    public void setAppTitle(String title) { appTitle = title + Strings.AppTitle; }

    public String getAppTitle() { return appTitle; }

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

    public boolean isNodeSourceOnCurrentRoute(Node node) {
        return currentBusRoute != null && currentBusRoute.isNodeSource(node);
    }

    public boolean isSegmentOnCurrentRoute(Segment segment) {
        return currentBusRoute != null && currentBusRoute.isSegmentOnRoute(segment);
    }

    public boolean isSegmentOnCurrentPassengerRoute(Segment segment) {
        return currentPassengerRoute != null && currentPassengerRoute.isSegmentOnRoute(segment);
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

    public PassengerRoute getCurrentPassengerRoute() {
        return currentPassengerRoute;
    }

    public void setCurrentPassengerRoute(PassengerRoute currentPassengerRoute) {
        this.currentPassengerRoute = currentPassengerRoute;
    }

    public ArrayList<BusRoute> getAvailableBusRoutes() {
        return availableBusRoutes;
    }

    public void setAvailableBusRoutes(ArrayList<BusRoute> availableBusRoutes) {
        this.availableBusRoutes = availableBusRoutes;
    }

    public ArrayList<BusRoute> getBusRoutesToShowInTree() {
        return availableBusRoutes != null ? availableBusRoutes : network.getBusRoutes();
    }

    public OpenedPanel getOpenedPanel() {
        return openedPanel;
    }

    public void setOpenedPanel(OpenedPanel openedPanel) {
        this.openedPanel = openedPanel;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public LinkedList<Simulation> getSimulations() {
        return simulations;
    }

    public int getRemainingSimulations() {
        return remainingSimulations;
    }

    public void setRemainingSimulations(int remainingSimulations) {
        this.remainingSimulations = remainingSimulations;
    }

    public Stack<ByteInputStream> getPrevStack() {
        return prevStack;
    }

    public Stack<ByteInputStream> getNextStack() {
        return nextStack;
    }

    public void setPrevStack(Stack<ByteInputStream> prevStack) {
        this.prevStack = prevStack;
    }

    public void setNextStack(Stack<ByteInputStream> nextStack) {
        this.nextStack = nextStack;
    }

    public ControllerMode getControllerMode() {
        return controllerMode;
    }

    public void setControllerMode(ControllerMode controllerMode) {
        this.controllerMode = controllerMode;
    }

    public void setSimulations(LinkedList<Simulation> simulations) {
        this.simulations = simulations;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeBoolean(backgroundImage != null);

        if (backgroundImage != null)
            ImageIO.write(backgroundImage, "png", stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (stream.readBoolean()) {
            backgroundImage = ImageIO.read(stream);
        }
        nextStack = new Stack<>();
        prevStack = new Stack<>();
    }
}