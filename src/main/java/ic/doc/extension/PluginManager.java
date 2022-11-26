package ic.doc.extension;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginManager {
    private Map o_plugins;
    private LTSA o_ltsa;

    public PluginManager(LTSA paramLTSA) {
        this.o_ltsa = paramLTSA;
        this.o_plugins = new HashMap();
        File file = new File("plugins");
        if (file.isDirectory()) {
            String[] arrayOfString = file.list();
            for (byte b = 0; b < arrayOfString.length; b++) {
                URLClassLoader uRLClassLoader = null;
                try {
                    URL uRL = (new File("plugins/" + arrayOfString[b])).toURL();
                    uRLClassLoader = new URLClassLoader(new URL[]{uRL});
                } catch (MalformedURLException malformedURLException) {
                    System.err.println(malformedURLException);
                }
                JarClassLoader jarClassLoader = new JarClassLoader("plugins/" + arrayOfString[b]);
                String str = getPluginClassName(jarClassLoader);
                str = convertSlashesToDots(str);
                jarClassLoader = null;
                System.gc();
                try {
                    Class clazz = Class.forName(str, true, uRLClassLoader);
                    Class[] arrayOfClass = {LTSA.class};
                    Constructor constructor = clazz.getConstructor(arrayOfClass);
                    Object[] arrayOfObject = {this.o_ltsa};
                    LTSAPlugin lTSAPlugin = (LTSAPlugin) constructor.newInstance(arrayOfObject);
                    this.o_plugins.put(lTSAPlugin.getName(), lTSAPlugin);
                } catch (ClassNotFoundException classNotFoundException) {
                    System.err.println(classNotFoundException);
                } catch (InstantiationException instantiationException) {
                    System.err.println(instantiationException);
                } catch (IllegalAccessException illegalAccessException) {
                    System.err.println(illegalAccessException);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public LTSAPlugin getPlugin(String paramString) {
        Object object = this.o_plugins.get(paramString);
        if (object != null && object instanceof LTSAPlugin)
            return (LTSAPlugin) object;
        return null;
    }

    public Iterator getPluginIterator() {
        return this.o_plugins.values().iterator();
    }

    public Iterator getPluginNameIterator() {
        return this.o_plugins.keySet().iterator();
    }

    private String getPluginClassName(JarClassLoader paramJarClassLoader) {
        Enumeration enumeration = paramJarClassLoader.enumerateResources();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement().toString();
            if (str.indexOf('$') != -1)
                continue;
            if (str.endsWith(".class")) {
                String str1 = str.substring(0, str.lastIndexOf("."));
                try {
                    Class clazz1 = Class.forName("ic.doc.extension.LTSAPlugin");
                    Class clazz2 = paramJarClassLoader.loadClass(str1);
                    if (clazz1.isAssignableFrom(clazz2))
                        return str1;
                } catch (NullPointerException nullPointerException) {
                    nullPointerException.printStackTrace();
                } catch (Exception exception) {
                    System.err.println(exception);
                }
            }
        }
        return "";
    }

    private String convertSlashesToDots(String paramString) {
        char[] arrayOfChar = paramString.toCharArray();
        for (byte b = 0; b < arrayOfChar.length; b++) {
            if (arrayOfChar[b] == '/')
                arrayOfChar[b] = '.';
        }
        return new String(arrayOfChar);
    }
}
