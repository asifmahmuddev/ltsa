package com.sun.xml.parser;

import com.sun.xml.util.XmlChars;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class InputEntity implements Locator {
    private int start;
    private int finish;
    private char[] buf;
    private int lineNumber = 1;
    private boolean returnedFirstHalf = false;
    private boolean maybeInCRLF = false;
    private String name;
    private InputEntity next;
    private InputSource input;
    private Reader reader;
    private boolean isClosed;
    private ErrorHandler errHandler;
    private Locale locale;
    private StringBuffer rememberedText;
    private int startRemember;
    private boolean isPE;
    private static final int BUFSIZ = 8193;
    private static final char[] newline = new char[]{'\n'};

    public static InputEntity getInputEntity(ErrorHandler paramErrorHandler, Locale paramLocale) {
        InputEntity inputEntity = new InputEntity();
        inputEntity.errHandler = paramErrorHandler;
        inputEntity.locale = paramLocale;
        return inputEntity;
    }

    public boolean isInternal() {
        return !(this.reader != null);
    }

    public boolean isDocument() {
        return !(this.next != null);
    }

    public boolean isParameterEntity() {
        return this.isPE;
    }

    public String getName() {
        return this.name;
    }

    public void init(InputSource paramInputSource, String paramString, InputEntity paramInputEntity, boolean paramBoolean) throws IOException, SAXException {
        this.input = paramInputSource;
        this.isPE = paramBoolean;
        this.reader = paramInputSource.getCharacterStream();
        if (this.reader == null) {
            InputStream inputStream = paramInputSource.getByteStream();
            if (inputStream == null) {
                this.reader = XmlReader.createReader((new URL(paramInputSource.getSystemId())).openStream());
            } else if (paramInputSource.getEncoding() != null) {
                this.reader = XmlReader.createReader(paramInputSource.getByteStream(), paramInputSource.getEncoding());
            } else {
                this.reader = XmlReader.createReader(paramInputSource.getByteStream());
            }
        }
        this.next = paramInputEntity;
        this.buf = new char[8193];
        this.name = paramString;
        checkRecursion(paramInputEntity);
    }

    public void init(char[] paramArrayOfchar, String paramString, InputEntity paramInputEntity, boolean paramBoolean) throws SAXException {
        this.next = paramInputEntity;
        this.buf = paramArrayOfchar;
        this.finish = paramArrayOfchar.length;
        this.name = paramString;
        this.isPE = paramBoolean;
        checkRecursion(paramInputEntity);
    }

    private void checkRecursion(InputEntity paramInputEntity) throws SAXException {
        if (paramInputEntity == null)
            return;
        for (paramInputEntity = paramInputEntity.next; paramInputEntity != null; paramInputEntity = paramInputEntity.next) {
            if (paramInputEntity.name != null && paramInputEntity.name.equals(this.name))
                fatal("P-069", new Object[]{this.name});
        }
    }

    public InputEntity pop() throws IOException {
        close();
        return this.next;
    }

    public boolean isEOF() throws IOException, SAXException {
        if (this.start >= this.finish) {
            fillbuf();
            return !(this.start < this.finish);
        }
        return false;
    }

    public String getEncoding() {
        if (this.reader == null)
            return null;
        if (this.reader instanceof XmlReader)
            return ((XmlReader) this.reader).getEncoding();
        if (this.reader instanceof InputStreamReader)
            return ((InputStreamReader) this.reader).getEncoding();
        return null;
    }

    public char getNameChar() throws IOException, SAXException {
        if (this.finish <= this.start)
            fillbuf();
        if (this.finish > this.start) {
            char c = this.buf[this.start++];
            if (XmlChars.isNameChar(c))
                return c;
            this.start--;
        }
        return Character.MIN_VALUE;
    }

    public char getc() throws IOException, SAXException {
        if (this.finish <= this.start)
            fillbuf();
        if (this.finish > this.start) {
            char c = this.buf[this.start++];
            if (this.returnedFirstHalf) {
                if (c >= '?' && c <= '?') {
                    this.returnedFirstHalf = false;
                    return c;
                }
                fatal("P-070", new Object[]{Integer.toHexString(c)});
            }
            if ((c < ' ' || c > '퟿') && c != '\t') {
                if (c >= '' && c <= '�')
                    return c;
            } else {
                return c;
            }
            if (c == '\r' && !isInternal()) {
                this.maybeInCRLF = true;
                c = getc();
                if (c != '\n')
                    ungetc();
                this.maybeInCRLF = false;
                this.lineNumber++;
                return '\n';
            }
            if (c == '\n' || c == '\r') {
                if (!isInternal() && !this.maybeInCRLF)
                    this.lineNumber++;
                return c;
            }
            if (c >= '?' && c < '?') {
                this.returnedFirstHalf = true;
                return c;
            }
            fatal("P-071", new Object[]{Integer.toHexString(c)});
        }
        throw new EndOfInputException();
    }

    public boolean peekc(char paramChar) throws IOException, SAXException {
        if (this.finish <= this.start)
            fillbuf();
        if (this.finish > this.start) {
            if (this.buf[this.start] == paramChar) {
                this.start++;
                return true;
            }
            return false;
        }
        return false;
    }

    public void ungetc() {
        if (this.start == 0)
            throw new InternalError("ungetc");
        this.start--;
        if (this.buf[this.start] == '\n' || this.buf[this.start] == '\r') {
            if (!isInternal())
                this.lineNumber--;
        } else if (this.returnedFirstHalf) {
            this.returnedFirstHalf = false;
        }
    }

    public boolean maybeWhitespace() throws IOException, SAXException {
        boolean bool1 = false;
        boolean bool2 = false;
        while (true) {
            if (this.finish <= this.start)
                fillbuf();
            if (this.finish <= this.start)
                return bool1;
            char c = this.buf[this.start++];
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                bool1 = true;
                if ((c == '\n' || c == '\r') && !isInternal()) {
                    if (c != '\n' || !bool2) {
                        this.lineNumber++;
                        bool2 = false;
                    }
                    if (c == '\r')
                        bool2 = true;
                }
                continue;
            }
            break;
        }
        this.start--;
        return bool1;
    }

    public boolean parsedContent(DocumentHandler paramDocumentHandler, ElementValidator paramElementValidator) throws IOException, SAXException {
        int i;
        int j;
        boolean bool;
        for (i = j = this.start, bool = false;; j++) {
            if (j >= this.finish) {
                if (j > i) {
                    paramElementValidator.text();
                    paramDocumentHandler.characters(this.buf, i, j - i);
                    bool = true;
                    this.start = j;
                }
                if (isEOF())
                    return bool;
                i = this.start;
                j = i - 1;
            } else {
                char c = this.buf[j];
                if ((c <= ']' || c > '퟿') && (c >= '&' || c < ' ') && (c <= '<' || c >= ']') && (c <= '&' || c >= '<') && c != '\t' && (c < '' || c > '�')) {
                    if (c == '<' || c == '&')
                        break;
                    if (c == '\n') {
                        if (!isInternal())
                            this.lineNumber++;
                    } else if (c == '\r') {
                        if (!isInternal()) {
                            paramDocumentHandler.characters(this.buf, i, j - i);
                            paramDocumentHandler.characters(newline, 0, 1);
                            bool = true;
                            this.lineNumber++;
                            if (this.finish > j + 1 && this.buf[j + 1] == '\n')
                                j++;
                            i = this.start = j + 1;
                        }
                    } else if (c == ']') {
                        switch (this.finish - j) {
                            case 2 :
                                if (this.buf[j + 1] != ']')
                                    break;
                            case 1 :
                                if (this.reader == null || this.isClosed)
                                    break;
                                if (j == i)
                                    throw new InternalError("fillbuf");
                                j--;
                                if (j > i) {
                                    paramElementValidator.text();
                                    paramDocumentHandler.characters(this.buf, i, j - i);
                                    bool = true;
                                    this.start = j;
                                }
                                fillbuf();
                                i = j = this.start;
                                break;
                            default :
                                if (this.buf[j + 1] == ']' && this.buf[j + 2] == '>')
                                    fatal("P-072", null);
                                break;
                        }
                    } else if (c >= '?' && c <= '?') {
                        if (j + 1 >= this.finish) {
                            if (j > i) {
                                paramElementValidator.text();
                                paramDocumentHandler.characters(this.buf, i, j - i);
                                bool = true;
                                this.start = j + 1;
                            }
                            if (isEOF())
                                fatal("P-081", new Object[]{Integer.toHexString(c)});
                            i = this.start;
                            j = i;
                        } else if (checkSurrogatePair(j)) {
                            j++;
                        } else {
                            j--;
                            break;
                        }
                    } else {
                        fatal("P-071", new Object[]{Integer.toHexString(c)});
                    }
                }
            }
        }
        if (j == i)
            return bool;
        paramElementValidator.text();
        paramDocumentHandler.characters(this.buf, i, j - i);
        this.start = j;
        return true;
    }

    public boolean unparsedContent(DocumentHandler paramDocumentHandler, ElementValidator paramElementValidator, boolean paramBoolean, String paramString) throws IOException, SAXException {
        // Byte code:
        //   0: aload_0
        //   1: ldc '![CDATA['
        //   3: aconst_null
        //   4: invokevirtual peek : (Ljava/lang/String;[C)Z
        //   7: ifne -> 12
        //   10: iconst_0
        //   11: ireturn
        //   12: aload_1
        //   13: instanceof com/sun/xml/parser/LexicalEventListener
        //   16: ifeq -> 28
        //   19: aload_1
        //   20: checkcast com/sun/xml/parser/LexicalEventListener
        //   23: invokeinterface startCDATA : ()V
        //   28: iconst_0
        //   29: istore #6
        //   31: iload_3
        //   32: istore #8
        //   34: aload_0
        //   35: getfield start : I
        //   38: istore #5
        //   40: goto -> 402
        //   43: aload_0
        //   44: getfield buf : [C
        //   47: iload #5
        //   49: caload
        //   50: istore #7
        //   52: iload #7
        //   54: invokestatic isChar : (I)Z
        //   57: ifne -> 121
        //   60: iconst_0
        //   61: istore #8
        //   63: iload #7
        //   65: ldc 55296
        //   67: if_icmplt -> 98
        //   70: iload #7
        //   72: ldc 57343
        //   74: if_icmpgt -> 98
        //   77: aload_0
        //   78: iload #5
        //   80: invokespecial checkSurrogatePair : (I)Z
        //   83: ifeq -> 92
        //   86: iinc #5, 1
        //   89: goto -> 399
        //   92: iinc #5, -1
        //   95: goto -> 411
        //   98: aload_0
        //   99: ldc 'P-071'
        //   101: iconst_1
        //   102: anewarray java/lang/Object
        //   105: dup
        //   106: iconst_0
        //   107: aload_0
        //   108: getfield buf : [C
        //   111: iload #5
        //   113: caload
        //   114: invokestatic toHexString : (I)Ljava/lang/String;
        //   117: aastore
        //   118: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   121: iload #7
        //   123: bipush #10
        //   125: if_icmpne -> 148
        //   128: aload_0
        //   129: invokevirtual isInternal : ()Z
        //   132: ifne -> 399
        //   135: aload_0
        //   136: dup
        //   137: getfield lineNumber : I
        //   140: iconst_1
        //   141: iadd
        //   142: putfield lineNumber : I
        //   145: goto -> 399
        //   148: iload #7
        //   150: bipush #13
        //   152: if_icmpne -> 321
        //   155: aload_0
        //   156: invokevirtual isInternal : ()Z
        //   159: ifne -> 399
        //   162: iload #8
        //   164: ifeq -> 236
        //   167: aload #4
        //   169: ifnull -> 201
        //   172: aload_0
        //   173: getfield errHandler : Lorg/xml/sax/ErrorHandler;
        //   176: new org/xml/sax/SAXParseException
        //   179: dup
        //   180: getstatic com/sun/xml/parser/Parser.messages : Lcom/sun/xml/parser/Parser$Catalog;
        //   183: aload_0
        //   184: getfield locale : Ljava/util/Locale;
        //   187: aload #4
        //   189: invokevirtual getMessage : (Ljava/util/Locale;Ljava/lang/String;)Ljava/lang/String;
        //   192: aload_0
        //   193: invokespecial <init> : (Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   196: invokeinterface error : (Lorg/xml/sax/SAXParseException;)V
        //   201: aload_1
        //   202: aload_0
        //   203: getfield buf : [C
        //   206: aload_0
        //   207: getfield start : I
        //   210: iload #5
        //   212: aload_0
        //   213: getfield start : I
        //   216: isub
        //   217: invokeinterface ignorableWhitespace : ([CII)V
        //   222: aload_1
        //   223: getstatic com/sun/xml/parser/InputEntity.newline : [C
        //   226: iconst_0
        //   227: iconst_1
        //   228: invokeinterface ignorableWhitespace : ([CII)V
        //   233: goto -> 272
        //   236: aload_2
        //   237: invokevirtual text : ()V
        //   240: aload_1
        //   241: aload_0
        //   242: getfield buf : [C
        //   245: aload_0
        //   246: getfield start : I
        //   249: iload #5
        //   251: aload_0
        //   252: getfield start : I
        //   255: isub
        //   256: invokeinterface characters : ([CII)V
        //   261: aload_1
        //   262: getstatic com/sun/xml/parser/InputEntity.newline : [C
        //   265: iconst_0
        //   266: iconst_1
        //   267: invokeinterface characters : ([CII)V
        //   272: aload_0
        //   273: dup
        //   274: getfield lineNumber : I
        //   277: iconst_1
        //   278: iadd
        //   279: putfield lineNumber : I
        //   282: aload_0
        //   283: getfield finish : I
        //   286: iload #5
        //   288: iconst_1
        //   289: iadd
        //   290: if_icmple -> 310
        //   293: aload_0
        //   294: getfield buf : [C
        //   297: iload #5
        //   299: iconst_1
        //   300: iadd
        //   301: caload
        //   302: bipush #10
        //   304: if_icmpne -> 310
        //   307: iinc #5, 1
        //   310: aload_0
        //   311: iload #5
        //   313: iconst_1
        //   314: iadd
        //   315: putfield start : I
        //   318: goto -> 399
        //   321: iload #7
        //   323: bipush #93
        //   325: if_icmpeq -> 348
        //   328: iload #7
        //   330: bipush #32
        //   332: if_icmpeq -> 399
        //   335: iload #7
        //   337: bipush #9
        //   339: if_icmpeq -> 399
        //   342: iconst_0
        //   343: istore #8
        //   345: goto -> 399
        //   348: iload #5
        //   350: iconst_2
        //   351: iadd
        //   352: aload_0
        //   353: getfield finish : I
        //   356: if_icmpge -> 411
        //   359: aload_0
        //   360: getfield buf : [C
        //   363: iload #5
        //   365: iconst_1
        //   366: iadd
        //   367: caload
        //   368: bipush #93
        //   370: if_icmpne -> 393
        //   373: aload_0
        //   374: getfield buf : [C
        //   377: iload #5
        //   379: iconst_2
        //   380: iadd
        //   381: caload
        //   382: bipush #62
        //   384: if_icmpne -> 393
        //   387: iconst_1
        //   388: istore #6
        //   390: goto -> 411
        //   393: iconst_0
        //   394: istore #8
        //   396: goto -> 399
        //   399: iinc #5, 1
        //   402: iload #5
        //   404: aload_0
        //   405: getfield finish : I
        //   408: if_icmplt -> 43
        //   411: iload #8
        //   413: ifeq -> 474
        //   416: aload #4
        //   418: ifnull -> 450
        //   421: aload_0
        //   422: getfield errHandler : Lorg/xml/sax/ErrorHandler;
        //   425: new org/xml/sax/SAXParseException
        //   428: dup
        //   429: getstatic com/sun/xml/parser/Parser.messages : Lcom/sun/xml/parser/Parser$Catalog;
        //   432: aload_0
        //   433: getfield locale : Ljava/util/Locale;
        //   436: aload #4
        //   438: invokevirtual getMessage : (Ljava/util/Locale;Ljava/lang/String;)Ljava/lang/String;
        //   441: aload_0
        //   442: invokespecial <init> : (Ljava/lang/String;Lorg/xml/sax/Locator;)V
        //   445: invokeinterface error : (Lorg/xml/sax/SAXParseException;)V
        //   450: aload_1
        //   451: aload_0
        //   452: getfield buf : [C
        //   455: aload_0
        //   456: getfield start : I
        //   459: iload #5
        //   461: aload_0
        //   462: getfield start : I
        //   465: isub
        //   466: invokeinterface ignorableWhitespace : ([CII)V
        //   471: goto -> 499
        //   474: aload_2
        //   475: invokevirtual text : ()V
        //   478: aload_1
        //   479: aload_0
        //   480: getfield buf : [C
        //   483: aload_0
        //   484: getfield start : I
        //   487: iload #5
        //   489: aload_0
        //   490: getfield start : I
        //   493: isub
        //   494: invokeinterface characters : ([CII)V
        //   499: iload #6
        //   501: ifeq -> 515
        //   504: aload_0
        //   505: iload #5
        //   507: iconst_3
        //   508: iadd
        //   509: putfield start : I
        //   512: goto -> 538
        //   515: aload_0
        //   516: iload #5
        //   518: putfield start : I
        //   521: aload_0
        //   522: invokevirtual isEOF : ()Z
        //   525: ifeq -> 28
        //   528: aload_0
        //   529: ldc 'P-073'
        //   531: aconst_null
        //   532: invokespecial fatal : (Ljava/lang/String;[Ljava/lang/Object;)V
        //   535: goto -> 28
        //   538: aload_1
        //   539: instanceof com/sun/xml/parser/LexicalEventListener
        //   542: ifeq -> 554
        //   545: aload_1
        //   546: checkcast com/sun/xml/parser/LexicalEventListener
        //   549: invokeinterface endCDATA : ()V
        //   554: iconst_1
        //   555: ireturn
        // Line number table:
        //   Java source line number -> byte code offset
        //   #567	-> 0
        //   #568	-> 10
        //   #569	-> 12
        //   #570	-> 19
        //   #576	-> 28
        //   #581	-> 31
        //   #583	-> 34
        //   #584	-> 43
        //   #589	-> 52
        //   #590	-> 60
        //   #591	-> 63
        //   #592	-> 77
        //   #593	-> 86
        //   #594	-> 89
        //   #596	-> 92
        //   #597	-> 95
        //   #600	-> 98
        //   #601	-> 101
        //   #600	-> 118
        //   #603	-> 121
        //   #604	-> 128
        //   #605	-> 135
        //   #606	-> 145
        //   #608	-> 148
        //   #610	-> 155
        //   #613	-> 162
        //   #614	-> 167
        //   #615	-> 172
        //   #616	-> 180
        //   #617	-> 187
        //   #616	-> 189
        //   #618	-> 192
        //   #615	-> 193
        //   #619	-> 201
        //   #620	-> 210
        //   #619	-> 217
        //   #621	-> 222
        //   #613	-> 233
        //   #623	-> 236
        //   #624	-> 240
        //   #625	-> 261
        //   #627	-> 272
        //   #628	-> 282
        //   #629	-> 293
        //   #630	-> 307
        //   #634	-> 310
        //   #635	-> 318
        //   #637	-> 321
        //   #638	-> 328
        //   #639	-> 342
        //   #640	-> 345
        //   #642	-> 348
        //   #643	-> 359
        //   #644	-> 387
        //   #645	-> 390
        //   #647	-> 393
        //   #648	-> 396
        //   #583	-> 399
        //   #654	-> 411
        //   #655	-> 416
        //   #656	-> 421
        //   #657	-> 429
        //   #658	-> 436
        //   #657	-> 438
        //   #659	-> 441
        //   #656	-> 442
        //   #660	-> 450
        //   #654	-> 471
        //   #662	-> 474
        //   #663	-> 478
        //   #665	-> 499
        //   #666	-> 504
        //   #667	-> 512
        //   #669	-> 515
        //   #670	-> 521
        //   #671	-> 528
        //   #575	-> 535
        //   #673	-> 538
        //   #674	-> 545
        //   #675	-> 554
    }

    private boolean checkSurrogatePair(int paramInt) throws SAXException {
        if (paramInt + 1 >= this.finish)
            return false;
        char c1 = this.buf[paramInt++];
        char c2 = this.buf[paramInt];
        if (c1 >= '?' && c1 < '?' && c2 >= '?' && c2 <= '?')
            return true;
        fatal("P-074", new Object[]{Integer.toHexString(c1 & Character.MAX_VALUE), Integer.toHexString(c2 & Character.MAX_VALUE)});
        return false;
    }

    public boolean ignorableWhitespace(DocumentHandler paramDocumentHandler) throws IOException, SAXException {
        boolean bool = false;
        int i = this.start;
        while (true) {
            if (this.finish <= this.start) {
                if (bool)
                    paramDocumentHandler.ignorableWhitespace(this.buf, i, this.start - i);
                fillbuf();
                i = this.start;
            }
            if (this.finish <= this.start)
                return bool;
            char c = this.buf[this.start++];
            switch (c) {
                case '\n' :
                    if (!isInternal())
                        this.lineNumber++;
                case '\t' :
                case ' ' :
                    bool = true;
                    continue;
                case '\r' :
                    bool = true;
                    if (!isInternal())
                        this.lineNumber++;
                    paramDocumentHandler.ignorableWhitespace(this.buf, i, this.start - 1 - i);
                    paramDocumentHandler.ignorableWhitespace(newline, 0, 1);
                    if (this.start < this.finish && this.buf[this.start] == '\n')
                        this.start++;
                    i = this.start;
                    continue;
            }
            break;
        }
        ungetc();
        if (bool)
            paramDocumentHandler.ignorableWhitespace(this.buf, i, this.start - i);
        return bool;
    }

    public boolean peek(String paramString, char[] paramArrayOfchar) throws IOException, SAXException {
        int i;
        byte b;
        if (paramArrayOfchar != null) {
            i = paramArrayOfchar.length;
        } else {
            i = paramString.length();
        }
        if (this.finish <= this.start || this.finish - this.start < i)
            fillbuf();
        if (this.finish <= this.start)
            return false;
        if (paramArrayOfchar != null) {
            for (b = 0; b < i && this.start + b < this.finish; b++) {
                if (this.buf[this.start + b] != paramArrayOfchar[b])
                    return false;
            }
        } else {
            for (b = 0; b < i && this.start + b < this.finish; b++) {
                if (this.buf[this.start + b] != paramString.charAt(b))
                    return false;
            }
        }
        if (b < i) {
            if (this.reader == null || this.isClosed)
                return false;
            if (i > this.buf.length)
                fatal("P-077", new Object[]{new Integer(this.buf.length)});
            fillbuf();
            return peek(paramString, paramArrayOfchar);
        }
        this.start += i;
        return true;
    }

    public void startRemembering() {
        if (this.startRemember != 0)
            throw new InternalError();
        this.startRemember = this.start;
    }

    public String rememberText() {
        String str;
        if (this.rememberedText != null) {
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
            str = this.rememberedText.toString();
        } else {
            str = new String(this.buf, this.startRemember, this.start - this.startRemember);
        }
        this.startRemember = 0;
        this.rememberedText = null;
        return str;
    }

    private Locator getLocator() {
        InputEntity inputEntity = this;
        while (inputEntity != null && inputEntity.input == null)
            inputEntity = inputEntity.next;
        return (inputEntity == null) ? this : inputEntity;
    }

    public String getPublicId() {
        Locator locator = getLocator();
        if (locator == this)
            return this.input.getPublicId();
        return locator.getPublicId();
    }

    public String getSystemId() {
        Locator locator = getLocator();
        if (locator == this)
            return this.input.getSystemId();
        return locator.getSystemId();
    }

    public int getLineNumber() {
        Locator locator = getLocator();
        if (locator == this)
            return this.lineNumber;
        return locator.getLineNumber();
    }

    public int getColumnNumber() {
        return -1;
    }

    private void fillbuf() throws IOException, SAXException {
        if (this.reader == null || this.isClosed)
            return;
        if (this.startRemember != 0) {
            if (this.rememberedText == null)
                this.rememberedText = new StringBuffer(this.buf.length);
            this.rememberedText.append(this.buf, this.startRemember, this.start - this.startRemember);
        }
        boolean bool = (this.finish <= 0 || this.start <= 0) ? false : true;
        if (bool)
            this.start--;
        int i = this.finish - this.start;
        System.arraycopy(this.buf, this.start, this.buf, 0, i);
        this.start = 0;
        this.finish = i;
        try {
            i = this.buf.length - i;
            i = this.reader.read(this.buf, this.finish, i);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            fatal("P-075", new Object[]{unsupportedEncodingException.getMessage()});
        } catch (CharConversionException charConversionException) {
            fatal("P-076", new Object[]{charConversionException.getMessage()});
        }
        if (i >= 0) {
            this.finish += i;
        } else {
            close();
        }
        if (bool)
            this.start++;
        if (this.startRemember != 0)
            this.startRemember = 1;
    }

    public void close() {
        try {
            if (this.reader != null && !this.isClosed)
                this.reader.close();
            this.isClosed = true;
        } catch (IOException iOException) {
        }
    }

    private void fatal(String paramString, Object[] paramArrayOfObject) throws SAXException {
        SAXParseException sAXParseException = new SAXParseException(Parser.messages.getMessage(this.locale, paramString, paramArrayOfObject), this);
        close();
        this.errHandler.fatalError(sAXParseException);
        throw sAXParseException;
    }
}
