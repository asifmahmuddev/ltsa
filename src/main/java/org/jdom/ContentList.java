package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.jdom.filter.Filter;

class ContentList extends AbstractList implements Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: ContentList.java,v $ $Revision: 1.11 $ $Date: 2002/03/16 12:15:40 $ $Name: jdom_1_0_b8 $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    private static final int CREATE = 0;
    private static final int HASPREV = 1;
    private static final int HASNEXT = 2;
    private static final int PREV = 3;
    private static final int NEXT = 4;
    private static final int ADD = 5;
    private static final int REMOVE = 6;
    protected ArrayList list;
    protected Object parent;

    private ContentList() {
    }

    protected ContentList(Document document) {
        this.parent = document;
        ensureCapacity(5);
    }

    protected ContentList(Element parent) {
        this.parent = parent;
        ensureCapacity(5);
    }

    public void add(int index, Object obj) {
        if (obj instanceof Element) {
            add(index, (Element) obj);
        } else if (obj instanceof Text) {
            add(index, (Text) obj);
        } else if (obj instanceof Comment) {
            add(index, (Comment) obj);
        } else if (obj instanceof ProcessingInstruction) {
            add(index, (ProcessingInstruction) obj);
        } else if (obj instanceof CDATA) {
            add(index, (CDATA) obj);
        } else if (obj instanceof EntityRef) {
            add(index, (EntityRef) obj);
        } else {
            if (obj == null)
                throw new IllegalAddException("Cannot add null object");
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is of unrecognized type and cannot be added");
        }
    }

    protected void add(int index, Element element) {
        if (element == null)
            throw new IllegalAddException("Cannot add null object");
        if (element.getParent() != null)
            throw new IllegalAddException("The element already has an existing parent \"" + element.getParent().getQualifiedName() + "\"");
        if (element == this.parent)
            throw new IllegalAddException("The element cannot be added to itself");
        if (this.parent instanceof Element && ((Element) this.parent).isAncestor(element))
            throw new IllegalAddException("The element cannot be added as a descendent of itself");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        if (this.parent instanceof Document) {
            if (indexOfFirstElement() >= 0)
                throw new IllegalAddException("Cannot add a second root element, only one is allowed");
            element.setDocument((Document) this.parent);
        } else {
            element.setParent((Element) this.parent);
        }
        this.list.add(index, element);
        this.modCount++;
    }

    protected void add(int index, Comment comment) {
        if (comment == null)
            throw new IllegalAddException("Cannot add null object");
        if (comment.getParent() != null)
            throw new IllegalAddException("The comment already has an existing parent \"" + comment.getParent().getQualifiedName() + "\"");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        if (this.parent instanceof Document) {
            comment.setDocument((Document) this.parent);
        } else {
            comment.setParent((Element) this.parent);
        }
        this.list.add(index, comment);
        this.modCount++;
    }

    protected void add(int index, ProcessingInstruction pi) {
        if (pi == null)
            throw new IllegalAddException("Cannot add null object");
        if (pi.getParent() != null)
            throw new IllegalAddException("The PI already has an existing parent \"" + pi.getParent().getQualifiedName() + "\"");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        if (this.parent instanceof Document) {
            pi.setDocument((Document) this.parent);
        } else {
            pi.setParent((Element) this.parent);
        }
        this.list.add(index, pi);
        this.modCount++;
    }

    protected void add(int index, CDATA cdata) {
        if (cdata == null)
            throw new IllegalAddException("Cannot add null object");
        if (this.parent instanceof Document)
            throw new IllegalAddException("A CDATA is not allowed at the document root");
        if (cdata.getParent() != null)
            throw new IllegalAddException("The CDATA already has an existing parent \"" + cdata.getParent().getQualifiedName() + "\"");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        this.list.add(index, cdata);
        cdata.setParent((Element) this.parent);
        this.modCount++;
    }

    protected void add(int index, Text text) {
        if (text == null)
            throw new IllegalAddException("Cannot add null object");
        if (this.parent instanceof Document)
            throw new IllegalAddException("A Text not allowed at the document root");
        if (text.getParent() != null)
            throw new IllegalAddException("The Text already has an existing parent \"" + text.getParent().getQualifiedName() + "\"");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        this.list.add(index, text);
        text.setParent((Element) this.parent);
        this.modCount++;
    }

    protected void add(int index, EntityRef entity) {
        if (entity == null)
            throw new IllegalAddException("Cannot add null object");
        if (this.parent instanceof Document)
            throw new IllegalAddException("An EntityRef is not allowed at the document root");
        if (entity.getParent() != null)
            throw new IllegalAddException("The EntityRef already has an existing parent \"" + entity.getParent().getQualifiedName() + "\"");
        if (this.list == null)
            if (index == 0) {
                ensureCapacity(5);
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
            }
        this.list.add(index, entity);
        entity.setParent((Element) this.parent);
        this.modCount++;
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

    protected void clearAndSet(Collection collection) {
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
            for (int i = 0; i < old.size(); i++)
                removeParent(old.get(i));
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

    protected List getView(Filter filter) {
        return new FilterList(this, filter);
    }

    protected int indexOfFirstElement() {
        if (this.list != null)
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i) instanceof Element)
                    return i;
            }
        return -1;
    }

    public Object remove(int index) {
        if (this.list == null)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        Object old = this.list.get(index);
        removeParent(old);
        this.list.remove(index);
        this.modCount++;
        return old;
    }

    private void removeParent(Object obj) {
        if (obj instanceof Element) {
            Element element = (Element) obj;
            element.setParent(null);
        } else if (obj instanceof Text) {
            Text text = (Text) obj;
            text.setParent(null);
        } else if (obj instanceof Comment) {
            Comment comment = (Comment) obj;
            comment.setParent(null);
        } else if (obj instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) obj;
            pi.setParent(null);
        } else if (obj instanceof CDATA) {
            CDATA cdata = (CDATA) obj;
            cdata.setParent(null);
        } else if (obj instanceof EntityRef) {
            EntityRef entity = (EntityRef) obj;
            entity.setParent(null);
        } else {
            throw new IllegalArgumentException("Object '" + obj + "' unknown");
        }
    }

    public Object set(int index, Object obj) {
        if (this.list == null)
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + size());
        if (obj instanceof Element && this.parent instanceof Document) {
            int root = indexOfFirstElement();
            if (root >= 0 && root != index)
                throw new IllegalAddException("Cannot add a second root element, only one is allowed");
        }
        Object old = remove(index);
        try {
            add(index, obj);
        } catch (RuntimeException exception) {
            add(index, old);
            throw exception;
        }
        return old;
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

    private int getModCount() {
        return this.modCount;
    }

    class FilterList extends AbstractList {
        private final ContentList this$0;
        protected Filter filter;
        int count;
        int expected;

        FilterList(ContentList this$0, Filter filter) {
            this.this$0 = this$0;
            this.count = 0;
            this.expected = -1;
            this.filter = filter;
        }

        public void add(int index, Object obj) {
            if (this.filter.canAdd(obj)) {
                int adjusted = getAdjustedIndex(index);
                this.this$0.add(adjusted, obj);
                this.expected++;
                this.count++;
            } else {
                throw new IllegalAddException("Filter won't allow the " + obj.getClass().getName() + " '" + obj + "' to be added to the list");
            }
        }

        public Object get(int index) {
            int adjusted = getAdjustedIndex(index);
            return this.this$0.get(adjusted);
        }

        public Iterator iterator() {
            return new ContentList.FilterListIterator(this.this$0, this.filter, 0);
        }

        public ListIterator listIterator() {
            return new ContentList.FilterListIterator(this.this$0, this.filter, 0);
        }

        public ListIterator listIterator(int index) {
            return new ContentList.FilterListIterator(this.this$0, this.filter, index);
        }

        public Object remove(int index) {
            int adjusted = getAdjustedIndex(index);
            Object old = this.this$0.get(adjusted);
            if (this.filter.canRemove(old)) {
                old = this.this$0.remove(adjusted);
                this.expected++;
                this.count--;
            } else {
                throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
            }
            return old;
        }

        public Object set(int index, Object obj) {
            Object old = null;
            if (this.filter.canAdd(obj)) {
                int adjusted = getAdjustedIndex(index);
                old = this.this$0.get(adjusted);
                if (!this.filter.canRemove(old))
                    throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
                old = this.this$0.set(adjusted, obj);
                this.expected += 2;
            } else {
                throw new IllegalAddException("Filter won't allow index " + index + " to be set to " + obj.getClass().getName());
            }
            return old;
        }

        public int size() {
            if (this.expected == this.this$0.getModCount())
                return this.count;
            this.count = 0;
            for (int i = 0; i < this.this$0.size(); i++) {
                Object obj = this.this$0.list.get(i);
                if (this.filter.matches(obj))
                    this.count++;
            }
            this.expected = this.this$0.getModCount();
            return this.count;
        }

        private final int getAdjustedIndex(int index) {
            int adjusted = 0;
            for (int i = 0; i < this.this$0.list.size(); i++) {
                Object obj = this.this$0.list.get(i);
                if (this.filter.matches(obj)) {
                    if (index == adjusted)
                        return i;
                    adjusted++;
                }
            }
            if (index == adjusted)
                return this.this$0.list.size();
            return this.this$0.list.size() + 1;
        }
    }

    class FilterListIterator implements ListIterator {
        private final ContentList this$0;
        Filter filter;
        int lastOperation;
        int initialCursor;
        int cursor;
        int last;
        int expected;

        FilterListIterator(ContentList this$0, Filter filter, int start) {
            this.this$0 = this$0;
            this.filter = filter;
            this.initialCursor = initializeCursor(start);
            this.last = -1;
            this.expected = this$0.getModCount();
            this.lastOperation = 0;
        }

        public boolean hasNext() {
            // Byte code:
            //   0: aload_0
            //   1: invokespecial checkConcurrentModification : ()V
            //   4: aload_0
            //   5: getfield lastOperation : I
            //   8: tableswitch default -> 123, 0 -> 52, 1 -> 106, 2 -> 133, 3 -> 63, 4 -> 74, 5 -> 74, 6 -> 91
            //   52: aload_0
            //   53: aload_0
            //   54: getfield initialCursor : I
            //   57: putfield cursor : I
            //   60: goto -> 133
            //   63: aload_0
            //   64: aload_0
            //   65: getfield last : I
            //   68: putfield cursor : I
            //   71: goto -> 133
            //   74: aload_0
            //   75: aload_0
            //   76: aload_0
            //   77: getfield last : I
            //   80: iconst_1
            //   81: iadd
            //   82: invokespecial moveForward : (I)I
            //   85: putfield cursor : I
            //   88: goto -> 133
            //   91: aload_0
            //   92: aload_0
            //   93: aload_0
            //   94: getfield last : I
            //   97: invokespecial moveForward : (I)I
            //   100: putfield cursor : I
            //   103: goto -> 133
            //   106: aload_0
            //   107: aload_0
            //   108: aload_0
            //   109: getfield cursor : I
            //   112: iconst_1
            //   113: iadd
            //   114: invokespecial moveForward : (I)I
            //   117: putfield cursor : I
            //   120: goto -> 133
            //   123: new java/lang/IllegalStateException
            //   126: dup
            //   127: ldc 'Unknown operation'
            //   129: invokespecial <init> : (Ljava/lang/String;)V
            //   132: athrow
            //   133: aload_0
            //   134: getfield lastOperation : I
            //   137: ifeq -> 145
            //   140: aload_0
            //   141: iconst_2
            //   142: putfield lastOperation : I
            //   145: aload_0
            //   146: getfield cursor : I
            //   149: aload_0
            //   150: getfield this$0 : Lorg/jdom/ContentList;
            //   153: invokevirtual size : ()I
            //   156: if_icmpge -> 163
            //   159: iconst_1
            //   160: goto -> 164
            //   163: iconst_0
            //   164: ireturn
            // Line number table:
            //   Java source line number -> byte code offset
            //   #942	-> 0
            //   #944	-> 4
            //   #945	-> 52
            //   #946	-> 60
            //   #947	-> 63
            //   #948	-> 71
            //   #950	-> 74
            //   #951	-> 88
            //   #952	-> 91
            //   #953	-> 103
            //   #954	-> 106
            //   #955	-> 120
            //   #957	-> 123
            //   #960	-> 133
            //   #961	-> 140
            //   #96	-> 141
            //   #961	-> 142
            //   #964	-> 145
            // Local variable table:
            //   start	length	slot	name	descriptor
            //   0	165	0	this	Lorg/jdom/ContentList$FilterListIterator;
        }

        public Object next() {
            checkConcurrentModification();
            if (hasNext()) {
                this.last = this.cursor;
            } else {
                this.last = this.this$0.size();
                throw new NoSuchElementException();
            }
            this.lastOperation = 4;
            return this.this$0.get(this.last);
        }

        public boolean hasPrevious() {
            // Byte code:
            //   0: aload_0
            //   1: invokespecial checkConcurrentModification : ()V
            //   4: aload_0
            //   5: getfield lastOperation : I
            //   8: tableswitch default -> 134, 0 -> 52, 1 -> 144, 2 -> 106, 3 -> 89, 4 -> 123, 5 -> 123, 6 -> 89
            //   52: aload_0
            //   53: aload_0
            //   54: getfield initialCursor : I
            //   57: putfield cursor : I
            //   60: aload_0
            //   61: getfield cursor : I
            //   64: aload_0
            //   65: getfield this$0 : Lorg/jdom/ContentList;
            //   68: invokevirtual size : ()I
            //   71: if_icmplt -> 144
            //   74: aload_0
            //   75: aload_0
            //   76: aload_0
            //   77: getfield initialCursor : I
            //   80: invokespecial moveBackward : (I)I
            //   83: putfield cursor : I
            //   86: goto -> 144
            //   89: aload_0
            //   90: aload_0
            //   91: aload_0
            //   92: getfield last : I
            //   95: iconst_1
            //   96: isub
            //   97: invokespecial moveBackward : (I)I
            //   100: putfield cursor : I
            //   103: goto -> 144
            //   106: aload_0
            //   107: aload_0
            //   108: aload_0
            //   109: getfield cursor : I
            //   112: iconst_1
            //   113: isub
            //   114: invokespecial moveBackward : (I)I
            //   117: putfield cursor : I
            //   120: goto -> 144
            //   123: aload_0
            //   124: aload_0
            //   125: getfield last : I
            //   128: putfield cursor : I
            //   131: goto -> 144
            //   134: new java/lang/IllegalStateException
            //   137: dup
            //   138: ldc 'Unknown operation'
            //   140: invokespecial <init> : (Ljava/lang/String;)V
            //   143: athrow
            //   144: aload_0
            //   145: getfield lastOperation : I
            //   148: ifeq -> 156
            //   151: aload_0
            //   152: iconst_1
            //   153: putfield lastOperation : I
            //   156: aload_0
            //   157: getfield cursor : I
            //   160: ifge -> 167
            //   163: iconst_0
            //   164: goto -> 168
            //   167: iconst_1
            //   168: ireturn
            // Line number table:
            //   Java source line number -> byte code offset
            //   #990	-> 0
            //   #992	-> 4
            //   #993	-> 52
            //   #994	-> 60
            //   #995	-> 74
            //   #997	-> 86
            //   #999	-> 89
            //   #1000	-> 103
            //   #1001	-> 106
            //   #1002	-> 120
            //   #1004	-> 123
            //   #1005	-> 131
            //   #1007	-> 134
            //   #1010	-> 144
            //   #1011	-> 151
            //   #95	-> 152
            //   #1011	-> 153
            //   #1014	-> 156
            // Local variable table:
            //   start	length	slot	name	descriptor
            //   0	169	0	this	Lorg/jdom/ContentList$FilterListIterator;
        }

        public Object previous() {
            checkConcurrentModification();
            if (hasPrevious()) {
                this.last = this.cursor;
            } else {
                this.last = -1;
                throw new NoSuchElementException();
            }
            this.lastOperation = 3;
            return this.this$0.get(this.last);
        }

        public int nextIndex() {
            checkConcurrentModification();
            hasNext();
            int count = 0;
            for (int i = 0; i < this.this$0.size(); i++) {
                if (this.filter.matches(this.this$0.get(i))) {
                    if (i == this.cursor)
                        return count;
                    count++;
                }
            }
            this.expected = this.this$0.getModCount();
            return count;
        }

        public int previousIndex() {
            checkConcurrentModification();
            if (hasPrevious()) {
                int count = 0;
                for (int i = 0; i < this.this$0.size(); i++) {
                    if (this.filter.matches(this.this$0.get(i))) {
                        if (i == this.cursor)
                            return count;
                        count++;
                    }
                }
            }
            return -1;
        }

        public void add(Object obj) {
            checkConcurrentModification();
            if (this.filter.canAdd(obj)) {
                this.last++;
                this.this$0.add(this.last, obj);
            } else {
                throw new IllegalAddException("Filter won't allow add of " + obj.getClass().getName());
            }
            this.expected = this.this$0.getModCount();
            this.lastOperation = 5;
        }

        public void remove() {
            checkConcurrentModification();
            if (this.last < 0 || this.lastOperation == 6)
                throw new IllegalStateException("no preceeding call to prev() or next()");
            if (this.lastOperation == 5)
                throw new IllegalStateException("cannot call remove() after add()");
            Object old = this.this$0.get(this.last);
            if (this.filter.canRemove(old)) {
                this.this$0.remove(this.last);
            } else {
                throw new IllegalAddException("Filter won't allow " + old.getClass().getName() + " (index " + this.last + ") to be removed");
            }
            this.expected = this.this$0.getModCount();
            this.lastOperation = 6;
        }

        public void set(Object obj) {
            checkConcurrentModification();
            if (this.lastOperation == 5 || this.lastOperation == 6)
                throw new IllegalStateException("cannot call set() after add() or remove()");
            if (this.last < 0)
                throw new IllegalStateException("no preceeding call to prev() or next()");
            if (this.filter.canAdd(obj)) {
                Object old = this.this$0.get(this.last);
                if (!this.filter.canRemove(old))
                    throw new IllegalAddException("Filter won't allow " + old.getClass().getName() + " (index " + this.last + ") to be removed");
                this.this$0.set(this.last, obj);
            } else {
                throw new IllegalAddException("Filter won't allow index " + this.last + " to be set to " + obj.getClass().getName());
            }
            this.expected = this.this$0.getModCount();
        }

        private int initializeCursor(int start) {
            if (start < 0)
                throw new IndexOutOfBoundsException("Index: " + start);
            int count = 0;
            for (int i = 0; i < this.this$0.size(); i++) {
                Object obj = this.this$0.get(i);
                if (this.filter.matches(obj)) {
                    if (start == count)
                        return i;
                    count++;
                }
            }
            if (start > count)
                throw new IndexOutOfBoundsException("Index: " + start + " Size: " + count);
            return this.this$0.size();
        }

        private int moveForward(int start) {
            if (start < 0)
                start = 0;
            for (int i = start; i < this.this$0.size(); i++) {
                Object obj = this.this$0.get(i);
                if (this.filter.matches(obj))
                    return i;
            }
            return this.this$0.size();
        }

        private int moveBackward(int start) {
            if (start >= this.this$0.size())
                start = this.this$0.size() - 1;
            for (int i = start; i >= 0; i--) {
                Object obj = this.this$0.get(i);
                if (this.filter.matches(obj))
                    return i;
            }
            return -1;
        }

        private void checkConcurrentModification() {
            if (this.expected != this.this$0.getModCount())
                throw new ConcurrentModificationException();
        }
    }
}
