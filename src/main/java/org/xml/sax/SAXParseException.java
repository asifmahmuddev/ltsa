package org.xml.sax;

public class SAXParseException extends SAXException {
    private String publicId;
    private String systemId;
    private int lineNumber;
    private int columnNumber;

    public SAXParseException(String paramString, Locator paramLocator) {
        super(paramString);
        this.publicId = paramLocator.getPublicId();
        this.systemId = paramLocator.getSystemId();
        this.lineNumber = paramLocator.getLineNumber();
        this.columnNumber = paramLocator.getColumnNumber();
    }

    public SAXParseException(String paramString, Locator paramLocator, Exception paramException) {
        super(paramString, paramException);
        this.publicId = paramLocator.getPublicId();
        this.systemId = paramLocator.getSystemId();
        this.lineNumber = paramLocator.getLineNumber();
        this.columnNumber = paramLocator.getColumnNumber();
    }

    public SAXParseException(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2) {
        super(paramString1);
        this.publicId = paramString2;
        this.systemId = paramString3;
        this.lineNumber = paramInt1;
        this.columnNumber = paramInt2;
    }

    public SAXParseException(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, Exception paramException) {
        super(paramString1, paramException);
        this.publicId = paramString2;
        this.systemId = paramString3;
        this.lineNumber = paramInt1;
        this.columnNumber = paramInt2;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }
}
