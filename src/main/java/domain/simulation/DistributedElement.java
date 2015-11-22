package domain.simulation;

import domain.IDistributableElement;

public class DistributedElement<T extends IDistributableElement> {
    private T element;
    private float value;

    public DistributedElement(T element) {
        this.element = element;
        value = element.generate();
    }

    public T getElement() {
        return element;
    }

    public float getValue() {
        return value;
    }
}
