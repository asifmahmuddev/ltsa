package com.sun.xml.parser;

import java.util.Vector;

final class AttributeListImpl implements AttributeListEx {
    private Vector names = new Vector();
    private Vector types = new Vector();
    private Vector values = new Vector();
    private Vector specified = new Vector();
    private Vector defaults = new Vector();
    private String idAttributeName;

    public void clear() {
        this.names.removeAllElements();
        this.types.removeAllElements();
        this.values.removeAllElements();
        this.specified.removeAllElements();
        this.defaults.removeAllElements();
    }

    public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean) {
        this.names.addElement(paramString1);
        this.types.addElement(paramString2);
        this.values.addElement(paramString3);
        this.defaults.addElement(paramString4);
        this.specified.addElement(paramBoolean ? Boolean.TRUE : null);
    }

    public int getLength() {
        return this.names.size();
    }

    public String getName(int paramInt) {
        try {
            if (paramInt < 0)
                return null;
            return this.names.elementAt(paramInt);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return null;
        }
    }

    public boolean isSpecified(int paramInt) {
        Boolean bool = (Boolean) this.specified.elementAt(paramInt);
        return !(bool != Boolean.TRUE);
    }

    public String getDefault(int paramInt) {
        try {
            if (paramInt < 0)
                return null;
            return this.defaults.elementAt(paramInt);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return null;
        }
    }

    public String getType(int paramInt) {
        try {
            if (paramInt < 0)
                return null;
            return this.types.elementAt(paramInt);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return null;
        }
    }

    public String getType(String paramString) {
        return getType(this.names.indexOf(paramString));
    }

    public String getValue(int paramInt) {
        try {
            if (paramInt < 0)
                return null;
            return this.values.elementAt(paramInt);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return null;
        }
    }

    public String getValue(String paramString) {
        return getValue(this.names.indexOf(paramString));
    }

    public String getIdAttributeName() {
        return this.idAttributeName;
    }

    void setIdAttributeName(String paramString) {
        this.idAttributeName = paramString;
    }
}
