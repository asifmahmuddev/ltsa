package uk.ac.ic.doc.natutil;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ChainedException extends Exception {
    private Throwable _cause;

    public ChainedException(String paramString, Throwable paramThrowable) {
        super(paramString);
        this._cause = paramThrowable;
    }

    public ChainedException(String paramString) {
        super(paramString);
        this._cause = null;
    }

    public ChainedException() {
        this._cause = null;
    }

    public String getMessage() {
        String str1 = super.getMessage();
        String str2 = (this._cause == null) ? null : this._cause.getMessage();
        if (str1 == null && str2 == null)
            return null;
        if (str1 == null)
            return str2;
        if (str2 == null)
            return str1;
        return String.valueOf(str1) + " (" + str2 + ")";
    }

    public Throwable getCause() {
        return this._cause;
    }

    public void printStackTrace(PrintWriter paramPrintWriter) {
        super.printStackTrace(paramPrintWriter);
        if (this._cause != null) {
            paramPrintWriter.println("Caused by");
            this._cause.printStackTrace(paramPrintWriter);
        }
    }

    public void printStackTrace(PrintStream paramPrintStream) {
        super.printStackTrace(paramPrintStream);
        if (this._cause != null) {
            paramPrintStream.println("Caused by");
            this._cause.printStackTrace(paramPrintStream);
        }
    }
}
