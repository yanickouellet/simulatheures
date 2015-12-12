package domain.simulation;

import domain.network.BusRoute;
import domain.network.Node;

import java.util.*;

public class Station {
    private Node node;
    private ArrayList<BusRoute> busRoutes;
    private TreeMap<Double, LinkedList<Passenger>> passengerMap;

    public Station(Node node) {
        this.node = node;
        busRoutes = new ArrayList<>();
        passengerMap = new TreeMap<>();
    }

    public void addPassengersAt(double time, Passenger passenger) {
        LinkedList<Passenger> basePassengers = getPassengersAt(time);
        LinkedList<Passenger> newPassengers = (LinkedList<Passenger>) basePassengers.clone();
        newPassengers.add(passenger);
        passengerMap.put(time, newPassengers);
    }

    public LinkedList<Passenger> getPassengersAt(double time) {
        Map.Entry<Double, LinkedList<Passenger>> entry = passengerMap.floorEntry(time);
        if (entry == null)
            return new LinkedList<>();
        return entry.getValue();
    }

    public int getPassengerCountAt(double time) {
        return getPassengersAt(time).size();
    }

    public void addBusRoute(BusRoute route) {
        busRoutes.add(route);
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
