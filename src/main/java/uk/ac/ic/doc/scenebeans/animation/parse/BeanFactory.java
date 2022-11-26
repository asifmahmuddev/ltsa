package uk.ac.ic.doc.scenebeans.animation.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class BeanFactory {
    private static class Package {
        private ClassLoader _loader;
        private String _package;

        public Package(ClassLoader param1ClassLoader, String param1String) {
            this._loader = param1ClassLoader;
            this._package = param1String;
        }

        public Package(String param1String) {
            this(ClassLoader.getSystemClassLoader(), param1String);
        }

        public Class loadClass(String param1String) {
            String str = this._package + "." + param1String;
            try {
                return this._loader.loadClass(str);
            } catch (ClassNotFoundException classNotFoundException) {
                return null;
            }
        }
    }

    private static class Category {
        private String _name;
        private List _packages = new ArrayList();
        private String _prefix;
        private String _postfix;
        private boolean _capitalise;

        Category(String param1String1, String param1String2, String param1String3, boolean param1Boolean) {
            this._name = param1String1;
            this._prefix = param1String2;
            this._postfix = param1String3;
            this._capitalise = param1Boolean;
        }

        public void addPackage(ClassLoader param1ClassLoader, String param1String) {
            this._packages.add(new BeanFactory.Package(param1ClassLoader, param1String));
        }

        public void addPackage(String param1String) {
            this._packages.add(new BeanFactory.Package(param1String));
        }

        Object newBean(String param1String) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            String str = this._prefix + Character.toUpperCase(param1String.charAt(0)) + param1String.substring(1) + this._postfix;
            Iterator iterator = this._packages.iterator();
            while (iterator.hasNext()) {
                Class clazz = ((BeanFactory.Package) iterator.next()).loadClass(str);
                if (clazz != null)
                    return clazz.newInstance();
            }
            throw new ClassNotFoundException("no class found for " + this._name + " bean of type \"" + param1String + "\"");
        }
    }

    private Map _categories = new HashMap();

    public void addCategory(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
        if (this._categories.get(paramString1) != null)
            throw new IllegalArgumentException("category name \"" + paramString1 + "\" already defined");
        this._categories.put(paramString1, new Category(paramString1, paramString2, paramString3, paramBoolean));
    }

    public void addPackage(String paramString1, String paramString2) {
        getCategory(paramString1).addPackage(paramString2);
    }

    public void addPackage(String paramString1, ClassLoader paramClassLoader, String paramString2) {
        getCategory(paramString1).addPackage(paramClassLoader, paramString2);
    }

    public Object newBean(String paramString1, String paramString2) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return getCategory(paramString1).newBean(paramString2);
    }

    private Category getCategory(String paramString) {
        Category category = (Category) this._categories.get(paramString);
        if (category != null)
            return category;
        throw new IllegalArgumentException("no category named \"" + paramString + "\"");
    }
}
