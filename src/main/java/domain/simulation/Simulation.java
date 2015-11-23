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

    private HashMap<Segment, Integer> segments;
    private HashMap<BusRoute, Integer> routes;
    private ArrayList<Vehicle> vehicles;

    public Simulation(LocalTime startAt, LocalTime endsAt, Network network) {
        this.startAt = startAt;
        this.endsAt = endsAt;
        this.network = network;

        segments = new HashMap<>();
        routes = new HashMap<>();
        vehicles = new ArrayList<>();

        for (Segment s : network.getSegments().values()) {
            segments.put(s, Math.round(s.generate()));
        }

        for (BusRoute r : network.getRoutes()) {
            int value = Math.round(r.generate());
            routes.put(r, value);

            long busStartAt =  r.getStartAt();
            while(busStartAt < endsAtMinute()) {
                vehicles.add(new Vehicle(r, busStartAt));
                busStartAt += value;
            }
        }
    }

    public Coordinate computePosition(Vehicle vehicle, long minutesSinceStart) {
        ArrayList<Segment> path = vehicle.getRoute().getSegments();
        int i = 0;
        long time = 0;
        Segment lastSegment = path.get(0);
        long timeToTravel = segments.get(lastSegment);

        while (time + timeToTravel < minutesSinceStart && i < path.size()) {
            time += timeToTravel;
            lastSegment = path.get(i);
            timeToTravel = segments.get(lastSegment);
        }

        if (time + timeToTravel < minutesSinceStart)
            return null;

        long timeOnSegment = minutesSinceStart - time;
        float rate = timeOnSegment / (float) timeToTravel;

        return lastSegment.getVector().computeNewCoordinate(rate);
    }

    public long endsAtMinute() {
        return ChronoUnit.MINUTES.between(startAt, endsAt);
    }
}
