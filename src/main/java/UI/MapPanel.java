package UI;

import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.drawString("Hello world!", 25, 25);
    }
}
