package domain;

public class TriangularDistribution {
    private double minValue;
    private double averageValue;
    private double maxValue;

    public TriangularDistribution(double minValue, double averageValue, double maxValue) {
        this.minValue = minValue;
        this.averageValue = averageValue;
        this.maxValue = maxValue;
    }

    //TODO Code that!
    public double generate() {
        return averageValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(double averageValue) {
        this.averageValue = averageValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
