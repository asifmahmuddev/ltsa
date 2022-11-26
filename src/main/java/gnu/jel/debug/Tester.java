package gnu.jel.debug;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Tester {
    private int succ_tst;
    private int fail_tst;
    private long initTime;
    private long totalTestTime;
    private String doing = null;
    private long initTestTime;
    private OutputStream log_stream;
    private PrintWriter log;

    public Tester(OutputStream paramOutputStream) {
        this.log_stream = paramOutputStream;
        this.log = new PrintWriter(this.log_stream, true);
        this.initTime = System.currentTimeMillis();
    }

    public boolean compare(int paramInt1, int paramInt2) {
        if (paramInt2 == paramInt1) {
            testOK();
            return true;
        }
        testFail();
        this.log.print("EXP: ");
        this.log.print(paramInt2);
        this.log.println();
        this.log.print("GOT: ");
        this.log.print(paramInt1);
        this.log.println();
        this.log.println();
        return false;
    }

    public boolean compare(String paramString1, String paramString2) {
        if (paramString2.equals(paramString1)) {
            testOK();
            return true;
        }
        testFail();
        this.log.print("EXP: \"");
        this.log.print(paramString2);
        this.log.println("\"");
        this.log.print("GOT: \"");
        this.log.print(paramString1);
        this.log.println("\"");
        this.log.println();
        return false;
    }

    public boolean compare(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        return true;
    }

    private String fillLeft(String paramString, int paramInt, char paramChar) {
        return "";
    }

    public static void main(String[] paramArrayOfString) {
    }

    public void printHexLine16(PrintWriter paramPrintWriter, byte[] paramArrayOfbyte, int paramInt) {
    }

    public void startTest(String paramString) {
    }

    public void summarize() {
    }

    public void testFail() {
    }

    public void testFailProgressing() {
    }

    public void testOK() {
    }
}
