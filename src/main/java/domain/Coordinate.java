package domain;

import java.text.DecimalFormat;

public class Coordinate {
    private float x;
    private float y;

    public Coordinate() {
        x = 0;
        y = 0;
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("#.####");
        return String.format("(%s, %s)", format.format(x), format.format(y));
    }
}
