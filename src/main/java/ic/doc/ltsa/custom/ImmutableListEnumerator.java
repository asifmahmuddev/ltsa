package ic.doc.ltsa.custom;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class ImmutableListEnumerator implements Enumeration {
    private ImmutableList current;

    ImmutableListEnumerator(ImmutableList paramImmutableList) {
        this.current = paramImmutableList;
    }

    public boolean hasMoreElements() {
        return (this.current != null);
    }

    public Object nextElement() {
        if (this.current != null) {
            Object object = this.current.item;
            this.current = this.current.next;
            return object;
        }
        throw new NoSuchElementException("ImmutableListEnumerator");
    }
}
