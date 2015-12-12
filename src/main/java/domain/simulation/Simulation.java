package domain.simulation;

import domain.Coordinate;
import domain.network.*;

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
    private HashMap<PassengerRoute, Double> passengerRoutes;
    private HashMap<Node, Station> stations;
    private ArrayList<Vehicle> vehicles;

    public Simulation(LocalTime startAt, LocalTime endsAt, Network network) {
        this.startAt = startAt;
        this.endsAt = endsAt;
        this.network = network;

        segments = new HashMap<>();
        routes = new HashMap<>();
        passengerRoutes = new HashMap<>();
        stations = new HashMap<>();
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

            for (Node stationNode : r.getStations()) {
                Station station;
                if (!stations.containsKey(stationNode)) {
                    station = new Station(stationNode);
                    stations.put(stationNode, station);
                } else {
                    station = stations.get(stationNode);
                }
                station.addBusRoute(r);
            }
        }

        double endsAtMinute = endsAtMinute();
        for (PassengerRoute r : network.getPassengerRoutes()) {
            double value = r.generate();
            passengerRoutes.put(r, value);

            PassengerRouteFragment firstFragment = r.getFragments().get(0);
            Node source = firstFragment.getSource();
            Station sourceStation = getStationForNode(source);

            for (double time = 0; time < endsAtMinute; time += value) {
                sourceStation.addPassengersAt(time, new Passenger(r));
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

    public int computePassengerCountAtStation(Node node, double time) {
        if (!stations.containsKey(node))
            return -1;
        return stations.get(node).getPassengerCountAt(time);
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

    private Station getStationForNode(Node node) {
        if (!stations.containsKey(node))
            throw new IllegalArgumentException("node is not a station");
        return stations.get(node);
    }
}
