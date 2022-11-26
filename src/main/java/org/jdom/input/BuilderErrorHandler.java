package org.jdom.input;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class BuilderErrorHandler implements ErrorHandler {
    private static final String CVS_ID = "@(#) $RCSfile: BuilderErrorHandler.java,v $ $Revision: 1.7 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public void warning(SAXParseException exception) throws SAXException {
    }

    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
}
