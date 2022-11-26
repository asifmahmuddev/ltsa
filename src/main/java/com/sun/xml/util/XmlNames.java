package com.sun.xml.util;

public class XmlNames {
    public static boolean isName(String paramString) {
        if (paramString == null)
            return false;
        char c = paramString.charAt(0);
        if (!XmlChars.isLetter(c) && c != '_' && c != ':')
            return false;
        for (byte b = 1; b < paramString.length(); b++) {
            if (!XmlChars.isNameChar(paramString.charAt(b)))
                return false;
        }
        return true;
    }

    public static boolean isUnqualifiedName(String paramString) {
        if (paramString == null)
            return false;
        char c = paramString.charAt(0);
        if (!XmlChars.isLetter(c) && c != '_')
            return false;
        for (byte b = 1; b < paramString.length(); b++) {
            if (!XmlChars.isNCNameChar(paramString.charAt(b)))
                return false;
        }
        return true;
    }

    public static boolean isQualifiedName(String paramString) {
        if (paramString == null)
            return false;
        int i = paramString.indexOf(':');
        int j = paramString.lastIndexOf(':');
        if (i < 0 || j != i)
            return false;
        return !(!isUnqualifiedName(paramString.substring(0, i - 1)) || !isUnqualifiedName(paramString.substring(i + 1)));
    }

    public static boolean isNmtoken(String paramString) {
        int i = paramString.length();
        for (byte b = 0; b < i; b++) {
            if (!XmlChars.isNameChar(paramString.charAt(b)))
                return false;
        }
        return true;
    }

    public static boolean isNCNmtoken(String paramString) {
        return !(!isNmtoken(paramString) || paramString.indexOf(':') >= 0);
    }
}
