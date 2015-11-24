package domain.network;

import domain.Coordinate;
import domain.IDistributableElement;
import domain.NodeVector;
import domain.TriangularDistribution;

public class Segment extends NetworkElement implements IDistributableElement {
    private NodeVector vector;
    private TriangularDistribution distribution;

    public Segment(NodeVector vector) {
        this.vector = vector;
        distribution = new TriangularDistribution(5, 5, 5);
    }

    public Node getSource() {
        return vector.getSource();
    }

    public Node getDestination() {
        return vector.getDestination();
    }

    public NodeVector getVector() {
        return vector;
    }

    @Override
    public boolean isOnCoordinate(Coordinate coords) {
        NodeVector sourceCoord = new NodeVector(vector.getSource(), new Node(coords));

        double deltaNorm = vector.computeNorm() - sourceCoord.computeNorm();
        double deltaAngle = vector.computeAngle() - sourceCoord.computeAngle();

        boolean sameSignX = Math.signum(vector.getComponentX()) == Math.signum(sourceCoord.getComponentX());
        boolean sameSignY = Math.signum(vector.getComponentY()) == Math.signum(sourceCoord.getComponentY());

        return deltaAngle < 0.08 && deltaNorm > 0 && sameSignX && sameSignY;
    }

    public TriangularDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(TriangularDistribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        return !(vector != null ? !vector.equals(segment.vector) : segment.vector != null);

    }

    @Override
    public int hashCode() {
        return vector != null ? vector.hashCode() : 0;
    }

    @Override
    public double generate() {
        return distribution.generate();
    }
}
