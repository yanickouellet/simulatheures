package domain;

import domain.network.*;
import domain.simulation.Simulation;
import ui.MainForm;
import util.CoordinateConverter;
import util.Strings;

import java.awt.*;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

public class Controller {
    private MainForm mainForm;
    private ApplicationState state;
    private ControllerMode controllerMode;
    private static final Color[] DefaultColors = new Color[] {
            Color.blue,
            Color.cyan,
            Color.darkGray,
            Color.green,
            Color.magenta,
            Color.pink,
            Color.yellow
    };

    public Controller(MainForm mainForm) {
        this.mainForm = mainForm;
        state = new ApplicationState();
        controllerMode = ControllerMode.Normal;
    }

    public void click(Point p, int maxWidth, int maxHeight) {
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
                selectionModeClick(coord);
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

    public void setEditionMode(EditionMode mode) {
        state.setMessage("");
        state.setCurrentMode(mode);

        state.setSelectedElement(null);

        if (mode != EditionMode.Simulation) {
            state.setSimulation(null);
            mainForm.pauseSimulation();
        }

        if (mode == EditionMode.AddBusRoute) {
            startBusRouteCreation();
        } else {
            controllerMode = ControllerMode.Normal;
            state.setCurrentBusRoute(null);
        }

        mainForm.update();
    }

    public void deleteSelectedElement() {
        NetworkElement elem = state.getSelectedElement();
        if (elem == null)
            state.setMessage(Strings.NoElementSelected);
        else
            state.getNetwork().deleteElement(elem);
    }

    public void validate() {
        switch (state.getCurrentMode()) {
            case AddBusRoute:
                if (controllerMode == ControllerMode.AddingBusRoute) {
                    controllerMode = ControllerMode.AddingBusRouteStation;
                    state.setMessage(Strings.SelectStations);
                } else if (controllerMode == ControllerMode.AddingBusRouteStation) {
                    BusRoute route = state.getCurrentBusRoute();
                    if (route.getSegments().size() > 0) {
                        controllerMode = ControllerMode.AddingBusRouteSource;
                        state.setMessage(Strings.SelectRouteBusSource);
                    } else {
                        state.setMessage(Strings.RouteMustContainsSegment);
                    }

                }
                break;
        }

        mainForm.update();
    }

    public void startSimulation() {
        Network network = state.getNetwork();
        Simulation simulation = new Simulation(LocalTime.of(5,0), LocalTime.of(14, 0), network);
        state.startSimulation(simulation);
        mainForm.update();
    }

    public void increaseSimulationTime(double speed) {
        double nextMinute = state.getCurrentMinute() + 0.05 * speed;

        if (state.getSimulation() != null && nextMinute > state.getSimulation().endsAtMinute()) {
            mainForm.pauseSimulation();
            state.setCurrentMinute(state.getSimulation().endsAtMinute());
        }
        else
            state.setCurrentMinute(nextMinute);

        mainForm.update();
    }

    public void stopSimulation() {
        state.setSimulation(null);
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
            state.setCurrentBusRoute(route);
        }
    }

    public ApplicationState getState() {
        return state;
    }

    private void startBusRouteCreation() {
        controllerMode = ControllerMode.AddingBusRoute;
        state.setCurrentBusRoute(null);
        state.setMessage(Strings.SelectRouteSource);
    }

    private void selectionModeClick(Coordinate coord) {
        state.setCurrentBusRoute(null);
        state.setSelectedElement(state.getNetwork().getElementOnCoords(coord));
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
                Color color = DefaultColors[network.getRoutes().size() % DefaultColors.length];
                state.setCurrentBusRoute(new BusRoute(node, color));
                state.setMessage(Strings.SelectConsecutiveSegments);
            } else {
                state.setMessage(Strings.SelectRouteSource);
            }
        } else if (controllerMode == ControllerMode.AddingBusRoute) {
            ArrayList<Segment> segments = network.getSegmentOnCoords(coord);

            for (Segment s : segments) {
                if (route.isConsecutive(s)) {
                    route.addSegment(s);
                    break;
                }
            }
            state.setMessage(Strings.SelectConsecutiveSegments);
        } else if (controllerMode == ControllerMode.AddingBusRouteStation) {
            Node node = network.getNodeOnCoords(coord);
            if (node != null) {
                route.toggleStation(node);
            }
            state.setMessage(Strings.SelectStations);
        } else if (controllerMode == ControllerMode.AddingBusRouteSource) {
            Node node = network.getNodeOnCoords(coord);
            if (route.isNodeOnRoute(node) &&
                    (route.getSegments().get(route.getSegments().size()-1).getDestination() != node || route.isLoopable())) {
                route.setBusSource(new Source(node));
                network.addRoute(route);
                startBusRouteCreation();
            } else {
                state.setMessage(Strings.IncorrectRouteBusSource);
            }
        }
    }
}
