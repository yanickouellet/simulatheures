package domain;

import java.util.Random;

public class TriangularDistribution {
    private double minValue;
    private double averageValue;
    private double maxValue;
    private static Random r = new Random();

    public TriangularDistribution(double minValue, double averageValue, double maxValue) {
        this.minValue = minValue;
        this.averageValue = averageValue;
        this.maxValue = maxValue;
    }

    //http://www.wikiwand.com/en/Triangular_distribution
    public double generate() {
        if (minValue == averageValue && averageValue == maxValue)
            return averageValue;

        double u = r.nextDouble();
        double fc = computeRepartition(maxValue);

        double value;
        if (u < fc) {
            value = minValue + Math.sqrt(u * (averageValue - minValue) * (maxValue - minValue));
        } else {
            value = maxValue - Math.sqrt((1 - u) * (averageValue - minValue) * (maxValue - minValue));
        }

        return value;
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

    private double computeRepartition(double x) {
        return (x - minValue) / (averageValue - minValue);
    }
}
