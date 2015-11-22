package domain;

import ui.MainForm;
import util.CoordinateConverter;
import util.Strings;

import java.awt.*;

public class Controller {
    private MainForm mainForm;
    private ApplicationState state;
    private ControllerMode controllerMode;

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
        currentCenter.translate(dx, dy);

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

    public void setControllerMode(EditionMode mode) {
        state.setMessage("");
        state.setCurrentMode(mode);

        state.setSelectedElement(null);

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
            state.getPlane().deleteElement(elem);
    }

    public void validate() {
        switch (state.getCurrentMode()) {
            case AddBusRoute:
                if (controllerMode == ControllerMode.AddingBusRoute) {
                    controllerMode = ControllerMode.AddingBusRouteStation;
                    state.setMessage(Strings.SelectStations);
                } else if (controllerMode == ControllerMode.AddingBusRouteStation) {
                    state.getPlane().addRoute(state.getCurrentBusRoute());
                    state.setCurrentBusRoute(null);
                    startBusRouteCreation();
                }
                break;
        }
    }

    public ApplicationState getState() {
        return state;
    }

    private void startBusRouteCreation() {
        controllerMode = ControllerMode.AddingBusRoute;
        state.setMessage(Strings.SelectRouteSource);
    }

    private void selectionModeClick(Coordinate coord) {
        state.setSelectedElement(state.getPlane().getElementOnCoords(coord));
    }

    private void addNodeClick(Coordinate coord) {
        if (!state.getPlane().addNode(coord)) {
            state.setMessage(Strings.NodeAlreadyExisting);
        }
    }

    private void addSegmentClick(Coordinate coord) {
        Plane plane = state.getPlane();
        Node node = plane.getNodeOnCoords(coord);
        NetworkElement selectedElem = state.getSelectedElement();
        Node previousNode = selectedElem instanceof Node ? (Node) selectedElem : null;

        if (node == null)
            return;

        if (previousNode != null && node != previousNode) {
           if (!plane.addSegment(previousNode, node)) {
               state.setMessage(Strings.SegmentAlreadyExisting);
           }

            state.setSelectedElement(null);
        } else  {
            state.setSelectedElement(node);
        }
    }

    private void addBusRouteClick(Coordinate coord) {
        Plane plane = state.getPlane();
        BusRoute route = state.getCurrentBusRoute();

        if (route == null) {
            Node node = plane.getNodeOnCoords(coord);
            if (node != null) {
                state.setCurrentBusRoute(new BusRoute(node));
                state.setMessage(Strings.SelectConsecutiveSegments);
            }
        } else if (controllerMode == ControllerMode.AddingBusRoute) {
            Segment segment = plane.getSegmentOnCoords(coord);
            if (segment != null && route.isConsecutive(segment)) {
                route.addSegment(segment);
            }
        } else if (controllerMode == ControllerMode.AddingBusRouteStation) {
            Node node = plane.getNodeOnCoords(coord);
            if (node != null) {
                route.toggleStation(node);
            }
        }
    }
}
