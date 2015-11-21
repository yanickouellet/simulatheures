package domain;

import ui.MainForm;
import util.CoordinateConverter;

import java.awt.*;

public class Controller {
    private MainForm mainForm;
    private ApplicationState state;

    public Controller(MainForm mainForm) {
        this.mainForm = mainForm;
        state = new ApplicationState();
    }

    public void click(Point p, int maxWidth, int maxHeight) {
        Coordinate coord = CoordinateConverter.PointToCoordinate(
                p,
                maxWidth,
                maxHeight,
                state.getCenterCoordinate(),
                state.getZoomRatio()
        );

        switch (state.getCurrentMode()) {
            case AddNode:
                addNodeClick(coord);
                break;
            case AddSegment:
                addSegmentClick(coord);
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

    public void setMode(EditionMode mode) {
        state.setCurrentMode(mode);

        if (mode != EditionMode.AddSegment) {
            state.setSelectedNode(null);
        }
    }

    public ApplicationState getState() {
        return state;
    }

    private void addNodeClick(Coordinate coord) {
        state.getPlane().addNode(coord);
    }

    private void addSegmentClick(Coordinate coord) {
        Plane plane = state.getPlane();
        Node node = plane.getHoveredNode(coord);
        Node previousNode = state.getSelectedNode();

        if (previousNode != null && node != previousNode) {
            plane.addSegment(previousNode, node);
            state.setSelectedNode(null);
        } else if (node != null) {
            state.setSelectedNode(node);
        }
    }
}
