package domain.simulation;

import domain.IDistributableElement;

public class DistributedElement<T extends IDistributableElement> {
    private T element;
    private int value;

    public DistributedElement(T element) {
        this.element = element;
        value = Math.round(element.generate());
    }

    public T getElement() {
        return element;
    }

    public int getValue() {
        return value;
    }
}
