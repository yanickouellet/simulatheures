package domain.simulation;

import domain.network.PassengerRoute;

public class StatEntry {
    private int nbPassenger;
    private double totalTime;
    private PassengerRoute route;

    public StatEntry(PassengerRoute route) {
        this.route = route;
        nbPassenger = 0;
        totalTime = 0;
    }

    public void addPassengerTime(double time) {
        totalTime += time;
        nbPassenger++;
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
}
