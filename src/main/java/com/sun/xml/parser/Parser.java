package com.sun.xml.parser;

import com.sun.xml.util.MessageCatalog;
import com.sun.xml.util.XmlChars;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Parser implements Parser {
    private InputEntity in;
    private AttributeListImpl attTmp;
    private StringBuffer strTmp;
    private char[] nameTmp;
    private NameCache nameCache;

    static Class class$(String paramString) {
        try {
            return Class.forName(paramString);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private char[] charTmp = new char[2];
    private boolean isValidating = false;
    private boolean fastStandalone = false;
    private boolean isInAttribute = false;
    private boolean inExternalPE;
    private boolean doLexicalPE;
    private boolean donePrologue;
    private boolean isStandalone;
    private String rootElementName;
    private boolean ignoreDeclarations;
    private SimpleHashtable elements = new SimpleHashtable(47);
    private SimpleHashtable params = new SimpleHashtable(7);
    Hashtable notations = new Hashtable(7);
    SimpleHashtable entities = new SimpleHashtable(17);
    private DocumentHandler docHandler;
    private DTDHandler dtdHandler;
    private EntityResolver resolver;
    private ErrorHandler errHandler;
    private Locale locale;
    private Locator locator;
    private DtdEventListener dtdListener;
    private LexicalEventListener lexicalListener;
    private static final boolean supportValidation = true;
    static final String strANY = "ANY";
    static final String strEMPTY = "EMPTY";

    public Parser() {
        this.locator = new DocLocator(this);
        setHandlers();
    }

    public void setLocale(Locale paramLocale) throws SAXException {
        if (paramLocale != null && !messages.isLocaleSupported(paramLocale.toString()))
            throw new SAXException(messages.getMessage(this.locale, "P-078", new Object[]{paramLocale}));
        this.locale = paramLocale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Locale chooseLocale(String[] paramArrayOfString) throws SAXException {
        Locale locale = messages.chooseLocale(paramArrayOfString);
        if (locale != null)
            setLocale(locale);
        return locale;
    }

    public void setEntityResolver(EntityResolver paramEntityResolver) {
        this.resolver = paramEntityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.resolver;
    }

    public void setDTDHandler(DTDHandler paramDTDHandler) {
        if (paramDTDHandler == null)
            paramDTDHandler = defaultHandler;
        this.dtdHandler = paramDTDHandler;
        if (paramDTDHandler instanceof DtdEventListener) {
            this.dtdListener = (DtdEventListener) paramDTDHandler;
        } else {
            this.dtdListener = defaultHandler;
        }
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setDocumentHandler(DocumentHandler paramDocumentHandler) {
        if (paramDocumentHandler == null)
            paramDocumentHandler = defaultHandler;
        this.docHandler = paramDocumentHandler;
        if (paramDocumentHandler instanceof LexicalEventListener) {
            this.lexicalListener = (LexicalEventListener) paramDocumentHandler;
        } else {
            this.lexicalListener = defaultHandler;
        }
    }

    public DocumentHandler getDocumentHandler() {
        return this.docHandler;
    }

    public void setErrorHandler(ErrorHandler paramErrorHandler) {
        this.errHandler = paramErrorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errHandler;
    }

    public void parse(InputSource paramInputSource) throws SAXException, IOException {
        init();
        parseInternal(paramInputSource);
    }

    public void parse(String paramString) throws SAXException, IOException {
        init();
        InputSource inputSource = this.resolver.resolveEntity(null, paramString);
        if (inputSource == null) {
            inputSource = Resolver.createInputSource(new URL(paramString), false);
        } else if (inputSource.getSystemId() == null) {
            warning("P-065", null);
            inputSource.setSystemId(paramString);
        }
        parseInternal(inputSource);
    }

    public void setFastStandalone(boolean paramBoolean) {
        this.fastStandalone = !(!paramBoolean || this.isValidating);
    }

    public boolean isFastStandalone() {
        return this.fastStandalone;
    }

    public void pushInputBuffer(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
        if (paramInt2 <= 0)
            return;
        if (paramInt1 != 0 || paramInt2 != paramArrayOfchar.length) {
            char[] arrayOfChar = new char[paramInt2];
            System.arraycopy(paramArrayOfchar, paramInt1, arrayOfChar, 0, paramInt2);
            paramArrayOfchar = arrayOfChar;
        }
        pushReader(paramArrayOfchar, null, false);
    }

    void setIsValidating(boolean paramBoolean) {
        this.isValidating = paramBoolean;
        if (paramBoolean)
            this.fastStandalone = false;
    }

    private void init() {
        this.in = null;
        this.attTmp = new AttributeListImpl();
        this.strTmp = new StringBuffer();
        this.nameTmp = new char[20];
        this.nameCache = new NameCache();
        this.isStandalone = false;
        this.rootElementName = null;
        this.isInAttribute = false;
        this.inExternalPE = false;
        this.doLexicalPE = false;
        this.donePrologue = false;
        this.entities.clear();
        this.notations.clear();
        this.params.clear();
        this.elements.clear();
        this.ignoreDeclarations = false;
        builtin("amp", "&#38;");
        builtin("lt", "&#60;");
        builtin("gt", ">");
        builtin("quot", "\"");
        builtin("apos", "'");
        if (this.locale == null)
            this.locale = Locale.getDefault();
        if (this.resolver == null)
            this.resolver = new Resolver();
        setHandlers();
    }

    private static final ListenerBase defaultHandler = new ListenerBase();
    private static final String XmlLang = "xml:lang";

    private void setHandlers() {
        if (this.dtdHandler == null)
            this.dtdHandler = defaultHandler;
        if (this.dtdListener == null)
            this.dtdListener = defaultHandler;
        if (this.errHandler == null)
            this.errHandler = defaultHandler;
        if (this.docHandler == null)
            this.docHandler = defaultHandler;
        if (this.lexicalListener == null)
            this.lexicalListener = defaultHandler;
    }

    private void builtin(String paramString1, String paramString2) {
        InternalEntity internalEntity = new InternalEntity(paramString1, paramString2.toCharArray());
        this.entities.put(paramString1, internalEntity);
    }

    private void parseInternal(InputSource paramInputSource) throws SAXException, IOException {
        if (paramInputSource == null)
            fatal("P-000");
        try {
            this.in = InputEntity.getInputEntity(this.errHandler, this.locale);
            this.in.init(paramInputSource, (String) null, (InputEntity) null, false);
            this.docHandler.setDocumentLocator(this.locator);
            this.docHandler.startDocument();
            maybeXmlDecl();
            maybeMisc(false);
            if (!maybeDoctypeDecl() && this.isValidating)
                warning("V-001", null);
            maybeMisc(false);
            this.donePrologue = true;
            if (!this.in.peekc('<') || !maybeElement(null))
                fatal("P-067");
            afterRoot();
            maybeMisc(true);
            if (!this.in.isEOF())
                fatal("P-001", new Object[]{Integer.toHexString(getc())});
            this.docHandler.endDocument();
        } catch (EndOfInputException endOfInputException) {
            if (!this.in.isDocument()) {
                String str = this.in.getName();
                while (true) {
                    this.in = this.in.pop();
                    if (!this.in.isInternal()) {
                        fatal("P-002", new Object[]{str}, endOfInputException);
                        return;
                    }
                }
            }
            fatal("P-003", null, endOfInputException);
        } catch (RuntimeException runtimeException) {
            throw new SAXParseException((runtimeException.getMessage() != null) ? runtimeException.getMessage() : runtimeException.getClass().getName(), this.locator.getPublicId(),
                this.locator.getSystemId(), this.locator.getLineNumber(), this.locator.getColumnNumber(), runtimeException);
        } finally {
            this.strTmp = null;
            this.attTmp = null;
            this.nameTmp = null;
            this.nameCache = null;
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
            this.params.clear();
            this.entities.clear();
            this.notations.clear();
            this.elements.clear();
            afterDocument();
        }
    }

    void afterRoot() throws SAXException {
    }

    void afterDocument() {
    }

    private void whitespace(String paramString) throws IOException, SAXException {
        if (!maybeWhitespace())
            fatal("P-004", new Object[]{messages.getMessage(this.locale, paramString)});
    }

    private boolean maybeWhitespace() throws IOException, SAXException {
        if (!this.inExternalPE || !this.doLexicalPE)
            return this.in.maybeWhitespace();
        char c = getc();
        boolean bool = false;
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            bool = true;
            if (this.in.isEOF() && !this.in.isInternal())
                return bool;
            c = getc();
        }
        ungetc();
        return bool;
    }

    private String maybeGetName() throws IOException, SAXException {
        NameCacheEntry nameCacheEntry = maybeGetNameCacheEntry();
        return (nameCacheEntry == null) ? null : nameCacheEntry.name;
    }

    private NameCacheEntry maybeGetNameCacheEntry() throws IOException, SAXException {
        char c = getc();
        if (!XmlChars.isLetter(c) && c != ':' && c != '_') {
            ungetc();
            return null;
        }
        return nameCharString(c);
    }

    private String getNmtoken() throws SAXException, IOException {
        char c = getc();
        if (!XmlChars.isNameChar(c))
            fatal("P-006", new Object[]{new Character(c)});
        return (nameCharString(c)).name;
    }

    private NameCacheEntry nameCharString(char paramChar) throws IOException, SAXException {
        byte b = 1;
        this.nameTmp[0] = paramChar;
        while ((paramChar = this.in.getNameChar()) != '\000') {
            if (b >= this.nameTmp.length) {
                char[] arrayOfChar = new char[this.nameTmp.length + 10];
                System.arraycopy(this.nameTmp, 0, arrayOfChar, 0, this.nameTmp.length);
                this.nameTmp = arrayOfChar;
            }
            this.nameTmp[b++] = paramChar;
        }
        return this.nameCache.lookupEntry(this.nameTmp, b);
    }

    private void parseLiteral(boolean paramBoolean) throws IOException, SAXException {
        char c = getc();
        InputEntity inputEntity = this.in;
        if (c != '\'' && c != '"')
            fatal("P-007");
        this.isInAttribute = paramBoolean ^ true;
        this.strTmp = new StringBuffer();
        while (true) {
            if (this.in != inputEntity && this.in.isEOF()) {
                this.in = this.in.pop();
                continue;
            }
            char c1;
            if ((c1 = getc()) != c || this.in != inputEntity) {
                if (c1 == '&') {
                    String str = maybeGetName();
                    if (str != null) {
                        nextChar(';', "F-020", str);
                        if (paramBoolean) {
                            this.strTmp.append('&');
                            this.strTmp.append(str);
                            this.strTmp.append(';');
                            continue;
                        }
                        expandEntityInLiteral(str, this.entities, paramBoolean);
                        continue;
                    }
                    if ((c1 = getc()) == '#') {
                        int i = parseCharNumber();
                        if (i > 65535) {
                            i = surrogatesToCharTmp(i);
                            this.strTmp.append(this.charTmp[0]);
                            if (i == 2)
                                this.strTmp.append(this.charTmp[1]);
                            continue;
                        }
                        this.strTmp.append((char) i);
                        continue;
                    }
                    fatal("P-009");
                    continue;
                }
                if (c1 == '%' && paramBoolean) {
                    String str = maybeGetName();
                    if (str != null) {
                        nextChar(';', "F-021", str);
                        if (this.inExternalPE) {
                            expandEntityInLiteral(str, this.params, paramBoolean);
                            continue;
                        }
                        fatal("P-010", new Object[]{str});
                        continue;
                    }
                    fatal("P-011");
                }
                if (!paramBoolean) {
                    if (c1 == ' ' || c1 == '\t' || c1 == '\n' || c1 == '\r') {
                        this.strTmp.append(' ');
                        continue;
                    }
                    if (c1 == '<')
                        fatal("P-012");
                }
                this.strTmp.append(c1);
                continue;
            }
            break;
        }
        this.isInAttribute = false;
    }

    private void expandEntityInLiteral(String paramString, SimpleHashtable paramSimpleHashtable, boolean paramBoolean) throws SAXException, IOException {
        Object object = paramSimpleHashtable.get(paramString);
        if (object instanceof InternalEntity) {
            InternalEntity internalEntity = (InternalEntity) object;
            if (this.isValidating && this.isStandalone && !internalEntity.isFromInternalSubset)
                error("V-002", new Object[]{paramString});
            pushReader(internalEntity.buf, paramString, internalEntity.isPE ^ true);
        } else if (object instanceof ExternalEntity) {
            if (!paramBoolean)
                fatal("P-013", new Object[]{paramString});
            pushReader((ExternalEntity) object);
        } else if (object == null) {
            fatal((paramSimpleHashtable == this.params) ? "V-022" : "P-014", new Object[]{paramString});
        }
    }

    private String getQuotedString(String paramString1, String paramString2) throws IOException, SAXException {
        char c1 = this.in.getc();
        if (c1 != '\'' && c1 != '"')
            fatal("P-015", new Object[]{messages.getMessage(this.locale, paramString1, new Object[]{paramString2})});
        this.strTmp = new StringBuffer();
        char c2;
        while ((c2 = this.in.getc()) != c1)
            this.strTmp.append(c2);
        return this.strTmp.toString();
    }

    private String parsePublicId() throws IOException, SAXException {
        String str = getQuotedString("F-033", null);
        for (byte b = 0; b < str.length(); b++) {
            char c = str.charAt(b);
            if (" \r\n-'()+,./:=?;!*#@$_%0123456789".indexOf(c) == -1 && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z'))
                fatal("P-016", new Object[]{new Character(c)});
        }
        this.strTmp = new StringBuffer();
        this.strTmp.append(str);
        return normalize(false);
    }

    private boolean maybeComment(boolean paramBoolean) throws IOException, SAXException {
        if (!this.in.peek(paramBoolean ? "!--" : "<!--", null))
            return false;
        boolean bool = this.doLexicalPE;
        this.doLexicalPE = false;
        int i = this.lexicalListener instanceof ListenerBase ^ true;
        if (i != 0)
            this.strTmp = new StringBuffer();
        while (true) {
            try {
                char c = getc();
                if (c == '-') {
                    c = getc();
                    if (c != '-') {
                        if (i != 0)
                            this.strTmp.append('-');
                        ungetc();
                        continue;
                    }
                    nextChar('>', "F-022", null);
                    break;
                }
                if (i != 0)
                    this.strTmp.append((char) c);
            } catch (EndOfInputException endOfInputException) {
                if (this.inExternalPE || (!this.donePrologue && this.in.isInternal())) {
                    if (this.isValidating)
                        error("V-021", null);
                    this.in = this.in.pop();
                    continue;
                }
                fatal("P-017");
            }
        }
        this.doLexicalPE = bool;
        if (i != 0)
            this.lexicalListener.comment(this.strTmp.toString());
        return true;
    }

    private boolean maybePI(boolean paramBoolean) throws IOException, SAXException {
        boolean bool = this.doLexicalPE;
        if (!this.in.peek(paramBoolean ? "?" : "<?", null))
            return false;
        this.doLexicalPE = false;
        String str = maybeGetName();
        if (str == null)
            fatal("P-018");
        if ("xml".equals(str))
            fatal("P-019");
        if ("xml".equalsIgnoreCase(str))
            fatal("P-020", new Object[]{str});
        if (maybeWhitespace()) {
            this.strTmp = new StringBuffer();
            try {
                while (true) {
                    char c = this.in.getc();
                    if (c != '?' || !this.in.peekc('>')) {
                        this.strTmp.append(c);
                        continue;
                    }
                    break;
                }
            } catch (EndOfInputException endOfInputException) {
                fatal("P-021");
            }
            this.docHandler.processingInstruction(str, this.strTmp.toString());
        } else {
            if (!this.in.peek("?>", null))
                fatal("P-022");
            this.docHandler.processingInstruction(str, "");
        }
        this.doLexicalPE = bool;
        return true;
    }

    private void maybeXmlDecl() throws IOException, SAXException {
        if (!peek("<?xml"))
            return;
        readVersion(true, "1.0");
        readEncoding(false);
        readStandalone();
        maybeWhitespace();
        if (!peek("?>")) {
            char c = getc();
            fatal("P-023", new Object[]{Integer.toHexString(c), new Character(c)});
        }
    }

    private String maybeReadAttribute(String paramString, boolean paramBoolean) throws IOException, SAXException {
        if (!maybeWhitespace()) {
            if (!paramBoolean)
                return null;
            fatal("P-024", new Object[]{paramString});
        }
        if (!peek(paramString))
            if (paramBoolean) {
                fatal("P-024", new Object[]{paramString});
            } else {
                ungetc();
                return null;
            }
        maybeWhitespace();
        nextChar('=', "F-023", null);
        maybeWhitespace();
        return getQuotedString("F-035", paramString);
    }

    private void readVersion(boolean paramBoolean, String paramString) throws IOException, SAXException {
        String str = maybeReadAttribute("version", paramBoolean);
        if (paramBoolean && str == null)
            fatal("P-025", new Object[]{paramString});
        if (str != null) {
            int i = str.length();
            for (byte b = 0; b < i; b++) {
                char c = str.charAt(b);
                if ((c < '0' || c > '9') && c != '_' && c != '.' && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != ':' && c != '-')
                    fatal("P-026", new Object[]{str});
            }
        }
        if (str != null && !str.equals(paramString))
            error("P-027", new Object[]{paramString, str});
    }

    private void maybeMisc(boolean paramBoolean) throws IOException, SAXException {
        while (!paramBoolean || !this.in.isEOF()) {
            if (maybeComment(false) || maybePI(false) || maybeWhitespace())
                continue;
            break;
        }
    }

    private String getMarkupDeclname(String paramString, boolean paramBoolean) throws IOException, SAXException {
        whitespace(paramString);
        String str = maybeGetName();
        if (str == null)
            fatal("P-005", new Object[]{messages.getMessage(this.locale, paramString)});
        return str;
    }

    private boolean maybeDoctypeDecl() throws IOException, SAXException {
        if (!peek("<!DOCTYPE"))
            return false;
        ExternalEntity externalEntity = null;
        this.rootElementName = getMarkupDeclname("F-014", true);
        this.dtdListener.startDtd(this.rootElementName);
        if (maybeWhitespace() && (externalEntity = maybeExternalID()) != null) {
            this.dtdListener.externalDtdDecl(externalEntity.publicId, externalEntity.systemId);
            maybeWhitespace();
        }
        if (this.in.peekc('[')) {
            this.in.startRemembering();
            while (true) {
                if (this.in.isEOF() && !this.in.isDocument()) {
                    this.in = this.in.pop();
                    continue;
                }
                if (maybeMarkupDecl() || maybePEReference() || maybeWhitespace())
                    continue;
                if (peek("<![")) {
                    fatal("P-028");
                    continue;
                }
                break;
            }
            this.dtdListener.internalDtdDecl(this.in.rememberText());
            nextChar(']', "F-024", null);
            maybeWhitespace();
        }
        nextChar('>', "F-025", null);
        if (externalEntity != null) {
            externalEntity.name = "(DOCTYPE)";
            externalEntity.isPE = true;
            externalParameterEntity(externalEntity);
        }
        this.params.clear();
        this.dtdListener.endDtd();
        Vector vector = new Vector();
        Enumeration enumeration = this.notations.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            Object object = this.notations.get(str);
            if (object == Boolean.TRUE) {
                if (this.isValidating)
                    error("V-003", new Object[]{str});
                vector.addElement(str);
                continue;
            }
            if (object instanceof String) {
                if (this.isValidating)
                    error("V-004", new Object[]{str});
                vector.addElement(str);
            }
        }
        while (!vector.isEmpty()) {
            Object object = vector.firstElement();
            vector.removeElement(object);
            this.notations.remove(object);
        }
        return true;
    }

    private boolean maybeMarkupDecl() throws IOException, SAXException {
        return !(!maybeElementDecl() && !maybeAttlistDecl() && !maybeEntityDecl() && !maybeNotationDecl() && !maybePI(false) && !maybeComment(false));
    }

    private void readStandalone() throws IOException, SAXException {
        String str = maybeReadAttribute("standalone", false);
        if (str == null || "no".equals(str))
            return;
        if ("yes".equals(str)) {
            this.isStandalone = true;
            return;
        }
        fatal("P-029", new Object[]{str});
    }

    private boolean isXmlLang(String paramString) {
        byte b;
        if (paramString.length() < 2)
            return false;
        char c = paramString.charAt(1);
        if (c == '-') {
            c = paramString.charAt(0);
            if (c != 'i' && c != 'I' && c != 'x' && c != 'X')
                return false;
            b = 1;
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            c = paramString.charAt(0);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                return false;
            b = 2;
        } else {
            return false;
        }
        while (b < paramString.length()) {
            c = paramString.charAt(b);
            if (c != '-')
                break;
            while (++b < paramString.length()) {
                c = paramString.charAt(b);
                if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                    break;
            }
        }
        return !(paramString.length() != b || c == '-');
    }

    private boolean maybeElement(ElementValidator paramElementValidator) throws IOException, SAXException {
        boolean bool1 = false;
        boolean bool2 = true;
        NameCacheEntry nameCacheEntry = maybeGetNameCacheEntry();
        if (nameCacheEntry == null)
            return false;
        if (paramElementValidator != null)
            paramElementValidator.consume(nameCacheEntry.name);
        ElementDecl elementDecl = (ElementDecl) this.elements.get(nameCacheEntry.name);
        if (this.isValidating) {
            if (elementDecl == null || elementDecl.contentType == null) {
                error("V-005", new Object[]{nameCacheEntry.name});
                elementDecl = new ElementDecl(nameCacheEntry.name);
                elementDecl.contentType = "ANY";
                this.elements.put(nameCacheEntry.name, elementDecl);
            }
            if (paramElementValidator == null && this.rootElementName != null && !this.rootElementName.equals(nameCacheEntry.name))
                error("V-006", new Object[]{nameCacheEntry.name, this.rootElementName});
        }
        int i = this.in.getLineNumber();
        boolean bool = this.in.maybeWhitespace();
        while (!this.in.peekc('>')) {
            String str2;
            if (this.in.peekc('/')) {
                bool2 = false;
                break;
            }
            if (!bool)
                fatal("P-030");
            String str1 = maybeGetName();
            if (str1 == null)
                fatal("P-031", new Object[]{new Character(getc())});
            if (this.attTmp.getValue(str1) != null)
                fatal("P-032", new Object[]{str1});
            this.in.maybeWhitespace();
            nextChar('=', "F-026", str1);
            this.in.maybeWhitespace();
            parseLiteral(false);
            bool = this.in.maybeWhitespace();
            AttributeDecl attributeDecl = (elementDecl == null) ? null : (AttributeDecl) elementDecl.attributes.get(str1);
            if (attributeDecl == null) {
                if (this.isValidating)
                    error("V-007", new Object[]{str1, nameCacheEntry.name});
                str2 = this.strTmp.toString();
            } else {
                if (!"CDATA".equals(attributeDecl.type)) {
                    str2 = normalize(attributeDecl.isFromInternalSubset ^ true);
                    if (this.isValidating)
                        validateAttributeSyntax(attributeDecl, str2);
                } else {
                    str2 = this.strTmp.toString();
                }
                if (this.isValidating && attributeDecl.isFixed && !str2.equals(attributeDecl.defaultValue))
                    error("V-008", new Object[]{str1, nameCacheEntry.name, attributeDecl.defaultValue});
            }
            if ("xml:lang".equals(str1) && !isXmlLang(str2))
                error("P-033", new Object[]{str2});
            this.attTmp.addAttribute(str1, (attributeDecl == null) ? "CDATA" : attributeDecl.type, str2, (attributeDecl == null) ? null : attributeDecl.defaultValue, true);
            bool1 = true;
        }
        if (elementDecl != null)
            this.attTmp.setIdAttributeName(elementDecl.id);
        if (elementDecl != null && elementDecl.attributes.size() != 0)
            bool1 = (!defaultAttributes(this.attTmp, elementDecl) && !bool1) ? false : true;
        this.docHandler.startElement(nameCacheEntry.name, this.attTmp);
        if (bool1)
            this.attTmp.clear();
        paramElementValidator = newValidator(elementDecl);
        if (bool2) {
            content(elementDecl, false, paramElementValidator);
            if (!this.in.peek(nameCacheEntry.name, nameCacheEntry.chars))
                fatal("P-034", new Object[]{nameCacheEntry.name, new Integer(i)});
            this.in.maybeWhitespace();
        }
        nextChar('>', "F-027", nameCacheEntry.name);
        paramElementValidator.done();
        this.docHandler.endElement(nameCacheEntry.name);
        return true;
    }

    ElementValidator newValidator(ElementDecl paramElementDecl) {
        return ElementValidator.ANY;
    }

    void validateAttributeSyntax(AttributeDecl paramAttributeDecl, String paramString) throws SAXException {
    }

    private boolean defaultAttributes(AttributeListImpl paramAttributeListImpl, ElementDecl paramElementDecl) throws SAXException {
        boolean bool = false;
        Enumeration enumeration = paramElementDecl.attributes.keys();
        while (enumeration.hasMoreElements()) {
            String str1 = enumeration.nextElement();
            String str2 = paramAttributeListImpl.getValue(str1);
            if (str2 == null) {
                AttributeDecl attributeDecl = (AttributeDecl) paramElementDecl.attributes.get(str1);
                if (this.isValidating && attributeDecl.isRequired)
                    error("V-009", new Object[]{str1});
                if (attributeDecl.defaultValue != null) {
                    if (this.isValidating && this.isStandalone && !attributeDecl.isFromInternalSubset)
                        error("V-010", new Object[]{str1});
                    paramAttributeListImpl.addAttribute(str1, attributeDecl.type, attributeDecl.defaultValue, attributeDecl.defaultValue, false);
                    bool = true;
                }
            }
        }
        return bool;
    }

    private void content(ElementDecl paramElementDecl, boolean paramBoolean, ElementValidator paramElementValidator) throws IOException, SAXException {
        while (true) {
            if (this.in.peekc('<'))
                if (!maybeElement(paramElementValidator)) {
                    if (this.in.peekc('/'))
                        return;
                    if (maybeComment(true) || maybePI(true))
                        continue;
                    if (!this.in.unparsedContent(this.docHandler, paramElementValidator, !(paramElementDecl == null || !paramElementDecl.ignoreWhitespace),
                        (this.isStandalone && this.isValidating && !paramElementDecl.isFromInternalSubset) ? "V-023" : null)) {
                        char c = getc();
                        fatal("P-079", new Object[]{Integer.toHexString(c), new Character(c)});
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            if (paramElementDecl != null && paramElementDecl.ignoreWhitespace && this.in.ignorableWhitespace(this.docHandler)) {
                if (this.isValidating && this.isStandalone && !paramElementDecl.isFromInternalSubset)
                    error("V-011", new Object[]{paramElementDecl.name});
                continue;
            }
            if (!this.in.parsedContent(this.docHandler, paramElementValidator)) {
                if (!this.in.isEOF()) {
                    if (!maybeReferenceInContent(paramElementDecl, paramElementValidator))
                        throw new InternalError();
                    continue;
                }
                break;
            }
        }
        if (!paramBoolean)
            fatal("P-035");
    }

    private boolean maybeElementDecl() throws IOException, SAXException {
        InputEntity inputEntity = peekDeclaration("!ELEMENT");
        if (inputEntity == null)
            return false;
        String str = getMarkupDeclname("F-015", true);
        ElementDecl elementDecl = (ElementDecl) this.elements.get(str);
        boolean bool = false;
        if (elementDecl != null) {
            if (elementDecl.contentType != null) {
                if (this.isValidating && elementDecl.contentType != null)
                    error("V-012", new Object[]{str});
                elementDecl = new ElementDecl(str);
            }
        } else {
            elementDecl = new ElementDecl(str);
            if (!this.ignoreDeclarations) {
                this.elements.put(elementDecl.name, elementDecl);
                bool = true;
            }
        }
        elementDecl.isFromInternalSubset = this.inExternalPE ^ true;
        whitespace("F-000");
        if (peek("EMPTY")) {
            elementDecl.contentType = "EMPTY";
            elementDecl.ignoreWhitespace = true;
        } else if (peek("ANY")) {
            elementDecl.contentType = "ANY";
            elementDecl.ignoreWhitespace = false;
        } else {
            elementDecl.contentType = getMixedOrChildren(elementDecl);
        }
        maybeWhitespace();
        char c = getc();
        if (c != '>')
            fatal("P-036", new Object[]{str, new Character(c)});
        if (this.isValidating && inputEntity != this.in)
            error("V-013", null);
        if (bool)
            this.dtdListener.elementDecl(elementDecl.name, elementDecl.contentType);
        return true;
    }

    private String getMixedOrChildren(ElementDecl paramElementDecl) throws IOException, SAXException {
        this.strTmp = new StringBuffer();
        nextChar('(', "F-028", paramElementDecl.name);
        InputEntity inputEntity = this.in;
        maybeWhitespace();
        this.strTmp.append('(');
        if (peek("#PCDATA")) {
            this.strTmp.append("#PCDATA");
            getMixed(paramElementDecl.name, inputEntity);
            paramElementDecl.ignoreWhitespace = false;
        } else {
            paramElementDecl.model = getcps(paramElementDecl.name, inputEntity);
            paramElementDecl.ignoreWhitespace = true;
        }
        return this.strTmp.toString();
    }

    ContentModel newContentModel(String paramString) {
        return null;
    }

    ContentModel newContentModel(char paramChar, ContentModel paramContentModel) {
        return null;
    }

    private ContentModel getcps(String paramString, InputEntity paramInputEntity) throws IOException, SAXException {
        // Byte code:
        //   0: iconst_0
        //   1: istore_3
        //   2: iconst_0
        //   3: istore #4
        //   5: aconst_null
        //   6: dup
        //   7: astore #7
        //   9: dup
        //   10: astore #6
        //   12: astore #5
        //   14: aload_0
        //   15: invokespecial maybeGetName : ()Ljava/lang/String;
        //   18: astore #8
        //   20: aload #8
        //   22: ifnull -> 50
        //   25: aload_0
        //   26: getfield strTmp : Ljava/lang/StringBuffer;
        //   29: aload #8
        //   31: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   34: pop
        //   35: aload_0
        //   36: aload_0
        //   37: aload #8
        //   39: invokevirtual newContentModel : (Ljava/lang/String;)Lcom/sun/xml/parser/ContentModel;
        //   42: invokespecial getFrequency : (Lcom/sun/xml/parser/ContentModel;)Lcom/sun/xml/parser/ContentModel;
        //   45: astore #7
        //   47: goto -> 142
        //   50: aload_0
        //   51: ldc '('
        //   53: invokespecial peek : (Ljava/lang/String;)Z
        //   56: ifeq -> 96
        //   59: aload_0
        //   60: getfield in : Lcom/sun/xml/parser/InputEntity;
        //   63: astore #9
        //   65: aload_0
        //   66: getfield strTmp : Ljava/lang/StringBuffer;
        //   69: bipush #40
        //   71: invokevirtual append : (C)Ljava/lang/StringBuffer;
        //   74: pop
        //   75: aload_0
        //   76: invokespecial maybeWhitespace : ()Z
        //   79: pop
        //   80: aload_0
        //   81: aload_0
        //   82: aload_1
        //   83: aload #9
        //   85: invokespecial getcps : (Ljava/lang/String;Lcom/sun/xml/parser/InputEntity;)Lcom/sun/xml/parser/ContentModel;
        //   88: invokespecial getFrequency : (Lcom/sun/xml/parser/ContentModel;)Lcom/sun/xml/parser/ContentModel;
        //   91: astore #7
        //   93: goto -> 142
        //   96: aload_0
        //   97: iload #4
        //   99: ifne -> 107
        //   102: ldc 'P-039'
        //   104: goto -> 121
        //   107: iload #4
        //   109: bipush #44
        //   111: if_icmpne -> 119
        //   114: ldc 'P-037'
        //   116: goto -> 121
        //   119: ldc 'P-038'
        //   121: iconst_1
        //   122: anewarray java/lang/Object
        //   125: dup
        //   126: iconst_0
        //   127: new java/lang/Character
        //   130: dup
        //   131: aload_0
        //   132: invokespecial getc : ()C
        //   135: invokespecial <init> : (C)V
        //   138: aastore
        //   139: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   142: aload_0
        //   143: invokespecial maybeWhitespace : ()Z
        //   146: pop
        //   147: iload_3
        //   148: ifeq -> 268
        //   151: aload_0
        //   152: invokespecial getc : ()C
        //   155: istore #9
        //   157: aload #6
        //   159: ifnull -> 182
        //   162: aload #6
        //   164: aload_0
        //   165: iload #4
        //   167: aload #7
        //   169: invokevirtual newContentModel : (CLcom/sun/xml/parser/ContentModel;)Lcom/sun/xml/parser/ContentModel;
        //   172: putfield next : Lcom/sun/xml/parser/ContentModel;
        //   175: aload #6
        //   177: getfield next : Lcom/sun/xml/parser/ContentModel;
        //   180: astore #6
        //   182: iload #9
        //   184: iload #4
        //   186: if_icmpne -> 207
        //   189: aload_0
        //   190: getfield strTmp : Ljava/lang/StringBuffer;
        //   193: iload #4
        //   195: invokevirtual append : (C)Ljava/lang/StringBuffer;
        //   198: pop
        //   199: aload_0
        //   200: invokespecial maybeWhitespace : ()Z
        //   203: pop
        //   204: goto -> 335
        //   207: iload #9
        //   209: bipush #41
        //   211: if_icmpne -> 221
        //   214: aload_0
        //   215: invokespecial ungetc : ()V
        //   218: goto -> 335
        //   221: aload_0
        //   222: iload #4
        //   224: ifne -> 232
        //   227: ldc 'P-041'
        //   229: goto -> 234
        //   232: ldc 'P-040'
        //   234: iconst_2
        //   235: anewarray java/lang/Object
        //   238: dup
        //   239: iconst_0
        //   240: new java/lang/Character
        //   243: dup
        //   244: iload #9
        //   246: invokespecial <init> : (C)V
        //   249: aastore
        //   250: dup
        //   251: iconst_1
        //   252: new java/lang/Character
        //   255: dup
        //   256: iload #4
        //   258: invokespecial <init> : (C)V
        //   261: aastore
        //   262: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   265: goto -> 330
        //   268: aload_0
        //   269: invokespecial getc : ()C
        //   272: istore #4
        //   274: iload #4
        //   276: bipush #124
        //   278: if_icmpeq -> 288
        //   281: iload #4
        //   283: bipush #44
        //   285: if_icmpne -> 306
        //   288: iconst_1
        //   289: istore_3
        //   290: aload_0
        //   291: iload #4
        //   293: aload #7
        //   295: invokevirtual newContentModel : (CLcom/sun/xml/parser/ContentModel;)Lcom/sun/xml/parser/ContentModel;
        //   298: dup
        //   299: astore #6
        //   301: astore #5
        //   303: goto -> 320
        //   306: aload #7
        //   308: dup
        //   309: astore #6
        //   311: astore #5
        //   313: aload_0
        //   314: invokespecial ungetc : ()V
        //   317: goto -> 335
        //   320: aload_0
        //   321: getfield strTmp : Ljava/lang/StringBuffer;
        //   324: iload #4
        //   326: invokevirtual append : (C)Ljava/lang/StringBuffer;
        //   329: pop
        //   330: aload_0
        //   331: invokespecial maybeWhitespace : ()Z
        //   334: pop
        //   335: aload_0
        //   336: ldc ')'
        //   338: invokespecial peek : (Ljava/lang/String;)Z
        //   341: ifeq -> 14
        //   344: aload_0
        //   345: getfield isValidating : Z
        //   348: ifeq -> 373
        //   351: aload_0
        //   352: getfield in : Lcom/sun/xml/parser/InputEntity;
        //   355: aload_2
        //   356: if_acmpeq -> 373
        //   359: aload_0
        //   360: ldc 'V-014'
        //   362: iconst_1
        //   363: anewarray java/lang/Object
        //   366: dup
        //   367: iconst_0
        //   368: aload_1
        //   369: aastore
        //   370: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   373: aload_0
        //   374: getfield strTmp : Ljava/lang/StringBuffer;
        //   377: bipush #41
        //   379: invokevirtual append : (C)Ljava/lang/StringBuffer;
        //   382: pop
        //   383: aload_0
        //   384: aload #5
        //   386: invokespecial getFrequency : (Lcom/sun/xml/parser/ContentModel;)Lcom/sun/xml/parser/ContentModel;
        //   389: areturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #1661	-> 0
        //   #1662	-> 2
        //   #1665	-> 5
        //   #1670	-> 14
        //   #1671	-> 20
        //   #1672	-> 25
        //   #1673	-> 35
        //   #1671	-> 47
        //   #1674	-> 50
        //   #1675	-> 59
        //   #1676	-> 65
        //   #1677	-> 75
        //   #1678	-> 80
        //   #1674	-> 93
        //   #1680	-> 96
        //   #1681	-> 107
        //   #1682	-> 121
        //   #1680	-> 139
        //   #1684	-> 142
        //   #1685	-> 147
        //   #1686	-> 151
        //   #1688	-> 157
        //   #1689	-> 162
        //   #1690	-> 175
        //   #1692	-> 182
        //   #1693	-> 189
        //   #1694	-> 199
        //   #1695	-> 204
        //   #1696	-> 207
        //   #1697	-> 214
        //   #1698	-> 218
        //   #1700	-> 221
        //   #1701	-> 234
        //   #1702	-> 240
        //   #1701	-> 249
        //   #1703	-> 252
        //   #1701	-> 261
        //   #1700	-> 262
        //   #1685	-> 265
        //   #1707	-> 268
        //   #1708	-> 274
        //   #1709	-> 288
        //   #1710	-> 290
        //   #1708	-> 303
        //   #1712	-> 306
        //   #1713	-> 313
        //   #1714	-> 317
        //   #1716	-> 320
        //   #1718	-> 330
        //   #1719	-> 335
        //   #1720	-> 344
        //   #1721	-> 359
        //   #1722	-> 373
        //   #1723	-> 383
    }

    private ContentModel getFrequency(ContentModel paramContentModel) throws IOException, SAXException {
        char c = getc();
        if (c == '?' || c == '+' || c == '*') {
            this.strTmp.append(c);
            if (paramContentModel == null)
                return null;
            if (paramContentModel.type == '\000') {
                paramContentModel.type = c;
                return paramContentModel;
            }
            return newContentModel(c, paramContentModel);
        }
        ungetc();
        return paramContentModel;
    }

    private void getMixed(String paramString, InputEntity paramInputEntity) throws IOException, SAXException {
        maybeWhitespace();
        if (peek(")*") || peek(")")) {
            if (this.isValidating && this.in != paramInputEntity)
                error("V-014", new Object[]{paramString});
            this.strTmp.append(')');
            return;
        }
        Vector vector = null;
        if (this.isValidating)
            vector = new Vector();
        while (peek("|")) {
            this.strTmp.append('|');
            maybeWhitespace();
            String str = maybeGetName();
            if (str == null)
                fatal("P-042", new Object[]{paramString, Integer.toHexString(getc())});
            if (this.isValidating)
                if (vector.contains(str)) {
                    error("V-015", new Object[]{str});
                } else {
                    vector.addElement(str);
                }
            this.strTmp.append(str);
            maybeWhitespace();
        }
        if (!peek(")*"))
            fatal("P-043", new Object[]{paramString, new Character(getc())});
        if (this.isValidating && this.in != paramInputEntity)
            error("V-014", new Object[]{paramString});
        this.strTmp.append(')');
    }

    private boolean maybeAttlistDecl() throws IOException, SAXException {
        // Byte code:
        //   0: aload_0
        //   1: ldc '!ATTLIST'
        //   3: invokespecial peekDeclaration : (Ljava/lang/String;)Lcom/sun/xml/parser/InputEntity;
        //   6: astore_1
        //   7: aload_1
        //   8: ifnonnull -> 13
        //   11: iconst_0
        //   12: ireturn
        //   13: aload_0
        //   14: ldc 'F-016'
        //   16: iconst_1
        //   17: invokespecial getMarkupDeclname : (Ljava/lang/String;Z)Ljava/lang/String;
        //   20: astore_2
        //   21: aload_0
        //   22: getfield elements : Lcom/sun/xml/parser/SimpleHashtable;
        //   25: aload_2
        //   26: invokevirtual get : (Ljava/lang/String;)Ljava/lang/Object;
        //   29: checkcast com/sun/xml/parser/ElementDecl
        //   32: astore_3
        //   33: aload_3
        //   34: ifnonnull -> 63
        //   37: new com/sun/xml/parser/ElementDecl
        //   40: dup
        //   41: aload_2
        //   42: invokespecial <init> : (Ljava/lang/String;)V
        //   45: astore_3
        //   46: aload_0
        //   47: getfield ignoreDeclarations : Z
        //   50: ifne -> 63
        //   53: aload_0
        //   54: getfield elements : Lcom/sun/xml/parser/SimpleHashtable;
        //   57: aload_2
        //   58: aload_3
        //   59: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   62: pop
        //   63: aload_0
        //   64: invokespecial maybeWhitespace : ()Z
        //   67: pop
        //   68: goto -> 1032
        //   71: aload_0
        //   72: invokespecial maybeGetName : ()Ljava/lang/String;
        //   75: astore_2
        //   76: aload_2
        //   77: ifnonnull -> 104
        //   80: aload_0
        //   81: ldc 'P-044'
        //   83: iconst_1
        //   84: anewarray java/lang/Object
        //   87: dup
        //   88: iconst_0
        //   89: new java/lang/Character
        //   92: dup
        //   93: aload_0
        //   94: invokespecial getc : ()C
        //   97: invokespecial <init> : (C)V
        //   100: aastore
        //   101: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   104: aload_0
        //   105: ldc 'F-001'
        //   107: invokespecial whitespace : (Ljava/lang/String;)V
        //   110: new com/sun/xml/parser/AttributeDecl
        //   113: dup
        //   114: aload_2
        //   115: invokespecial <init> : (Ljava/lang/String;)V
        //   118: astore #4
        //   120: aload #4
        //   122: aload_0
        //   123: getfield inExternalPE : Z
        //   126: iconst_1
        //   127: ixor
        //   128: putfield isFromInternalSubset : Z
        //   131: aload_0
        //   132: ldc 'CDATA'
        //   134: invokespecial peek : (Ljava/lang/String;)Z
        //   137: ifeq -> 150
        //   140: aload #4
        //   142: ldc 'CDATA'
        //   144: putfield type : Ljava/lang/String;
        //   147: goto -> 647
        //   150: aload_0
        //   151: ldc 'IDREFS'
        //   153: invokespecial peek : (Ljava/lang/String;)Z
        //   156: ifeq -> 169
        //   159: aload #4
        //   161: ldc 'IDREFS'
        //   163: putfield type : Ljava/lang/String;
        //   166: goto -> 647
        //   169: aload_0
        //   170: ldc 'IDREF'
        //   172: invokespecial peek : (Ljava/lang/String;)Z
        //   175: ifeq -> 188
        //   178: aload #4
        //   180: ldc 'IDREF'
        //   182: putfield type : Ljava/lang/String;
        //   185: goto -> 647
        //   188: aload_0
        //   189: ldc 'ID'
        //   191: invokespecial peek : (Ljava/lang/String;)Z
        //   194: ifeq -> 246
        //   197: aload #4
        //   199: ldc 'ID'
        //   201: putfield type : Ljava/lang/String;
        //   204: aload_3
        //   205: getfield id : Ljava/lang/String;
        //   208: ifnull -> 238
        //   211: aload_0
        //   212: getfield isValidating : Z
        //   215: ifeq -> 647
        //   218: aload_0
        //   219: ldc 'V-016'
        //   221: iconst_1
        //   222: anewarray java/lang/Object
        //   225: dup
        //   226: iconst_0
        //   227: aload_3
        //   228: getfield id : Ljava/lang/String;
        //   231: aastore
        //   232: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   235: goto -> 647
        //   238: aload_3
        //   239: aload_2
        //   240: putfield id : Ljava/lang/String;
        //   243: goto -> 647
        //   246: aload_0
        //   247: ldc 'ENTITY'
        //   249: invokespecial peek : (Ljava/lang/String;)Z
        //   252: ifeq -> 265
        //   255: aload #4
        //   257: ldc 'ENTITY'
        //   259: putfield type : Ljava/lang/String;
        //   262: goto -> 647
        //   265: aload_0
        //   266: ldc 'ENTITIES'
        //   268: invokespecial peek : (Ljava/lang/String;)Z
        //   271: ifeq -> 284
        //   274: aload #4
        //   276: ldc 'ENTITIES'
        //   278: putfield type : Ljava/lang/String;
        //   281: goto -> 647
        //   284: aload_0
        //   285: ldc 'NMTOKENS'
        //   287: invokespecial peek : (Ljava/lang/String;)Z
        //   290: ifeq -> 303
        //   293: aload #4
        //   295: ldc 'NMTOKENS'
        //   297: putfield type : Ljava/lang/String;
        //   300: goto -> 647
        //   303: aload_0
        //   304: ldc 'NMTOKEN'
        //   306: invokespecial peek : (Ljava/lang/String;)Z
        //   309: ifeq -> 322
        //   312: aload #4
        //   314: ldc 'NMTOKEN'
        //   316: putfield type : Ljava/lang/String;
        //   319: goto -> 647
        //   322: aload_0
        //   323: ldc 'NOTATION'
        //   325: invokespecial peek : (Ljava/lang/String;)Z
        //   328: ifeq -> 497
        //   331: aload #4
        //   333: ldc 'NOTATION'
        //   335: putfield type : Ljava/lang/String;
        //   338: aload_0
        //   339: ldc 'F-002'
        //   341: invokespecial whitespace : (Ljava/lang/String;)V
        //   344: aload_0
        //   345: bipush #40
        //   347: ldc 'F-029'
        //   349: aconst_null
        //   350: invokespecial nextChar : (CLjava/lang/String;Ljava/lang/String;)V
        //   353: aload_0
        //   354: invokespecial maybeWhitespace : ()Z
        //   357: pop
        //   358: new java/util/Vector
        //   361: dup
        //   362: invokespecial <init> : ()V
        //   365: astore #5
        //   367: aload_0
        //   368: invokespecial maybeGetName : ()Ljava/lang/String;
        //   371: dup
        //   372: astore_2
        //   373: ifnonnull -> 382
        //   376: aload_0
        //   377: ldc 'P-068'
        //   379: invokespecial fatal : (Ljava/lang/String;)V
        //   382: aload_0
        //   383: getfield isValidating : Z
        //   386: ifeq -> 410
        //   389: aload_0
        //   390: getfield notations : Ljava/util/Hashtable;
        //   393: aload_2
        //   394: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
        //   397: ifnonnull -> 410
        //   400: aload_0
        //   401: getfield notations : Ljava/util/Hashtable;
        //   404: aload_2
        //   405: aload_2
        //   406: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   409: pop
        //   410: aload #5
        //   412: aload_2
        //   413: invokevirtual addElement : (Ljava/lang/Object;)V
        //   416: aload_0
        //   417: invokespecial maybeWhitespace : ()Z
        //   420: pop
        //   421: aload_0
        //   422: ldc '|'
        //   424: invokespecial peek : (Ljava/lang/String;)Z
        //   427: ifeq -> 435
        //   430: aload_0
        //   431: invokespecial maybeWhitespace : ()Z
        //   434: pop
        //   435: aload_0
        //   436: ldc ')'
        //   438: invokespecial peek : (Ljava/lang/String;)Z
        //   441: ifeq -> 367
        //   444: aload #4
        //   446: aload #5
        //   448: invokevirtual size : ()I
        //   451: anewarray java/lang/String
        //   454: putfield values : [Ljava/lang/String;
        //   457: iconst_0
        //   458: istore #6
        //   460: goto -> 484
        //   463: aload #4
        //   465: getfield values : [Ljava/lang/String;
        //   468: iload #6
        //   470: aload #5
        //   472: iload #6
        //   474: invokevirtual elementAt : (I)Ljava/lang/Object;
        //   477: checkcast java/lang/String
        //   480: aastore
        //   481: iinc #6, 1
        //   484: iload #6
        //   486: aload #5
        //   488: invokevirtual size : ()I
        //   491: if_icmplt -> 463
        //   494: goto -> 647
        //   497: aload_0
        //   498: ldc '('
        //   500: invokespecial peek : (Ljava/lang/String;)Z
        //   503: ifeq -> 619
        //   506: aload #4
        //   508: ldc 'ENUMERATION'
        //   510: putfield type : Ljava/lang/String;
        //   513: aload_0
        //   514: invokespecial maybeWhitespace : ()Z
        //   517: pop
        //   518: new java/util/Vector
        //   521: dup
        //   522: invokespecial <init> : ()V
        //   525: astore #5
        //   527: aload_0
        //   528: invokespecial getNmtoken : ()Ljava/lang/String;
        //   531: astore_2
        //   532: aload #5
        //   534: aload_2
        //   535: invokevirtual addElement : (Ljava/lang/Object;)V
        //   538: aload_0
        //   539: invokespecial maybeWhitespace : ()Z
        //   542: pop
        //   543: aload_0
        //   544: ldc '|'
        //   546: invokespecial peek : (Ljava/lang/String;)Z
        //   549: ifeq -> 557
        //   552: aload_0
        //   553: invokespecial maybeWhitespace : ()Z
        //   556: pop
        //   557: aload_0
        //   558: ldc ')'
        //   560: invokespecial peek : (Ljava/lang/String;)Z
        //   563: ifeq -> 527
        //   566: aload #4
        //   568: aload #5
        //   570: invokevirtual size : ()I
        //   573: anewarray java/lang/String
        //   576: putfield values : [Ljava/lang/String;
        //   579: iconst_0
        //   580: istore #6
        //   582: goto -> 606
        //   585: aload #4
        //   587: getfield values : [Ljava/lang/String;
        //   590: iload #6
        //   592: aload #5
        //   594: iload #6
        //   596: invokevirtual elementAt : (I)Ljava/lang/Object;
        //   599: checkcast java/lang/String
        //   602: aastore
        //   603: iinc #6, 1
        //   606: iload #6
        //   608: aload #5
        //   610: invokevirtual size : ()I
        //   613: if_icmplt -> 585
        //   616: goto -> 647
        //   619: aload_0
        //   620: ldc 'P-045'
        //   622: iconst_2
        //   623: anewarray java/lang/Object
        //   626: dup
        //   627: iconst_0
        //   628: aload_2
        //   629: aastore
        //   630: dup
        //   631: iconst_1
        //   632: new java/lang/Character
        //   635: dup
        //   636: aload_0
        //   637: invokespecial getc : ()C
        //   640: invokespecial <init> : (C)V
        //   643: aastore
        //   644: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   647: aload_0
        //   648: ldc 'F-003'
        //   650: invokespecial whitespace : (Ljava/lang/String;)V
        //   653: aload_0
        //   654: ldc '#REQUIRED'
        //   656: invokespecial peek : (Ljava/lang/String;)Z
        //   659: ifeq -> 671
        //   662: aload #4
        //   664: iconst_1
        //   665: putfield isRequired : Z
        //   668: goto -> 896
        //   671: aload_0
        //   672: ldc '#FIXED'
        //   674: invokespecial peek : (Ljava/lang/String;)Z
        //   677: ifeq -> 791
        //   680: aload_0
        //   681: getfield isValidating : Z
        //   684: ifeq -> 715
        //   687: aload #4
        //   689: getfield type : Ljava/lang/String;
        //   692: ldc 'ID'
        //   694: if_acmpne -> 715
        //   697: aload_0
        //   698: ldc 'V-017'
        //   700: iconst_1
        //   701: anewarray java/lang/Object
        //   704: dup
        //   705: iconst_0
        //   706: aload #4
        //   708: getfield name : Ljava/lang/String;
        //   711: aastore
        //   712: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   715: aload #4
        //   717: iconst_1
        //   718: putfield isFixed : Z
        //   721: aload_0
        //   722: ldc 'F-004'
        //   724: invokespecial whitespace : (Ljava/lang/String;)V
        //   727: aload_0
        //   728: iconst_0
        //   729: invokespecial parseLiteral : (Z)V
        //   732: aload #4
        //   734: getfield type : Ljava/lang/String;
        //   737: ldc 'CDATA'
        //   739: if_acmpeq -> 755
        //   742: aload #4
        //   744: aload_0
        //   745: iconst_0
        //   746: invokespecial normalize : (Z)Ljava/lang/String;
        //   749: putfield defaultValue : Ljava/lang/String;
        //   752: goto -> 767
        //   755: aload #4
        //   757: aload_0
        //   758: getfield strTmp : Ljava/lang/StringBuffer;
        //   761: invokevirtual toString : ()Ljava/lang/String;
        //   764: putfield defaultValue : Ljava/lang/String;
        //   767: aload #4
        //   769: getfield type : Ljava/lang/String;
        //   772: ldc 'CDATA'
        //   774: if_acmpeq -> 896
        //   777: aload_0
        //   778: aload #4
        //   780: aload #4
        //   782: getfield defaultValue : Ljava/lang/String;
        //   785: invokevirtual validateAttributeSyntax : (Lcom/sun/xml/parser/AttributeDecl;Ljava/lang/String;)V
        //   788: goto -> 896
        //   791: aload_0
        //   792: ldc '#IMPLIED'
        //   794: invokespecial peek : (Ljava/lang/String;)Z
        //   797: ifne -> 896
        //   800: aload_0
        //   801: getfield isValidating : Z
        //   804: ifeq -> 835
        //   807: aload #4
        //   809: getfield type : Ljava/lang/String;
        //   812: ldc 'ID'
        //   814: if_acmpne -> 835
        //   817: aload_0
        //   818: ldc 'V-018'
        //   820: iconst_1
        //   821: anewarray java/lang/Object
        //   824: dup
        //   825: iconst_0
        //   826: aload #4
        //   828: getfield name : Ljava/lang/String;
        //   831: aastore
        //   832: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   835: aload_0
        //   836: iconst_0
        //   837: invokespecial parseLiteral : (Z)V
        //   840: aload #4
        //   842: getfield type : Ljava/lang/String;
        //   845: ldc 'CDATA'
        //   847: if_acmpeq -> 863
        //   850: aload #4
        //   852: aload_0
        //   853: iconst_0
        //   854: invokespecial normalize : (Z)Ljava/lang/String;
        //   857: putfield defaultValue : Ljava/lang/String;
        //   860: goto -> 875
        //   863: aload #4
        //   865: aload_0
        //   866: getfield strTmp : Ljava/lang/StringBuffer;
        //   869: invokevirtual toString : ()Ljava/lang/String;
        //   872: putfield defaultValue : Ljava/lang/String;
        //   875: aload #4
        //   877: getfield type : Ljava/lang/String;
        //   880: ldc 'CDATA'
        //   882: if_acmpeq -> 896
        //   885: aload_0
        //   886: aload #4
        //   888: aload #4
        //   890: getfield defaultValue : Ljava/lang/String;
        //   893: invokevirtual validateAttributeSyntax : (Lcom/sun/xml/parser/AttributeDecl;Ljava/lang/String;)V
        //   896: ldc 'xml:lang'
        //   898: aload #4
        //   900: getfield name : Ljava/lang/String;
        //   903: invokevirtual equals : (Ljava/lang/Object;)Z
        //   906: ifeq -> 947
        //   909: aload #4
        //   911: getfield defaultValue : Ljava/lang/String;
        //   914: ifnull -> 947
        //   917: aload_0
        //   918: aload #4
        //   920: getfield defaultValue : Ljava/lang/String;
        //   923: invokespecial isXmlLang : (Ljava/lang/String;)Z
        //   926: ifne -> 947
        //   929: aload_0
        //   930: ldc 'P-033'
        //   932: iconst_1
        //   933: anewarray java/lang/Object
        //   936: dup
        //   937: iconst_0
        //   938: aload #4
        //   940: getfield defaultValue : Ljava/lang/String;
        //   943: aastore
        //   944: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   947: aload_0
        //   948: getfield ignoreDeclarations : Z
        //   951: ifne -> 1027
        //   954: aload_3
        //   955: getfield attributes : Lcom/sun/xml/parser/SimpleHashtable;
        //   958: aload #4
        //   960: getfield name : Ljava/lang/String;
        //   963: invokevirtual get : (Ljava/lang/String;)Ljava/lang/Object;
        //   966: ifnonnull -> 1027
        //   969: aload_3
        //   970: getfield attributes : Lcom/sun/xml/parser/SimpleHashtable;
        //   973: aload #4
        //   975: getfield name : Ljava/lang/String;
        //   978: aload #4
        //   980: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   983: pop
        //   984: aload_0
        //   985: getfield dtdListener : Lcom/sun/xml/parser/DtdEventListener;
        //   988: aload_3
        //   989: getfield name : Ljava/lang/String;
        //   992: aload #4
        //   994: getfield name : Ljava/lang/String;
        //   997: aload #4
        //   999: getfield type : Ljava/lang/String;
        //   1002: aload #4
        //   1004: getfield values : [Ljava/lang/String;
        //   1007: aload #4
        //   1009: getfield defaultValue : Ljava/lang/String;
        //   1012: aload #4
        //   1014: getfield isFixed : Z
        //   1017: aload #4
        //   1019: getfield isRequired : Z
        //   1022: invokeinterface attributeDecl : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;ZZ)V
        //   1027: aload_0
        //   1028: invokespecial maybeWhitespace : ()Z
        //   1031: pop
        //   1032: aload_0
        //   1033: ldc '>'
        //   1035: invokespecial peek : (Ljava/lang/String;)Z
        //   1038: ifeq -> 71
        //   1041: aload_0
        //   1042: getfield isValidating : Z
        //   1045: ifeq -> 1063
        //   1048: aload_1
        //   1049: aload_0
        //   1050: getfield in : Lcom/sun/xml/parser/InputEntity;
        //   1053: if_acmpeq -> 1063
        //   1056: aload_0
        //   1057: ldc 'V-013'
        //   1059: aconst_null
        //   1060: invokevirtual error : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   1063: iconst_1
        //   1064: ireturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #1798	-> 0
        //   #1800	-> 7
        //   #1801	-> 11
        //   #1803	-> 13
        //   #1804	-> 21
        //   #1806	-> 33
        //   #1808	-> 37
        //   #1809	-> 46
        //   #1810	-> 53
        //   #1813	-> 63
        //   #1814	-> 68
        //   #1818	-> 71
        //   #1819	-> 76
        //   #1820	-> 80
        //   #1821	-> 104
        //   #1823	-> 110
        //   #1824	-> 120
        //   #1830	-> 131
        //   #1831	-> 140
        //   #1830	-> 147
        //   #1838	-> 150
        //   #1839	-> 159
        //   #1838	-> 166
        //   #1840	-> 169
        //   #1841	-> 178
        //   #1840	-> 185
        //   #1842	-> 188
        //   #1843	-> 197
        //   #1844	-> 204
        //   #1845	-> 211
        //   #1846	-> 218
        //   #1844	-> 235
        //   #1848	-> 238
        //   #1842	-> 243
        //   #1849	-> 246
        //   #1850	-> 255
        //   #1849	-> 262
        //   #1851	-> 265
        //   #1852	-> 274
        //   #1851	-> 281
        //   #1853	-> 284
        //   #1854	-> 293
        //   #1853	-> 300
        //   #1855	-> 303
        //   #1856	-> 312
        //   #1855	-> 319
        //   #1861	-> 322
        //   #1862	-> 331
        //   #1863	-> 338
        //   #1864	-> 344
        //   #1865	-> 353
        //   #1867	-> 358
        //   #1869	-> 367
        //   #1870	-> 376
        //   #1872	-> 382
        //   #1873	-> 389
        //   #1874	-> 400
        //   #1875	-> 410
        //   #1876	-> 416
        //   #1877	-> 421
        //   #1878	-> 430
        //   #1879	-> 435
        //   #1880	-> 444
        //   #1881	-> 457
        //   #1882	-> 463
        //   #1881	-> 481
        //   #1861	-> 494
        //   #1885	-> 497
        //   #1886	-> 506
        //   #1887	-> 513
        //   #1889	-> 518
        //   #1891	-> 527
        //   #1892	-> 532
        //   #1893	-> 538
        //   #1894	-> 543
        //   #1895	-> 552
        //   #1896	-> 557
        //   #1897	-> 566
        //   #1898	-> 579
        //   #1899	-> 585
        //   #1898	-> 603
        //   #1885	-> 616
        //   #1901	-> 619
        //   #1902	-> 622
        //   #1901	-> 644
        //   #1907	-> 647
        //   #1908	-> 653
        //   #1909	-> 662
        //   #1908	-> 668
        //   #1910	-> 671
        //   #1911	-> 680
        //   #1912	-> 687
        //   #1913	-> 697
        //   #1914	-> 715
        //   #1915	-> 721
        //   #1916	-> 727
        //   #1917	-> 732
        //   #1918	-> 742
        //   #1917	-> 752
        //   #1920	-> 755
        //   #1921	-> 767
        //   #1922	-> 777
        //   #1910	-> 788
        //   #1923	-> 791
        //   #1924	-> 800
        //   #1925	-> 807
        //   #1926	-> 817
        //   #1927	-> 835
        //   #1928	-> 840
        //   #1929	-> 850
        //   #1928	-> 860
        //   #1931	-> 863
        //   #1932	-> 875
        //   #1933	-> 885
        //   #1936	-> 896
        //   #1937	-> 909
        //   #1938	-> 917
        //   #1939	-> 929
        //   #1941	-> 947
        //   #1942	-> 954
        //   #1943	-> 969
        //   #1944	-> 984
        //   #1945	-> 992
        //   #1946	-> 1002
        //   #1947	-> 1012
        //   #1944	-> 1022
        //   #1950	-> 1027
        //   #1814	-> 1032
        //   #1952	-> 1041
        //   #1953	-> 1056
        //   #1954	-> 1063
    }

    private String normalize(boolean paramBoolean) throws SAXException {
        String str1 = this.strTmp.toString();
        String str2 = str1.trim();
        boolean bool = false;
        if (str1 != str2) {
            str1 = str2;
            str2 = null;
            bool = true;
        }
        this.strTmp = new StringBuffer();
        for (byte b = 0; b < str1.length(); b++) {
            char c = str1.charAt(b);
            if (!XmlChars.isSpace(c)) {
                this.strTmp.append(c);
            } else {
                this.strTmp.append(' ');
                while (++b < str1.length() && XmlChars.isSpace(str1.charAt(b)))
                    bool = true;
                b--;
            }
        }
        if (this.isValidating && this.isStandalone && paramBoolean && (str2 == null || bool))
            error("V-019", null);
        if (bool)
            return this.strTmp.toString();
        return str1;
    }

    private boolean maybeConditionalSect() throws IOException, SAXException {
        if (!peek("<!["))
            return false;
        InputEntity inputEntity = this.in;
        maybeWhitespace();
        String str;
        if ((str = maybeGetName()) == null)
            fatal("P-046");
        maybeWhitespace();
        nextChar('[', "F-030", null);
        if ("INCLUDE".equals(str)) {
            while (true) {
                if (!this.in.isEOF() || this.in == inputEntity) {
                    if (this.in.isEOF()) {
                        if (this.isValidating)
                            error("V-020", null);
                        this.in = this.in.pop();
                    }
                    if (!peek("]]>")) {
                        this.doLexicalPE = false;
                        if (!maybeWhitespace())
                            if (!maybePEReference()) {
                                this.doLexicalPE = true;
                                if (maybeMarkupDecl() || maybeConditionalSect())
                                    continue;
                                fatal("P-047");
                            }
                        continue;
                    }
                    break;
                }
                this.in = this.in.pop();
            }
        } else if ("IGNORE".equals(str)) {
            byte b = 1;
            this.doLexicalPE = false;
            while (!b) {
                char c = getc();
                if (c == '<') {
                    if (peek("!["))
                        b++;
                    continue;
                }
                if (c == ']' && peek("]>"))
                    b--;
            }
        } else {
            fatal("P-048", new Object[]{str});
        }
        return true;
    }

    private boolean maybeReferenceInContent(ElementDecl paramElementDecl, ElementValidator paramElementValidator) throws IOException, SAXException {
        if (!this.in.peekc('&'))
            return false;
        if (!this.in.peekc('#')) {
            String str = maybeGetName();
            if (str == null)
                fatal("P-009");
            nextChar(';', "F-020", str);
            expandEntityInContent(paramElementDecl, str, paramElementValidator);
            return true;
        }
        paramElementValidator.text();
        this.docHandler.characters(this.charTmp, 0, surrogatesToCharTmp(parseCharNumber()));
        return true;
    }

    private int parseCharNumber() throws SAXException, IOException {
        int i = 0;
        if (getc() != 'x') {
            ungetc();
            while (true) {
                char c = getc();
                if (c >= '0' && c <= '9') {
                    i *= 10;
                    i += c - 48;
                    continue;
                }
                if (c == ';')
                    return i;
                fatal("P-049");
            }
        }
        while (true) {
            char c = getc();
            if (c >= '0' && c <= '9') {
                i <<= 4;
                i += c - 48;
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                i <<= 4;
                i += 10 + c - 97;
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                i <<= 4;
                i += 10 + c - 65;
                continue;
            }
            if (c == ';')
                return i;
            fatal("P-050");
        }
    }

    private int surrogatesToCharTmp(int paramInt) throws SAXException {
        if (paramInt <= 65535) {
            if (XmlChars.isChar(paramInt)) {
                this.charTmp[0] = (char) paramInt;
                return 1;
            }
        } else if (paramInt <= 1114111) {
            paramInt -= 65536;
            this.charTmp[0] = (char) (0xD800 | paramInt >> 10 & 0x3FF);
            this.charTmp[1] = (char) (0xDC00 | paramInt & 0x3FF);
            return 2;
        }
        fatal("P-051", new Object[]{Integer.toHexString(paramInt)});
        return -1;
    }

    private void expandEntityInContent(ElementDecl paramElementDecl, String paramString, ElementValidator paramElementValidator) throws SAXException, IOException {
        Object object = this.entities.get(paramString);
        InputEntity inputEntity = this.in;
        if (object == null)
            fatal("P-014", new Object[]{paramString});
        if (object instanceof InternalEntity) {
            InternalEntity internalEntity = (InternalEntity) object;
            if (this.isValidating && this.isStandalone && !internalEntity.isFromInternalSubset)
                error("V-002", new Object[]{paramString});
            pushReader(internalEntity.buf, paramString, true);
            content(paramElementDecl, true, paramElementValidator);
            if (this.in != inputEntity && !this.in.isEOF()) {
                while (this.in.isInternal())
                    this.in = this.in.pop();
                fatal("P-052", new Object[]{paramString});
            }
            this.lexicalListener.endParsedEntity(paramString, true);
            this.in = this.in.pop();
        } else if (object instanceof ExternalEntity) {
            ExternalEntity externalEntity = (ExternalEntity) object;
            if (externalEntity.notation != null)
                fatal("P-053", new Object[]{paramString});
            if (this.isValidating && this.isStandalone && !externalEntity.isFromInternalSubset)
                error("V-002", new Object[]{paramString});
            externalParsedEntity(paramElementDecl, externalEntity, paramElementValidator);
        } else {
            throw new InternalError(paramString);
        }
    }

    private boolean maybePEReference() throws IOException, SAXException {
        if (!this.in.peekc('%'))
            return false;
        String str = maybeGetName();
        if (str == null)
            fatal("P-011");
        nextChar(';', "F-021", str);
        Object object = this.params.get(str);
        if (object instanceof InternalEntity) {
            InternalEntity internalEntity = (InternalEntity) object;
            pushReader(internalEntity.buf, str, false);
        } else if (object instanceof ExternalEntity) {
            externalParameterEntity((ExternalEntity) object);
        } else if (object == null) {
            this.ignoreDeclarations = true;
            if (this.isValidating) {
                error("V-022", new Object[]{str});
            } else {
                warning("V-022", new Object[]{str});
            }
        }
        return true;
    }

    private boolean maybeEntityDecl() throws IOException, SAXException {
        SimpleHashtable simpleHashtable;
        InputEntity inputEntity = peekDeclaration("!ENTITY");
        if (inputEntity == null)
            return false;
        this.doLexicalPE = false;
        whitespace("F-005");
        if (this.in.peekc('%')) {
            whitespace("F-006");
            simpleHashtable = this.params;
        } else {
            simpleHashtable = this.entities;
        }
        ungetc();
        this.doLexicalPE = true;
        String str = getMarkupDeclname("F-017", false);
        whitespace("F-007");
        ExternalEntity externalEntity = maybeExternalID();
        int i = (simpleHashtable.get(str) != null) ? 0 : 1;
        if (!i && simpleHashtable == this.entities)
            warning("P-054", new Object[]{str});
        i &= this.ignoreDeclarations ^ true;
        if (externalEntity == null) {
            this.doLexicalPE = false;
            parseLiteral(true);
            this.doLexicalPE = true;
            if (i != 0) {
                char[] arrayOfChar = new char[this.strTmp.length()];
                if (arrayOfChar.length != 0)
                    this.strTmp.getChars(0, arrayOfChar.length, arrayOfChar, 0);
                InternalEntity internalEntity = new InternalEntity(str, arrayOfChar);
                internalEntity.isPE = !(simpleHashtable != this.params);
                internalEntity.isFromInternalSubset = this.inExternalPE ^ true;
                simpleHashtable.put(str, internalEntity);
                if (simpleHashtable == this.entities && !(this.dtdListener instanceof ListenerBase))
                    this.dtdListener.internalEntityDecl(str, new String(arrayOfChar));
            }
        } else {
            if (simpleHashtable == this.entities && maybeWhitespace() && peek("NDATA")) {
                externalEntity.notation = getMarkupDeclname("F-018", false);
                if (this.isValidating && this.notations.get(externalEntity.notation) == null)
                    this.notations.put(externalEntity.notation, Boolean.TRUE);
            }
            externalEntity.name = str;
            externalEntity.isPE = !(simpleHashtable != this.params);
            externalEntity.isFromInternalSubset = this.inExternalPE ^ true;
            if (i != 0) {
                simpleHashtable.put(str, externalEntity);
                if (externalEntity.notation != null) {
                    this.dtdHandler.unparsedEntityDecl(str, externalEntity.publicId, externalEntity.systemId, externalEntity.notation);
                } else if (simpleHashtable == this.entities) {
                    this.dtdListener.externalEntityDecl(str, externalEntity.publicId, externalEntity.systemId);
                }
            }
        }
        maybeWhitespace();
        nextChar('>', "F-031", str);
        if (this.isValidating && inputEntity != this.in)
            error("V-013", null);
        return true;
    }

    private ExternalEntity maybeExternalID() throws IOException, SAXException {
        String str = null;
        if (peek("PUBLIC")) {
            whitespace("F-009");
            str = parsePublicId();
        } else if (!peek("SYSTEM")) {
            return null;
        }
        ExternalEntity externalEntity = new ExternalEntity(this.in);
        externalEntity.publicId = str;
        whitespace("F-008");
        externalEntity.systemId = parseSystemId();
        return externalEntity;
    }

    private String parseSystemId() throws IOException, SAXException {
        String str = getQuotedString("F-034", null);
        int i = str.indexOf(':');
        if (i == -1 || str.indexOf('/') < i) {
            String str1 = this.in.getSystemId();
            if (str1 == null)
                fatal("P-055", new Object[]{str});
            if (str.length() == 0)
                str = ".";
            str1 = str1.substring(0, str1.lastIndexOf('/') + 1);
            if (str.charAt(0) != '/') {
                str = String.valueOf(str1) + str;
            } else {
                throw new InternalError();
            }
        }
        if (str.indexOf('#') != -1)
            error("P-056", new Object[]{str});
        return str;
    }

    private void maybeTextDecl() throws IOException, SAXException {
        if (peek("<?xml")) {
            readVersion(false, "1.0");
            readEncoding(true);
            maybeWhitespace();
            if (!peek("?>"))
                fatal("P-057");
        }
    }

    private boolean externalParsedEntity(ElementDecl paramElementDecl, ExternalEntity paramExternalEntity, ElementValidator paramElementValidator) throws IOException, SAXException {
        if (!pushReader(paramExternalEntity)) {
            if (!this.isInAttribute)
                this.lexicalListener.endParsedEntity(paramExternalEntity.name, false);
            return false;
        }
        maybeTextDecl();
        content(paramElementDecl, true, paramElementValidator);
        if (!this.in.isEOF())
            fatal("P-058", new Object[]{paramExternalEntity.name});
        this.in = this.in.pop();
        if (!this.isInAttribute)
            this.lexicalListener.endParsedEntity(paramExternalEntity.name, true);
        return true;
    }

    private void externalParameterEntity(ExternalEntity paramExternalEntity) throws IOException, SAXException {
        if (this.isStandalone && this.fastStandalone)
            return;
        this.inExternalPE = true;
        pushReader(paramExternalEntity);
        InputEntity inputEntity = this.in;
        maybeTextDecl();
        while (!inputEntity.isEOF()) {
            if (this.in.isEOF()) {
                this.in = this.in.pop();
                continue;
            }
            this.doLexicalPE = false;
            if (!maybeWhitespace())
                if (!maybePEReference()) {
                    this.doLexicalPE = true;
                    if (maybeMarkupDecl() || maybeConditionalSect())
                        continue;
                    break;
                }
        }
        if (!inputEntity.isEOF())
            fatal("P-059", new Object[]{this.in.getName()});
        this.in = this.in.pop();
        this.inExternalPE = this.in.isDocument() ^ true;
        this.doLexicalPE = false;
    }

    private void readEncoding(boolean paramBoolean) throws IOException, SAXException {
        String str1 = maybeReadAttribute("encoding", paramBoolean);
        if (str1 == null)
            return;
        for (byte b = 0; b < str1.length(); b++) {
            char c = str1.charAt(b);
            if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z'))
                if (b == 0 || ((c < '0' || c > '9') && c != '-' && c != '_' && c != '.'))
                    fatal("P-060", new Object[]{new Character(c)});
        }
        String str2 = this.in.getEncoding();
        if (str2 != null && !str1.equalsIgnoreCase(str2))
            warning("P-061", new Object[]{str1, str2});
    }

    private boolean maybeNotationDecl() throws IOException, SAXException {
        InputEntity inputEntity = peekDeclaration("!NOTATION");
        if (inputEntity == null)
            return false;
        String str = getMarkupDeclname("F-019", false);
        ExternalEntity externalEntity = new ExternalEntity(this.in);
        whitespace("F-011");
        if (peek("PUBLIC")) {
            whitespace("F-009");
            externalEntity.publicId = parsePublicId();
            if (maybeWhitespace() && !peek(">"))
                externalEntity.systemId = parseSystemId();
        } else if (peek("SYSTEM")) {
            whitespace("F-008");
            externalEntity.systemId = parseSystemId();
        } else {
            fatal("P-062");
        }
        maybeWhitespace();
        nextChar('>', "F-032", str);
        if (this.isValidating && inputEntity != this.in)
            error("V-013", null);
        if (externalEntity.systemId != null && externalEntity.systemId.indexOf('#') != -1)
            error("P-056", new Object[]{externalEntity.systemId});
        Object object = this.notations.get(str);
        if (object != null && object instanceof ExternalEntity) {
            warning("P-063", new Object[]{str});
        } else if (!this.ignoreDeclarations) {
            this.notations.put(str, externalEntity);
            this.dtdHandler.notationDecl(str, externalEntity.publicId, externalEntity.systemId);
        }
        return true;
    }

    private char getc() throws IOException, SAXException {
        if (!this.inExternalPE || !this.doLexicalPE) {
            char c1 = this.in.getc();
            if (c1 == '%' && this.doLexicalPE)
                fatal("P-080");
            return c1;
        }
        while (this.in.isEOF()) {
            if (this.in.isInternal() || (this.doLexicalPE && !this.in.isDocument())) {
                this.in = this.in.pop();
                continue;
            }
            fatal("P-064", new Object[]{this.in.getName()});
        }
        char c;
        if ((c = this.in.getc()) == '%' && this.doLexicalPE) {
            String str = maybeGetName();
            if (str == null)
                fatal("P-011");
            nextChar(';', "F-021", str);
            Object object = this.params.get(str);
            pushReader(" ".toCharArray(), null, false);
            if (object instanceof InternalEntity) {
                pushReader(((InternalEntity) object).buf, str, false);
            } else if (object instanceof ExternalEntity) {
                pushReader((ExternalEntity) object);
            } else if (object == null) {
                fatal("V-022");
            } else {
                throw new InternalError();
            }
            pushReader(" ".toCharArray(), null, false);
            return this.in.getc();
        }
        return c;
    }

    private void ungetc() {
        this.in.ungetc();
    }

    private boolean peek(String paramString) throws IOException, SAXException {
        return this.in.peek(paramString, null);
    }

    private InputEntity peekDeclaration(String paramString) throws IOException, SAXException {
        if (!this.in.peekc('<'))
            return null;
        InputEntity inputEntity = this.in;
        if (this.in.peek(paramString, null))
            return inputEntity;
        this.in.ungetc();
        return null;
    }

    private void nextChar(char paramChar, String paramString1, String paramString2) throws IOException, SAXException {
        while (this.in.isEOF() && !this.in.isDocument())
            this.in = this.in.pop();
        if (!this.in.peekc(paramChar))
            fatal("P-008", new Object[]{new Character(paramChar), messages.getMessage(this.locale, paramString1), (paramString2 == null) ? "" : (String.valueOf('"') + paramString2 + '"')});
    }

    private void pushReader(char[] paramArrayOfchar, String paramString, boolean paramBoolean) throws SAXException {
        if (paramBoolean && !this.isInAttribute)
            this.lexicalListener.startParsedEntity(paramString);
        InputEntity inputEntity = InputEntity.getInputEntity(this.errHandler, this.locale);
        inputEntity.init(paramArrayOfchar, paramString, this.in, paramBoolean ^ true);
        this.in = inputEntity;
    }

    private boolean pushReader(ExternalEntity paramExternalEntity) throws SAXException, IOException {
        if (!paramExternalEntity.isPE && !this.isInAttribute)
            this.lexicalListener.startParsedEntity(paramExternalEntity.name);
        InputEntity inputEntity = InputEntity.getInputEntity(this.errHandler, this.locale);
        InputSource inputSource = paramExternalEntity.getInputSource(this.resolver);
        inputEntity.init(inputSource, paramExternalEntity.name, this.in, paramExternalEntity.isPE);
        this.in = inputEntity;
        return true;
    }

    private void warning(String paramString, Object[] paramArrayOfObject) throws SAXException {
        SAXParseException sAXParseException = new SAXParseException(messages.getMessage(this.locale, paramString, paramArrayOfObject), this.locator);
        this.errHandler.warning(sAXParseException);
    }

    void error(String paramString, Object[] paramArrayOfObject) throws SAXException {
        SAXParseException sAXParseException = new SAXParseException(messages.getMessage(this.locale, paramString, paramArrayOfObject), this.locator);
        this.errHandler.error(sAXParseException);
    }

    private void fatal(String paramString) throws SAXException {
        fatal(paramString, null, null);
    }

    private void fatal(String paramString, Object[] paramArrayOfObject) throws SAXException {
        fatal(paramString, paramArrayOfObject, null);
    }

    private void fatal(String paramString, Object[] paramArrayOfObject, Exception paramException) throws SAXException {
        SAXParseException sAXParseException = new SAXParseException(messages.getMessage(this.locale, paramString, paramArrayOfObject), this.locator, paramException);
        this.errHandler.fatalError(sAXParseException);
        throw sAXParseException;
    }

    class DocLocator implements Locator {
        private final Parser this$0;

        DocLocator(Parser this$0) {
            this.this$0 = this$0;
        }

        public String getPublicId() {
            return (this.this$0.in == null) ? null : this.this$0.in.getPublicId();
        }

        public String getSystemId() {
            return (this.this$0.in == null) ? null : this.this$0.in.getSystemId();
        }

        public int getLineNumber() {
            return (this.this$0.in == null) ? -1 : this.this$0.in.getLineNumber();
        }

        public int getColumnNumber() {
            return (this.this$0.in == null) ? -1 : this.this$0.in.getColumnNumber();
        }
    }

    static class NameCache {
        Parser.NameCacheEntry[] hashtable;

        NameCache() {
            this.hashtable = new Parser.NameCacheEntry[541];
        }

        String lookup(char[] param1ArrayOfchar, int param1Int) {
            return (lookupEntry(param1ArrayOfchar, param1Int)).name;
        }

        Parser.NameCacheEntry lookupEntry(char[] param1ArrayOfchar, int param1Int) {
            int i = 0;
            for (byte b = 0; b < param1Int; b++)
                i = i * 31 + param1ArrayOfchar[b];
            i &= Integer.MAX_VALUE;
            i %= this.hashtable.length;
            Parser.NameCacheEntry nameCacheEntry = this.hashtable[i];
            for (; nameCacheEntry != null; nameCacheEntry = nameCacheEntry.next) {
                if (nameCacheEntry.matches(param1ArrayOfchar, param1Int))
                    return nameCacheEntry;
            }
            nameCacheEntry = new Parser.NameCacheEntry();
            nameCacheEntry.chars = new char[param1Int];
            System.arraycopy(param1ArrayOfchar, 0, nameCacheEntry.chars, 0, param1Int);
            nameCacheEntry.name = new String(nameCacheEntry.chars);
            nameCacheEntry.name = nameCacheEntry.name.intern();
            nameCacheEntry.next = this.hashtable[i];
            this.hashtable[i] = nameCacheEntry;
            return nameCacheEntry;
        }
    }

    static class NameCacheEntry {
        String name;
        char[] chars;
        NameCacheEntry next;

        boolean matches(char[] param1ArrayOfchar, int param1Int) {
            if (this.chars.length != param1Int)
                return false;
            for (byte b = 0; b < param1Int; b++) {
                if (param1ArrayOfchar[b] != this.chars[b])
                    return false;
            }
            return true;
        }
    }

    static class ListenerBase extends HandlerBase implements DtdEventListener, LexicalEventListener {
        public void startDtd(String param1String) {
        }

        public void externalDtdDecl(String param1String1, String param1String2) {
        }

        public void internalDtdDecl(String param1String) {
        }

        public void externalEntityDecl(String param1String1, String param1String2, String param1String3) {
        }

        public void internalEntityDecl(String param1String1, String param1String2) {
        }

        public void elementDecl(String param1String1, String param1String2) {
        }

        public void attributeDecl(String param1String1, String param1String2, String param1String3, String[] param1ArrayOfString, String param1String4, boolean param1Boolean1,
            boolean param1Boolean2) {
        }

        public void endDtd() {
        }

        public void startParsedEntity(String param1String) {
        }

        public void endParsedEntity(String param1String, boolean param1Boolean) {
        }

        public void startCDATA() {
        }

        public void endCDATA() {
        }

        public void comment(String param1String) {
        }
    }

    static final Catalog messages = new Catalog();
    static Class class$com$sun$xml$parser$Parser;

    static final class Catalog extends MessageCatalog {
        Catalog() {
            super((Parser.class$com$sun$xml$parser$Parser != null) ? Parser.class$com$sun$xml$parser$Parser : (Parser.class$com$sun$xml$parser$Parser = Parser.class$("com.sun.xml.parser.Parser")));
        }
    }
}
