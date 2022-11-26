package org.jdom.filter;

public class ContentFilter implements Filter {
    private static final String CVS_ID = "@(#) $RCSfile: ContentFilter.java,v $ $Revision: 1.1 $ $Date: 2002/03/12 07:00:50 $ $Name: jdom_1_0_b8 $";
    public static final int ELEMENT = 1;
    public static final int CDATA = 2;
    public static final int TEXT = 4;
    public static final int COMMENT = 8;
    public static final int PI = 16;
    public static final int ENTITYREF = 32;
    public static final int DOCUMENT = 64;
    protected int filterMask;

    public ContentFilter() {
        setDefaultMask();
    }

    public ContentFilter(boolean allVisible) {
        if (allVisible) {
            setDefaultMask();
        } else {
            this.filterMask &= this.filterMask ^ 0xFFFFFFFF;
        }
    }

    public ContentFilter(int mask) {
        setFilterMask(mask);
    }

    public int getFilterMask() {
        return this.filterMask;
    }

    public void setFilterMask(int mask) {
        setDefaultMask();
        this.filterMask &= mask;
    }

    public void setDefaultMask() {
        this.filterMask = 127;
    }

    public void setDocumentContent() {
        this.filterMask = 25;
    }

    public void setElementContent() {
        this.filterMask = 63;
    }

    public void setElementVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x1;
        } else {
            this.filterMask &= 0xFFFFFFFE;
        }
    }

    public void setCDATAVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x2;
        } else {
            this.filterMask &= 0xFFFFFFFD;
        }
    }

    public void setTextVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x4;
        } else {
            this.filterMask &= 0xFFFFFFFB;
        }
    }

    public void setCommentVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x8;
        } else {
            this.filterMask &= 0xFFFFFFF7;
        }
    }

    public void setPIVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x10;
        } else {
            this.filterMask &= 0xFFFFFFEF;
        }
    }

    public void setEntityRefVisible(boolean visible) {
        if (visible) {
            this.filterMask |= 0x20;
        } else {
            this.filterMask &= 0xFFFFFFDF;
        }
    }

    public boolean canAdd(Object obj) {
        return matches(obj);
    }

    public boolean canRemove(Object obj) {
        return matches(obj);
    }

    public boolean matches(Object obj) {
        if (obj instanceof org.jdom.Element)
            return !((this.filterMask & 0x1) == 0);
        if (obj instanceof org.jdom.CDATA)
            return !((this.filterMask & 0x2) == 0);
        if (obj instanceof org.jdom.Text)
            return !((this.filterMask & 0x4) == 0);
        if (obj instanceof org.jdom.Comment)
            return !((this.filterMask & 0x8) == 0);
        if (obj instanceof org.jdom.ProcessingInstruction)
            return !((this.filterMask & 0x10) == 0);
        if (obj instanceof org.jdom.EntityRef)
            return !((this.filterMask & 0x20) == 0);
        if (obj instanceof org.jdom.Document)
            return !((this.filterMask & 0x40) == 0);
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ContentFilter) {
            ContentFilter filter = (ContentFilter) obj;
            return !(this.filterMask != filter.filterMask);
        }
        return false;
    }
}
