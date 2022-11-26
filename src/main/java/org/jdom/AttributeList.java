package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class AttributeList extends AbstractList implements List, Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: AttributeList.java,v $ $Revision: 1.8 $ $Date: 2002/03/20 10:07:54 $ $Name: jdom_1_0_b8 $";
    private static final int INITIAL_ARRAY_SIZE = 3;
    protected ArrayList list;
    protected Element parent;

    private AttributeList() {
    }

    public AttributeList(Element parent) {
        this.parent = parent;
    }

    public boolean add(Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            int duplicate = indexOfDuplicate(attribute);
            if (duplicate < 0) {
                add(size(), attribute);
            } else {
                set(duplicate, attribute);
            }
        } else {
            if (obj == null)
                throw new IllegalAddException("Cannot add null attribute");
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        return true;
    }

    public void add(int index, Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            int duplicate = indexOfDuplicate(attribute);
            if (duplicate >= 0)
                throw new IllegalAddException("Cannot add duplicate attribute");
            add(index, attribute);
        } else {
            if (obj == null)
                throw new IllegalAddException("Cannot add null attribute");
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
        }
        this.modCount++;
    }

    protected void add(int index, Attribute attribute) {
        if (attribute.getParent() != null)
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null)
            throw new IllegalAddException(this.parent, attribute, reason);
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(3);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        this.list.add(index, attribute);
        attribute.setParent(this.parent);
    }

    public boolean addAll(Collection collection) {
        return addAll(size(), collection);
    }

    public boolean addAll(int index, Collection collection) {
        if (this.list == null && index != 0)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        if (collection == null || collection.size() == 0)
            return false;
        int count = 0;
        try {
            Iterator i = collection.iterator();
            while (i.hasNext()) {
                Object obj = i.next();
                add(index + count, obj);
                count++;
            }
        } catch (RuntimeException exception) {
            for (int i = 0; i < count; i++)
                remove(index + i);
            throw exception;
        }
        return true;
    }

    public void clear() {
        if (this.list != null) {
            for (int i = 0; i < this.list.size(); i++) {
                Attribute attribute = this.list.get(i);
                attribute.setParent(null);
            }
            this.list = null;
        }
        this.modCount++;
    }

    public void clearAndSet(Collection collection) {
        ArrayList old = this.list;
        this.list = null;
        if (collection != null && collection.size() != 0) {
            ensureCapacity(collection.size());
            try {
                addAll(0, collection);
            } catch (RuntimeException exception) {
                this.list = old;
                throw exception;
            }
        }
        if (old != null)
            for (int i = 0; i < old.size(); i++) {
                Attribute attribute = old.get(i);
                attribute.setParent(null);
            }
    }

    protected void ensureCapacity(int minCapacity) {
        if (this.list == null) {
            this.list = new ArrayList(minCapacity);
        } else {
            this.list.ensureCapacity(minCapacity);
        }
    }

    public Object get(int index) {
        if (this.list == null)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        return this.list.get(index);
    }

    protected Object get(String name, Namespace namespace) {
        int index = indexOf(name, namespace);
        if (index < 0)
            return null;
        return this.list.get(index);
    }

    protected int indexOf(String name, Namespace namespace) {
        String uri = namespace.getURI();
        if (this.list != null)
            for (int i = 0; i < this.list.size(); i++) {
                Attribute old = this.list.get(i);
                String oldURI = old.getNamespaceURI();
                String oldName = old.getName();
                if (oldURI.equals(uri) && oldName.equals(name))
                    return i;
            }
        return -1;
    }

    public Object remove(int index) {
        if (this.list == null)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        Attribute old = this.list.get(index);
        old.setParent(null);
        this.modCount++;
        this.list.remove(index);
        return old;
    }

    protected boolean remove(String name, Namespace namespace) {
        int index = indexOf(name, namespace);
        if (index < 0)
            return false;
        remove(index);
        return true;
    }

    public Object set(int index, Object obj) {
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            int duplicate = indexOfDuplicate(attribute);
            if (duplicate >= 0 && duplicate != index)
                throw new IllegalAddException("Cannot set duplicate attribute");
            return set(index, attribute);
        }
        if (obj == null)
            throw new IllegalAddException("Cannot add null attribute");
        throw new IllegalAddException("Class " + obj.getClass().getName() + " is not an attribute");
    }

    protected Object set(int index, Attribute attribute) {
        if (this.list == null)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        if (attribute.getParent() != null)
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null)
            throw new IllegalAddException(this.parent, attribute, reason);
        Attribute old = this.list.get(index);
        old.setParent(null);
        this.list.set(index, attribute);
        return old;
    }

    private int indexOfDuplicate(Attribute attribute) {
        int duplicate = -1;
        String name = attribute.getName();
        Namespace namespace = attribute.getNamespace();
        duplicate = indexOf(name, namespace);
        return duplicate;
    }

    public int size() {
        if (this.list == null)
            return 0;
        return this.list.size();
    }

    public String toString() {
        if (this.list != null && this.list.size() > 0)
            return this.list.toString();
        return "[]";
    }
}
