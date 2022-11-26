package gnu.jel;

import gnu.jel.debug.Debug;
import gnu.jel.debug.Tester;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Library {
    private Class[] staticLib;
    private Class[] dynamicLib;
    private Hashtable names;
    private Hashtable dynIDs;
    private Hashtable stateless;

    public Library(Class[] paramArrayOfClass1, Class[] paramArrayOfClass2) {
        this.staticLib = paramArrayOfClass1;
        this.dynamicLib = paramArrayOfClass2;
        rehash();
    }

    private String describe(String paramString, Class[] paramArrayOfClass) {
        null = "";
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(paramString);
        stringBuffer.append('(');
        if (paramArrayOfClass != null)
            for (byte b = 0; b < paramArrayOfClass.length; b++) {
                if (b != 0)
                    stringBuffer.append(',');
                stringBuffer.append(paramArrayOfClass[b].toString());
            }
        stringBuffer.append(')');
        return stringBuffer.toString();
    }

    int getDynamicMethodClassID(Object paramObject) {
        Integer integer = (Integer) this.dynIDs.get(paramObject);
        return (integer == null) ? -1 : integer.intValue();
    }

    Object getMethod(String paramString, Class[] paramArrayOfClass) throws NoSuchMethodException {
        Hashtable hashtable = (Hashtable) this.names.get(paramString);
        if (hashtable == null)
            throw new NoSuchMethodException("The name \"" + paramString + "\" is not defined.");
        Vector vector = new Vector();
        Enumeration enumeration1 = hashtable.elements();
        while (enumeration1.hasMoreElements()) {
            Object object1 = enumeration1.nextElement();
            Class[] arrayOfClass1 = getParameterTypes(object1);
            boolean bool = false;
            if (paramArrayOfClass != null) {
                if (arrayOfClass1.length == paramArrayOfClass.length) {
                    bool = true;
                    for (byte b = 0; b < arrayOfClass1.length && bool; b++)
                        bool = ExpressionImage.canConvertByWidening(paramArrayOfClass[b], arrayOfClass1[b]);
                }
            } else {
                bool = !(arrayOfClass1.length != 0);
            }
            if (bool)
                vector.addElement(object1);
        }
        if (vector.size() == 0)
            throw new NoSuchMethodException("Function \"" + paramString + "\" exists," + " but parameters " + describe(paramString, paramArrayOfClass) + " can not be accepted by it.");
        if (vector.size() == 1)
            return vector.firstElement();
        Enumeration enumeration2 = vector.elements();
        Object object = enumeration2.nextElement();
        Class[] arrayOfClass = getParameterTypes(object);
        while (enumeration2.hasMoreElements()) {
            Object object1 = enumeration2.nextElement();
            Class[] arrayOfClass1 = getParameterTypes(object1);
            boolean bool1 = true;
            boolean bool2 = true;
            for (byte b = 0; b < arrayOfClass1.length; b++) {
                bool1 = (!bool1 || !ExpressionImage.canConvertByWidening(arrayOfClass1[b], arrayOfClass[b])) ? false : true;
                bool2 = (!bool2 || !ExpressionImage.canConvertByWidening(arrayOfClass[b], arrayOfClass1[b])) ? false : true;
            }
            if (bool1 && !bool2) {
                object = object1;
                arrayOfClass = arrayOfClass1;
            }
            if ((bool1 ^ bool2) == 0)
                throw new NoSuchMethodException("Ambiguity detected between \"" + describe(paramString, arrayOfClass) + "\" and \"" + describe(paramString, arrayOfClass1) + "\" on invocation \""
                    + describe(paramString, paramArrayOfClass) + "\" .");
        }
        return object;
    }

    private Class[] getParameterTypes(Object paramObject) {
        Class[] arrayOfClass = null;
        if (paramObject instanceof Field) {
            arrayOfClass = new Class[0];
        } else if (paramObject instanceof Method) {
            arrayOfClass = ((Method) paramObject).getParameterTypes();
        }
        return arrayOfClass;
    }

    public boolean isStateless(Object paramObject) {
        return this.stateless.containsKey(paramObject);
    }

    public static void main(String[] paramArrayOfString) {
        Tester tester = new Tester(System.out);
        test(tester);
        tester.summarize();
    }

    public void markStateDependent(String paramString, Class[] paramArrayOfClass) throws NoSuchMethodException {
        Object object1 = getMethod(paramString, paramArrayOfClass);
        Object object2 = this.stateless.remove(object1);
    }

    private void rehash() {
        this.names = new Hashtable();
        this.dynIDs = new Hashtable();
        this.stateless = new Hashtable();
        if (this.staticLib != null)
            rehash(this.staticLib, true);
        if (this.dynamicLib != null)
            rehash(this.dynamicLib, false);
    }

    private boolean rehash(String paramString1, String paramString2, Object paramObject) {
        Hashtable hashtable = (Hashtable) this.names.get(paramString1);
        if (hashtable == null) {
            Hashtable hashtable1 = new Hashtable();
            hashtable1.put(paramString2, paramObject);
            this.names.put(paramString1, hashtable1);
            return true;
        }
        Object object = hashtable.get(paramString2);
        if (object == null) {
            hashtable.put(paramString2, paramObject);
            return true;
        }
        return false;
    }

    private boolean rehash(Field paramField) {
        return rehash(paramField.getName(), "()" + ExpressionImage.getSignature(paramField.getType()), paramField);
    }

    private boolean rehash(Method paramMethod) {
        return rehash(paramMethod.getName(), ExpressionImage.getSignature(paramMethod), paramMethod);
    }

    private void rehash(Class[] paramArrayOfClass, boolean paramBoolean) {
        for (byte b = 0; b < paramArrayOfClass.length; b++) {
            Integer integer = new Integer(b);
            Method[] arrayOfMethod = paramArrayOfClass[b].getMethods();
            for (byte b1 = 0; b1 < arrayOfMethod.length; b1++) {
                if (Modifier.isStatic(arrayOfMethod[b1].getModifiers())) {
                    if (paramBoolean && rehash(arrayOfMethod[b1]))
                        this.stateless.put(arrayOfMethod[b1], Boolean.TRUE);
                } else if (!paramBoolean && rehash(arrayOfMethod[b1])) {
                    this.dynIDs.put(arrayOfMethod[b1], integer);
                }
            }
            Field[] arrayOfField = paramArrayOfClass[b].getFields();
            for (byte b2 = 0; b2 < arrayOfField.length; b2++) {
                if (Modifier.isStatic(arrayOfField[b2].getModifiers())) {
                    if (paramBoolean && rehash(arrayOfField[b2]))
                        this.stateless.put(arrayOfField[b2], Boolean.TRUE);
                } else if (!paramBoolean && rehash(arrayOfField[b2])) {
                    this.dynIDs.put(arrayOfField[b2], integer);
                }
            }
        }
    }

    public static void test(Tester paramTester) {
        Library library = null;
        Class clazz = null;
        try {
            clazz = Class.forName("java.lang.Math");
        } catch (ClassNotFoundException classNotFoundException) {
            Debug.println("It is IMPOSSIBLE :)");
        }
        paramTester.startTest("Creating the library of java.lang.Math");
        try {
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = clazz;
            Library library1 = new Library(arrayOfClass, null);
            library = library1;
            paramTester.testOK();
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation round(double)");
        try {
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = double.class;
            Method method = (Method) library.getMethod("round", arrayOfClass);
            if (method != null && method.equals(clazz.getMethod("round", arrayOfClass))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation round(float)");
        try {
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = float.class;
            Method method = (Method) library.getMethod("round", arrayOfClass);
            if (method != null && method.equals(clazz.getMethod("round", arrayOfClass))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation round(int) best is round(float)");
        try {
            Class[] arrayOfClass1 = new Class[1];
            arrayOfClass1[0] = int.class;
            Method method = (Method) library.getMethod("round", arrayOfClass1);
            Class[] arrayOfClass2 = new Class[1];
            arrayOfClass2[0] = float.class;
            if (method != null && method.equals(clazz.getMethod("round", arrayOfClass2))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation abs(int) best is abs(int)");
        try {
            Class[] arrayOfClass1 = new Class[1];
            arrayOfClass1[0] = int.class;
            Method method = (Method) library.getMethod("abs", arrayOfClass1);
            Class[] arrayOfClass2 = new Class[1];
            arrayOfClass2[0] = int.class;
            if (method != null && method.equals(clazz.getMethod("abs", arrayOfClass2))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation abs(byte) best is abs(int)");
        try {
            Class[] arrayOfClass1 = new Class[1];
            arrayOfClass1[0] = byte.class;
            Method method = (Method) library.getMethod("abs", arrayOfClass1);
            Class[] arrayOfClass2 = new Class[1];
            arrayOfClass2[0] = int.class;
            if (method != null && method.equals(clazz.getMethod("abs", arrayOfClass2))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation abs(char) best is abs(int)");
        try {
            Class[] arrayOfClass1 = new Class[1];
            arrayOfClass1[0] = char.class;
            Method method = (Method) library.getMethod("abs", arrayOfClass1);
            Class[] arrayOfClass2 = new Class[1];
            arrayOfClass2[0] = int.class;
            if (method != null && method.equals(clazz.getMethod("abs", arrayOfClass2))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of invocation min(int,float) best is min(float,float)");
        try {
            Class[] arrayOfClass1 = new Class[2];
            arrayOfClass1[0] = int.class;
            arrayOfClass1[1] = float.class;
            Method method = (Method) library.getMethod("min", arrayOfClass1);
            Class[] arrayOfClass2 = new Class[2];
            arrayOfClass2[0] = float.class;
            arrayOfClass2[1] = float.class;
            if (method != null && method.equals(clazz.getMethod("min", arrayOfClass2))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Attempt of access to the field PI");
        try {
            Class[] arrayOfClass = new Class[0];
            Field field = (Field) library.getMethod("PI", arrayOfClass);
            if (field != null && field.getName().equals("PI")) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
        paramTester.startTest("Checking assignment of state dependence ");
        try {
            library.markStateDependent("random", null);
            if (!library.isStateless(library.getMethod("random", null))) {
                paramTester.testOK();
            } else {
                paramTester.testFail();
            }
        } catch (Throwable throwable) {
            Debug.reportThrowable(throwable);
            paramTester.testFail();
        }
    }
}
