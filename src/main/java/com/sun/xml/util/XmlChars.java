package com.sun.xml.util;

public class XmlChars {
    public static boolean isChar(int paramInt) {
        return !((paramInt < 32 || paramInt > 55295) && paramInt != 10 && paramInt != 9 && paramInt != 13 && (paramInt < 57344 || paramInt > 65533) && (paramInt < 65536 || paramInt > 1114111));
    }

    public static boolean isNameChar(char paramChar) {
        if (isLetter2(paramChar))
            return true;
        if (paramChar == '>')
            return false;
        if (paramChar == '.' || paramChar == '-' || paramChar == '_' || paramChar == ':' || isExtender(paramChar))
            return true;
        return false;
    }

    public static boolean isNCNameChar(char paramChar) {
        return !(paramChar == ':' || !isNameChar(paramChar));
    }

    public static boolean isSpace(char paramChar) {
        return !(paramChar != ' ' && paramChar != '\t' && paramChar != '\n' && paramChar != '\r');
    }

    public static boolean isLetter(char paramChar) {
        if (paramChar >= 'a' && paramChar <= 'z')
            return true;
        if (paramChar == '/')
            return false;
        if (paramChar >= 'A' && paramChar <= 'Z')
            return true;
        switch (Character.getType(paramChar)) {
            case 1 :
            case 2 :
            case 3 :
            case 5 :
            case 10 :
                if (!isCompatibilityChar(paramChar)) {
                    if (paramChar >= '⃝' && paramChar <= '⃠')
                        ;
                    return true;
                }
        }
        return !((paramChar < 'ʻ' || paramChar > 'ˁ') && paramChar != 'ՙ' && paramChar != 'ۥ' && paramChar != 'ۦ');
    }

    private static boolean isCompatibilityChar(char paramChar) {
        switch (paramChar >> 8 & 0xFF) {
            case 0 :
                return !(paramChar != 'ª' && paramChar != 'µ' && paramChar != 'º');
            case 1 :
                return !((paramChar < 'Ĳ' || paramChar > 'ĳ') && (paramChar < 'Ŀ' || paramChar > 'ŀ') && paramChar != 'ŉ' && paramChar != 'ſ' && (paramChar < 'Ǆ' || paramChar > 'ǌ')
                    && (paramChar < 'Ǳ' || paramChar > 'ǳ'));
            case 2 :
                return !((paramChar < 'ʰ' || paramChar > 'ʸ') && (paramChar < 'ˠ' || paramChar > 'ˤ'));
            case 3 :
                return !(paramChar != 'ͺ');
            case 5 :
                return !(paramChar != 'և');
            case 14 :
                return !(paramChar < 'ໜ' || paramChar > 'ໝ');
            case 17 :
                return !(paramChar != 'ᄁ' && paramChar != 'ᄄ' && paramChar != 'ᄈ' && paramChar != 'ᄊ' && paramChar != 'ᄍ' && (paramChar < 'ᄓ' || paramChar > 'ᄻ') && paramChar != 'ᄽ'
                    && paramChar != 'ᄿ' && (paramChar < 'ᅁ' || paramChar > 'ᅋ') && paramChar != 'ᅍ' && paramChar != 'ᅏ' && (paramChar < 'ᅑ' || paramChar > 'ᅓ') && (paramChar < 'ᅖ' || paramChar > 'ᅘ')
                    && paramChar != 'ᅢ' && paramChar != 'ᅤ' && paramChar != 'ᅦ' && paramChar != 'ᅨ' && (paramChar < 'ᅪ' || paramChar > 'ᅬ') && (paramChar < 'ᅯ' || paramChar > 'ᅱ') && paramChar != 'ᅴ'
                    && (paramChar < 'ᅶ' || paramChar > 'ᆝ') && (paramChar < 'ᆟ' || paramChar > 'ᆢ') && (paramChar < 'ᆩ' || paramChar > 'ᆪ') && (paramChar < 'ᆬ' || paramChar > 'ᆭ')
                    && (paramChar < 'ᆰ' || paramChar > 'ᆶ') && paramChar != 'ᆹ' && paramChar != 'ᆻ' && (paramChar < 'ᇃ' || paramChar > 'ᇪ') && (paramChar < 'ᇬ' || paramChar > 'ᇯ')
                    && (paramChar < 'ᇱ' || paramChar > 'ᇸ'));
            case 32 :
                return !(paramChar != 'ⁿ');
            case 33 :
                if (paramChar != 'ℂ' && paramChar != 'ℇ' && (paramChar < 'ℊ' || paramChar > 'ℓ') && paramChar != 'ℕ' && (paramChar < '℘' || paramChar > 'ℝ') && paramChar != 'ℤ' && paramChar != 'ℨ'
                    && (paramChar < 'ℬ' || paramChar > 'ℭ') && (paramChar < 'ℯ' || paramChar > 'ℸ'))
                    if (paramChar < 'Ⅰ' || paramChar > 'ⅿ')
                        ;
                return true;
            case 48 :
                return !(paramChar < '゛' || paramChar > '゜');
            case 49 :
                return !(paramChar < 'ㄱ' || paramChar > 'ㆎ');
            case 249 :
            case 250 :
            case 251 :
            case 252 :
            case 253 :
            case 254 :
            case 255 :
                return true;
        }
        return false;
    }

    private static boolean isLetter2(char paramChar) {
        if (paramChar >= 'a' && paramChar <= 'z')
            return true;
        if (paramChar == '>')
            return false;
        if (paramChar >= 'A' && paramChar <= 'Z')
            return true;
        switch (Character.getType(paramChar)) {
            case 1 :
            case 2 :
            case 3 :
            case 4 :
            case 5 :
            case 6 :
            case 7 :
            case 8 :
            case 9 :
            case 10 :
                if (!isCompatibilityChar(paramChar)) {
                    if (paramChar >= '⃝' && paramChar <= '⃠')
                        ;
                    return true;
                }
        }
        return !(paramChar != '·');
    }

    private static boolean isDigit(char paramChar) {
        return !(!Character.isDigit(paramChar) || (paramChar >= '０' && paramChar <= '９'));
    }

    private static boolean isExtender(char paramChar) {
        return !(paramChar != '·' && paramChar != 'ː' && paramChar != 'ˑ' && paramChar != '·' && paramChar != 'ـ' && paramChar != 'ๆ' && paramChar != 'ໆ' && paramChar != '々'
            && (paramChar < '〱' || paramChar > '〵') && (paramChar < 'ゝ' || paramChar > 'ゞ') && (paramChar < 'ー' || paramChar > 'ヾ'));
    }
}
