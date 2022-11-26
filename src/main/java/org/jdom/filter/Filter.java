package org.jdom.filter;

public interface Filter {
    boolean canAdd(Object paramObject);

    boolean canRemove(Object paramObject);

    boolean matches(Object paramObject);
}
