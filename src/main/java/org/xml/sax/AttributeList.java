package org.xml.sax;

public interface AttributeList {
    int getLength();

    String getName(int paramInt);

    String getType(int paramInt);

    String getType(String paramString);

    String getValue(int paramInt);

    String getValue(String paramString);
}
