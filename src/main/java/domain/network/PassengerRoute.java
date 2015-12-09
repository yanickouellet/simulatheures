package domain.network;

import domain.TriangularDistribution;

import java.util.ArrayList;

public class PassengerRoute {
    private int timeBeforeFirst;
    private ArrayList<PassengerRouteFragment> fragments;
    private TriangularDistribution distribution;

    public PassengerRoute() {
        fragments = new ArrayList<>();
    }

    public void addFragment(PassengerRouteFragment fragment) {
        fragments.add(fragment);
    }

    public ArrayList<PassengerRouteFragment> getFragments() {
        return fragments;
    }

    public int getTimeBeforeFirst() {
        return timeBeforeFirst;
    }

    public void setTimeBeforeFirst(int timeBeforeFirst) {
        this.timeBeforeFirst = timeBeforeFirst;
    }

    public TriangularDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(TriangularDistribution distribution) {
        this.distribution = distribution;
    }

    public boolean isSegmentOnRoute(Segment s) {
        for(PassengerRouteFragment f : fragments) {
            if (f.getBusRoute().getSegmentsBetweenNodes(f.getSource(), f.getDestination()).contains(s))
                return true;
        }
        return false;
    }
}
