package ui;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private MapDrawer drawer;

    public MapPanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawer != null)
            drawer.draw((Graphics2D)g, getWidth(), getHeight());
    }

    public void setDrawer(MapDrawer drawer) {
        this.drawer = drawer;
    }
}
