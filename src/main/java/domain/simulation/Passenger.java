package domain.simulation;

import domain.network.PassengerRoute;

import java.io.Serializable;

public class Passenger implements Serializable {
    private PassengerRoute passengerRoute;
    private double startTime;
    private double arrivalTime;

    public Passenger(PassengerRoute passengerRoute, double time) {
        this.passengerRoute = passengerRoute;
        startTime = time;
        arrivalTime = -1;
    }

    public PassengerRoute getPassengerRoute() {
        return passengerRoute;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getTravelTime() {
        if (arrivalTime == -1)
            throw new IllegalArgumentException("Passenger did not reach destination");

        return arrivalTime - startTime;
    }
}
