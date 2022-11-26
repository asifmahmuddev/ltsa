package com.sun.xml.parser;

import com.sun.xml.util.XmlNames;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidatingParser extends Parser {
    private SimpleHashtable ids = new SimpleHashtable();
    private final EmptyValidator EMPTY;

    public ValidatingParser(boolean paramBoolean) {
        this();
        if (paramBoolean)
            setErrorHandler(new HandlerBase() {
                public void error(SAXParseException param1SAXParseException) throws SAXException {
                    throw param1SAXParseException;
                }
            });
    }

    void afterRoot() throws SAXException {
        Enumeration enumeration = this.ids.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            Boolean bool = (Boolean) this.ids.get(str);
            if (Boolean.FALSE == bool)
                error("V-024", new Object[]{str});
        }
    }

    void afterDocument() {
        this.ids.clear();
    }

    void validateAttributeSyntax(AttributeDecl paramAttributeDecl, String paramString) throws SAXException {
        if (paramAttributeDecl.type == "ID") {
            if (!XmlNames.isName(paramString))
                error("V-025", new Object[]{paramString});
            Boolean bool = (Boolean) this.ids.getNonInterned(paramString);
            if (bool == null || bool.equals(Boolean.FALSE)) {
                this.ids.put(paramString.intern(), Boolean.TRUE);
            } else {
                error("V-026", new Object[]{paramString});
            }
        } else if (paramAttributeDecl.type == "IDREF") {
            if (!XmlNames.isName(paramString))
                error("V-027", new Object[]{paramString});
            Boolean bool = (Boolean) this.ids.getNonInterned(paramString);
            if (bool == null)
                this.ids.put(paramString.intern(), Boolean.FALSE);
        } else if (paramAttributeDecl.type == "IDREFS") {
            StringTokenizer stringTokenizer = new StringTokenizer(paramString);
            boolean bool = false;
            while (stringTokenizer.hasMoreTokens()) {
                paramString = stringTokenizer.nextToken();
                if (!XmlNames.isName(paramString))
                    error("V-027", new Object[]{paramString});
                Boolean bool1 = (Boolean) this.ids.getNonInterned(paramString);
                if (bool1 == null)
                    this.ids.put(paramString.intern(), Boolean.FALSE);
                bool = true;
            }
            if (!bool)
                error("V-039", null);
        } else if (paramAttributeDecl.type == "NMTOKEN") {
            if (!XmlNames.isNmtoken(paramString))
                error("V-028", new Object[]{paramString});
        } else if (paramAttributeDecl.type == "NMTOKENS") {
            StringTokenizer stringTokenizer = new StringTokenizer(paramString);
            boolean bool = false;
            while (stringTokenizer.hasMoreTokens()) {
                paramString = stringTokenizer.nextToken();
                if (!XmlNames.isNmtoken(paramString))
                    error("V-028", new Object[]{paramString});
                bool = true;
            }
            if (!bool)
                error("V-032", null);
        } else if (paramAttributeDecl.type == "ENUMERATION") {
            for (byte b = 0; b < paramAttributeDecl.values.length; b++) {
                if (paramString.equals(paramAttributeDecl.values[b]))
                    return;
            }
            error("V-029", new Object[]{paramString});
        } else if (paramAttributeDecl.type == "NOTATION") {
            for (byte b = 0; b < paramAttributeDecl.values.length; b++) {
                if (paramString.equals(paramAttributeDecl.values[b]))
                    return;
            }
            error("V-030", new Object[]{paramString});
        } else if (paramAttributeDecl.type == "ENTITY") {
            if (!isUnparsedEntity(paramString))
                error("V-031", new Object[]{paramString});
        } else if (paramAttributeDecl.type == "ENTITIES") {
            StringTokenizer stringTokenizer = new StringTokenizer(paramString);
            boolean bool = false;
            while (stringTokenizer.hasMoreTokens()) {
                paramString = stringTokenizer.nextToken();
                if (!isUnparsedEntity(paramString))
                    error("V-031", new Object[]{paramString});
                bool = true;
            }
            if (!bool)
                error("V-040", null);
        } else if (paramAttributeDecl.type != "CDATA") {
            throw new InternalError(paramAttributeDecl.type);
        }
    }

    ContentModel newContentModel(String paramString) {
        return new ContentModel(paramString);
    }

    ContentModel newContentModel(char paramChar, ContentModel paramContentModel) {
        return new ContentModel(paramChar, paramContentModel);
    }

    ElementValidator newValidator(ElementDecl paramElementDecl) {
        if (paramElementDecl.validator != null)
            return paramElementDecl.validator;
        if (paramElementDecl.model != null)
            return new ChildrenValidator(this, paramElementDecl);
        if (paramElementDecl.contentType == null || paramElementDecl.contentType == "ANY") {
            paramElementDecl.validator = ElementValidator.ANY;
        } else if (paramElementDecl.contentType == "EMPTY") {
            paramElementDecl.validator = this.EMPTY;
        } else {
            paramElementDecl.validator = new MixedValidator(this, paramElementDecl);
        }
        return paramElementDecl.validator;
    }

    public ValidatingParser() {
        this.EMPTY = new EmptyValidator(this);
        setIsValidating(true);
    }

    class EmptyValidator extends ElementValidator {
        private final ValidatingParser this$0;

        EmptyValidator(ValidatingParser this$0) {
            this.this$0 = this$0;
        }

        public void consume(String param1String) throws SAXException {
            this.this$0.error("V-033", null);
        }

        public void text() throws SAXException {
            this.this$0.error("V-033", null);
        }
    }

    class MixedValidator extends ElementValidator {
        private final ValidatingParser this$0;
        private ElementDecl element;

        MixedValidator(ValidatingParser this$0, ElementDecl param1ElementDecl) {
            this.this$0 = this$0;
            this.element = param1ElementDecl;
        }

        public void consume(String param1String) throws SAXException {
            String str = this.element.contentType;
            int i = 8;
            while ((i = str.indexOf(param1String, i + 1)) >= 9) {
                if (str.charAt(i - 1) == '|') {
                    char c = str.charAt(i + param1String.length());
                    if (c == '|' || c == ')')
                        return;
                }
            }
            this.this$0.error("V-034", new Object[]{this.element.name, param1String, str});
        }
    }

    class ChildrenValidator extends ElementValidator {
        private final ValidatingParser this$0;
        private ContentModelState state;
        private String name;

        ChildrenValidator(ValidatingParser this$0, ElementDecl param1ElementDecl) {
            this.this$0 = this$0;
            this.state = new ContentModelState(param1ElementDecl.model);
            this.name = param1ElementDecl.name;
        }

        public void consume(String param1String) throws SAXException {
            if (this.state == null) {
                this.this$0.error("V-035", new Object[]{this.name, param1String});
            } else {
                try {
                    this.state = this.state.advance(param1String);
                } catch (EndOfInputException endOfInputException) {
                    this.this$0.error("V-036", new Object[]{this.name, param1String});
                }
            }
        }

        public void text() throws SAXException {
            this.this$0.error("V-037", new Object[]{this.name});
        }

        public void done() throws SAXException {
            if (this.state != null && !this.state.terminate())
                this.this$0.error("V-038", new Object[]{this.name});
        }
    }

    private boolean isUnparsedEntity(String paramString) {
        Object object = this.entities.getNonInterned(paramString);
        if (object == null || !(object instanceof ExternalEntity))
            return false;
        return !(((ExternalEntity) object).notation == null);
    }
}
