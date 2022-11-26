package ic.doc.extension;

import java.util.Enumeration;

public class JarClassLoader extends MultiClassLoader {
    private JarResources jarResources;

    public JarClassLoader(String paramString) {
        this.jarResources = new JarResources(paramString);
    }

    protected byte[] loadClassBytes(String paramString) {
        paramString = formatClassName(paramString);
        return this.jarResources.getResource(paramString);
    }

    public Enumeration enumerateResources() {
        return this.jarResources.enumerateResources();
    }
}
