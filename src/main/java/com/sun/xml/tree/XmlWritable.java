package com.sun.xml.tree;

import java.io.IOException;

public interface XmlWritable {
    void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException;

    void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException;
}
