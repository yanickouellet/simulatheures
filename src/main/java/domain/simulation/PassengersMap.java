package domain.simulation;

import java.io.Serializable;
import java.util.*;

public class PassengersMap implements Serializable {
    private TreeMap<Double, LinkedList<Passenger>> passengerMap;

    public PassengersMap() {
        passengerMap = new TreeMap<>();
    }

    public void addPassengersAt(double time, Passenger passenger) {
        LinkedList<Passenger> basePassengers = getPassengersAt(time);
        LinkedList<Passenger> newPassengers = (LinkedList<Passenger>) basePassengers.clone();
        newPassengers.add(passenger);

        Double key = passengerMap.floorKey(time);
        if (key != null) {
            for (LinkedList<Passenger> entry : passengerMap.tailMap(key, false).values()) {
                entry.add(passenger);
            }
        }
        passengerMap.put(time, newPassengers);
    }

    public void removePassengerAt(double time, Passenger passenger) {
        LinkedList<Passenger> basePassengers = getPassengersAt(time);
        LinkedList<Passenger> newPassengers = (LinkedList<Passenger>) basePassengers.clone();
        newPassengers.remove(passenger);

        double key = passengerMap.floorKey(time);
        for (LinkedList<Passenger> entry : passengerMap.tailMap(key, false).values()) {
            entry.remove(passenger);
        }

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
}
