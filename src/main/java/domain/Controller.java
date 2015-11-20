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
        state.getPlane().addNode(CoordinateConverter.PointToCoordinate(p, maxWidth, maxHeight, state.getZoomRatio()));
        mainForm.update();
    }

    public void mouseOver(Point p, int maxWidth, int maxHeight) {
        state.setCurrentPosition(CoordinateConverter.PointToCoordinate(p, maxWidth, maxHeight, state.getZoomRatio()));
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

    public ApplicationState getState() {
        return state;
    }
}
