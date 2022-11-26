package ic.doc.extension;

import java.util.Hashtable;

public abstract class MultiClassLoader extends ClassLoader {
    private Hashtable classes = new Hashtable<>();
    private char classNameReplacementChar;
    protected boolean monitorOn = false;
    protected boolean sourceMonitorOn = true;

    public Class loadClass(String paramString) throws ClassNotFoundException {
        return loadClass(paramString, true);
    }

    public synchronized Class loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
        paramString = paramString.replace('/', '.');
        monitor(">> MultiClassLoader.loadClass(" + paramString + ", " + paramBoolean + ")");
        Class<?> clazz = (Class) this.classes.get(paramString);
        if (clazz != null) {
            monitor(">> returning cached result.");
            return clazz;
        }
        try {
            clazz = findSystemClass(paramString);
            monitor(">> returning system class (in CLASSPATH).");
            return clazz;
        } catch (ClassNotFoundException classNotFoundException) {
            monitor(">> Not a system class.");
            byte[] arrayOfByte = loadClassBytes(paramString);
            if (arrayOfByte == null)
                throw new ClassNotFoundException();
            clazz = defineClass(paramString, arrayOfByte, 0, arrayOfByte.length);
            if (clazz == null)
                throw new ClassFormatError();
            if (paramBoolean)
                resolveClass(clazz);
            this.classes.put(paramString, clazz);
            monitor(">> Returning newly loaded class.");
            return clazz;
        }
    }

    public void setClassNameReplacementChar(char paramChar) {
        this.classNameReplacementChar = paramChar;
    }

    protected abstract byte[] loadClassBytes(String paramString);

    protected String formatClassName(String paramString) {
        if (this.classNameReplacementChar == '\000')
            return paramString.replace('.', '/') + ".class";
        return paramString.replace('.', this.classNameReplacementChar) + ".class";
    }

    protected void monitor(String paramString) {
        if (this.monitorOn)
            print(paramString);
    }

    protected static void print(String paramString) {
        System.out.println(paramString);
    }
}
