package domain.simulation;

import domain.Coordinate;
import domain.network.BusRoute;
import domain.network.Network;
import domain.network.Segment;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class Simulation {
    private LocalTime startAt;
    private LocalTime endsAt;
    private Network network;

    private HashMap<Segment, Double> segments;
    private HashMap<BusRoute, Double> routes;
    private ArrayList<Vehicle> vehicles;

    public Simulation(LocalTime startAt, LocalTime endsAt, Network network) {
        this.startAt = startAt;
        this.endsAt = endsAt;
        this.network = network;

        segments = new HashMap<>();
        routes = new HashMap<>();
        vehicles = new ArrayList<>();

        for (Segment s : network.getSegments().values()) {
            segments.put(s, s.generate());
        }

        for (BusRoute r : network.getRoutes()) {
            double value = r.generate();
            routes.put(r, value);

            long busStartAt =  r.getStartAt();
            while(busStartAt < endsAtMinute()) {
                vehicles.add(new Vehicle(r, busStartAt));
                busStartAt += value;
            }
        }
    }

    public Coordinate computePosition(Vehicle vehicle, double minutesSinceStart) {
        double minutesOnCircuit = minutesSinceStart - vehicle.getArrivalTime();
        if (minutesOnCircuit < 0)
            return null;

        ArrayList<Segment> path = vehicle.getRoute().getSegments();
        double time = 0;
        Segment lastSegment = path.get(0);
        double timeToTravel = segments.get(lastSegment);

        int i = 1;
        while (time + timeToTravel < minutesOnCircuit && i < path.size()) {
            time += timeToTravel;
            lastSegment = path.get(i);
            timeToTravel = segments.get(lastSegment);
            i++;
        }

        if (time + timeToTravel < minutesOnCircuit)
            return null;

        double timeOnSegment = minutesOnCircuit - time;
        double rate = timeOnSegment / timeToTravel;

        return lastSegment.getVector().computeNewCoordinate(rate);
    }

    public long endsAtMinute() {
        return ChronoUnit.MINUTES.between(startAt, endsAt);
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public LocalTime getEndsAt() {
        return endsAt;
    }
}
