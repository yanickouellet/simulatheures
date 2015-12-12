package domain.simulation;

import domain.Coordinate;
import domain.network.BusRoute;
import domain.network.Segment;

import java.util.Map;
import java.util.TreeMap;

public class Vehicle {
    private BusRoute route;
    private long arrivalTime;
    private TreeMap<Double, Segment> segmentsMap;

    public Vehicle(BusRoute route, long arrivalTime) {
        this.route = route;
        this.arrivalTime = arrivalTime;
        segmentsMap = new TreeMap<>();
    }

    public BusRoute getRoute() {
        return route;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void addSegment(double startTime, Segment segment) {
        segmentsMap.put(startTime, segment);
    }

    public Map.Entry<Double, Segment> getSegmentAtTime(double time) {
        return segmentsMap.floorEntry(time);
    }
}
