package domain;

import java.io.Serializable;
import java.util.Random;

public class TriangularDistribution implements Serializable {
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
        double lowerRange = averageValue - minValue;
        double higherRanger = maxValue - averageValue;
        double totalRange = maxValue - minValue;

        double value;
        if (u < lowerRange / totalRange) {
            value = minValue + Math.sqrt(u * lowerRange * totalRange);
        } else {
            value = maxValue - Math.sqrt((1 - u) * higherRanger * totalRange);
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
}
