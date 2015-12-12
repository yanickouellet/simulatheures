package domain.network;

import domain.IDistributableElement;
import domain.TriangularDistribution;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class PassengerRoute implements Serializable, IDistributableElement {
    private int timeBeforeFirst;
    private ArrayList<PassengerRouteFragment> fragments;
    private TriangularDistribution distribution;
    private Color color;
    private String name;
    private int maxPersonNumber;

    private static int i = 0;

    public PassengerRoute() {
        name = "Itinéraire " + i++;
        fragments = new ArrayList<>();
        distribution = new TriangularDistribution(15, 15, 15);
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
        return getBusRouteForSegment(s) != null;
    }

    public BusRoute getBusRouteForSegment(Segment s) {
        for(PassengerRouteFragment f : fragments) {
            if (f.getBusRoute().getSegmentsBetweenNodes(f.getSource(), f.getDestination()).contains(s))
                return f.getBusRoute();
        }
        return null;
    }

    public boolean isLastSegmentOfRoute(Segment s) {
        int position = getFragmentPositionForSegment(s);
        return position == fragments.size() - 1 && isLastSegmentOfFragment(s);
    }

    public boolean isLastSegmentOfFragment(Segment s) {
        int position = getFragmentPositionForSegment(s);
        PassengerRouteFragment fragment = fragments.get(position);
        return fragment.getDestination().equals(s.getDestination());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPersonNumber() { return maxPersonNumber; }

    public void setMaxPersonNumber(int maxNb){ this.maxPersonNumber = maxNb; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public double generate() {
        return distribution.generate();
    }

    private int getFragmentPositionForSegment(Segment s) {
        int i = 0;
        for(PassengerRouteFragment f : fragments) {
            if (f.getBusRoute().getSegmentsBetweenNodes(f.getSource(), f.getDestination()).contains(s))
                return i;
            i++;
        }
        return -1;
    }
}
