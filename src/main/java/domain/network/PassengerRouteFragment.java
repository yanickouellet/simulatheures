package domain.network;

import java.util.ArrayList;

public class PassengerRouteFragment {
    private Node source;
    private Node destination;
    private BusRoute busRoute;

    public PassengerRouteFragment(Node source, Node destination, BusRoute busRoute) {
        this.source = source;
        this.destination = destination;
        this.busRoute = busRoute;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }
}
