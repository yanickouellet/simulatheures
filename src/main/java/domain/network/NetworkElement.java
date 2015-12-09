package domain.network;

import domain.Coordinate;

import java.io.Serializable;

public abstract class NetworkElement implements Serializable {
    public abstract boolean isOnCoordinate(Coordinate coords);
}
