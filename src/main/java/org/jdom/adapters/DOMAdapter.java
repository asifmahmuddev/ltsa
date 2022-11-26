package org.jdom.adapters;

import java.io.File;
import java.io.InputStream;
import org.jdom.DocType;
import org.w3c.dom.Document;

public interface DOMAdapter {
    Document createDocument() throws Exception;

    Document createDocument(DocType paramDocType) throws Exception;

    Document getDocument(File paramFile, boolean paramBoolean) throws Exception;

    Document getDocument(InputStream paramInputStream, boolean paramBoolean) throws Exception;
}
