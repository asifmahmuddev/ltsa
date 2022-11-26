package gov.nasa.arc.ase.util.graph;

public class Pair {
    int value;
    Object element;

    public Pair(int paramInt, Object paramObject) {
        this.value = paramInt;
        this.element = paramObject;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int paramInt) {
        this.value = paramInt;
    }

    public Object getElement() {
        return this.element;
    }

    public void setElement(Object paramObject) {
        this.element = paramObject;
    }

    public String toString() {
        return "(" + this.value + ", " + this.element.toString() + ")";
    }
}
