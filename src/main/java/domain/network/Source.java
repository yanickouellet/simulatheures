package domain.network;

import domain.TriangularDistribution;

public class Source {
    private Node node;
    private TriangularDistribution distribution;
    private Integer timeBeforeFirstVehicule;
    private Integer numberMaxVehicule;

    public Source(Node node) {
        this.node = node;
        this.distribution = new TriangularDistribution(15, 15, 15);
        this.timeBeforeFirstVehicule = 0;
    }

    public Node getNode() { return node; }

    public void setNode(Node node) {
        this.node = node;
    }

    public TriangularDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(TriangularDistribution distribution) {
        this.distribution = distribution;
    }

    public Integer getNumberMaxVehicule() { return numberMaxVehicule; }

    public void setNumberMaxVehicule(Integer numberMaxVehicule) {
        this.numberMaxVehicule = numberMaxVehicule;
    }

    public Integer getTimeBeforeFirstVehicule() { return timeBeforeFirstVehicule; }

    public void setTimeBeforeFirstVehicule(Integer timeBeforeFirstVehicule) {
        this.timeBeforeFirstVehicule = timeBeforeFirstVehicule;
    }
}
