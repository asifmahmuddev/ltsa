package uk.ac.ic.doc.natutil;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class CommandLineParser {
    public static void printOptions(Object paramObject) {
        printOptions(System.err, paramObject);
    }

    public static void printOptions(OutputStream paramOutputStream, Object paramObject) {
        printOptions(new PrintWriter(paramOutputStream), paramObject);
    }

    public static void printOptions(PrintWriter paramPrintWriter, Object paramObject) {
        try {
            Field[] arrayOfField = paramObject.getClass().getFields();
            for (byte b = 0; b < arrayOfField.length; b++) {
                Field field = arrayOfField[b];
                paramPrintWriter.print(fieldNameToOption(field.getName()));
                paramPrintWriter.print(" : ");
                paramPrintWriter.print(field.getType().getName());
                paramPrintWriter.print(" [");
                paramPrintWriter.print(field.get(paramObject).toString());
                paramPrintWriter.println("]");
            }
            paramPrintWriter.flush();
        } catch (IllegalAccessException illegalAccessException) {
            throw new Error("cannot access fields of options structure");
        }
    }

    public static void parseOptions(Object paramObject, String[] paramArrayOfString) throws CommandLineException {
        byte b = 0;
        try {
            Class clazz = paramObject.getClass();
            for (b = 0; b < paramArrayOfString.length; b += 2) {
                String str = optionToFieldName(paramArrayOfString[b].substring(1));
                Field field = clazz.getField(str);
                Object object = Instantiate.newObject(field.getType(), paramArrayOfString[b + 1]);
                field.set(paramObject, object);
            }
        } catch (Exception exception) {
            throw new CommandLineException("failed to parse " + paramArrayOfString[b] + " option: " + exception.getMessage());
        }
    }

    private static String optionToFieldName(String paramString) throws CommandLineException {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < paramString.length(); b++) {
            char c = paramString.charAt(b);
            if ((c == '\000' && Character.isJavaIdentifierStart(c)) || (c > '\000' && Character.isJavaIdentifierPart(c))) {
                stringBuffer.append(c);
            } else if (c == '-') {
                stringBuffer.append('_');
            } else {
                throw new CommandLineException("invalid option name \"" + paramString + "\"");
            }
        }
        return stringBuffer.toString();
    }

    private static String fieldNameToOption(String paramString) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < paramString.length(); b++) {
            char c = paramString.charAt(b);
            if (c == '_') {
                stringBuffer.append('-');
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }
}
