package ui.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class ColoredMutableTreeNode extends DefaultMutableTreeNode {
    private Color color;

    public ColoredMutableTreeNode(Object object, Color color) {
        super(object);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
