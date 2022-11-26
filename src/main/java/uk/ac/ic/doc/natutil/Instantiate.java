package uk.ac.ic.doc.natutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instantiate {
    private static Map _parsers = new HashMap();

    static Parser getParser(Class paramClass) {
        synchronized (_parsers) {
            return (Parser) _parsers.get(paramClass);
        }
    }

    public static void addParser(Class paramClass, Parser paramParser) {
        synchronized (_parsers) {
            _parsers.put(paramClass, paramParser);
        }
    }

    public static Object newObject(Class paramClass, String paramString) throws IllegalArgumentException {
        Parser parser = getParser(paramClass);
        if (parser != null)
            return parser.parse(paramString);
        if (paramClass == boolean.class || paramClass == Boolean.class)
            return Boolean.valueOf(paramString);
        if (paramClass == byte.class || paramClass == Byte.class)
            return Byte.valueOf(paramString);
        if (paramClass == short.class || paramClass == Short.class)
            return Short.valueOf(paramString);
        if (paramClass == int.class || paramClass == Integer.class)
            return Integer.valueOf(paramString);
        if (paramClass == long.class || paramClass == Long.class)
            return Long.valueOf(paramString);
        if (paramClass == float.class || paramClass == Float.class)
            return Float.valueOf(paramString);
        if (paramClass == double.class || paramClass == Double.class)
            return Double.valueOf(paramString);
        if (paramClass == char.class || paramClass == Character.class) {
            if (paramString.length() != 1)
                throw new IllegalArgumentException("too many characters - one is enough!");
            return new Character(paramString.charAt(0));
        }
        if (paramClass == String.class)
            return paramString;
        Constructor[] arrayOfConstructor = (Constructor[]) paramClass.getConstructors();
        for (byte b = 0; b < arrayOfConstructor.length; b++) {
            Class[] arrayOfClass = arrayOfConstructor[b].getParameterTypes();
            if (arrayOfClass.length == 1)
                try {
                    Object object = newObject(arrayOfClass[b], paramString);
                    return arrayOfConstructor[b].newInstance(new Object[]{object});
                } catch (InstantiationException instantiationException) {
                } catch (IllegalAccessException illegalAccessException) {
                } catch (IllegalArgumentException illegalArgumentException) {
                } catch (InvocationTargetException invocationTargetException) {
                }
        }
        throw new IllegalArgumentException("cannot convert \"" + paramString + "\" to instance of class " + paramClass.getName());
    }

    public static Object newObject(Class paramClass, List paramList) throws IllegalArgumentException {
        Constructor[] arrayOfConstructor = (Constructor[]) paramClass.getConstructors();
        for (byte b = 0; b < arrayOfConstructor.length; b++) {
            Class[] arrayOfClass = arrayOfConstructor[b].getParameterTypes();
            if (arrayOfClass.length == paramList.size())
                try {
                    Object[] arrayOfObject = new Object[arrayOfClass.length];
                    for (byte b1 = 0; b1 < arrayOfObject.length; b1++)
                        arrayOfObject[b1] = newObject(arrayOfClass[b1], paramList.get(b1));
                    return arrayOfConstructor[b].newInstance(arrayOfObject);
                } catch (InstantiationException instantiationException) {
                } catch (IllegalAccessException illegalAccessException) {
                } catch (IllegalArgumentException illegalArgumentException) {
                } catch (InvocationTargetException invocationTargetException) {
                }
        }
        throw new IllegalArgumentException("failed to find a suitable constructor of class " + paramClass.getName());
    }

    public static interface Parser {
        Object parse(String param1String) throws IllegalArgumentException;
    }
}
