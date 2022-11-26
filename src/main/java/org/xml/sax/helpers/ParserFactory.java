package org.xml.sax.helpers;

import org.xml.sax.Parser;

public class ParserFactory {
    private static final String DEFAULT_PARSER = "com.sun.xml.parser.Parser";

    public static Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException {
        String str = "com.sun.xml.parser.Parser";
        try {
            str = System.getProperty("org.xml.sax.parser", "com.sun.xml.parser.Parser");
        } catch (SecurityException securityException) {
        }
        return makeParser(str);
    }

    public static Parser makeParser(String paramString) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException {
        return (Parser) Class.forName(paramString).newInstance();
    }
}
