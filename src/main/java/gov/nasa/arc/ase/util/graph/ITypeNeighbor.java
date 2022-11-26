package gov.nasa.arc.ase.util.graph;

public class ITypeNeighbor extends Pair implements Comparable {
    public ITypeNeighbor(int paramInt, String paramString) {
        super(paramInt, paramString);
    }

    public int getColor() {
        return getValue();
    }

    public void setColor(int paramInt) {
        setValue(paramInt);
    }

    public String getTransition() {
        return (String) getElement();
    }

    public void setTransition(String paramString) {
        setElement(paramString);
    }

    public int compareTo(Object paramObject) {
        ITypeNeighbor iTypeNeighbor = (ITypeNeighbor) paramObject;
        int i = getTransition().compareTo(iTypeNeighbor.getTransition());
        if (i == 0) {
            if (getColor() < iTypeNeighbor.getColor())
                return -1;
            if (getColor() == iTypeNeighbor.getColor())
                return 0;
            if (getColor() > iTypeNeighbor.getColor())
                return 1;
        }
        return i;
    }
}
