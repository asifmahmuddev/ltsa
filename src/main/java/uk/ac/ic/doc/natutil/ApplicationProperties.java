package uk.ac.ic.doc.natutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

public class ApplicationProperties extends Properties {
    public ApplicationProperties(String paramString, Class paramClass) throws IOException {
        this(paramString, paramClass, String.valueOf(paramString) + ".properties");
    }

    public ApplicationProperties(String paramString1, Class paramClass, String paramString2) throws IOException {
        try {
            InputStream inputStream = openApplicationProperties(paramString1, paramClass, paramString2);
            try {
                load(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException iOException) {
        }
    }

    public static InputStream openApplicationProperties(String paramString1, Class paramClass, String paramString2) throws IOException {
        File file = getPropertiesFile(paramString1, paramString2);
        if (file != null)
            return new FileInputStream(file);
        InputStream inputStream = paramClass.getResourceAsStream(paramString2);
        if (inputStream != null)
            return inputStream;
        throw new IOException("resource \"" + paramString2 + "\" not found");
    }

    private static File getPropertiesFile(String paramString1, String paramString2) throws IOException {
        String str = String.valueOf(paramString1) + ".jar";
        StringTokenizer stringTokenizer = new StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"));
        File file = null;
        while (stringTokenizer.hasMoreTokens()) {
            File file1 = new File(stringTokenizer.nextToken());
            if (file1.isDirectory()) {
                file = findDirProperties(file1, paramString1, paramString2);
            } else if (file1.getName().equals(str)) {
                file = findJarProperties(file1, paramString1, paramString2);
            }
            if (file != null && file.exists())
                return file;
        }
        return null;
    }

    private static File findDirProperties(File paramFile, String paramString1, String paramString2) {
        File file = paramFile.getParentFile();
        if (file != null && file.exists() && file.getName().equals(paramString1))
            return new File(new File(file, "lib"), paramString2);
        return null;
    }

    private static File findJarProperties(File paramFile, String paramString1, String paramString2) {
        File file = paramFile.getParentFile();
        if (file != null)
            return new File(file, paramString2);
        return null;
    }
}
