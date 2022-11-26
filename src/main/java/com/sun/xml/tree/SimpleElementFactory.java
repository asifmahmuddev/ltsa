package com.sun.xml.tree;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;

public class SimpleElementFactory implements ElementFactory {
    private Dictionary defaultMapping;
    private ClassLoader defaultLoader;
    private String defaultNs;
    private Dictionary nsMappings;
    private Dictionary nsLoaders;
    private Locale locale = Locale.getDefault();

    public void addMapping(Dictionary paramDictionary, ClassLoader paramClassLoader) {
        if (paramDictionary == null)
            throw new IllegalArgumentException();
        this.defaultMapping = paramDictionary;
        this.defaultLoader = paramClassLoader;
    }

    public void addMapping(String paramString, Dictionary paramDictionary, ClassLoader paramClassLoader) {
        if (paramString == null || paramDictionary == null)
            throw new IllegalArgumentException();
        if (this.nsMappings == null) {
            this.nsMappings = new Hashtable();
            this.nsLoaders = new Hashtable();
        }
        this.nsMappings.put(paramString, paramDictionary);
        if (paramClassLoader != null)
            this.nsLoaders.put(paramString, paramClassLoader);
    }

    public void setDefaultNamespace(String paramString) {
        this.defaultNs = paramString;
    }

    private Class map2Class(String paramString, Dictionary paramDictionary, ClassLoader paramClassLoader) {
        Object object = paramDictionary.get(paramString);
        if (object instanceof Class)
            return (Class) object;
        if (object == null)
            return null;
        if (object instanceof String) {
            String str = (String) object;
            try {
                Class clazz;
                if (paramClassLoader == null) {
                    clazz = Class.forName(str);
                } else {
                    clazz = paramClassLoader.loadClass(str);
                }
                if (!ElementNode.class.isAssignableFrom(clazz))
                    throw new IllegalArgumentException(getMessage("SEF-000", new Object[]{paramString, str}));
                paramDictionary.put(paramString, clazz);
                return clazz;
            } catch (ClassNotFoundException classNotFoundException) {
                throw new IllegalArgumentException(getMessage("SEF-001", new Object[]{paramString, str, classNotFoundException.getMessage()}));
            }
        }
        throw new IllegalArgumentException(getMessage("SEF-002", new Object[]{paramString}));
    }

    private ElementNode doMap(String paramString, Dictionary paramDictionary, ClassLoader paramClassLoader) {
        ElementNode elementNode;
        Class clazz = map2Class(paramString, paramDictionary, paramClassLoader);
        if (clazz == null)
            clazz = map2Class("*Element", paramDictionary, paramClassLoader);
        if (clazz == null) {
            elementNode = new ElementNode();
        } else {
            try {
                elementNode = clazz.newInstance();
            } catch (Exception exception) {
                throw new IllegalArgumentException(getMessage("SEF-003", new Object[]{paramString, clazz.getName(), exception.getMessage()}));
            }
        }
        return elementNode;
    }

    public ElementEx createElementEx(String paramString1, String paramString2) {
        Dictionary dictionary = null;
        if (paramString1 == null)
            paramString1 = this.defaultNs;
        if (this.nsMappings != null)
            dictionary = (Dictionary) this.nsMappings.get(paramString1);
        if (dictionary == null)
            return doMap(paramString2, this.defaultMapping, this.defaultLoader);
        return doMap(paramString2, dictionary, (ClassLoader) this.nsLoaders.get(paramString1));
    }

    public ElementEx createElementEx(String paramString) {
        return doMap(paramString, this.defaultMapping, this.defaultLoader);
    }

    String getMessage(String paramString) {
        return getMessage(paramString, null);
    }

    String getMessage(String paramString, Object[] paramArrayOfObject) {
        return XmlDocument.catalog.getMessage(this.locale, paramString, paramArrayOfObject);
    }
}
