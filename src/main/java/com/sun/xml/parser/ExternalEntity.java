package com.sun.xml.parser;

import java.io.IOException;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class ExternalEntity extends EntityDecl {
    String systemId;
    String publicId;
    String notation;

    public ExternalEntity(Locator paramLocator) {
    }

    public InputSource getInputSource(EntityResolver paramEntityResolver) throws SAXException, IOException {
        InputSource inputSource = paramEntityResolver.resolveEntity(this.publicId, this.systemId);
        if (inputSource == null)
            inputSource = Resolver.createInputSource(new URL(this.systemId), false);
        return inputSource;
    }
}
