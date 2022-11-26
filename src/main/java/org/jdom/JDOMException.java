package org.jdom;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.SQLException;
import org.xml.sax.SAXException;

public class JDOMException extends Exception {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMException.java,v $ $Revision: 1.11 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";
    protected Throwable cause;

    public JDOMException() {
        super("Error occurred in JDOM application.");
    }

    public JDOMException(String message) {
        super(message);
    }

    public JDOMException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable initCause(Throwable cause) {
        this.cause = cause;
        return cause;
    }

    public String getMessage() {
        String msg = super.getMessage();
        Throwable parent = this;
        Throwable child;
        while ((child = getNestedException(parent)) != null) {
            String msg2 = child.getMessage();
            if (child instanceof SAXException) {
                Throwable grandchild = ((SAXException) child).getException();
                if (grandchild != null && msg2 != null && msg2.equals(grandchild.getMessage()))
                    msg2 = null;
            }
            if (msg2 != null)
                if (msg != null) {
                    msg = String.valueOf(msg) + ": " + msg2;
                } else {
                    msg = msg2;
                }
            if (!(child instanceof JDOMException)) {
                parent = child;
                continue;
            }
            break;
        }
        return msg;
    }

    public void printStackTrace() {
        super.printStackTrace();
        Throwable parent = this;
        Throwable child;
        while ((child = getNestedException(parent)) != null) {
            if (child != null) {
                System.err.print("Caused by: ");
                child.printStackTrace();
                if (!(child instanceof JDOMException)) {
                    parent = child;
                    continue;
                }
                break;
            }
        }
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        Throwable parent = this;
        Throwable child;
        while ((child = getNestedException(parent)) != null) {
            if (child != null) {
                System.err.print("Caused by: ");
                child.printStackTrace(s);
                if (!(child instanceof JDOMException)) {
                    parent = child;
                    continue;
                }
                break;
            }
        }
    }

    public void printStackTrace(PrintWriter w) {
        super.printStackTrace(w);
        Throwable parent = this;
        Throwable child;
        while ((child = getNestedException(parent)) != null) {
            if (child != null) {
                System.err.print("Caused by: ");
                child.printStackTrace(w);
                if (!(child instanceof JDOMException)) {
                    parent = child;
                    continue;
                }
                break;
            }
        }
    }

    public Throwable getCause() {
        return this.cause;
    }

    private static Throwable getNestedException(Throwable parent) {
        if (parent instanceof JDOMException)
            return ((JDOMException) parent).getCause();
        if (parent instanceof SAXException)
            return ((SAXException) parent).getException();
        if (parent instanceof SQLException)
            return ((SQLException) parent).getNextException();
        if (parent instanceof InvocationTargetException)
            return ((InvocationTargetException) parent).getTargetException();
        if (parent instanceof ExceptionInInitializerError)
            return ((ExceptionInInitializerError) parent).getException();
        if (parent instanceof RemoteException)
            return ((RemoteException) parent).detail;
        Throwable nestedException = getNestedException(parent, "javax.naming.NamingException", "getRootCause");
        if (nestedException != null)
            return nestedException;
        nestedException = getNestedException(parent, "javax.servlet.ServletException", "getRootCause");
        if (nestedException != null)
            return nestedException;
        return null;
    }

    private static Throwable getNestedException(Throwable parent, String className, String methodName) {
        try {
            Class testClass = Class.forName(className);
            Class objectClass = parent.getClass();
            if (testClass.isAssignableFrom(objectClass)) {
                Class[] argClasses = new Class[0];
                Method method = testClass.getMethod(methodName, argClasses);
                Object[] args = new Object[0];
                return (Throwable) method.invoke(parent, args);
            }
        } catch (Exception exception) {
        }
        return null;
    }
}
