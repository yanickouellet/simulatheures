package domain.simulation;

import domain.network.PassengerRoute;

public class Passenger {
    private PassengerRoute passengerRoute;

    public Passenger(PassengerRoute passengerRoute) {
        this.passengerRoute = passengerRoute;
    }

    public PassengerRoute getPassengerRoute() {
        return passengerRoute;
    }
}
