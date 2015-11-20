package domain;

import java.util.ArrayList;

public class Plane {
    private ArrayList<Node> nodes;

    public Plane() {
        nodes = new ArrayList<>();
    }

    public void addNode(Coordinate coords) {
        nodes.add(new Node(coords));
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
}
