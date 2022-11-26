package uk.ac.ic.doc.natutil;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class MacroExpander {
    private static final int SYNTAX_ESCAPE = 92;
    private static final int SYNTAX_SUBST = 36;
    private static final int SYNTAX_BEGIN = 123;
    private static final int SYNTAX_END = 125;
    private static final int SYNTAX_DEFAULT = 61;
    private Map _macro_table = new HashMap();

    public void addMacro(String paramString1, String paramString2) throws MacroException {
        if (this._macro_table.containsKey(paramString1))
            throw new MacroException("macro \"" + paramString1 + "\" already defined");
        this._macro_table.put(paramString1, paramString2);
    }

    public void removeMacro(String paramString) {
        this._macro_table.remove(paramString);
    }

    public String expandMacros(String paramString) throws MacroException {
        StringReader stringReader = new StringReader(paramString);
        StringWriter stringWriter = new StringWriter();
        expandMacros(stringReader, stringWriter);
        return stringWriter.toString();
    }

    public void expandMacros(Reader paramReader, Writer paramWriter) throws MacroException {
        try {
            int i;
            while ((i = readMacroChar(paramReader)) != -1) {
                switch (i) {
                    case -36 :
                        expandNextMacro(paramReader, paramWriter);
                        continue;
                    case -125 :
                    case -123 :
                    case -61 :
                        paramWriter.write(-i);
                        continue;
                }
                paramWriter.write(i);
            }
        } catch (IOException iOException) {
            throw new MacroException("I/O exception while reading input: " + iOException.getMessage());
        }
    }

    private void expandNextMacro(Reader paramReader, Writer paramWriter) throws IOException, MacroException {
        if (paramReader.read() != 123)
            throw new MacroException("syntax error in macro: 123 expected");
        String str1 = null;
        String str2 = null;
        StringBuffer stringBuffer = new StringBuffer();
        int i;
        while ((i = readMacroChar(paramReader)) != -125) {
            switch (i) {
                case -123 :
                case -36 :
                    throw new MacroException("syntax error in macro: \"" + -i + "\" character not expected");
                case -61 :
                    str1 = stringBuffer.toString();
                    stringBuffer.setLength(0);
                    continue;
            }
            stringBuffer.append((char) i);
        }
        if (str1 == null) {
            str1 = stringBuffer.toString();
        } else {
            str2 = stringBuffer.toString();
        }
        String str3 = (String) this._macro_table.get(str1);
        if (str3 == null)
            if (str2 != null) {
                str3 = str2;
            } else {
                throw new MacroException("macro \"" + str1 + "\" not defined");
            }
        paramWriter.write(str3);
    }

    private int readMacroChar(Reader paramReader) throws IOException, MacroException {
        int i = paramReader.read();
        switch (i) {
            case 92 :
                i = paramReader.read();
                if (i == -1)
                    throw new MacroException("premature end of input");
                return i;
            case 36 :
            case 61 :
            case 123 :
            case 125 :
                return -i;
        }
        return i;
    }
}
