package org.xml.sax;

import java.io.IOException;
import java.util.Locale;

public interface Parser {
    void parse(String paramString) throws SAXException, IOException;

    void parse(InputSource paramInputSource) throws SAXException, IOException;

    void setDTDHandler(DTDHandler paramDTDHandler);

    void setDocumentHandler(DocumentHandler paramDocumentHandler);

    void setEntityResolver(EntityResolver paramEntityResolver);

    void setErrorHandler(ErrorHandler paramErrorHandler);

    void setLocale(Locale paramLocale) throws SAXException;
}
