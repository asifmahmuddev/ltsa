package uk.ac.ic.doc.scenebeans.animation.parse;

import org.w3c.dom.Element;

class XMLUtil {
    static String getRequiredAttribute(Element paramElement, String paramString) throws AnimationParseException {
        String str = paramElement.getAttribute(paramString);
        if (str.equals(""))
            throw new AnimationParseException("required attribute \"" + paramString + "\" not found");
        return str;
    }

    static String getOptionalAttribute(Element paramElement, String paramString) {
        String str = paramElement.getAttribute(paramString);
        if (str.equals(""))
            return null;
        return str;
    }

    static void checkElementType(Element paramElement, String paramString) throws AnimationParseException {
        if (!paramElement.getTagName().equals(paramString))
            throw new AnimationParseException(paramString + " element expected");
    }
}
