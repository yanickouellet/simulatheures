package domain;

public class TriangularDistribution {
    private float minValue;
    private float averageValue;
    private float maxValue;

    public TriangularDistribution(float minValue, float averageValue, float maxValue) {
        this.minValue = minValue;
        this.averageValue = averageValue;
        this.maxValue = maxValue;
    }

    //TODO Code that!
    public float generate() {
        return averageValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(float averageValue) {
        this.averageValue = averageValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
}
