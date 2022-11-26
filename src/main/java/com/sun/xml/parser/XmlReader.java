package com.sun.xml.parser;

import java.io.ByteArrayInputStream;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Hashtable;

final class XmlReader extends Reader {
    private static final int MAXPUSHBACK = 512;
    private Reader in;
    private String assignedEncoding;
    private boolean closed;

    public static Reader createReader(InputStream paramInputStream) throws IOException {
        return new XmlReader(paramInputStream);
    }

    public static Reader createReader(InputStream paramInputStream, String paramString) throws IOException {
        if (paramString == null)
            return new XmlReader(paramInputStream);
        if ("UTF-8".equalsIgnoreCase(paramString) || "UTF8".equalsIgnoreCase(paramString))
            return new Utf8Reader(paramInputStream);
        if ("US-ASCII".equalsIgnoreCase(paramString) || "ASCII".equalsIgnoreCase(paramString))
            return new AsciiReader(paramInputStream);
        if ("ISO-8859-1".equalsIgnoreCase(paramString))
            return new Iso8859_1Reader(paramInputStream);
        return new InputStreamReader(paramInputStream, std2java(paramString));
    }

    private static final Hashtable charsets = new Hashtable(31);
    static {
        charsets.put("UTF-16", "Unicode");
        charsets.put("ISO-10646-UCS-2", "Unicode");
        charsets.put("EBCDIC-CP-US", "cp037");
        charsets.put("EBCDIC-CP-CA", "cp037");
        charsets.put("EBCDIC-CP-NL", "cp037");
        charsets.put("EBCDIC-CP-WT", "cp037");
        charsets.put("EBCDIC-CP-DK", "cp277");
        charsets.put("EBCDIC-CP-NO", "cp277");
        charsets.put("EBCDIC-CP-FI", "cp278");
        charsets.put("EBCDIC-CP-SE", "cp278");
        charsets.put("EBCDIC-CP-IT", "cp280");
        charsets.put("EBCDIC-CP-ES", "cp284");
        charsets.put("EBCDIC-CP-GB", "cp285");
        charsets.put("EBCDIC-CP-FR", "cp297");
        charsets.put("EBCDIC-CP-AR1", "cp420");
        charsets.put("EBCDIC-CP-HE", "cp424");
        charsets.put("EBCDIC-CP-BE", "cp500");
        charsets.put("EBCDIC-CP-CH", "cp500");
        charsets.put("EBCDIC-CP-ROECE", "cp870");
        charsets.put("EBCDIC-CP-YU", "cp870");
        charsets.put("EBCDIC-CP-IS", "cp871");
        charsets.put("EBCDIC-CP-AR2", "cp918");
    }

    private static String std2java(String paramString) {
        String str = paramString.toUpperCase();
        str = (String) charsets.get(str);
        return (str != null) ? str : paramString;
    }

    public String getEncoding() {
        return this.assignedEncoding;
    }

    private XmlReader(InputStream paramInputStream) throws IOException {
        super(paramInputStream);
        PushbackInputStream pushbackInputStream;
        if (paramInputStream instanceof PushbackInputStream) {
            pushbackInputStream = (PushbackInputStream) paramInputStream;
        } else {
            pushbackInputStream = new PushbackInputStream(paramInputStream, 512);
        }
        byte[] arrayOfByte = new byte[4];
        int i = pushbackInputStream.read(arrayOfByte);
        if (i > 0)
            pushbackInputStream.unread(arrayOfByte, 0, i);
        if (i == 4)
            switch (arrayOfByte[0] & 0xFF) {
                case 0 :
                    if (arrayOfByte[1] == 60 && arrayOfByte[2] == 0 && arrayOfByte[3] == 63) {
                        setEncoding(pushbackInputStream, "UnicodeBig");
                        return;
                    }
                    break;
                case 60 :
                    switch (arrayOfByte[1] & 0xFF) {
                        case 0 :
                            if (arrayOfByte[2] == 63 && arrayOfByte[3] == 0) {
                                setEncoding(pushbackInputStream, "UnicodeLittle");
                                return;
                            }
                            break;
                        case 63 :
                            if (arrayOfByte[2] != 120 || arrayOfByte[3] != 109)
                                break;
                            useEncodingDecl(pushbackInputStream, "UTF8");
                            return;
                    }
                    break;
                case 76 :
                    if (arrayOfByte[1] == 111 && (0xFF & arrayOfByte[2]) == 167 && (0xFF & arrayOfByte[3]) == 148) {
                        useEncodingDecl(pushbackInputStream, "CP037");
                        return;
                    }
                    break;
                case 254 :
                    if ((arrayOfByte[1] & 0xFF) == 255) {
                        setEncoding(pushbackInputStream, "UTF-16");
                        return;
                    }
                    break;
                case 255 :
                    if ((arrayOfByte[1] & 0xFF) == 254) {
                        setEncoding(pushbackInputStream, "UTF-16");
                        return;
                    }
                    break;
            }
        setEncoding(pushbackInputStream, "UTF-8");
    }

    private void useEncodingDecl(PushbackInputStream paramPushbackInputStream, String paramString) throws IOException {
        byte[] arrayOfByte = new byte[512];
        int i = paramPushbackInputStream.read(arrayOfByte, 0, arrayOfByte.length);
        paramPushbackInputStream.unread(arrayOfByte, 0, i);
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(arrayOfByte, 4, i), paramString);
        int j;
        if ((j = inputStreamReader.read()) != 108) {
            setEncoding(paramPushbackInputStream, "UTF-8");
            return;
        }
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = null;
        String str = null;
        boolean bool1 = false;
        char c = Character.MIN_VALUE;
        boolean bool2 = false;
        byte b;
        label77 : for (b = 0; b < 'ǻ' && (j = inputStreamReader.read()) != -1; b++) {
            if (j == 32 || j == 9 || j == 10 || j == 13)
                continue;
            if (b != 0) {
                if (j == 63) {
                    bool2 = true;
                } else if (bool2) {
                    if (j != 62) {
                        bool2 = false;
                    } else {
                        break;
                    }
                }
                if (str == null || !bool1) {
                    if (stringBuffer2 == null) {
                        if (!Character.isWhitespace((char) j)) {
                            stringBuffer2 = stringBuffer1;
                            stringBuffer1.setLength(0);
                            stringBuffer1.append((char) j);
                            bool1 = false;
                        }
                    } else if (Character.isWhitespace((char) j)) {
                        str = stringBuffer2.toString();
                    } else if (j == 61) {
                        if (str == null)
                            str = stringBuffer2.toString();
                        bool1 = true;
                        stringBuffer2 = null;
                        c = Character.MIN_VALUE;
                    } else {
                        stringBuffer2.append((char) j);
                    }
                    continue;
                }
                if (!Character.isWhitespace((char) j)) {
                    if (j == 34 || j == 39) {
                        if (!c) {
                            c = (char) j;
                            stringBuffer1.setLength(0);
                            continue;
                        }
                        if (j == c) {
                            if ("encoding".equals(str)) {
                                this.assignedEncoding = stringBuffer1.toString();
                                for (b = 0; b < this.assignedEncoding.length(); b++) {
                                    j = this.assignedEncoding.charAt(b);
                                    if ((j >= 65 && j <= 90) || (j >= 97 && j <= 122))
                                        continue;
                                    if (b != 0) {
                                        if (b > 0) {
                                            if (j == 45 || (j >= 48 && j <= 57) || j == 46 || j == 95)
                                                continue;
                                            break label77;
                                        }
                                        break label77;
                                    }
                                    break label77;
                                }
                                setEncoding(paramPushbackInputStream, this.assignedEncoding);
                                return;
                            }
                            str = null;
                            continue;
                        }
                    }
                    stringBuffer1.append((char) j);
                }
                continue;
            }
        }
        setEncoding(paramPushbackInputStream, "UTF-8");
    }

    private void setEncoding(InputStream paramInputStream, String paramString) throws IOException {
        this.assignedEncoding = paramString;
        this.in = createReader(paramInputStream, paramString);
    }

    public int read(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException {
        if (this.closed)
            return -1;
        int i = this.in.read(paramArrayOfchar, paramInt1, paramInt2);
        if (i == -1)
            close();
        return i;
    }

    public int read() throws IOException {
        if (this.closed)
            throw new IOException("closed");
        int i = this.in.read();
        if (i == -1)
            close();
        return i;
    }

    public boolean markSupported() {
        return (this.in == null) ? false : this.in.markSupported();
    }

    public void mark(int paramInt) throws IOException {
        if (this.in != null)
            this.in.mark(paramInt);
    }

    public void reset() throws IOException {
        if (this.in != null)
            this.in.reset();
    }

    public long skip(long paramLong) throws IOException {
        return (this.in == null) ? 0L : this.in.skip(paramLong);
    }

    public boolean ready() throws IOException {
        return (this.in == null) ? false : this.in.ready();
    }

    public void close() throws IOException {
        if (this.closed)
            return;
        this.in.close();
        this.in = null;
        this.closed = true;
    }

    static abstract class BaseReader extends Reader {
        protected InputStream instream;
        protected byte[] buffer;
        protected int start;
        protected int finish;

        BaseReader(InputStream param1InputStream) {
            super(param1InputStream);
            this.instream = param1InputStream;
            this.buffer = new byte[8192];
        }

        public boolean ready() throws IOException {
            return !(this.instream != null && this.finish - this.start <= 0 && this.instream.available() == 0);
        }

        public void close() throws IOException {
            if (this.instream != null) {
                this.instream.close();
                this.start = this.finish = 0;
                this.buffer = null;
                this.instream = null;
            }
        }
    }

    static final class Utf8Reader extends BaseReader {
        private char nextChar;

        Utf8Reader(InputStream param1InputStream) {
            super(param1InputStream);
        }

        public int read(char[] param1ArrayOfchar, int param1Int1, int param1Int2) throws IOException {
            byte b = 0;
            int i = 0;
            if (param1Int2 <= 0)
                return 0;
            if (this.nextChar != '\000') {
                param1ArrayOfchar[param1Int1 + b++] = this.nextChar;
                this.nextChar = Character.MIN_VALUE;
            }
            while (b < param1Int2) {
                if (this.finish <= this.start) {
                    if (this.instream == null) {
                        i = -1;
                        break;
                    }
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        close();
                        i = -1;
                        break;
                    }
                }
                i = this.buffer[this.start] & 0xFF;
                if ((i & 0x80) == 0) {
                    this.start++;
                    param1ArrayOfchar[param1Int1 + b++] = (char) i;
                    continue;
                }
                int j = this.start;
                try {
                    if ((this.buffer[j] & 0xE0) == 192) {
                        i = (this.buffer[j++] & 0x1F) << 6;
                        i += this.buffer[j++] & 0x3F;
                    } else if ((this.buffer[j] & 0xF0) == 224) {
                        i = (this.buffer[j++] & 0xF) << 12;
                        i += (this.buffer[j++] & 0x3F) << 6;
                        i += this.buffer[j++] & 0x3F;
                    } else if ((this.buffer[j] & 0xF8) == 240) {
                        i = (this.buffer[j++] & 0x7) << 18;
                        i += (this.buffer[j++] & 0x3F) << 12;
                        i += (this.buffer[j++] & 0x3F) << 6;
                        i += this.buffer[j++] & 0x3F;
                        if (i > 1114111)
                            throw new CharConversionException("UTF-8 encoding of character 0x00" + Integer.toHexString(i) + " can't be converted to Unicode.");
                        i -= 65536;
                        this.nextChar = (char) (56320 + (i & 0x3FF));
                        i = 55296 + (i >> 10);
                    } else {
                        throw new CharConversionException("Unconvertible UTF-8 character beginning with 0x" + Integer.toHexString(this.buffer[this.start] & 0xFF));
                    }
                } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    i = 0;
                }
                if (j > this.finish) {
                    System.arraycopy(this.buffer, this.start, this.buffer, 0, this.finish - this.start);
                    this.finish -= this.start;
                    this.start = 0;
                    j = this.instream.read(this.buffer, this.finish, this.buffer.length - this.finish);
                    if (j < 0) {
                        close();
                        throw new CharConversionException("Partial UTF-8 char");
                    }
                    this.finish += j;
                    continue;
                }
                this.start++;
                for (; this.start < j; this.start++) {
                    if ((this.buffer[this.start] & 0xC0) != 128) {
                        close();
                        throw new CharConversionException("Malformed UTF-8 char -- is an XML encoding declaration missing?");
                    }
                }
                param1ArrayOfchar[param1Int1 + b++] = (char) i;
                if (this.nextChar != '\000' && b < param1Int2) {
                    param1ArrayOfchar[param1Int1 + b++] = this.nextChar;
                    this.nextChar = Character.MIN_VALUE;
                }
            }
            if (b > 0)
                return b;
            return (i == -1) ? -1 : 0;
        }
    }

    static final class AsciiReader extends BaseReader {
        AsciiReader(InputStream param1InputStream) {
            super(param1InputStream);
        }

        public int read(char[] param1ArrayOfchar, int param1Int1, int param1Int2) throws IOException {
            if (this.instream == null)
                return -1;
            byte b;
            for (b = 0; b < param1Int2; b++) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish <= 0)
                            close();
                        break;
                    }
                }
                byte b1 = this.buffer[this.start++];
                if ((b1 & 0x80) != 0)
                    throw new CharConversionException("Illegal ASCII character, 0x" + Integer.toHexString(b1 & 0xFF));
                param1ArrayOfchar[param1Int1 + b] = (char) b1;
            }
            if (b == 0 && this.finish <= 0)
                return -1;
            return b;
        }
    }

    static final class Iso8859_1Reader extends BaseReader {
        Iso8859_1Reader(InputStream param1InputStream) {
            super(param1InputStream);
        }

        public int read(char[] param1ArrayOfchar, int param1Int1, int param1Int2) throws IOException {
            if (this.instream == null)
                return -1;
            byte b;
            for (b = 0; b < param1Int2; b++) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish <= 0)
                            close();
                        break;
                    }
                }
                param1ArrayOfchar[param1Int1 + b] = (char) (0xFF & this.buffer[this.start++]);
            }
            if (b == 0 && this.finish <= 0)
                return -1;
            return b;
        }
    }
}
