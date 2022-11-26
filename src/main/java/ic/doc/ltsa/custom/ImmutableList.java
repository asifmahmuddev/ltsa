package ic.doc.ltsa.custom;

import java.util.Enumeration;

public class ImmutableList {
    ImmutableList next;
    Object item;

    private ImmutableList(ImmutableList paramImmutableList, Object paramObject) {
        this.next = paramImmutableList;
        this.item = paramObject;
    }

    public static ImmutableList add(ImmutableList paramImmutableList, Object paramObject) {
        return new ImmutableList(paramImmutableList, paramObject);
    }

    public static ImmutableList remove(ImmutableList paramImmutableList, Object paramObject) {
        if (paramImmutableList == null)
            return null;
        return paramImmutableList.remove(paramObject);
    }

    private ImmutableList remove(Object paramObject) {
        if (this.item == paramObject)
            return this.next;
        ImmutableList immutableList = remove(this.next, paramObject);
        if (immutableList == this.next)
            return this;
        return new ImmutableList(immutableList, this.item);
    }

    public static Enumeration elements(ImmutableList paramImmutableList) {
        return new ImmutableListEnumerator(paramImmutableList);
    }
}
