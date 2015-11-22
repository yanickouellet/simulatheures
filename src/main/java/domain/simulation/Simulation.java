package domain.simulation;

import domain.network.BusRoute;
import domain.network.Network;
import domain.network.Segment;

import java.sql.Time;
import java.util.ArrayList;

public class Simulation {
    private Time startAt;
    private Time endsAt;
    private Network network;

    private ArrayList<DistributedElement<Segment>> segments;
    private ArrayList<DistributedElement<BusRoute>> routes;

    public Simulation(Time startAt, Time endsAt, Network network) {
        this.startAt = startAt;
        this.endsAt = endsAt;
        this.network = network;

        segments = new ArrayList<>();
        routes = new ArrayList<>();

        for (Segment s : network.getSegments().values()) {
            segments.add(new DistributedElement<>(s));
        }

        for (BusRoute r : network.getRoutes()) {
            routes.add(new DistributedElement<>(r));
        }
    }
}
