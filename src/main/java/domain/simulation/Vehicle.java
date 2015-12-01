package domain.simulation;

import domain.Coordinate;
import domain.network.BusRoute;

public class Vehicle {
    private BusRoute route;
    private long arrivalTime;

    public Vehicle(BusRoute route, long arrivalTime) {
        this.route = route;
        this.arrivalTime = arrivalTime;
    }

    public BusRoute getRoute() {
        return route;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }
}
