package domain.simulation;

import domain.network.BusRoute;
import domain.network.Node;

import java.io.Serializable;
import java.util.*;

public class Station implements Serializable {
    private Node node;
    private ArrayList<BusRoute> busRoutes;
    private PassengersMap passengerMap;

    public Station(Node node) {
        this.node = node;
        busRoutes = new ArrayList<>();
        passengerMap = new PassengersMap();
    }

    public PassengersMap getPassengerMap() {
        return passengerMap;
    }

    public void addBusRoute(BusRoute r) {
        busRoutes.add(r);
    }

    public Node getNode() {
        return node;
    }

    public ArrayList<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return !(node != null ? !node.equals(station.node) : station.node != null);

    }

    @Override
    public int hashCode() {
        return node != null ? node.hashCode() : 0;
    }
}
