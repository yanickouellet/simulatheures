package ui;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private int x;

    public MapPanel() {
        x = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(x % 8 == 0 ? Color.WHITE : Color.cyan);
        g.drawString("Hello world!", x, 25);
        x = x > getWidth() - 25 ? 0 : x + 1;
    }
}
