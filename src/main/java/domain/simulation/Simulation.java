package domain.simulation;

import domain.Coordinate;
import domain.network.BusRoute;
import domain.network.Network;
import domain.network.Segment;
import domain.network.Source;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        for (BusRoute r : network.getBusRoutes()) {
            double value = r.generate();
            routes.put(r, value);

            int i = 0;
            int maxVehicles = r.getBusSource().getNumberMaxVehicule();
            long busStartAt =  r.getBusSource().getTimeBeforeFirstVehicule();
            while(busStartAt < endsAtMinute() && (i < maxVehicles || maxVehicles <= 0)) {
                Vehicle v = new Vehicle(r, busStartAt);
                initializeVehicle(v);
                vehicles.add(v);
                busStartAt += value;
                i++;
            }
        }
    }

    public Coordinate computePosition(Vehicle vehicle, double minutesSinceStart) {
        Map.Entry<Double, Segment> entry = vehicle.getSegmentAtTime(minutesSinceStart);
        if (entry == null)
            return null;

        Segment segment = entry.getValue();

        double startTime = entry.getKey();
        double timeToTravel = segments.get(segment);
        double endTime = startTime + timeToTravel;
        double timeOnSegment = minutesSinceStart - startTime;

        if (minutesSinceStart >= endTime)
            return null;

        if (timeOnSegment == 0) {
            return segment.getVector().getSource().getCoordinate();
        }

        double rate = timeOnSegment / timeToTravel;

        return segment.getVector().computeNewCoordinate(rate);
    }

    public long endsAtMinute() {
        long min = ChronoUnit.MINUTES.between(startAt, endsAt);
        if (min < 0) {
            min = 1440 + min;
        }

        return min;
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

    public void initializeVehicle(Vehicle v) {
        ArrayList<Segment> path = v.getRoute().getSegments();
        Source source = v.getRoute().getBusSource();
        boolean isLoop = v.getRoute().getIsLoop();
        double time = v.getArrivalTime();
        int i = 0;

        while (source.getNode() != path.get(i++).getSource());

        Segment lastSegment = path.get(--i);
        double timeToTravel = segments.get(lastSegment);
        double endsAtMinute = endsAtMinute();

        while (time + timeToTravel < endsAtMinute && i < path.size()) {
            v.addSegment(time, lastSegment);

            time += timeToTravel;
            ++i;
            if (isLoop && i == path.size())
                i = 0;

            if (i < path.size()) {
                lastSegment = path.get(i);
                timeToTravel = segments.get(lastSegment);
            }
        }
    }
}
