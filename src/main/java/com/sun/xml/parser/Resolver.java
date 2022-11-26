package com.sun.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Resolver implements EntityResolver {
    private boolean ignoringMIME;
    private Hashtable id2uri;
    private Hashtable id2resource;
    private Hashtable id2loader;
    private static final String[] types = new String[]{"application/xml", "text/xml", "text/plain", "text/html", "application/x-netcdf", "content/unknown"};

    public static InputSource createInputSource(String paramString1, InputStream paramInputStream, boolean paramBoolean, String paramString2) throws IOException {
        String str = null;
        if (paramString1 != null) {
            paramString1 = paramString1.toLowerCase();
            int i = paramString1.indexOf(';');
            if (i != -1) {
                String str1 = paramString1.substring(i + 1);
                paramString1 = paramString1.substring(0, i);
                i = str1.indexOf("charset");
                if (i != -1) {
                    str1 = str1.substring(i + 7);
                    if ((i = str1.indexOf(';')) != -1)
                        str1 = str1.substring(0, i);
                    if ((i = str1.indexOf('=')) != -1) {
                        str1 = str1.substring(i + 1);
                        if ((i = str1.indexOf('(')) != -1)
                            str1 = str1.substring(0, i);
                        if ((i = str1.indexOf('"')) != -1) {
                            str1 = str1.substring(i + 1);
                            str1 = str1.substring(0, str1.indexOf('"'));
                        }
                        str = str1.trim();
                    }
                }
            }
            if (paramBoolean) {
                boolean bool = false;
                for (byte b = 0; b < types.length; b++) {
                    if (types[b].equals(paramString1)) {
                        bool = true;
                        break;
                    }
                }
                if (!bool)
                    throw new IOException("Not XML: " + paramString1);
            }
            if (str == null) {
                paramString1 = paramString1.trim();
                if (paramString1.startsWith("text/") && !"file".equalsIgnoreCase(paramString2))
                    str = "US-ASCII";
            }
        }
        InputSource inputSource = new InputSource(XmlReader.createReader(paramInputStream, str));
        inputSource.setByteStream(paramInputStream);
        inputSource.setEncoding(str);
        return inputSource;
    }

    public static InputSource createInputSource(URL paramURL, boolean paramBoolean) throws IOException {
        InputSource inputSource;
        URLConnection uRLConnection = paramURL.openConnection();
        if (paramBoolean) {
            String str = uRLConnection.getContentType();
            inputSource = createInputSource(str, uRLConnection.getInputStream(), false, paramURL.getProtocol());
        } else {
            inputSource = new InputSource(XmlReader.createReader(uRLConnection.getInputStream()));
        }
        inputSource.setSystemId(uRLConnection.getURL().toString());
        return inputSource;
    }

    public static InputSource createInputSource(File paramFile) throws IOException {
        InputSource inputSource = new InputSource(XmlReader.createReader(new FileInputStream(paramFile)));
        String str = paramFile.getAbsolutePath();
        if (File.separatorChar != '/')
            str = str.replace(File.separatorChar, '/');
        if (!str.startsWith("/"))
            str = "/" + str;
        if (!str.endsWith("/") && paramFile.isDirectory())
            str = String.valueOf(str) + "/";
        inputSource.setSystemId("file:" + str);
        return inputSource;
    }

    public InputSource resolveEntity(String paramString1, String paramString2) throws IOException, SAXException {
        InputSource inputSource;
        String str = name2uri(paramString1);
        InputStream inputStream;
        if (str == null && (inputStream = mapResource(paramString1)) != null) {
            paramString2 = "java:resource:" + (String) this.id2resource.get(paramString1);
            inputSource = new InputSource(XmlReader.createReader(inputStream));
        } else {
            if (str != null) {
                paramString2 = str;
            } else if (paramString2 == null) {
                return null;
            }
            URL uRL = new URL(paramString2);
            URLConnection uRLConnection = uRL.openConnection();
            paramString2 = uRLConnection.getURL().toString();
            if (this.ignoringMIME) {
                inputSource = new InputSource(XmlReader.createReader(uRLConnection.getInputStream()));
            } else {
                String str1 = uRLConnection.getContentType();
                inputSource = createInputSource(str1, uRLConnection.getInputStream(), false, uRL.getProtocol());
            }
        }
        inputSource.setSystemId(paramString2);
        inputSource.setPublicId(paramString1);
        return inputSource;
    }

    public boolean isIgnoringMIME() {
        return this.ignoringMIME;
    }

    public void setIgnoringMIME(boolean paramBoolean) {
        this.ignoringMIME = paramBoolean;
    }

    private String name2uri(String paramString) {
        if (paramString == null || this.id2uri == null)
            return null;
        return (String) this.id2uri.get(paramString);
    }

    public void registerCatalogEntry(String paramString1, String paramString2) {
        if (this.id2uri == null)
            this.id2uri = new Hashtable(17);
        this.id2uri.put(paramString1, paramString2);
    }

    private InputStream mapResource(String paramString) {
        if (paramString == null || this.id2resource == null)
            return null;
        String str = (String) this.id2resource.get(paramString);
        ClassLoader classLoader = null;
        if (str == null)
            return null;
        if (this.id2loader != null)
            classLoader = (ClassLoader) this.id2loader.get(paramString);
        if (classLoader == null)
            return ClassLoader.getSystemResourceAsStream(str);
        return classLoader.getResourceAsStream(str);
    }

    public void registerCatalogEntry(String paramString1, String paramString2, ClassLoader paramClassLoader) {
        if (this.id2resource == null)
            this.id2resource = new Hashtable(17);
        this.id2resource.put(paramString1, paramString2);
        if (paramClassLoader != null) {
            if (this.id2loader == null)
                this.id2loader = new Hashtable(17);
            this.id2loader.put(paramString1, paramClassLoader);
        }
    }
}
