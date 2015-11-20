package domain;

import ui.MainForm;
import util.CoordinateConverter;

import java.awt.*;

public class Controller {
    private MainForm mainForm;
    private Map map;

    public Controller(MainForm mainForm) {
        this.mainForm = mainForm;
        map = new Map();
    }

    public void click(Point p, int maxWidth, int maxHeight) {
    }

    public void mouseOver(Point p, int maxWidth, int maxHeight) {
        map.setCurrentPosition(CoordinateConverter.PointToCoordinate(p, maxWidth, maxHeight, 1));
        mainForm.update(map);
    }
}
