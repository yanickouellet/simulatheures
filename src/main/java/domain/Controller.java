package domain;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import domain.network.*;
import domain.pathfinding.Pathfinder;
import domain.simulation.Simulation;
import ui.MainForm;
import util.CoordinateConverter;
import util.Strings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Stack;

public class Controller {
    private MainForm mainForm;
    private ApplicationState state;

    private static final Color[] DefaultColors = new Color[] {
            Color.blue,
            Color.cyan,
            Color.MAGENTA,
            Color.green,
            Color.pink,
            Color.yellow
    };

    public Controller(MainForm mainForm) {
        this.mainForm = mainForm;
        state = new ApplicationState();
    }

    public void click(Point p, int maxWidth, int maxHeight, boolean leftClick) {
        saveState();

        state.setMessage("");

        Coordinate coord = CoordinateConverter.PointToCoordinate(
                p,
                maxWidth,
                maxHeight,
                state.getCenterCoordinate(),
                state.getZoomRatio()
        );

        switch (state.getCurrentMode()) {
            case None:
                selectionModeClick(coord, leftClick);
                break;
            case AddNode:
                addNodeClick(coord);
                break;
            case AddSegment:
                addSegmentClick(coord);
                break;
            case AddBusRoute:
                addBusRouteClick(coord);
                break;
            case AddPassengerRoute:
                addPassengerRouteClick(coord);
                break;
            case Dijkstra:
                dijkstraClick(coord);
                break;
        }

        mainForm.update();
    }

    public void mouseOver(Point p, int maxWidth, int maxHeight) {
        state.setCurrentPosition(CoordinateConverter.PointToCoordinate(
                p,
                maxWidth,
                maxHeight,
                state.getCenterCoordinate(),
                state.getZoomRatio())
        );
        mainForm.update();
    }

    public void dragMap(int dx, int dy, int maxWidth, int maxHeight) {
        saveState();

        Point currentCenter = CoordinateConverter.CoordinateToPoint(
                state.getCenterCoordinate(),
                maxWidth,
                maxHeight,
                new Coordinate(),
                state.getZoomRatio()
        );
        currentCenter.translate(dx, -dy);

        state.setCenterCoordinate(CoordinateConverter.PointToCoordinate(
                currentCenter,
                maxWidth,
                maxHeight,
                new Coordinate(),
                state.getZoomRatio()
        ));

        mainForm.update();
    }

    public void adjustZoom(boolean increase) {
        saveState();

        int level = state.getZoomLevel();
        if (increase) {
            level += level >= 5 ? 5 : 1;
        } else if (level >= 10) {
            level -= 5;
        } else if (level >= 2) {
            level -= 1;
        }

        state.setZoomLevel(level);
        mainForm.update();
    }

    public void setOpenedPanel(OpenedPanel panel) {
        state.setOpenedPanel(panel);
    }

    public OpenedPanel getOpenedPanel() { return state.getOpenedPanel(); }

    public void setEditionMode(EditionMode mode) {
        saveState();

        state.setMessage("");
        state.setCurrentMode(mode);

        state.setSelectedElement(null);

        if (mode != EditionMode.Simulation) {
            state.setSimulation(null);
            mainForm.pauseSimulation();
        }

        if (mode == EditionMode.AddBusRoute) {
            startBusRouteCreation();
        } else if (mode == EditionMode.AddPassengerRoute) {
            startPassengerRouteCreation();
        } else {
            state.setControllerMode(ControllerMode.Normal);
            state.setCurrentBusRoute(null);
            state.setAvailableBusRoutes(null);
            state.setCurrentPassengerRoute(null);
        }

        mainForm.update();
    }

    public void deleteSelectedElement() {
        NetworkElement elem = state.getSelectedElement();

        if (state.getCurrentPassengerRoute() != null && state.getCurrentMode() == EditionMode.None)  {
            saveState();
            state.getNetwork().getPassengerRoutes().remove(state.getCurrentPassengerRoute());
            state.setCurrentPassengerRoute(null);
        } else if (state.getCurrentBusRoute() != null && state.getCurrentMode() == EditionMode.None) {
            saveState();
            state.getNetwork().deletePassengerRouteForBusRoute(state.getCurrentBusRoute());
            state.getNetwork().getBusRoutes().remove(state.getCurrentBusRoute());
            state.setCurrentBusRoute(null);
        } else if (elem != null) {
            saveState();
            state.getNetwork().deleteElement(elem);

        } else {
            state.setMessage(Strings.NoElementSelected);
        }

        mainForm.update();
    }

    public void validate() {
        switch (state.getCurrentMode()) {
            case AddBusRoute:
                saveState();

                if (state.getControllerMode() == ControllerMode.AddingBusRoute) {
                    state.setControllerMode(ControllerMode.AddingBusRouteStation);
                    state.setMessage(Strings.SelectStations);
                } else if (state.getControllerMode() == ControllerMode.AddingBusRouteStation) {
                    BusRoute route = state.getCurrentBusRoute();
                    if (route.getSegments().size() > 0) {
                        state.setControllerMode(ControllerMode.AddingBusRouteSource);
                        state.setMessage(Strings.SelectRouteBusSource);
                    } else {
                        state.setMessage(Strings.RouteMustContainsSegment);
                    }

                }
                break;
            case AddPassengerRoute:
                saveState();

                if (state.getControllerMode() == ControllerMode.SelectPassengerFragmentBusRoute
                        && state.getCurrentPassengerRoute().getFragments().size() > 0) {
                    PassengerRoute route = state.getCurrentPassengerRoute();
                    Color color = DefaultColors[state.getNetwork().getPassengerRoutes().size() % DefaultColors.length];
                    route.setColor(color);

                    state.getNetwork().addPassengerRoute(route);
                    startPassengerRouteCreation();
                }
        }

        mainForm.update();
    }

    public void startSimulation(int nbSim, LocalTime startAt, LocalTime endsAt) {
        nbSim = Math.max(1, nbSim);

        Network network = state.getNetwork();
        Simulation simulation = new Simulation(startAt, endsAt, network);
        state.startSimulation(simulation);
        state.setRemainingSimulations(nbSim - 1);
        mainForm.update();
    }

    public void increaseSimulationTime(double speed) {
        double nextMinute = state.getCurrentMinute() + 0.05 * speed;
        Simulation sim = state.getSimulation();

        if (sim != null && nextMinute > sim.endsAtMinute()) {
            state.setCurrentMinute(sim.endsAtMinute());
            state.getSimulations().add(sim);
            if (state.getRemainingSimulations() > 0) {
                startSimulation(state.getRemainingSimulations(), sim.getStartAt(), sim.getEndsAt());
            } else {
                mainForm.pauseSimulation();
            }
        }
        else
            state.setCurrentMinute(nextMinute);

        mainForm.update();
    }

    public void stopSimulation() {
        state.setSimulation(null);
        state.getSimulations().clear();
        state.setCurrentMode(EditionMode.None);
        mainForm.pauseSimulation();
        mainForm.update();
    }

    public void restartSimulation() {
        state.setCurrentMinute(0);
        mainForm.update();
    }

    public void setCurrentBusRoute(BusRoute route) {
        if (state.getCurrentMode() == EditionMode.None) {
            saveState();
            state.setCurrentBusRoute(route);
        } else if (state.getCurrentMode() == EditionMode.AddPassengerRoute &&
                    state.getControllerMode() == ControllerMode.SelectPassengerFragmentBusRoute) {
            saveState();
            state.setCurrentBusRoute(route);
            state.setMessage(Strings.SelectPassengerStop);
            state.setControllerMode(ControllerMode.AddingPassengerFragmentDestination);
        }
        mainForm.update();
    }

    public void setCurrentPassengerRoute(PassengerRoute route) {
        if (state.getCurrentMode() == EditionMode.None) {
            saveState();
            state.setCurrentPassengerRoute(route);
        }
        mainForm.update();
    }

    public boolean save(File file) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeObject(state);
            state.setAppTitle(file.getName());
            oStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void newState() {
        saveState();
        Stack<ByteInputStream> prev = state.getPrevStack();
        state = new ApplicationState();
        state.setPrevStack(prev);
    }

    public boolean load(File file) {
        try {
            FileInputStream stream = new FileInputStream(file);
            ObjectInputStream oStream = new ObjectInputStream(stream);
            state = (ApplicationState) oStream.readObject();
            state.setAppTitle(file.getName());
            oStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean loadBackgroundImage(File file) {
        saveState();

        try {
            state.setBackgroundImage(ImageIO.read(file));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ApplicationState getState() {
        return state;
    }

    public void undo() {
        if (state.getPrevStack().isEmpty())
            return;

        Stack<ByteInputStream> prev = state.getPrevStack();
        Stack<ByteInputStream> next = state.getNextStack();

        next.push(getStateAsStream());
        ByteInputStream stream = prev.pop();
        loadState(stream);

        state.setNextStack(next);
        state.setPrevStack(prev);

        mainForm.update();
    }

    public void redo() {
        if (state.getNextStack().isEmpty())
            return;

        Stack<ByteInputStream> prev = state.getPrevStack();
        Stack<ByteInputStream> next = state.getNextStack();

        prev.push(getStateAsStream());
        ByteInputStream stream = next.pop();
        loadState(stream);

        state.setNextStack(next);
        state.setPrevStack(prev);

        mainForm.update();
    }

    public void saveState() {
        state.getNextStack().clear();
        state.getPrevStack().push(getStateAsStream());
    }

    private void loadState(ByteInputStream stream) {
        Coordinate pos = state.getCurrentPosition();
        try {
            ObjectInputStream oStream = new ObjectInputStream(stream);
            state = (ApplicationState) oStream.readObject();
            state.setCurrentPosition(pos);
            oStream.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ByteInputStream getStateAsStream() {
        ApplicationState state = getState();
        try {
            ByteOutputStream stream = new ByteOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeObject(state);
            ByteInputStream inputStream = stream.newInputStream();
            oStream.close();
            stream.close();

            return inputStream;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startBusRouteCreation() {
        state.setControllerMode(ControllerMode.AddingBusRoute);
        state.setCurrentBusRoute(null);
        state.setMessage(Strings.SelectRouteSource);
    }

    private void selectionModeClick(Coordinate coord, boolean leftClick) {
        state.setCurrentBusRoute(null);
        NetworkElement elem = state.getNetwork().getElementOnCoords(coord);

        if (elem instanceof Node) {
            state.setSelectedElement(elem);
        } else if (elem != null) {
            ArrayList<Segment> segs = state.getNetwork().getSegmentOnCoords(coord);
            if (segs.size() == 1 || leftClick)
                state.setSelectedElement(segs.get(0));
            else
                state.setSelectedElement(segs.get(1));
        }
    }

    private void addNodeClick(Coordinate coord) {
        if (!state.getNetwork().addNode(coord)) {
            state.setMessage(Strings.NodeAlreadyExisting);
        }
    }

    private void addSegmentClick(Coordinate coord) {
        Network network = state.getNetwork();
        Node node = network.getNodeOnCoords(coord);
        NetworkElement selectedElem = state.getSelectedElement();
        Node previousNode = selectedElem instanceof Node ? (Node) selectedElem : null;

        if (node == null) {
            state.setSelectedElement(null);
            return;
        }

        if (previousNode != null && node != previousNode) {
           if (!network.addSegment(previousNode, node)) {
               state.setMessage(Strings.SegmentAlreadyExisting);
           } else {
               state.setSelectedElement(node);
           }
        } else  {
            state.setSelectedElement(node);
        }
    }

    private void addBusRouteClick(Coordinate coord) {
        Network network = state.getNetwork();
        BusRoute route = state.getCurrentBusRoute();

        if (route == null) {
            Node node = network.getNodeOnCoords(coord);
            if (node != null) {
                Color color = DefaultColors[network.getBusRoutes().size() % DefaultColors.length];
                state.setCurrentBusRoute(new BusRoute(node, color));
                state.setMessage(Strings.SelectConsecutiveSegments);
            } else {
                state.setMessage(Strings.SelectRouteSource);
            }
        } else if (state.getControllerMode() == ControllerMode.AddingBusRoute) {
            ArrayList<Segment> segments = network.getSegmentOnCoords(coord);

            for (Segment s : segments) {
                if (route.isConsecutive(s)) {
                    route.addSegment(s);
                    break;
                }
            }
            state.setMessage(Strings.SelectConsecutiveSegments);
        } else if (state.getControllerMode() == ControllerMode.AddingBusRouteStation) {
            Node node = network.getNodeOnCoords(coord);
            if (node != null) {
                route.toggleStation(node);
            }
            state.setMessage(Strings.SelectStations);
        } else if (state.getControllerMode() == ControllerMode.AddingBusRouteSource) {
            Node node = network.getNodeOnCoords(coord);
            if (route.isNodeOnRoute(node) &&
                    (route.getSegments().get(route.getSegments().size()-1).getDestination() != node || route.isLoopable())) {
                route.setBusSource(new Source(node));
                network.addBusRoute(route);
                startBusRouteCreation();
            } else {
                state.setMessage(Strings.IncorrectRouteBusSource);
            }
        }
    }

    private void startPassengerRouteCreation() {
        state.setControllerMode(ControllerMode.AddingPassengerFragmentSource);
        state.setSelectedElement(null);
        state.setCurrentPassengerRoute(new PassengerRoute());
        state.setMessage(Strings.SelectPassengerSource);
    }

    private void addPassengerRouteClick(Coordinate coord) {
        Network network = state.getNetwork();
        Node node = network.getNodeOnCoords(coord);

        if (state.getControllerMode() == ControllerMode.AddingPassengerFragmentSource) {
            ArrayList<BusRoute> availableRoutes = network.getBusRoutesWithStation(node);
            if (availableRoutes.size() > 0) {
                state.setSelectedElement(node);
                state.setAvailableBusRoutes(availableRoutes);
                state.setMessage(Strings.SelectBusRoute);
                state.setControllerMode(ControllerMode.SelectPassengerFragmentBusRoute);
            } else {
                state.setMessage(Strings.SelectPassengerSource);
            }
        } else if (state.getControllerMode() == ControllerMode.AddingPassengerFragmentDestination) {
            BusRoute busRoute = state.getCurrentBusRoute();
            Node previousNode = (Node) state.getSelectedElement();
            if (busRoute.getStationPosition(node) > busRoute.getStationPosition(previousNode) || busRoute.getIsLoop()) {
                PassengerRouteFragment fragment = new PassengerRouteFragment(previousNode, node, busRoute);
                state.getCurrentPassengerRoute().addFragment(fragment);

                state.setAvailableBusRoutes(network.getBusRoutesWithStation(node));
                state.setSelectedElement(node);
                state.setCurrentBusRoute(null);
                state.setMessage(Strings.SelectBusRoute);
                state.setControllerMode(ControllerMode.SelectPassengerFragmentBusRoute);
            }
        }
    }

    private void dijkstraClick(Coordinate coord) {
        Network network = state.getNetwork();
        Node node = network.getNodeOnCoords(coord);

        if (state.getSelectedElement() instanceof Node) {
            Node source = (Node) state.getSelectedElement();
            Pathfinder find = new Pathfinder(network, source, node);
            PassengerRoute r = find.find();
            if (r != null) {
                network.addPassengerRoute(r);
                state.setSelectedElement(null);
                setEditionMode(EditionMode.None);
                state.setOpenedPanel(OpenedPanel.PassengerRoutes);
                state.setCurrentPassengerRoute(r);
            } else {
                state.setMessage(Strings.NoPathFound);
            }

            mainForm.update();
        } else {
            state.setSelectedElement(node);
        }

    }
}
