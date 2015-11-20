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
    }

    public void mouseOver(Point p, int maxWidth, int maxHeight) {
        state.setCurrentPosition(CoordinateConverter.PointToCoordinate(p, maxWidth, maxHeight, 1));
        mainForm.update();
    }

    public ApplicationState getState() {
        return state;
    }
}
