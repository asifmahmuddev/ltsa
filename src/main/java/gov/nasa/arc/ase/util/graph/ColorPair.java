package gov.nasa.arc.ase.util.graph;

import java.util.TreeSet;

public class ColorPair extends Pair implements Comparable {
    public ColorPair(int paramInt, TreeSet paramTreeSet) {
        super(paramInt, paramTreeSet);
    }

    public int getColor() {
        return getValue();
    }

    public void setColor(int paramInt) {
        setValue(paramInt);
    }

    public TreeSet getIMaxSet() {
        return (TreeSet) getElement();
    }

    public void setIMaxSet(TreeSet paramTreeSet) {
        setElement(paramTreeSet);
    }

    public boolean equals(Object paramObject) {
        ColorPair colorPair = (ColorPair) paramObject;
        TreeSet treeSet = colorPair.getIMaxSet();
        if (getIMaxSet().size() < treeSet.size())
            return false;
        if (getIMaxSet().size() > treeSet.size())
            return false;
        if (getColor() != colorPair.getColor())
            return false;
        byte b = 0;
        for (ITypeNeighbor iTypeNeighbor : getIMaxSet()) {
            Object[] arrayOfObject = treeSet.toArray();
            int i = iTypeNeighbor.compareTo(arrayOfObject[b]);
            if (i < 0 || i > 0)
                return false;
            b++;
        }
        return true;
    }

    public int compareTo(Object paramObject) {
        ColorPair colorPair = (ColorPair) paramObject;
        TreeSet treeSet = colorPair.getIMaxSet();
        if (getIMaxSet().size() < treeSet.size())
            return -1;
        if (getIMaxSet().size() > treeSet.size())
            return 1;
        byte b = 0;
        for (ITypeNeighbor iTypeNeighbor : getIMaxSet()) {
            Object[] arrayOfObject = treeSet.toArray();
            int i = iTypeNeighbor.compareTo(arrayOfObject[b]);
            if (i < 0 || i > 0)
                return i;
            b++;
        }
        if (getColor() < colorPair.getColor())
            return -1;
        if (getColor() > colorPair.getColor())
            return 1;
        return 0;
    }
}
