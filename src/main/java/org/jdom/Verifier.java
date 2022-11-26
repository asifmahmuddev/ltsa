package org.jdom;

import java.util.Iterator;
import java.util.List;

public final class Verifier {
    private static final String CVS_ID = "@(#) $RCSfile: Verifier.java,v $ $Revision: 1.32 $ $Date: 2002/03/12 07:57:06 $ $Name: jdom_1_0_b8 $";

    public static final String checkElementName(String name) {
        String reason;
        if ((reason = checkXMLName(name)) != null)
            return reason;
        if (name.indexOf(":") != -1)
            return "Element names cannot contain colons";
        return null;
    }

    public static final String checkAttributeName(String name) {
        String reason;
        if ((reason = checkXMLName(name)) != null)
            return reason;
        if (name.equals("xml:space") || name.equals("xml:lang"))
            return null;
        if (name.indexOf(":") != -1)
            return "Attribute names cannot contain colons";
        if (name.equals("xmlns"))
            return "An Attribute name may not be \"xmlns\"; use the Namespace class to manage namespaces";
        return null;
    }

    public static final String checkCharacterData(String text) {
        if (text == null)
            return "A null is not a legal XML value";
        for (int i = 0, len = text.length(); i < len; i++) {
            if (!isXMLCharacter(text.charAt(i)))
                return "0x" + Integer.toHexString(text.charAt(i)) + " is not a legal XML character";
        }
        return null;
    }

    public static final String checkCDATASection(String data) {
        String reason = null;
        if ((reason = checkCharacterData(data)) != null)
            return reason;
        if (data.indexOf("]]>") != -1)
            return "CDATA cannot internally contain a CDATA ending delimiter (]]>)";
        return null;
    }

    public static final String checkNamespacePrefix(String prefix) {
        if (prefix == null || prefix.equals(""))
            return null;
        char first = prefix.charAt(0);
        if (isXMLDigit(first))
            return "Namespace prefixes cannot begin with a number";
        if (first == '$')
            return "Namespace prefixes cannot begin with a dollar sign ($)";
        if (first == '-')
            return "Namespace prefixes cannot begin with a hyphen (-)";
        if (first == '.')
            return "Namespace prefixes cannot begin with a period (.)";
        if (prefix.toLowerCase().startsWith("xml"))
            return "Namespace prefixes cannot begin with \"xml\" in any combination of case";
        for (int i = 0, len = prefix.length(); i < len; i++) {
            char c = prefix.charAt(i);
            if (!isXMLNameCharacter(c))
                return "Namespace prefixes cannot contain the character \"" + c + "\"";
        }
        if (prefix.indexOf(":") != -1)
            return "Namespace prefixes cannot contain colons";
        return null;
    }

    public static final String checkNamespaceURI(String uri) {
        if (uri == null || uri.equals(""))
            return null;
        char first = uri.charAt(0);
        if (Character.isDigit(first))
            return "Namespace URIs cannot begin with a number";
        if (first == '$')
            return "Namespace URIs cannot begin with a dollar sign ($)";
        if (first == '-')
            return "Namespace URIs cannot begin with a hyphen (-)";
        return null;
    }

    public static final String checkNamespaceCollision(Namespace namespace, Namespace other) {
        if (namespace == Namespace.NO_NAMESPACE || other == Namespace.NO_NAMESPACE)
            return null;
        String reason = null;
        String p1 = namespace.getPrefix();
        String u1 = namespace.getURI();
        String p2 = other.getPrefix();
        String u2 = other.getURI();
        if (p1.equals(p2) && !u1.equals(u2))
            reason = "The namespace prefix \"" + p1 + "\" collides";
        return reason;
    }

    public static final String checkNamespaceCollision(Attribute attribute, Element element) {
        Namespace namespace = attribute.getNamespace();
        String prefix = namespace.getPrefix();
        if ("".equals(prefix))
            return null;
        return checkNamespaceCollision(namespace, element);
    }

    public static final String checkNamespaceCollision(Namespace namespace, Element element) {
        String reason = checkNamespaceCollision(namespace, element.getNamespace());
        if (reason != null)
            return String.valueOf(reason) + " with the element namespace prefix";
        reason = checkNamespaceCollision(namespace, element.getAdditionalNamespaces());
        if (reason != null)
            return reason;
        reason = checkNamespaceCollision(namespace, element.getAttributes());
        if (reason != null)
            return reason;
        return null;
    }

    public static final String checkNamespaceCollision(Namespace namespace, Attribute attribute) {
        String reason = checkNamespaceCollision(namespace, attribute.getNamespace());
        if (reason != null)
            reason = String.valueOf(reason) + " with an attribute namespace prefix on the element";
        return reason;
    }

    public static final String checkNamespaceCollision(Namespace namespace, List list) {
        if (list == null)
            return null;
        String reason = null;
        Iterator i = list.iterator();
        while (reason == null && i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Attribute) {
                reason = checkNamespaceCollision(namespace, (Attribute) obj);
                continue;
            }
            if (obj instanceof Element) {
                reason = checkNamespaceCollision(namespace, (Element) obj);
                continue;
            }
            if (obj instanceof Namespace) {
                reason = checkNamespaceCollision(namespace, (Namespace) obj);
                if (reason != null)
                    reason = String.valueOf(reason) + " with an additional namespace declared by the element";
            }
        }
        return reason;
    }

    public static final String checkProcessingInstructionTarget(String target) {
        String reason;
        if ((reason = checkXMLName(target)) != null)
            return reason;
        if (target.indexOf(":") != -1)
            return "Processing instruction targets cannot contain colons";
        if (target.equalsIgnoreCase("xml"))
            return "Processing instructions cannot have a target of \"xml\" in any combination of case. (Note that the \"<?xml ... ?>\" declaration at the beginning of a document is not a processing instruction and should not be added as one; it is written automatically during output, e.g. by XMLOutputter.)";
        return null;
    }

    public static final String checkCommentData(String data) {
        String reason = null;
        if ((reason = checkCharacterData(data)) != null)
            return reason;
        if (data.indexOf("--") != -1)
            return "Comments cannot contain double hyphens (--)";
        return null;
    }

    private static boolean isXMLPublicIDCharacter(char c) {
        if (c >= 'a' && c <= 'z')
            return true;
        if (c >= '?' && c <= 'Z')
            return true;
        if (c >= '\'' && c <= ';')
            return true;
        if (c == ' ')
            return true;
        if (c == '!')
            return true;
        if (c == '=')
            return true;
        if (c == '#')
            return true;
        if (c == '$')
            return true;
        if (c == '_')
            return true;
        if (c == '%')
            return true;
        if (c == '\n')
            return true;
        if (c == '\r')
            return true;
        if (c == '\t')
            return true;
        return false;
    }

    public static final String checkPublicID(String publicID) {
        String reason = null;
        if (publicID == null)
            return null;
        for (int i = 0; i < publicID.length(); i++) {
            char c = publicID.charAt(i);
            if (!isXMLPublicIDCharacter(c)) {
                reason = String.valueOf(c) + " is not a legal character in public IDs";
                break;
            }
        }
        return reason;
    }

    public static final String checkSystemLiteral(String systemLiteral) {
        String reason = null;
        if (systemLiteral == null)
            return null;
        if (systemLiteral.indexOf('\'') != -1 && systemLiteral.indexOf('"') != -1) {
            reason = "System literals cannot simultaneously contain both single and double quotes.";
        } else {
            reason = checkCharacterData(systemLiteral);
        }
        return reason;
    }

    public static String checkXMLName(String name) {
        if (name == null || name.length() == 0 || name.trim().equals(""))
            return "XML names cannot be null or empty";
        char first = name.charAt(0);
        if (!isXMLNameStartCharacter(first))
            return "XML names cannot begin with the character \"" + first + "\"";
        for (int i = 0, len = name.length(); i < len; i++) {
            char c = name.charAt(i);
            if (!isXMLNameCharacter(c))
                return "XML names cannot contain the character \"" + c + "\"";
        }
        return null;
    }

    public static boolean isXMLCharacter(char c) {
        if (c == '\n')
            return true;
        if (c == '\r')
            return true;
        if (c == '\t')
            return true;
        if (c < ' ')
            return false;
        if (c <= '퟿')
            return true;
        if (c < '')
            return false;
        if (c <= '�')
            return true;
        if (c < 65536)
            return false;
        if (c <= 1114111)
            return true;
        return false;
    }

    public static boolean isXMLNameCharacter(char c) {
        return !(!isXMLLetter(c) && !isXMLDigit(c) && c != '.' && c != '-' && c != '_' && c != ':' && !isXMLCombiningChar(c) && !isXMLExtender(c));
    }

    public static boolean isXMLNameStartCharacter(char c) {
        return !(!isXMLLetter(c) && c != '_' && c != ':');
    }

    public static boolean isXMLLetterOrDigit(char c) {
        return !(!isXMLLetter(c) && !isXMLDigit(c));
    }

    public static boolean isXMLLetter(char c) {
        if (c < 'A')
            return false;
        if (c <= 'Z')
            return true;
        if (c < 'a')
            return false;
        if (c <= 'z')
            return true;
        if (c < 'À')
            return false;
        if (c <= 'Ö')
            return true;
        if (c < 'Ø')
            return false;
        if (c <= 'ö')
            return true;
        if (c < 'ø')
            return false;
        if (c <= 'ÿ')
            return true;
        if (c < 'Ā')
            return false;
        if (c <= 'ı')
            return true;
        if (c < 'Ĵ')
            return false;
        if (c <= 'ľ')
            return true;
        if (c < 'Ł')
            return false;
        if (c <= 'ň')
            return true;
        if (c < 'Ŋ')
            return false;
        if (c <= 'ž')
            return true;
        if (c < 'ƀ')
            return false;
        if (c <= 'ǃ')
            return true;
        if (c < 'Ǎ')
            return false;
        if (c <= 'ǰ')
            return true;
        if (c < 'Ǵ')
            return false;
        if (c <= 'ǵ')
            return true;
        if (c < 'Ǻ')
            return false;
        if (c <= 'ȗ')
            return true;
        if (c < 'ɐ')
            return false;
        if (c <= 'ʨ')
            return true;
        if (c < 'ʻ')
            return false;
        if (c <= 'ˁ')
            return true;
        if (c == 'Ά')
            return true;
        if (c < 'Έ')
            return false;
        if (c <= 'Ί')
            return true;
        if (c == 'Ό')
            return true;
        if (c < 'Ύ')
            return false;
        if (c <= 'Ρ')
            return true;
        if (c < 'Σ')
            return false;
        if (c <= 'ώ')
            return true;
        if (c < 'ϐ')
            return false;
        if (c <= 'ϖ')
            return true;
        if (c == 'Ϛ')
            return true;
        if (c == 'Ϝ')
            return true;
        if (c == 'Ϟ')
            return true;
        if (c == 'Ϡ')
            return true;
        if (c < 'Ϣ')
            return false;
        if (c <= 'ϳ')
            return true;
        if (c < 'Ё')
            return false;
        if (c <= 'Ќ')
            return true;
        if (c < 'Ў')
            return false;
        if (c <= 'я')
            return true;
        if (c < 'ё')
            return false;
        if (c <= 'ќ')
            return true;
        if (c < 'ў')
            return false;
        if (c <= 'ҁ')
            return true;
        if (c < 'Ґ')
            return false;
        if (c <= 'ӄ')
            return true;
        if (c < 'Ӈ')
            return false;
        if (c <= 'ӈ')
            return true;
        if (c < 'Ӌ')
            return false;
        if (c <= 'ӌ')
            return true;
        if (c < 'Ӑ')
            return false;
        if (c <= 'ӫ')
            return true;
        if (c < 'Ӯ')
            return false;
        if (c <= 'ӵ')
            return true;
        if (c < 'Ӹ')
            return false;
        if (c <= 'ӹ')
            return true;
        if (c < 'Ա')
            return false;
        if (c <= 'Ֆ')
            return true;
        if (c == 'ՙ')
            return true;
        if (c < 'ա')
            return false;
        if (c <= 'ֆ')
            return true;
        if (c < 'א')
            return false;
        if (c <= 'ת')
            return true;
        if (c < 'װ')
            return false;
        if (c <= 'ײ')
            return true;
        if (c < 'ء')
            return false;
        if (c <= 'غ')
            return true;
        if (c < 'ف')
            return false;
        if (c <= 'ي')
            return true;
        if (c < 'ٱ')
            return false;
        if (c <= 'ڷ')
            return true;
        if (c < 'ں')
            return false;
        if (c <= 'ھ')
            return true;
        if (c < 'ۀ')
            return false;
        if (c <= 'ێ')
            return true;
        if (c < 'ې')
            return false;
        if (c <= 'ۓ')
            return true;
        if (c == 'ە')
            return true;
        if (c < 'ۥ')
            return false;
        if (c <= 'ۦ')
            return true;
        if (c < 'अ')
            return false;
        if (c <= 'ह')
            return true;
        if (c == 'ऽ')
            return true;
        if (c < 'क़')
            return false;
        if (c <= 'ॡ')
            return true;
        if (c < 'অ')
            return false;
        if (c <= 'ঌ')
            return true;
        if (c < 'এ')
            return false;
        if (c <= 'ঐ')
            return true;
        if (c < 'ও')
            return false;
        if (c <= 'ন')
            return true;
        if (c < 'প')
            return false;
        if (c <= 'র')
            return true;
        if (c == 'ল')
            return true;
        if (c < 'শ')
            return false;
        if (c <= 'হ')
            return true;
        if (c < 'ড়')
            return false;
        if (c <= 'ঢ়')
            return true;
        if (c < 'য়')
            return false;
        if (c <= 'ৡ')
            return true;
        if (c < 'ৰ')
            return false;
        if (c <= 'ৱ')
            return true;
        if (c < 'ਅ')
            return false;
        if (c <= 'ਊ')
            return true;
        if (c < 'ਏ')
            return false;
        if (c <= 'ਐ')
            return true;
        if (c < 'ਓ')
            return false;
        if (c <= 'ਨ')
            return true;
        if (c < 'ਪ')
            return false;
        if (c <= 'ਰ')
            return true;
        if (c < 'ਲ')
            return false;
        if (c <= 'ਲ਼')
            return true;
        if (c < 'ਵ')
            return false;
        if (c <= 'ਸ਼')
            return true;
        if (c < 'ਸ')
            return false;
        if (c <= 'ਹ')
            return true;
        if (c < 'ਖ਼')
            return false;
        if (c <= 'ੜ')
            return true;
        if (c == 'ਫ਼')
            return true;
        if (c < 'ੲ')
            return false;
        if (c <= 'ੴ')
            return true;
        if (c < 'અ')
            return false;
        if (c <= 'ઋ')
            return true;
        if (c == 'ઍ')
            return true;
        if (c < 'એ')
            return false;
        if (c <= 'ઑ')
            return true;
        if (c < 'ઓ')
            return false;
        if (c <= 'ન')
            return true;
        if (c < 'પ')
            return false;
        if (c <= 'ર')
            return true;
        if (c < 'લ')
            return false;
        if (c <= 'ળ')
            return true;
        if (c < 'વ')
            return false;
        if (c <= 'હ')
            return true;
        if (c == 'ઽ')
            return true;
        if (c == 'ૠ')
            return true;
        if (c < 'ଅ')
            return false;
        if (c <= 'ଌ')
            return true;
        if (c < 'ଏ')
            return false;
        if (c <= 'ଐ')
            return true;
        if (c < 'ଓ')
            return false;
        if (c <= 'ନ')
            return true;
        if (c < 'ପ')
            return false;
        if (c <= 'ର')
            return true;
        if (c < 'ଲ')
            return false;
        if (c <= 'ଳ')
            return true;
        if (c < 'ଶ')
            return false;
        if (c <= 'ହ')
            return true;
        if (c == 'ଽ')
            return true;
        if (c < 'ଡ଼')
            return false;
        if (c <= 'ଢ଼')
            return true;
        if (c < 'ୟ')
            return false;
        if (c <= 'ୡ')
            return true;
        if (c < 'அ')
            return false;
        if (c <= 'ஊ')
            return true;
        if (c < 'எ')
            return false;
        if (c <= 'ஐ')
            return true;
        if (c < 'ஒ')
            return false;
        if (c <= 'க')
            return true;
        if (c < 'ங')
            return false;
        if (c <= 'ச')
            return true;
        if (c == 'ஜ')
            return true;
        if (c < 'ஞ')
            return false;
        if (c <= 'ட')
            return true;
        if (c < 'ண')
            return false;
        if (c <= 'த')
            return true;
        if (c < 'ந')
            return false;
        if (c <= 'ப')
            return true;
        if (c < 'ம')
            return false;
        if (c <= 'வ')
            return true;
        if (c < 'ஷ')
            return false;
        if (c <= 'ஹ')
            return true;
        if (c < 'అ')
            return false;
        if (c <= 'ఌ')
            return true;
        if (c < 'ఎ')
            return false;
        if (c <= 'ఐ')
            return true;
        if (c < 'ఒ')
            return false;
        if (c <= 'న')
            return true;
        if (c < 'ప')
            return false;
        if (c <= 'ళ')
            return true;
        if (c < 'వ')
            return false;
        if (c <= 'హ')
            return true;
        if (c < 'ౠ')
            return false;
        if (c <= 'ౡ')
            return true;
        if (c < 'ಅ')
            return false;
        if (c <= 'ಌ')
            return true;
        if (c < 'ಎ')
            return false;
        if (c <= 'ಐ')
            return true;
        if (c < 'ಒ')
            return false;
        if (c <= 'ನ')
            return true;
        if (c < 'ಪ')
            return false;
        if (c <= 'ಳ')
            return true;
        if (c < 'ವ')
            return false;
        if (c <= 'ಹ')
            return true;
        if (c == 'ೞ')
            return true;
        if (c < 'ೠ')
            return false;
        if (c <= 'ೡ')
            return true;
        if (c < 'അ')
            return false;
        if (c <= 'ഌ')
            return true;
        if (c < 'എ')
            return false;
        if (c <= 'ഐ')
            return true;
        if (c < 'ഒ')
            return false;
        if (c <= 'ന')
            return true;
        if (c < 'പ')
            return false;
        if (c <= 'ഹ')
            return true;
        if (c < 'ൠ')
            return false;
        if (c <= 'ൡ')
            return true;
        if (c < 'ก')
            return false;
        if (c <= 'ฮ')
            return true;
        if (c == 'ะ')
            return true;
        if (c < 'า')
            return false;
        if (c <= 'ำ')
            return true;
        if (c < 'เ')
            return false;
        if (c <= 'ๅ')
            return true;
        if (c < 'ກ')
            return false;
        if (c <= 'ຂ')
            return true;
        if (c == 'ຄ')
            return true;
        if (c < 'ງ')
            return false;
        if (c <= 'ຈ')
            return true;
        if (c == 'ຊ')
            return true;
        if (c == 'ຍ')
            return true;
        if (c < 'ດ')
            return false;
        if (c <= 'ທ')
            return true;
        if (c < 'ນ')
            return false;
        if (c <= 'ຟ')
            return true;
        if (c < 'ມ')
            return false;
        if (c <= 'ຣ')
            return true;
        if (c == 'ລ')
            return true;
        if (c == 'ວ')
            return true;
        if (c < 'ສ')
            return false;
        if (c <= 'ຫ')
            return true;
        if (c < 'ອ')
            return false;
        if (c <= 'ຮ')
            return true;
        if (c == 'ະ')
            return true;
        if (c < 'າ')
            return false;
        if (c <= 'ຳ')
            return true;
        if (c == 'ຽ')
            return true;
        if (c < 'ເ')
            return false;
        if (c <= 'ໄ')
            return true;
        if (c < 'ཀ')
            return false;
        if (c <= 'ཇ')
            return true;
        if (c < 'ཉ')
            return false;
        if (c <= 'ཀྵ')
            return true;
        if (c < 'Ⴀ')
            return false;
        if (c <= 'Ⴥ')
            return true;
        if (c < 'ა')
            return false;
        if (c <= 'ჶ')
            return true;
        if (c == 'ᄀ')
            return true;
        if (c < 'ᄂ')
            return false;
        if (c <= 'ᄃ')
            return true;
        if (c < 'ᄅ')
            return false;
        if (c <= 'ᄇ')
            return true;
        if (c == 'ᄉ')
            return true;
        if (c < 'ᄋ')
            return false;
        if (c <= 'ᄌ')
            return true;
        if (c < 'ᄎ')
            return false;
        if (c <= 'ᄒ')
            return true;
        if (c == 'ᄼ')
            return true;
        if (c == 'ᄾ')
            return true;
        if (c == 'ᅀ')
            return true;
        if (c == 'ᅌ')
            return true;
        if (c == 'ᅎ')
            return true;
        if (c == 'ᅐ')
            return true;
        if (c < 'ᅔ')
            return false;
        if (c <= 'ᅕ')
            return true;
        if (c == 'ᅙ')
            return true;
        if (c < 'ᅟ')
            return false;
        if (c <= 'ᅡ')
            return true;
        if (c == 'ᅣ')
            return true;
        if (c == 'ᅥ')
            return true;
        if (c == 'ᅧ')
            return true;
        if (c == 'ᅩ')
            return true;
        if (c < 'ᅭ')
            return false;
        if (c <= 'ᅮ')
            return true;
        if (c < 'ᅲ')
            return false;
        if (c <= 'ᅳ')
            return true;
        if (c == 'ᅵ')
            return true;
        if (c == 'ᆞ')
            return true;
        if (c == 'ᆨ')
            return true;
        if (c == 'ᆫ')
            return true;
        if (c < 'ᆮ')
            return false;
        if (c <= 'ᆯ')
            return true;
        if (c < 'ᆷ')
            return false;
        if (c <= 'ᆸ')
            return true;
        if (c == 'ᆺ')
            return true;
        if (c < 'ᆼ')
            return false;
        if (c <= 'ᇂ')
            return true;
        if (c == 'ᇫ')
            return true;
        if (c == 'ᇰ')
            return true;
        if (c == 'ᇹ')
            return true;
        if (c < 'Ḁ')
            return false;
        if (c <= 'ẛ')
            return true;
        if (c < 'Ạ')
            return false;
        if (c <= 'ỹ')
            return true;
        if (c < 'ἀ')
            return false;
        if (c <= 'ἕ')
            return true;
        if (c < 'Ἐ')
            return false;
        if (c <= 'Ἕ')
            return true;
        if (c < 'ἠ')
            return false;
        if (c <= 'ὅ')
            return true;
        if (c < 'Ὀ')
            return false;
        if (c <= 'Ὅ')
            return true;
        if (c < 'ὐ')
            return false;
        if (c <= 'ὗ')
            return true;
        if (c == 'Ὑ')
            return true;
        if (c == 'Ὓ')
            return true;
        if (c == 'Ὕ')
            return true;
        if (c < 'Ὗ')
            return false;
        if (c <= 'ώ')
            return true;
        if (c < 'ᾀ')
            return false;
        if (c <= 'ᾴ')
            return true;
        if (c < 'ᾶ')
            return false;
        if (c <= 'ᾼ')
            return true;
        if (c == 'ι')
            return true;
        if (c < 'ῂ')
            return false;
        if (c <= 'ῄ')
            return true;
        if (c < 'ῆ')
            return false;
        if (c <= 'ῌ')
            return true;
        if (c < 'ῐ')
            return false;
        if (c <= 'ΐ')
            return true;
        if (c < 'ῖ')
            return false;
        if (c <= 'Ί')
            return true;
        if (c < 'ῠ')
            return false;
        if (c <= 'Ῥ')
            return true;
        if (c < 'ῲ')
            return false;
        if (c <= 'ῴ')
            return true;
        if (c < 'ῶ')
            return false;
        if (c <= 'ῼ')
            return true;
        if (c == 'Ω')
            return true;
        if (c < 'K')
            return false;
        if (c <= 'Å')
            return true;
        if (c == '℮')
            return true;
        if (c < 'ↀ')
            return false;
        if (c <= 'ↂ')
            return true;
        if (c == '〇')
            return true;
        if (c < '〡')
            return false;
        if (c <= '〩')
            return true;
        if (c < 'ぁ')
            return false;
        if (c <= 'ゔ')
            return true;
        if (c < 'ァ')
            return false;
        if (c <= 'ヺ')
            return true;
        if (c < 'ㄅ')
            return false;
        if (c <= 'ㄬ')
            return true;
        if (c < '一')
            return false;
        if (c <= '龥')
            return true;
        if (c < '가')
            return false;
        if (c <= '힣')
            return true;
        return false;
    }

    public static boolean isXMLCombiningChar(char c) {
        if (c < '̀')
            return false;
        if (c <= 'ͅ')
            return true;
        if (c < '͠')
            return false;
        if (c <= '͡')
            return true;
        if (c < '҃')
            return false;
        if (c <= '҆')
            return true;
        if (c < '֑')
            return false;
        if (c <= '֡')
            return true;
        if (c < '֣')
            return false;
        if (c <= 'ֹ')
            return true;
        if (c < 'ֻ')
            return false;
        if (c <= 'ֽ')
            return true;
        if (c == 'ֿ')
            return true;
        if (c < 'ׁ')
            return false;
        if (c <= 'ׂ')
            return true;
        if (c == 'ׄ')
            return true;
        if (c < 'ً')
            return false;
        if (c <= 'ْ')
            return true;
        if (c == 'ٰ')
            return true;
        if (c < 'ۖ')
            return false;
        if (c <= 'ۜ')
            return true;
        if (c < '۝')
            return false;
        if (c <= '۟')
            return true;
        if (c < '۠')
            return false;
        if (c <= 'ۤ')
            return true;
        if (c < 'ۧ')
            return false;
        if (c <= 'ۨ')
            return true;
        if (c < '۪')
            return false;
        if (c <= 'ۭ')
            return true;
        if (c < 'ँ')
            return false;
        if (c <= 'ः')
            return true;
        if (c == '़')
            return true;
        if (c < 'ा')
            return false;
        if (c <= 'ौ')
            return true;
        if (c == '्')
            return true;
        if (c < '॑')
            return false;
        if (c <= '॔')
            return true;
        if (c < 'ॢ')
            return false;
        if (c <= 'ॣ')
            return true;
        if (c < 'ঁ')
            return false;
        if (c <= 'ঃ')
            return true;
        if (c == '়')
            return true;
        if (c == 'া')
            return true;
        if (c == 'ি')
            return true;
        if (c < 'ী')
            return false;
        if (c <= 'ৄ')
            return true;
        if (c < 'ে')
            return false;
        if (c <= 'ৈ')
            return true;
        if (c < 'ো')
            return false;
        if (c <= '্')
            return true;
        if (c == 'ৗ')
            return true;
        if (c < 'ৢ')
            return false;
        if (c <= 'ৣ')
            return true;
        if (c == 'ਂ')
            return true;
        if (c == '਼')
            return true;
        if (c == 'ਾ')
            return true;
        if (c == 'ਿ')
            return true;
        if (c < 'ੀ')
            return false;
        if (c <= 'ੂ')
            return true;
        if (c < 'ੇ')
            return false;
        if (c <= 'ੈ')
            return true;
        if (c < 'ੋ')
            return false;
        if (c <= '੍')
            return true;
        if (c < 'ੰ')
            return false;
        if (c <= 'ੱ')
            return true;
        if (c < 'ઁ')
            return false;
        if (c <= 'ઃ')
            return true;
        if (c == '઼')
            return true;
        if (c < 'ા')
            return false;
        if (c <= 'ૅ')
            return true;
        if (c < 'ે')
            return false;
        if (c <= 'ૉ')
            return true;
        if (c < 'ો')
            return false;
        if (c <= '્')
            return true;
        if (c < 'ଁ')
            return false;
        if (c <= 'ଃ')
            return true;
        if (c == '଼')
            return true;
        if (c < 'ା')
            return false;
        if (c <= 'ୃ')
            return true;
        if (c < 'େ')
            return false;
        if (c <= 'ୈ')
            return true;
        if (c < 'ୋ')
            return false;
        if (c <= '୍')
            return true;
        if (c < 'ୖ')
            return false;
        if (c <= 'ୗ')
            return true;
        if (c < 'ஂ')
            return false;
        if (c <= 'ஃ')
            return true;
        if (c < 'ா')
            return false;
        if (c <= 'ூ')
            return true;
        if (c < 'ெ')
            return false;
        if (c <= 'ை')
            return true;
        if (c < 'ொ')
            return false;
        if (c <= '்')
            return true;
        if (c == 'ௗ')
            return true;
        if (c < 'ఁ')
            return false;
        if (c <= 'ః')
            return true;
        if (c < 'ా')
            return false;
        if (c <= 'ౄ')
            return true;
        if (c < 'ె')
            return false;
        if (c <= 'ై')
            return true;
        if (c < 'ొ')
            return false;
        if (c <= '్')
            return true;
        if (c < 'ౕ')
            return false;
        if (c <= 'ౖ')
            return true;
        if (c < 'ಂ')
            return false;
        if (c <= 'ಃ')
            return true;
        if (c < 'ಾ')
            return false;
        if (c <= 'ೄ')
            return true;
        if (c < 'ೆ')
            return false;
        if (c <= 'ೈ')
            return true;
        if (c < 'ೊ')
            return false;
        if (c <= '್')
            return true;
        if (c < 'ೕ')
            return false;
        if (c <= 'ೖ')
            return true;
        if (c < 'ം')
            return false;
        if (c <= 'ഃ')
            return true;
        if (c < 'ാ')
            return false;
        if (c <= 'ൃ')
            return true;
        if (c < 'െ')
            return false;
        if (c <= 'ൈ')
            return true;
        if (c < 'ൊ')
            return false;
        if (c <= '്')
            return true;
        if (c == 'ൗ')
            return true;
        if (c == 'ั')
            return true;
        if (c < 'ิ')
            return false;
        if (c <= 'ฺ')
            return true;
        if (c < '็')
            return false;
        if (c <= '๎')
            return true;
        if (c == 'ັ')
            return true;
        if (c < 'ິ')
            return false;
        if (c <= 'ູ')
            return true;
        if (c < 'ົ')
            return false;
        if (c <= 'ຼ')
            return true;
        if (c < '່')
            return false;
        if (c <= 'ໍ')
            return true;
        if (c < '༘')
            return false;
        if (c <= '༙')
            return true;
        if (c == '༵')
            return true;
        if (c == '༷')
            return true;
        if (c == '༹')
            return true;
        if (c == '༾')
            return true;
        if (c == '༿')
            return true;
        if (c < 'ཱ')
            return false;
        if (c <= '྄')
            return true;
        if (c < '྆')
            return false;
        if (c <= 'ྋ')
            return true;
        if (c < 'ྐ')
            return false;
        if (c <= 'ྕ')
            return true;
        if (c == 'ྗ')
            return true;
        if (c < 'ྙ')
            return false;
        if (c <= 'ྭ')
            return true;
        if (c < 'ྱ')
            return false;
        if (c <= 'ྷ')
            return true;
        if (c == 'ྐྵ')
            return true;
        if (c < '⃐')
            return false;
        if (c <= '⃜')
            return true;
        if (c == '⃡')
            return true;
        if (c < '〪')
            return false;
        if (c <= '〯')
            return true;
        if (c == '゙')
            return true;
        if (c == '゚')
            return true;
        return false;
    }

    public static boolean isXMLExtender(char c) {
        if (c < '¶')
            return false;
        if (c == '·')
            return true;
        if (c == 'ː')
            return true;
        if (c == 'ˑ')
            return true;
        if (c == '·')
            return true;
        if (c == 'ـ')
            return true;
        if (c == 'ๆ')
            return true;
        if (c == 'ໆ')
            return true;
        if (c == '々')
            return true;
        if (c < '〱')
            return false;
        if (c <= '〵')
            return true;
        if (c < 'ゝ')
            return false;
        if (c <= 'ゞ')
            return true;
        if (c < 'ー')
            return false;
        if (c <= 'ヾ')
            return true;
        return false;
    }

    public static boolean isXMLDigit(char c) {
        if (c < '0')
            return false;
        if (c <= '9')
            return true;
        if (c < '٠')
            return false;
        if (c <= '٩')
            return true;
        if (c < '۰')
            return false;
        if (c <= '۹')
            return true;
        if (c < '०')
            return false;
        if (c <= '९')
            return true;
        if (c < '০')
            return false;
        if (c <= '৯')
            return true;
        if (c < '੦')
            return false;
        if (c <= '੯')
            return true;
        if (c < '૦')
            return false;
        if (c <= '૯')
            return true;
        if (c < '୦')
            return false;
        if (c <= '୯')
            return true;
        if (c < '௧')
            return false;
        if (c <= '௯')
            return true;
        if (c < '౦')
            return false;
        if (c <= '౯')
            return true;
        if (c < '೦')
            return false;
        if (c <= '೯')
            return true;
        if (c < '൦')
            return false;
        if (c <= '൯')
            return true;
        if (c < '๐')
            return false;
        if (c <= '๙')
            return true;
        if (c < '໐')
            return false;
        if (c <= '໙')
            return true;
        if (c < '༠')
            return false;
        if (c <= '༩')
            return true;
        return false;
    }

    private static boolean isXMLLetterOld(char c) {
        if (c >= 'A' && c <= 'Z')
            return true;
        if (c >= 'a' && c <= 'z')
            return true;
        if (c >= 'À' && c <= 'Ö')
            return true;
        if (c >= 'Ø' && c <= 'ö')
            return true;
        if (c >= 'ø' && c <= 'ÿ')
            return true;
        if (c >= 'Ā' && c <= 'ı')
            return true;
        if (c >= 'Ĵ' && c <= 'ľ')
            return true;
        if (c >= 'Ł' && c <= 'ň')
            return true;
        if (c >= 'Ŋ' && c <= 'ž')
            return true;
        if (c >= 'ƀ' && c <= 'ǃ')
            return true;
        if (c >= 'Ǎ' && c <= 'ǰ')
            return true;
        if (c >= 'Ǵ' && c <= 'ǵ')
            return true;
        if (c >= 'Ǻ' && c <= 'ȗ')
            return true;
        if (c >= 'ɐ' && c <= 'ʨ')
            return true;
        if (c >= 'ʻ' && c <= 'ˁ')
            return true;
        if (c >= 'Έ' && c <= 'Ί')
            return true;
        if (c == 'Ά')
            return true;
        if (c == 'Ό')
            return true;
        if (c >= 'Ύ' && c <= 'Ρ')
            return true;
        if (c >= 'Σ' && c <= 'ώ')
            return true;
        if (c >= 'ϐ' && c <= 'ϖ')
            return true;
        if (c == 'Ϛ')
            return true;
        if (c == 'Ϝ')
            return true;
        if (c == 'Ϟ')
            return true;
        if (c == 'Ϡ')
            return true;
        if (c >= 'Ϣ' && c <= 'ϳ')
            return true;
        if (c >= 'Ё' && c <= 'Ќ')
            return true;
        if (c >= 'Ў' && c <= 'я')
            return true;
        if (c >= 'ё' && c <= 'ќ')
            return true;
        if (c >= 'ў' && c <= 'ҁ')
            return true;
        if (c >= 'Ґ' && c <= 'ӄ')
            return true;
        if (c >= 'Ӈ' && c <= 'ӈ')
            return true;
        if (c >= 'Ӌ' && c <= 'ӌ')
            return true;
        if (c >= 'Ӑ' && c <= 'ӫ')
            return true;
        if (c >= 'Ӯ' && c <= 'ӵ')
            return true;
        if (c >= 'Ӹ' && c <= 'ӹ')
            return true;
        if (c >= 'Ա' && c <= 'Ֆ')
            return true;
        if (c == 'ՙ')
            return true;
        if (c >= 'ա' && c <= 'ֆ')
            return true;
        if (c >= 'א' && c <= 'ת')
            return true;
        if (c >= 'װ' && c <= 'ײ')
            return true;
        if (c >= 'ء' && c <= 'غ')
            return true;
        if (c >= 'ف' && c <= 'ي')
            return true;
        if (c >= 'ٱ' && c <= 'ڷ')
            return true;
        if (c >= 'ں' && c <= 'ھ')
            return true;
        if (c >= 'ۀ' && c <= 'ێ')
            return true;
        if (c >= 'ې' && c <= 'ۓ')
            return true;
        if (c == 'ە')
            return true;
        if (c >= 'ۥ' && c <= 'ۦ')
            return true;
        if (c >= 'अ' && c <= 'ह')
            return true;
        if (c == 'ऽ')
            return true;
        if (c >= 'क़' && c <= 'ॡ')
            return true;
        if (c >= 'অ' && c <= 'ঌ')
            return true;
        if (c >= 'এ' && c <= 'ঐ')
            return true;
        if (c >= 'ও' && c <= 'ন')
            return true;
        if (c >= 'প' && c <= 'র')
            return true;
        if (c == 'ল')
            return true;
        if (c >= 'শ' && c <= 'হ')
            return true;
        if (c >= 'ড়' && c <= 'ঢ়')
            return true;
        if (c >= 'য়' && c <= 'ৡ')
            return true;
        if (c >= 'ৰ' && c <= 'ৱ')
            return true;
        if (c >= 'ਅ' && c <= 'ਊ')
            return true;
        if (c >= 'ਏ' && c <= 'ਐ')
            return true;
        if (c >= 'ਓ' && c <= 'ਨ')
            return true;
        if (c >= 'ਪ' && c <= 'ਰ')
            return true;
        if (c >= 'ਲ' && c <= 'ਲ਼')
            return true;
        if (c >= 'ਵ' && c <= 'ਸ਼')
            return true;
        if (c >= 'ਸ' && c <= 'ਹ')
            return true;
        if (c >= 'ਖ਼' && c <= 'ੜ')
            return true;
        if (c == 'ਫ਼')
            return true;
        if (c >= 'ੲ' && c <= 'ੴ')
            return true;
        if (c >= 'અ' && c <= 'ઋ')
            return true;
        if (c == 'ઍ')
            return true;
        if (c >= 'એ' && c <= 'ઑ')
            return true;
        if (c >= 'ઓ' && c <= 'ન')
            return true;
        if (c >= 'પ' && c <= 'ર')
            return true;
        if (c >= 'લ' && c <= 'ળ')
            return true;
        if (c >= 'વ' && c <= 'હ')
            return true;
        if (c == 'ઽ')
            return true;
        if (c == 'ૠ')
            return true;
        if (c >= 'ଅ' && c <= 'ଌ')
            return true;
        if (c >= 'ଏ' && c <= 'ଐ')
            return true;
        if (c >= 'ଓ' && c <= 'ନ')
            return true;
        if (c >= 'ପ' && c <= 'ର')
            return true;
        if (c >= 'ଲ' && c <= 'ଳ')
            return true;
        if (c >= 'ଶ' && c <= 'ହ')
            return true;
        if (c == 'ଽ')
            return true;
        if (c >= 'ଡ଼' && c <= 'ଢ଼')
            return true;
        if (c >= 'ୟ' && c <= 'ୡ')
            return true;
        if (c >= 'அ' && c <= 'ஊ')
            return true;
        if (c >= 'எ' && c <= 'ஐ')
            return true;
        if (c >= 'ஒ' && c <= 'க')
            return true;
        if (c >= 'ங' && c <= 'ச')
            return true;
        if (c == 'ஜ')
            return true;
        if (c >= 'ஞ' && c <= 'ட')
            return true;
        if (c >= 'ண' && c <= 'த')
            return true;
        if (c >= 'ந' && c <= 'ப')
            return true;
        if (c >= 'ம' && c <= 'வ')
            return true;
        if (c >= 'ஷ' && c <= 'ஹ')
            return true;
        if (c >= 'అ' && c <= 'ఌ')
            return true;
        if (c >= 'ఎ' && c <= 'ఐ')
            return true;
        if (c >= 'ఒ' && c <= 'న')
            return true;
        if (c >= 'ప' && c <= 'ళ')
            return true;
        if (c >= 'వ' && c <= 'హ')
            return true;
        if (c >= 'ౠ' && c <= 'ౡ')
            return true;
        if (c >= 'ಅ' && c <= 'ಌ')
            return true;
        if (c >= 'ಎ' && c <= 'ಐ')
            return true;
        if (c >= 'ಒ' && c <= 'ನ')
            return true;
        if (c >= 'ಪ' && c <= 'ಳ')
            return true;
        if (c >= 'ವ' && c <= 'ಹ')
            return true;
        if (c == 'ೞ')
            return true;
        if (c >= 'ೠ' && c <= 'ೡ')
            return true;
        if (c >= 'അ' && c <= 'ഌ')
            return true;
        if (c >= 'എ' && c <= 'ഐ')
            return true;
        if (c >= 'ഒ' && c <= 'ന')
            return true;
        if (c >= 'പ' && c <= 'ഹ')
            return true;
        if (c >= 'ൠ' && c <= 'ൡ')
            return true;
        if (c >= 'ก' && c <= 'ฮ')
            return true;
        if (c == 'ะ')
            return true;
        if (c >= 'า' && c <= 'ำ')
            return true;
        if (c >= 'เ' && c <= 'ๅ')
            return true;
        if (c >= 'ກ' && c <= 'ຂ')
            return true;
        if (c == 'ຄ')
            return true;
        if (c >= 'ງ' && c <= 'ຈ')
            return true;
        if (c == 'ຊ')
            return true;
        if (c == 'ຍ')
            return true;
        if (c >= 'ດ' && c <= 'ທ')
            return true;
        if (c >= 'ນ' && c <= 'ຟ')
            return true;
        if (c >= 'ມ' && c <= 'ຣ')
            return true;
        if (c == 'ລ')
            return true;
        if (c == 'ວ')
            return true;
        if (c >= 'ສ' && c <= 'ຫ')
            return true;
        if (c >= 'ອ' && c <= 'ຮ')
            return true;
        if (c == 'ະ')
            return true;
        if (c >= 'າ' && c <= 'ຳ')
            return true;
        if (c == 'ຽ')
            return true;
        if (c >= 'ເ' && c <= 'ໄ')
            return true;
        if (c >= 'ཀ' && c <= 'ཇ')
            return true;
        if (c >= 'ཉ' && c <= 'ཀྵ')
            return true;
        if (c >= 'Ⴀ' && c <= 'Ⴥ')
            return true;
        if (c >= 'ა' && c <= 'ჶ')
            return true;
        if (c == 'ᄀ')
            return true;
        if (c >= 'ᄂ' && c <= 'ᄃ')
            return true;
        if (c >= 'ᄅ' && c <= 'ᄇ')
            return true;
        if (c == 'ᄉ')
            return true;
        if (c >= 'ᄋ' && c <= 'ᄌ')
            return true;
        if (c >= 'ᄎ' && c <= 'ᄒ')
            return true;
        if (c == 'ᄼ')
            return true;
        if (c == 'ᄾ')
            return true;
        if (c == 'ᅀ')
            return true;
        if (c == 'ᅌ')
            return true;
        if (c == 'ᅎ')
            return true;
        if (c == 'ᅐ')
            return true;
        if (c >= 'ᅔ' && c <= 'ᅕ')
            return true;
        if (c == 'ᅙ')
            return true;
        if (c >= 'ᅟ' && c <= 'ᅡ')
            return true;
        if (c == 'ᅣ')
            return true;
        if (c == 'ᅥ')
            return true;
        if (c == 'ᅧ')
            return true;
        if (c == 'ᅩ')
            return true;
        if (c >= 'ᅭ' && c <= 'ᅮ')
            return true;
        if (c >= 'ᅲ' && c <= 'ᅳ')
            return true;
        if (c == 'ᅵ')
            return true;
        if (c == 'ᆞ')
            return true;
        if (c == 'ᆨ')
            return true;
        if (c == 'ᆫ')
            return true;
        if (c >= 'ᆮ' && c <= 'ᆯ')
            return true;
        if (c >= 'ᆷ' && c <= 'ᆸ')
            return true;
        if (c == 'ᆺ')
            return true;
        if (c >= 'ᆼ' && c <= 'ᇂ')
            return true;
        if (c == 'ᇫ')
            return true;
        if (c == 'ᇰ')
            return true;
        if (c == 'ᇹ')
            return true;
        if (c >= 'Ḁ' && c <= 'ẛ')
            return true;
        if (c >= 'Ạ' && c <= 'ỹ')
            return true;
        if (c >= 'ἀ' && c <= 'ἕ')
            return true;
        if (c >= 'Ἐ' && c <= 'Ἕ')
            return true;
        if (c >= 'ἠ' && c <= 'ὅ')
            return true;
        if (c >= 'Ὀ' && c <= 'Ὅ')
            return true;
        if (c >= 'ὐ' && c <= 'ὗ')
            return true;
        if (c == 'Ὑ')
            return true;
        if (c == 'Ὓ')
            return true;
        if (c == 'Ὕ')
            return true;
        if (c >= 'Ὗ' && c <= 'ώ')
            return true;
        if (c >= 'ᾀ' && c <= 'ᾴ')
            return true;
        if (c >= 'ᾶ' && c <= 'ᾼ')
            return true;
        if (c == 'ι')
            return true;
        if (c >= 'ῂ' && c <= 'ῄ')
            return true;
        if (c >= 'ῆ' && c <= 'ῌ')
            return true;
        if (c >= 'ῐ' && c <= 'ΐ')
            return true;
        if (c >= 'ῖ' && c <= 'Ί')
            return true;
        if (c >= 'ῠ' && c <= 'Ῥ')
            return true;
        if (c >= 'ῲ' && c <= 'ῴ')
            return true;
        if (c >= 'ῶ' && c <= 'ῼ')
            return true;
        if (c == 'Ω')
            return true;
        if (c >= 'K' && c <= 'Å')
            return true;
        if (c == '℮')
            return true;
        if (c >= 'ↀ' && c <= 'ↂ')
            return true;
        if (c >= 'ぁ' && c <= 'ゔ')
            return true;
        if (c >= 'ァ' && c <= 'ヺ')
            return true;
        if (c >= 'ㄅ' && c <= 'ㄬ')
            return true;
        if (c >= '가' && c <= '힣')
            return true;
        if (c >= '一' && c <= '龥')
            return true;
        if (c == '〇')
            return true;
        if (c >= '〡' && c <= '〩')
            return true;
        return false;
    }

    private static boolean isXMLDigitOld(char c) {
        if (c >= '0' && c <= '9')
            return true;
        if (c >= '٠' && c <= '٩')
            return true;
        if (c >= '۰' && c <= '۹')
            return true;
        if (c >= '०' && c <= '९')
            return true;
        if (c >= '০' && c <= '৯')
            return true;
        if (c >= '੦' && c <= '੯')
            return true;
        if (c >= '૦' && c <= '૯')
            return true;
        if (c >= '୦' && c <= '୯')
            return true;
        if (c >= '௧' && c <= '௯')
            return true;
        if (c >= '౦' && c <= '౯')
            return true;
        if (c >= '೦' && c <= '೯')
            return true;
        if (c >= '൦' && c <= '൯')
            return true;
        if (c >= '๐' && c <= '๙')
            return true;
        if (c >= '໐' && c <= '໙')
            return true;
        if (c >= '༠' && c <= '༩')
            return true;
        return false;
    }

    private static boolean isXMLCombiningCharOld(char c) {
        if (c >= '̀' && c <= 'ͅ')
            return true;
        if (c >= '͠' && c <= '͡')
            return true;
        if (c >= '҃' && c <= '҆')
            return true;
        if (c >= '֑' && c <= '֡')
            return true;
        if (c >= '֣' && c <= 'ֹ')
            return true;
        if (c >= 'ֻ' && c <= 'ֽ')
            return true;
        if (c == 'ֿ')
            return true;
        if (c >= 'ׁ' && c <= 'ׂ')
            return true;
        if (c == 'ׄ')
            return true;
        if (c >= 'ً' && c <= 'ْ')
            return true;
        if (c == 'ٰ')
            return true;
        if (c >= 'ۖ' && c <= 'ۜ')
            return true;
        if (c >= '۝' && c <= '۟')
            return true;
        if (c >= '۠' && c <= 'ۤ')
            return true;
        if (c >= 'ۧ' && c <= 'ۨ')
            return true;
        if (c >= '۪' && c <= 'ۭ')
            return true;
        if (c >= 'ँ' && c <= 'ः')
            return true;
        if (c == '़')
            return true;
        if (c >= 'ा' && c <= 'ौ')
            return true;
        if (c == '्')
            return true;
        if (c >= '॑' && c <= '॔')
            return true;
        if (c >= 'ॢ' && c <= 'ॣ')
            return true;
        if (c >= 'ঁ' && c <= 'ঃ')
            return true;
        if (c == '়')
            return true;
        if (c == 'া')
            return true;
        if (c == 'ি')
            return true;
        if (c >= 'ী' && c <= 'ৄ')
            return true;
        if (c >= 'ে' && c <= 'ৈ')
            return true;
        if (c >= 'ো' && c <= '্')
            return true;
        if (c == 'ৗ')
            return true;
        if (c >= 'ৢ' && c <= 'ৣ')
            return true;
        if (c == 'ਂ')
            return true;
        if (c == '਼')
            return true;
        if (c == 'ਾ')
            return true;
        if (c == 'ਿ')
            return true;
        if (c >= 'ੀ' && c <= 'ੂ')
            return true;
        if (c >= 'ੇ' && c <= 'ੈ')
            return true;
        if (c >= 'ੋ' && c <= '੍')
            return true;
        if (c >= 'ੰ' && c <= 'ੱ')
            return true;
        if (c >= 'ઁ' && c <= 'ઃ')
            return true;
        if (c == '઼')
            return true;
        if (c >= 'ા' && c <= 'ૅ')
            return true;
        if (c >= 'ે' && c <= 'ૉ')
            return true;
        if (c >= 'ો' && c <= '્')
            return true;
        if (c >= 'ଁ' && c <= 'ଃ')
            return true;
        if (c == '଼')
            return true;
        if (c >= 'ା' && c <= 'ୃ')
            return true;
        if (c >= 'େ' && c <= 'ୈ')
            return true;
        if (c >= 'ୋ' && c <= '୍')
            return true;
        if (c >= 'ୖ' && c <= 'ୗ')
            return true;
        if (c >= 'ஂ' && c <= 'ஃ')
            return true;
        if (c >= 'ா' && c <= 'ூ')
            return true;
        if (c >= 'ெ' && c <= 'ை')
            return true;
        if (c >= 'ொ' && c <= '்')
            return true;
        if (c == 'ௗ')
            return true;
        if (c >= 'ఁ' && c <= 'ః')
            return true;
        if (c >= 'ా' && c <= 'ౄ')
            return true;
        if (c >= 'ె' && c <= 'ై')
            return true;
        if (c >= 'ొ' && c <= '్')
            return true;
        if (c >= 'ౕ' && c <= 'ౖ')
            return true;
        if (c >= 'ಂ' && c <= 'ಃ')
            return true;
        if (c >= 'ಾ' && c <= 'ೄ')
            return true;
        if (c >= 'ೆ' && c <= 'ೈ')
            return true;
        if (c >= 'ೊ' && c <= '್')
            return true;
        if (c >= 'ೕ' && c <= 'ೖ')
            return true;
        if (c >= 'ം' && c <= 'ഃ')
            return true;
        if (c >= 'ാ' && c <= 'ൃ')
            return true;
        if (c >= 'െ' && c <= 'ൈ')
            return true;
        if (c >= 'ൊ' && c <= '്')
            return true;
        if (c == 'ൗ')
            return true;
        if (c == 'ั')
            return true;
        if (c >= 'ิ' && c <= 'ฺ')
            return true;
        if (c >= '็' && c <= '๎')
            return true;
        if (c == 'ັ')
            return true;
        if (c >= 'ິ' && c <= 'ູ')
            return true;
        if (c >= 'ົ' && c <= 'ຼ')
            return true;
        if (c >= '່' && c <= 'ໍ')
            return true;
        if (c >= '༘' && c <= '༙')
            return true;
        if (c == '༵')
            return true;
        if (c == '༷')
            return true;
        if (c == '༹')
            return true;
        if (c == '༾')
            return true;
        if (c == '༿')
            return true;
        if (c >= 'ཱ' && c <= '྄')
            return true;
        if (c >= '྆' && c <= 'ྋ')
            return true;
        if (c >= 'ྐ' && c <= 'ྕ')
            return true;
        if (c == 'ྗ')
            return true;
        if (c >= 'ྙ' && c <= 'ྭ')
            return true;
        if (c >= 'ྱ' && c <= 'ྷ')
            return true;
        if (c == 'ྐྵ')
            return true;
        if (c >= '⃐' && c <= '⃜')
            return true;
        if (c == '⃡')
            return true;
        if (c >= '〪' && c <= '〯')
            return true;
        if (c == '゙')
            return true;
        if (c == '゚')
            return true;
        return false;
    }

    private static boolean isXMLExtenderOld(char c) {
        if (c == '·')
            return true;
        if (c == 'ː')
            return true;
        if (c == 'ˑ')
            return true;
        if (c == '·')
            return true;
        if (c == 'ـ')
            return true;
        if (c == 'ๆ')
            return true;
        if (c == 'ໆ')
            return true;
        if (c == '々')
            return true;
        if (c >= '〱' && c <= '〵')
            return true;
        if (c >= 'ゝ' && c <= 'ゞ')
            return true;
        if (c >= 'ー' && c <= 'ヾ')
            return true;
        return false;
    }

    private static boolean isXMLCharacterOld(char c) {
        if (c >= ' ' && c <= '퟿')
            return true;
        if (c >= '' && c <= '�')
            return true;
        if (c >= 65536 && c <= 1114111)
            return true;
        if (c == '\n')
            return true;
        if (c == '\r')
            return true;
        if (c == '\t')
            return true;
        return false;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 65536; i++) {
            if (isXMLLetter((char) i) != isXMLLetterOld((char) i))
                System.out.println("isXMLLetter mismatch: " + i + " hex: " + Integer.toHexString(i));
        }
        for (int j = 0; j < 65536; j++) {
            if (isXMLDigit((char) j) != isXMLDigitOld((char) j))
                System.out.println("isXMLDigit mismatch: " + j + " hex: " + Integer.toHexString(j));
        }
        for (int k = 0; k < 65536; k++) {
            if (isXMLCombiningChar((char) k) != isXMLCombiningCharOld((char) k))
                System.out.println("isXMLCombiningChar mismatch: " + k + " hex: " + Integer.toHexString(k));
        }
        for (int m = 0; m < 65536; m++) {
            if (isXMLExtender((char) m) != isXMLExtenderOld((char) m))
                System.out.println("isXMLExtender mismatch: " + m + " hex: " + Integer.toHexString(m));
        }
        for (int n = 0; n < 65536; n++) {
            if (isXMLCharacter((char) n) != isXMLCharacterOld((char) n))
                System.out.println("isXMLCharacter mismatch: " + n + " hex: " + Integer.toHexString(n));
        }
    }
}
