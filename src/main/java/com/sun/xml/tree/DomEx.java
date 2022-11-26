package com.sun.xml.tree;

import java.util.Locale;
import org.w3c.dom.DOMException;

class DomEx extends DOMException {
    static String messageString(Locale paramLocale, int paramInt) {
        switch (paramInt) {
            case 1 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-000");
            case 2 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-001");
            case 3 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-002");
            case 4 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-003");
            case 5 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-004");
            case 6 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-005");
            case 7 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-006");
            case 8 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-007");
            case 9 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-008");
            case 10 :
                return XmlDocument.catalog.getMessage(paramLocale, "D-009");
        }
        return XmlDocument.catalog.getMessage(paramLocale, "D-010");
    }

    public DomEx(short paramShort) {
        super(paramShort, messageString(Locale.getDefault(), paramShort));
    }

    public DomEx(Locale paramLocale, short paramShort) {
        super(paramShort, messageString(paramLocale, paramShort));
    }
}
