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

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(5));

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        g2.drawRect(0, 0, w, h);

        g2.setStroke(new BasicStroke(1));
        g2.drawLine(w/2, 0, w/2, h);
        g2.drawLine(0, h/2, w, h/2);

        if (drawer != null)
            drawer.draw((Graphics2D)g, getWidth(), getHeight());
    }

    public void setDrawer(MapDrawer drawer) {
        this.drawer = drawer;
    }
}
