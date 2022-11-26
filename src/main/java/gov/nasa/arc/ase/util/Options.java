package gov.nasa.arc.ase.util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Vector;

public class Options {
    private Hashtable parsers;
    private Hashtable printers;
    private Class mainClass;
    private String defaultParam;
    private boolean multipleParam;
    private String[] args;

    public Options(Class paramClass) {
        init(paramClass, null, true);
    }

    public Options(Class paramClass, String paramString) {
        init(paramClass, paramString, true);
    }

    public Options(Class paramClass, String paramString, boolean paramBoolean) {
        init(paramClass, paramString, paramBoolean);
    }

    public void addPrinter(String paramString1, String paramString2) {
        Class[] arrayOfClass = new Class[1];
        arrayOfClass[0] = Object.class;
        try {
            this.printers.put(paramString1, getClass().getMethod(paramString2, arrayOfClass));
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
            System.exit(1);
        }
    }

    public void addParser(String paramString1, String paramString2) {
        Class[] arrayOfClass = new Class[1];
        arrayOfClass[0] = String.class;
        try {
            this.parsers.put(paramString1, getClass().getMethod(paramString2, arrayOfClass));
        } catch (NoSuchMethodException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
            System.exit(1);
        }
    }

    public void print(PrintStream paramPrintStream) {
        Field[] arrayOfField = getClass().getFields();
        int i = 0;
        for (byte b1 = 0; b1 < arrayOfField.length; b1++) {
            if (Modifier.isPublic(arrayOfField[b1].getModifiers())) {
                int j = arrayOfField[b1].getName().length();
                if (i < j)
                    i = j;
            }
        }
        for (byte b2 = 0; b2 < arrayOfField.length; b2++) {
            if (Modifier.isPublic(arrayOfField[b2].getModifiers())) {
                String str = arrayOfField[b2].getName();
                while (str.length() != i)
                    str = str + " ";
                paramPrintStream.print(str + ": ");
                try {
                    String str2;
                    Object object = arrayOfField[b2].get(this);
                    String str1 = arrayOfField[b2].getType().getName();
                    Method method = (Method) this.printers.get(str1);
                    if (object == null) {
                        str2 = "null";
                    } else {
                        str2 = object.toString();
                    }
                    if (method != null) {
                        Object[] arrayOfObject = new Object[1];
                        arrayOfObject[0] = object;
                        try {
                            str2 = (String) method.invoke(this, arrayOfObject);
                        } catch (InvocationTargetException invocationTargetException) {
                        }
                    }
                    paramPrintStream.println(str2);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public void print() {
        Field[] arrayOfField = getClass().getFields();
        int i = 0;
        for (byte b1 = 0; b1 < arrayOfField.length; b1++) {
            if (Modifier.isPublic(arrayOfField[b1].getModifiers())) {
                int j = arrayOfField[b1].getName().length();
                if (i < j)
                    i = j;
            }
        }
        for (byte b2 = 0; b2 < arrayOfField.length; b2++) {
            if (Modifier.isPublic(arrayOfField[b2].getModifiers())) {
                String str = arrayOfField[b2].getName();
                while (str.length() != i)
                    str = str + " ";
                Debug.print(1, str + ": ");
                try {
                    String str2;
                    Object object = arrayOfField[b2].get(this);
                    String str1 = arrayOfField[b2].getType().getName();
                    Method method = (Method) this.printers.get(str1);
                    if (object == null) {
                        str2 = "null";
                    } else {
                        str2 = object.toString();
                    }
                    if (method != null) {
                        Object[] arrayOfObject = new Object[1];
                        arrayOfObject[0] = object;
                        try {
                            str2 = (String) method.invoke(this, arrayOfObject);
                        } catch (InvocationTargetException invocationTargetException) {
                        }
                    }
                    Debug.println(1, str2);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }

    public void usage() {
        Debug.println(0, "usage: ");
        Debug.println(0);
        Debug.print(0, this.mainClass.getName() + " ");
        if (this.defaultParam != null) {
            Debug.print(0, "<" + this.defaultParam + ">");
            if (this.multipleParam)
                Debug.print(0, " ...");
        }
        Debug.println(0);
        Field[] arrayOfField = getClass().getFields();
        for (byte b = 0; b < arrayOfField.length; b++) {
            if (Modifier.isPublic(arrayOfField[b].getModifiers())) {
                String str1 = arrayOfField[b].getName().replace('_', '-');
                String str2 = arrayOfField[b].getType().getName();
                Debug.print(0, "\t-" + str1);
                if (str2.equals("boolean")) {
                    Debug.print(0, " | -no-" + str1);
                } else {
                    Debug.print(0, " <" + str1 + ">");
                }
                Debug.println(0);
            }
        }
        Debug.println(0);
        Debug.println(0, "The default values are:");
        Debug.println(0, "-----------------------");
        print();
        System.exit(1);
    }

    public String[] parse(String[] paramArrayOfString) {
        this.args = paramArrayOfString;
        boolean bool = (this.defaultParam != null);
        boolean bool1 = false;
        Vector vector = new Vector();
        for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
            String str = paramArrayOfString[b1];
            if (str.startsWith("-") && !bool1) {
                boolean bool2 = false;
                Field[] arrayOfField = getClass().getFields();
                if (str.equals("--"))
                    bool1 = bool2 = true;
                for (byte b = 0; b < arrayOfField.length && !bool2; b++) {
                    if (Modifier.isPublic(arrayOfField[b].getModifiers())) {
                        String str1 = arrayOfField[b].getName().replace('_', '-');
                        String str2 = arrayOfField[b].getType().getName();
                        try {
                            if (str2.equals("boolean")) {
                                if (str.equals("-" + str1)) {
                                    arrayOfField[b].setBoolean(this, true);
                                    bool2 = true;
                                }
                                if (str.equals("-no-" + str1)) {
                                    arrayOfField[b].setBoolean(this, false);
                                    bool2 = true;
                                }
                            } else if (str.equals("-" + str1)) {
                                String str3 = null;
                                bool2 = true;
                                b1++;
                                if (b1 != paramArrayOfString.length)
                                    str3 = paramArrayOfString[b1];
                                Method method = (Method) this.parsers.get(str2);
                                if (method == null) {
                                    Debug.println(0, this.mainClass.getName() + ": invalid type -- " + str2);
                                    System.exit(1);
                                }
                                Object[] arrayOfObject = new Object[1];
                                arrayOfObject[0] = str3;
                                try {
                                    arrayOfField[b].set(this, method.invoke(this, arrayOfObject));
                                } catch (InvocationTargetException invocationTargetException) {
                                    if (str3 == null && invocationTargetException.getTargetException() instanceof IllegalArgumentException) {
                                        Debug.println(0, this.mainClass.getName() + ": option requires an argument -- " + str);
                                        System.exit(1);
                                    }
                                    invocationTargetException.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        } catch (IllegalAccessException illegalAccessException) {
                            illegalAccessException.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
                if (!bool2) {
                    Debug.println(0, this.mainClass.getName() + ": invalid option -- " + str);
                    System.exit(1);
                }
            } else if (!bool) {
                Debug.println(0, this.mainClass.getName() + ": invalid parameter -- " + str);
                System.exit(1);
            } else {
                bool = this.multipleParam;
                vector.addElement(str);
            }
        }
        String[] arrayOfString = new String[vector.size()];
        for (byte b2 = 0; b2 < vector.size(); b2++)
            arrayOfString[b2] = vector.elementAt(b2);
        return arrayOfString;
    }

    public Object parseString(String paramString) {
        if (paramString == null)
            throw new IllegalArgumentException();
        return paramString;
    }

    public Object parseLong(String paramString) {
        if (paramString == null)
            throw new IllegalArgumentException();
        return new Long(Long.parseLong(paramString));
    }

    public Object parseInt(String paramString) {
        if (paramString == null)
            throw new IllegalArgumentException();
        return new Integer(Integer.parseInt(paramString));
    }

    public Class getMainClass() {
        return this.mainClass;
    }

    private void init(Class paramClass, String paramString, boolean paramBoolean) {
        this.mainClass = paramClass;
        this.defaultParam = paramString;
        this.multipleParam = paramBoolean;
        this.parsers = new Hashtable();
        this.printers = new Hashtable();
        addParser("java.lang.String", "parseString");
        addParser("int", "parseInt");
        addParser("long", "parseLong");
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < this.args.length; b++) {
            if (this.args[b].indexOf(" ") != -1)
                stringBuffer.append('"');
            stringBuffer.append(this.args[b]);
            if (this.args[b].indexOf(" ") != -1)
                stringBuffer.append('"');
            stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }
}
