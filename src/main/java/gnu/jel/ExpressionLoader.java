package gnu.jel;

class ExpressionLoader extends ClassLoader {
    String name;
    byte[] bytes;
    Class c;
    ClassLoader parent;

    ExpressionLoader(String paramString, byte[] paramArrayOfbyte) {
        this.name = paramString;
        this.bytes = paramArrayOfbyte;
        this.c = null;
        this.parent = getClass().getClassLoader();
    }

    protected synchronized Class loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
        if (!paramString.equals(this.name))
            return (this.parent != null) ? this.parent.loadClass(paramString) : findSystemClass(paramString);
        if (this.c == null) {
            this.c = defineClass(paramString, this.bytes, 0, this.bytes.length);
            if (paramBoolean)
                resolveClass(this.c);
        }
        return this.c;
    }
}
