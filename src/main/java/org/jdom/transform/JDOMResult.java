package org.jdom.transform;

import java.io.IOException;
import javax.xml.transform.sax.SAXResult;
import org.jdom.Document;
import org.jdom.input.JDOMFactory;
import org.jdom.input.SAXHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JDOMResult extends SAXResult {
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMResult/feature";
    private Document result = null;
    private JDOMFactory factory = null;

    public JDOMResult() {
        DocumentBuilder builder = new DocumentBuilder(this);
        super.setHandler(builder);
        super.setLexicalHandler(builder);
    }

    public void setDocument(Document document) {
        this.result = document;
    }

    public Document getDocument() {
        return this.result;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setHandler(ContentHandler handler) {
    }

    public void setLexicalHandler(LexicalHandler handler) {
    }

    private class DocumentBuilder extends XMLFilterImpl implements LexicalHandler {
        private final JDOMResult this$0;
        private SAXHandler saxHandler;

        DocumentBuilder(JDOMResult this$0) {
            this.this$0 = this$0;
            this.saxHandler = null;
        }

        public void startDocument() throws SAXException {
            try {
                this.this$0.setDocument(null);
                this.saxHandler = new SAXHandler(this.this$0.getFactory());
                setContentHandler((ContentHandler) this.saxHandler);
                super.startDocument();
            } catch (IOException e) {
                throw new SAXException("SAXHandler allocation failure", e);
            }
        }

        public void endDocument() throws SAXException {
            this.this$0.setDocument(this.saxHandler.getDocument());
            super.endDocument();
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this.saxHandler.startDTD(name, publicId, systemId);
        }

        public void endDTD() throws SAXException {
            this.saxHandler.endDTD();
        }

        public void startEntity(String name) throws SAXException {
            this.saxHandler.startEntity(name);
        }

        public void endEntity(String name) throws SAXException {
            this.saxHandler.endEntity(name);
        }

        public void startCDATA() throws SAXException {
            this.saxHandler.startCDATA();
        }

        public void endCDATA() throws SAXException {
            this.saxHandler.endCDATA();
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            this.saxHandler.comment(ch, start, length);
        }
    }
}
