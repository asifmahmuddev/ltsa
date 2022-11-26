package com.sun.xml.tree;

import java.util.Locale;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

public interface ParseContext {
    ErrorHandler getErrorHandler();

    Locale getLocale();

    Locator getLocator();
}
