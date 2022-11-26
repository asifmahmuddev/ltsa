package org.jdom.input;

import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public interface JDOMFactory {
    Attribute attribute(String paramString1, String paramString2);

    Attribute attribute(String paramString1, String paramString2, int paramInt);

    Attribute attribute(String paramString1, String paramString2, int paramInt, Namespace paramNamespace);

    Attribute attribute(String paramString1, String paramString2, Namespace paramNamespace);

    CDATA cdata(String paramString);

    Comment comment(String paramString);

    DocType docType(String paramString);

    DocType docType(String paramString1, String paramString2);

    DocType docType(String paramString1, String paramString2, String paramString3);

    Document document(Element paramElement);

    Document document(Element paramElement, DocType paramDocType);

    Element element(String paramString);

    Element element(String paramString1, String paramString2);

    Element element(String paramString1, String paramString2, String paramString3);

    Element element(String paramString, Namespace paramNamespace);

    EntityRef entityRef(String paramString);

    EntityRef entityRef(String paramString1, String paramString2, String paramString3);

    ProcessingInstruction processingInstruction(String paramString1, String paramString2);

    ProcessingInstruction processingInstruction(String paramString, Map paramMap);

    Text text(String paramString);
}
