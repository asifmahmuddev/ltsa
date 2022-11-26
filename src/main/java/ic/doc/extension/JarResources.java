package ic.doc.extension;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class JarResources {
    public boolean debugOn = false;
    private Hashtable htSizes = new Hashtable();
    private Hashtable htJarContents = new Hashtable();
    private String jarFileName;

    public JarResources(String paramString) {
        this.jarFileName = paramString;
        init();
    }

    public Enumeration enumerateResources() {
        return this.htJarContents.keys();
    }

    public byte[] getResource(String paramString) {
        return (byte[]) this.htJarContents.get(paramString);
    }

    private void init() {
        try {
            ZipFile zipFile = new ZipFile(this.jarFileName);
            Enumeration enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry1 = enumeration.nextElement();
                if (this.debugOn)
                    System.out.println(dumpZipEntry(zipEntry1));
                this.htSizes.put(zipEntry1.getName(), new Integer((int) zipEntry1.getSize()));
            }
            zipFile.close();
            FileInputStream fileInputStream = new FileInputStream(this.jarFileName);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ZipInputStream zipInputStream = new ZipInputStream(bufferedInputStream);
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory())
                    continue;
                if (this.debugOn)
                    System.out.println("ze.getName()=" + zipEntry.getName() + "," + "getSize()=" + zipEntry.getSize());
                int i = (int) zipEntry.getSize();
                if (i == -1)
                    i = ((Integer) this.htSizes.get(zipEntry.getName())).intValue();
                byte[] arrayOfByte = new byte[i];
                int j = 0;
                int k = 0;
                while (i - j > 0) {
                    k = zipInputStream.read(arrayOfByte, j, i - j);
                    if (k == -1)
                        break;
                    j += k;
                }
                this.htJarContents.put(zipEntry.getName(), arrayOfByte);
                if (this.debugOn)
                    System.out.println(zipEntry.getName() + "  rb=" + j + ",size=" + i + ",csize=" + zipEntry.getCompressedSize());
            }
        } catch (NullPointerException nullPointerException) {
            System.out.println("done.");
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private String dumpZipEntry(ZipEntry paramZipEntry) {
        StringBuffer stringBuffer = new StringBuffer();
        if (paramZipEntry.isDirectory()) {
            stringBuffer.append("d ");
        } else {
            stringBuffer.append("f ");
        }
        if (paramZipEntry.getMethod() == 0) {
            stringBuffer.append("stored   ");
        } else {
            stringBuffer.append("defalted ");
        }
        stringBuffer.append(paramZipEntry.getName());
        stringBuffer.append("\t");
        stringBuffer.append("" + paramZipEntry.getSize());
        if (paramZipEntry.getMethod() == 8)
            stringBuffer.append("/" + paramZipEntry.getCompressedSize());
        return stringBuffer.toString();
    }

    public static void main(String[] paramArrayOfString) throws IOException {
        if (paramArrayOfString.length != 2) {
            System.err.println("usage: java JarResources <jar file name> <resource name>");
            System.exit(1);
        }
        JarResources jarResources = new JarResources(paramArrayOfString[0]);
        byte[] arrayOfByte = jarResources.getResource(paramArrayOfString[1]);
        if (arrayOfByte == null) {
            System.out.println("Could not find " + paramArrayOfString[1] + ".");
        } else {
            System.out.println("Found " + paramArrayOfString[1] + " (length=" + arrayOfByte.length + ").");
        }
    }
}
