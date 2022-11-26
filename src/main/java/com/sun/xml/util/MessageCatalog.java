package com.sun.xml.util;

import java.io.InputStream;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class MessageCatalog {
    private String bundleName;
    private Hashtable cache;

    protected MessageCatalog(Class paramClass) {
        this(paramClass, "Messages");
    }

    private MessageCatalog(Class paramClass, String paramString) {
        this.cache = new Hashtable(5);
        this.bundleName = paramClass.getName();
        int i = this.bundleName.lastIndexOf('.');
        if (i == -1) {
            this.bundleName = "";
        } else {
            this.bundleName = String.valueOf(this.bundleName.substring(0, i)) + ".";
        }
        this.bundleName = String.valueOf(this.bundleName) + "resources." + paramString;
    }

    public String getMessage(Locale paramLocale, String paramString) {
        if (paramLocale == null)
            paramLocale = Locale.getDefault();
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(this.bundleName, paramLocale);
            return resourceBundle.getString(paramString);
        } catch (MissingResourceException missingResourceException) {
            return packagePrefix(paramString);
        }
    }

    private String packagePrefix(String paramString) {
        String str = getClass().getName();
        int i = str.lastIndexOf('.');
        if (i == -1) {
            str = "";
        } else {
            str = str.substring(0, i);
        }
        return String.valueOf(str) + '/' + paramString;
    }

    public String getMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) {
        MessageFormat messageFormat;
        if (paramArrayOfObject == null)
            return getMessage(paramLocale, paramString);
        for (byte b = 0; b < paramArrayOfObject.length; b++) {
            if (!(paramArrayOfObject[b] instanceof String) && !(paramArrayOfObject[b] instanceof Number) && !(paramArrayOfObject[b] instanceof java.util.Date))
                if (paramArrayOfObject[b] == null) {
                    paramArrayOfObject[b] = "(null)";
                } else {
                    paramArrayOfObject[b] = paramArrayOfObject[b].toString();
                }
        }
        if (paramLocale == null)
            paramLocale = Locale.getDefault();
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(this.bundleName, paramLocale);
            messageFormat = new MessageFormat(resourceBundle.getString(paramString));
        } catch (MissingResourceException missingResourceException) {
            String str = packagePrefix(paramString);
            for (byte b1 = 0; b1 < paramArrayOfObject.length; b1++) {
                str = String.valueOf(str) + ' ';
                str = String.valueOf(str) + paramArrayOfObject[b1];
            }
            return str;
        }
        messageFormat.setLocale(paramLocale);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = messageFormat.format(paramArrayOfObject, stringBuffer, new FieldPosition(0));
        return stringBuffer.toString();
    }

    public Locale chooseLocale(String[] paramArrayOfString) {
        if ((paramArrayOfString = canonicalize(paramArrayOfString)) != null)
            for (byte b = 0; b < paramArrayOfString.length; b++) {
                if (isLocaleSupported(paramArrayOfString[b]))
                    return getLocale(paramArrayOfString[b]);
            }
        return null;
    }

    private String[] canonicalize(String[] paramArrayOfString) {
        boolean bool = false;
        byte b1 = 0;
        if (paramArrayOfString == null)
            return paramArrayOfString;
        for (byte b2 = 0; b2 < paramArrayOfString.length; b2++) {
            String str = paramArrayOfString[b2];
            int i = str.length();
            if (i != 2 && i != 5) {
                if (!bool) {
                    paramArrayOfString = (String[]) paramArrayOfString.clone();
                    bool = true;
                }
                paramArrayOfString[b2] = null;
                b1++;
            } else if (i == 2) {
                str = str.toLowerCase();
                if (str != paramArrayOfString[b2]) {
                    if (!bool) {
                        paramArrayOfString = (String[]) paramArrayOfString.clone();
                        bool = true;
                    }
                    paramArrayOfString[b2] = str;
                }
            } else {
                char[] arrayOfChar = new char[5];
                arrayOfChar[0] = Character.toLowerCase(str.charAt(0));
                arrayOfChar[1] = Character.toLowerCase(str.charAt(1));
                arrayOfChar[2] = '_';
                arrayOfChar[3] = Character.toUpperCase(str.charAt(3));
                arrayOfChar[4] = Character.toUpperCase(str.charAt(4));
                if (!bool) {
                    paramArrayOfString = (String[]) paramArrayOfString.clone();
                    bool = true;
                }
                paramArrayOfString[b2] = new String(arrayOfChar);
            }
        }
        if (b1 != 0) {
            String[] arrayOfString = new String[paramArrayOfString.length - b1];
            for (byte b = 0; b < arrayOfString.length; b++) {
                while (paramArrayOfString[b + b1] == null)
                    b1++;
                arrayOfString[b] = paramArrayOfString[b + b1];
            }
            paramArrayOfString = arrayOfString;
        }
        return paramArrayOfString;
    }

    private Locale getLocale(String paramString) {
        String str1, str2;
        int i = paramString.indexOf('_');
        if (i == -1) {
            if (paramString.equals("de"))
                return Locale.GERMAN;
            if (paramString.equals("en"))
                return Locale.ENGLISH;
            if (paramString.equals("fr"))
                return Locale.FRENCH;
            if (paramString.equals("it"))
                return Locale.ITALIAN;
            if (paramString.equals("ja"))
                return Locale.JAPANESE;
            if (paramString.equals("ko"))
                return Locale.KOREAN;
            if (paramString.equals("zh"))
                return Locale.CHINESE;
            str1 = paramString;
            str2 = "";
        } else {
            if (paramString.equals("zh_CN"))
                return Locale.SIMPLIFIED_CHINESE;
            if (paramString.equals("zh_TW"))
                return Locale.TRADITIONAL_CHINESE;
            str1 = paramString.substring(0, i);
            str2 = paramString.substring(i + 1);
        }
        return new Locale(str1, str2);
    }

    public boolean isLocaleSupported(String paramString) {
        Boolean bool = (Boolean) this.cache.get(paramString);
        if (bool != null)
            return bool.booleanValue();
        ClassLoader classLoader = null;
        while (true) {
            String str = String.valueOf(this.bundleName) + "_" + paramString;
            try {
                Class.forName(str);
                this.cache.put(paramString, Boolean.TRUE);
                return true;
            } catch (Exception exception) {
                InputStream inputStream;
                if (classLoader == null)
                    classLoader = getClass().getClassLoader();
                str = str.replace('.', '/');
                str = String.valueOf(str) + ".properties";
                if (classLoader == null) {
                    inputStream = ClassLoader.getSystemResourceAsStream(str);
                } else {
                    inputStream = classLoader.getResourceAsStream(str);
                }
                if (inputStream != null) {
                    this.cache.put(paramString, Boolean.TRUE);
                    return true;
                }
                int i = paramString.indexOf('_');
                if (i > 0) {
                    paramString = paramString.substring(0, i);
                    continue;
                }
                break;
            }
        }
        this.cache.put(paramString, Boolean.FALSE);
        return false;
    }
}
