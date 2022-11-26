package org.xml.sax;

import java.io.IOException;

public interface EntityResolver {
    InputSource resolveEntity(String paramString1, String paramString2) throws SAXException, IOException;
}
