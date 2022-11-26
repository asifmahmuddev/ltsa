package org.jdom.transform;

import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.sax.SAXSource;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.SAXOutputter;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class JDOMSource extends SAXSource {
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMSource/feature";
    private XMLFilter xmlFilter = null;

    public JDOMSource(Document source) {
        setDocument(source);
    }

    public void setDocument(Document source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public Document getDocument() {
        return ((JDOMInputSource) getInputSource()).getDocument();
    }

    public void setInputSource(InputSource inputSource) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void setXMLReader(XMLReader reader) throws UnsupportedOperationException {
        if (reader instanceof XMLFilter) {
            this.xmlFilter = (XMLFilter) reader;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public XMLReader getXMLReader() {
        XMLReader documentReader = new DocumentReader();
        if (this.xmlFilter != null) {
            XMLFilter root = this.xmlFilter;
            while (root.getParent() instanceof XMLFilter)
                root = (XMLFilter) root.getParent();
            root.setParent(documentReader);
            documentReader = this.xmlFilter;
        }
        return documentReader;
    }

    private static class JDOMInputSource extends InputSource {
        private Document document = null;

        public JDOMInputSource(Document source) {
            setDocument(source);
        }

        public void setDocument(Document source) {
            if (source == null)
                throw new NullPointerException("source");
            this.document = source;
        }

        public Document getDocument() {
            return this.document;
        }

        public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public Reader getCharacterStream() {
            Document doc = getDocument();
            Reader reader = null;
            if (doc != null)
                reader = new StringReader((new XMLOutputter()).outputString(doc));
            return reader;
        }
    }

    private static class DocumentReader extends SAXOutputter implements XMLReader {
        public void parse(String systemId) throws SAXNotSupportedException {
            throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
        }

        public void parse(InputSource input) throws SAXException {
            if (input instanceof JDOMSource.JDOMInputSource) {
                try {
                    output(((JDOMSource.JDOMInputSource) input).getDocument());
                } catch (JDOMException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            } else {
                throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
            }
        }
    }
}
