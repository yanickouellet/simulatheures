package domain.simulation;

import domain.network.PassengerRoute;

import java.io.Serializable;

public class StatEntry implements Serializable {
    private int nbPassenger;
    private double totalTime;
    private PassengerRoute route;
    private double min;
    private double max;

    public StatEntry(PassengerRoute route) {
        this.route = route;
        nbPassenger = 0;
        totalTime = 0;
        max = -1;
        min = Double.MAX_VALUE;
    }

    public void addPassengerTime(double time) {
        totalTime += time;
        nbPassenger++;

        if (time > max)
            max = time;
        if (time < min)
            min = time;
    }

    public int getNbPassenger() {
        return nbPassenger;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getAverage() {
        if (nbPassenger == 0) return 0;
        return totalTime / nbPassenger;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
