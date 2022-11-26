package gnu.jel.generated;

import gnu.jel.CompilationException;
import gnu.jel.ExpressionImage;
import gnu.jel.Optimizer;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

public class EC implements ECConstants {
    public ECTokenManager token_source;
    ASCII_CharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1 = new int[24];
    private final int[] jj_la1_0 = new int[]{0, 16384, 4096, 8192, 32768, 2048, 196608, 196608, 3932160, 3932160, 29360128, 29360128, 48, 48, 1792, 1792, 224, -1912602624, -1912602624, -1912602400,
        -2013265920};
    private final int[] jj_la1_1 = new int[]{64, 270, 1024, 270, 4096, 270, 256, 6};
    private final JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public EC(ECTokenManager paramECTokenManager) {
        this.token_source = paramECTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public EC(InputStream paramInputStream) {
        this.jj_input_stream = new ASCII_CharStream(paramInputStream, 1, 1);
        this.token_source = new ECTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public EC(Reader paramReader) {
        this.jj_input_stream = new ASCII_CharStream(paramReader, 1, 1);
        this.token_source = new ECTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public void ReInit(ECTokenManager paramECTokenManager) {
        this.token_source = paramECTokenManager;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public void ReInit(InputStream paramInputStream) {
        this.jj_input_stream.ReInit(paramInputStream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public void ReInit(Reader paramReader) {
        this.jj_input_stream.ReInit(paramReader, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (byte b1 = 0; b1 < 24; b1++)
            this.jj_la1[b1] = -1;
        for (byte b2 = 0; b2 < this.jj_2_rtns.length; b2++)
            this.jj_2_rtns[b2] = new JJCalls();
    }

    public final void band(Optimizer paramOptimizer) throws ParseException, CompilationException {
        equality(paramOptimizer);
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[5] = this.jj_gen;
                    break;
                case 11 :
                    break;
            }
            Token token = jj_consume_token(11);
            paramOptimizer.binaryOP_param();
            equality(paramOptimizer);
            try {
                paramOptimizer.binaryOP(5, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void bor(Optimizer paramOptimizer) throws ParseException, CompilationException {
        bxor(paramOptimizer);
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[3] = this.jj_gen;
                    break;
                case 13 :
                    break;
            }
            Token token = jj_consume_token(13);
            paramOptimizer.binaryOP_param();
            bxor(paramOptimizer);
            try {
                paramOptimizer.binaryOP(6, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void bxor(Optimizer paramOptimizer) throws ParseException, CompilationException {
        band(paramOptimizer);
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[4] = this.jj_gen;
                    break;
                case 15 :
                    break;
            }
            Token token = jj_consume_token(15);
            paramOptimizer.binaryOP_param();
            band(paramOptimizer);
            try {
                paramOptimizer.binaryOP(7, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void conditional(Optimizer paramOptimizer) throws ParseException, CompilationException {
        lor(paramOptimizer);
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 38 :
                jj_consume_token(38);
                try {
                    paramOptimizer.conditional_true();
                } catch (IllegalStateException illegalStateException) {
                    throw new CompilationException(this.token, illegalStateException.getMessage());
                }
                conditional(paramOptimizer);
                jj_consume_token(39);
                paramOptimizer.conditional_false();
                conditional(paramOptimizer);
                try {
                    paramOptimizer.conditional_end();
                } catch (IllegalStateException illegalStateException) {
                    throw new CompilationException(this.token, illegalStateException.getMessage());
                }
                return;
        }
        this.jj_la1[0] = this.jj_gen;
    }

    public final void disable_tracing() {
    }

    public final void element(Optimizer paramOptimizer) throws ParseException, CompilationException {
        Token token;
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 27 :
            case 31 :
            case 33 :
            case 34 :
                literal(paramOptimizer);
                return;
            case 26 :
                jj_consume_token(26);
                paramOptimizer.load(true);
                return;
            case 25 :
                jj_consume_token(25);
                paramOptimizer.load(false);
                return;
            case 35 :
                function(paramOptimizer);
                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                    case 42 :
                        token = jj_consume_token(42);
                        try {
                            paramOptimizer.load_array_param();
                        } catch (IllegalStateException illegalStateException) {
                            throw new CompilationException(token, illegalStateException.getMessage());
                        }
                        conditional(paramOptimizer);
                        token = jj_consume_token(43);
                        try {
                            paramOptimizer.load_array();
                        } catch (IllegalStateException illegalStateException) {
                            throw new CompilationException(token, illegalStateException.getMessage());
                        }
                        return;
                }
                this.jj_la1[18] = this.jj_gen;
                return;
            case 40 :
                jj_consume_token(40);
                conditional(paramOptimizer);
                jj_consume_token(41);
                return;
        }
        this.jj_la1[19] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }

    public final void enable_tracing() {
    }

    public final void equality(Optimizer paramOptimizer) throws ParseException, CompilationException {
        relation(paramOptimizer);
        while (true) {
            Token token;
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[6] = this.jj_gen;
                    break;
                case 16 :
                case 17 :
                    break;
            }
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 16 :
                    token = jj_consume_token(16);
                    break;
                case 17 :
                    token = jj_consume_token(17);
                    break;
                default :
                    this.jj_la1[7] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            paramOptimizer.binaryOP_param();
            relation(paramOptimizer);
            try {
                byte b = 8;
                if (token.kind == 17)
                    b = 9;
                paramOptimizer.binaryOP(b, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void expression(Optimizer paramOptimizer, Class paramClass) throws ParseException, CompilationException {
        conditional(paramOptimizer);
        Token token = jj_consume_token(0);
        try {
            if (paramClass != null)
                paramOptimizer.convert(paramClass, true);
            paramOptimizer.finish();
        } catch (IllegalStateException illegalStateException) {
            throw new CompilationException(token, illegalStateException.getMessage());
        }
    }

    public final void function(Optimizer paramOptimizer) throws ParseException, CompilationException {
        Token token = jj_consume_token(35);
        paramOptimizer.function_start();
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 40 :
                jj_consume_token(40);
                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                    case 5 :
                    case 6 :
                    case 7 :
                    case 25 :
                    case 26 :
                    case 27 :
                    case 31 :
                    case 33 :
                    case 34 :
                    case 35 :
                    case 40 :
                        conditional(paramOptimizer);
                        paramOptimizer.function_param();
                        while (true) {
                            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                                default :
                                    this.jj_la1[20] = this.jj_gen;
                                    break;
                                case 44 :
                                    break;
                            }
                            jj_consume_token(44);
                            conditional(paramOptimizer);
                            paramOptimizer.function_param();
                        }
                        break;
                    default :
                        this.jj_la1[21] = this.jj_gen;
                        break;
                }
                jj_consume_token(41);
                break;
            default :
                this.jj_la1[22] = this.jj_gen;
                break;
        }
        try {
            paramOptimizer.function_call(token.image);
        } catch (IllegalStateException illegalStateException) {
            throw new CompilationException(token, illegalStateException.getMessage());
        }
    }

    public final ParseException generateParseException() {
        this.jj_expentries.removeAllElements();
        boolean[] arrayOfBoolean = new boolean[45];
        for (byte b1 = 0; b1 < 45; b1++)
            arrayOfBoolean[b1] = false;
        if (this.jj_kind >= 0) {
            arrayOfBoolean[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (byte b2 = 0; b2 < 24; b2++) {
            if (this.jj_la1[b2] == this.jj_gen)
                for (byte b = 0; b < 32; b++) {
                    if ((this.jj_la1_0[b2] & 1 << b) != 0)
                        arrayOfBoolean[b] = true;
                    if ((this.jj_la1_1[b2] & 1 << b) != 0)
                        arrayOfBoolean[32 + b] = true;
                }
        }
        for (byte b3 = 0; b3 < 45; b3++) {
            if (arrayOfBoolean[b3]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = b3;
                this.jj_expentries.addElement(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] arrayOfInt = new int[this.jj_expentries.size()][];
        for (byte b4 = 0; b4 < this.jj_expentries.size(); b4++)
            arrayOfInt[b4] = this.jj_expentries.elementAt(b4);
        return new ParseException(this.token, arrayOfInt, ECConstants.tokenImage);
    }

    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }
        this.jj_ntk = -1;
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int paramInt) {
        Token token = this.lookingAhead ? this.jj_scanpos : this.token;
        for (byte b = 0; b < paramInt; b++) {
            if (token.next != null) {
                token = token.next;
            } else {
                token = token.next = this.token_source.getNextToken();
            }
        }
        return token;
    }

    private final boolean jj_2_1(int paramInt) {
        this.jj_la = paramInt;
        this.jj_lastpos = this.jj_scanpos = this.token;
        int i = jj_3_1() ^ true;
        jj_save(0, paramInt);
        return i;
    }

    private final boolean jj_3_1() {
        return jj_scan_token(40)
            ? true
            : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
                ? false
                : (jj_scan_token(35)
                    ? true
                    : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(41) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : false)))));
    }

    private void jj_add_error_token(int paramInt1, int paramInt2) {
        if (paramInt2 >= 100)
            return;
        if (paramInt2 == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = paramInt1;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (byte b = 0; b < this.jj_endpos; b++)
                this.jj_expentry[b] = this.jj_lasttokens[b];
            boolean bool = false;
            Enumeration enumeration = this.jj_expentries.elements();
            while (enumeration.hasMoreElements()) {
                int[] arrayOfInt = enumeration.nextElement();
                if (arrayOfInt.length == this.jj_expentry.length) {
                    bool = true;
                    for (byte b1 = 0; b1 < this.jj_expentry.length; b1++) {
                        if (arrayOfInt[b1] != this.jj_expentry[b1]) {
                            bool = false;
                            break;
                        }
                    }
                    if (bool)
                        break;
                }
            }
            if (!bool)
                this.jj_expentries.addElement(this.jj_expentry);
            if (paramInt2 != 0)
                this.jj_lasttokens[(this.jj_endpos = paramInt2) - 1] = paramInt1;
        }
    }

    private final Token jj_consume_token(int paramInt) throws ParseException {
        Token token;
        if ((token = this.token).next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }
        this.jj_ntk = -1;
        if (this.token.kind == paramInt) {
            this.jj_gen++;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (byte b = 0; b < this.jj_2_rtns.length; b++) {
                    for (JJCalls jJCalls = this.jj_2_rtns[b]; jJCalls != null; jJCalls = jJCalls.next) {
                        if (jJCalls.gen < this.jj_gen)
                            jJCalls.first = null;
                    }
                }
            }
            return this.token;
        }
        this.token = token;
        this.jj_kind = paramInt;
        throw generateParseException();
    }

    private final int jj_ntk() {
        return ((this.jj_nt = this.token.next) == null) ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
    }

    private final void jj_rescan_token() {
        this.jj_rescan = true;
        byte b = 0;
        while (b < 1) {
            JJCalls jJCalls = this.jj_2_rtns[b];
            while (true) {
                if (jJCalls.gen > this.jj_gen) {
                    this.jj_la = jJCalls.arg;
                    this.jj_lastpos = this.jj_scanpos = jJCalls.first;
                    switch (b) {
                        case 0 :
                            jj_3_1();
                            break;
                    }
                }
                jJCalls = jJCalls.next;
                if (jJCalls == null)
                    b++;
            }
        }
        this.jj_rescan = false;
    }

    private final void jj_save(int paramInt1, int paramInt2) {
        JJCalls jJCalls;
        for (jJCalls = this.jj_2_rtns[paramInt1]; jJCalls.gen > this.jj_gen; jJCalls = jJCalls.next) {
            if (jJCalls.next == null) {
                jJCalls = jJCalls.next = new JJCalls();
                break;
            }
        }
        jJCalls.gen = this.jj_gen + paramInt2 - this.jj_la;
        jJCalls.first = this.token;
        jJCalls.arg = paramInt2;
    }

    private final boolean jj_scan_token(int paramInt) {
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;
            if (this.jj_scanpos.next == null) {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            byte b = 0;
            for (Token token = this.token; token != null && token != this.jj_scanpos; token = token.next)
                b++;
            if (token != null)
                jj_add_error_token(paramInt, b);
        }
        return !(this.jj_scanpos.kind == paramInt);
    }

    public final void land(Optimizer paramOptimizer) throws ParseException, CompilationException {
        bor(paramOptimizer);
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[2] = this.jj_gen;
                    break;
                case 12 :
                    break;
            }
            Token token = jj_consume_token(12);
            paramOptimizer.binaryOP_param();
            bor(paramOptimizer);
            try {
                paramOptimizer.binaryOP(0, true);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void literal(Optimizer paramOptimizer) throws ParseException, CompilationException {
        String str2;
        char c;
        String str1;
        long l;
        char c1;
        StringBuffer stringBuffer;
        String str3;
        byte b;
        boolean bool;
        Double double_;
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 27 :
                jj_consume_token(27);
                str2 = this.token.image.toUpperCase();
                l = 0L;
                bool = str2.endsWith("L");
                if (bool)
                    str2 = str2.substring(0, str2.length() - 1);
                try {
                    if (str2.startsWith("0x") || str2.startsWith("0X")) {
                        str2 = str2.substring(2);
                        l = Long.parseLong(str2, 16);
                    } else if (this.token.image.startsWith("0")) {
                        l = Long.parseLong(str2, 8);
                    } else {
                        l = Long.parseLong(str2, 10);
                    }
                } catch (NumberFormatException numberFormatException) {
                    throw new CompilationException(this.token, "Number \"" + this.token.image + "\" is too large, it does not fit even " + "into 64 bit long.");
                }
                if (!bool) {
                    if (l <= 127L) {
                        paramOptimizer.load((byte) (int) l);
                    } else if (l <= 32767L) {
                        paramOptimizer.load((short) (int) l);
                    } else if (l <= 2147483647L) {
                        paramOptimizer.load((int) l);
                    } else {
                        throw new CompilationException(this.token, "Integer number \"" + this.token.image + "\" is too large for type 'int'. Be sure" + " to add 'L' suffix to use 'long' type.");
                    }
                } else {
                    paramOptimizer.load(l);
                }
                return;
            case 31 :
                jj_consume_token(31);
                c = Character.toUpperCase(this.token.image.charAt(this.token.image.length() - 1));
                c1 = (c != 'F') ? Character.MIN_VALUE : '\001';
                str3 = this.token.image;
                if (c == 'D' || c == 'F')
                    str3 = str3.substring(0, str3.length() - 1);
                double_ = null;
                try {
                    double_ = new Double(str3);
                } catch (NumberFormatException numberFormatException) {
                }
                if (c1) {
                    paramOptimizer.load(double_.floatValue());
                } else {
                    paramOptimizer.load(double_.doubleValue());
                }
                return;
            case 33 :
                jj_consume_token(33);
                str1 = this.token.image.substring(1, this.token.image.length() - 1);
                if (str1.length() == 1) {
                    paramOptimizer.load(str1.charAt(0));
                } else {
                    c1 = str1.charAt(1);
                    try {
                        switch (c1) {
                            case 'n' :
                                c1 = '\n';
                                break;
                            case 't' :
                                c1 = '\t';
                                break;
                            case 'b' :
                                c1 = '\b';
                                break;
                            case 'r' :
                                c1 = '\r';
                                break;
                            case 'f' :
                                c1 = '\f';
                                break;
                            case '\\' :
                                c1 = '\\';
                                break;
                            case '\'' :
                                c1 = '\'';
                                break;
                            case '"' :
                                c1 = '"';
                                break;
                            default :
                                c1 = (char) Integer.parseInt(str1.substring(1), 8);
                                break;
                        }
                    } catch (NumberFormatException numberFormatException) {
                    }
                    paramOptimizer.load(c1);
                }
                return;
            case 34 :
                jj_consume_token(34);
                str1 = this.token.image.substring(1, this.token.image.length() - 1);
                stringBuffer = new StringBuffer(str1.length());
                for (b = 0; b < str1.length(); b++) {
                    char c2 = str1.charAt(b);
                    if (c2 == '\\') {
                        int i;
                        c2 = str1.charAt(++b);
                        switch (c2) {
                            case 'n' :
                                c2 = '\n';
                                break;
                            case 't' :
                                c2 = '\t';
                                break;
                            case 'b' :
                                c2 = '\b';
                                break;
                            case 'r' :
                                c2 = '\r';
                                break;
                            case 'f' :
                                c2 = '\f';
                                break;
                            case '\\' :
                                c2 = '\\';
                                break;
                            case '\'' :
                                c2 = '\'';
                                break;
                            case '"' :
                                c2 = '"';
                                break;
                            default :
                                i = 0;
                                while (b < str1.length() && (c2 = str1.charAt(b)) >= '0' && c2 <= '7') {
                                    i <<= 3 + c2 - 48;
                                    b++;
                                }
                                b--;
                                c2 = (char) i;
                                break;
                        }
                    }
                    stringBuffer.append(c2);
                }
                paramOptimizer.load(stringBuffer.toString());
                return;
        }
        this.jj_la1[23] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }

    public final void lor(Optimizer paramOptimizer) throws ParseException, CompilationException {
        land(paramOptimizer);
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[1] = this.jj_gen;
                    break;
                case 14 :
                    break;
            }
            Token token = jj_consume_token(14);
            paramOptimizer.binaryOP_param();
            land(paramOptimizer);
            try {
                paramOptimizer.binaryOP(1, true);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void relation(Optimizer paramOptimizer) throws ParseException, CompilationException {
        shift(paramOptimizer);
        while (true) {
            Token token;
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[8] = this.jj_gen;
                    break;
                case 18 :
                case 19 :
                case 20 :
                case 21 :
                    break;
            }
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 18 :
                    token = jj_consume_token(18);
                    break;
                case 19 :
                    token = jj_consume_token(19);
                    break;
                case 20 :
                    token = jj_consume_token(20);
                    break;
                case 21 :
                    token = jj_consume_token(21);
                    break;
                default :
                    this.jj_la1[9] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            paramOptimizer.binaryOP_param();
            shift(paramOptimizer);
            try {
                char c = 'ϧ';
                switch (token.kind) {
                    case 18 :
                        c = '\n';
                        break;
                    case 19 :
                        c = '\013';
                        break;
                    case 20 :
                        c = '\f';
                        break;
                    case 21 :
                        c = '\r';
                        break;
                }
                paramOptimizer.binaryOP(c, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void shift(Optimizer paramOptimizer) throws ParseException, CompilationException {
        sum(paramOptimizer);
        while (true) {
            Token token;
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[10] = this.jj_gen;
                    break;
                case 22 :
                case 23 :
                case 24 :
                    break;
            }
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 22 :
                    token = jj_consume_token(22);
                    break;
                case 23 :
                    token = jj_consume_token(23);
                    break;
                case 24 :
                    token = jj_consume_token(24);
                    break;
                default :
                    this.jj_la1[11] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            paramOptimizer.binaryOP_param();
            sum(paramOptimizer);
            try {
                char c = 'ϧ';
                switch (token.kind) {
                    case 22 :
                        c = '\016';
                        break;
                    case 23 :
                        c = '\017';
                        break;
                    case 24 :
                        c = '\020';
                        break;
                }
                paramOptimizer.binaryOP(c, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void sum(Optimizer paramOptimizer) throws ParseException, CompilationException {
        term(paramOptimizer);
        while (true) {
            Token token;
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[12] = this.jj_gen;
                    break;
                case 4 :
                case 5 :
                    break;
            }
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 4 :
                    token = jj_consume_token(4);
                    break;
                case 5 :
                    token = jj_consume_token(5);
                    break;
                default :
                    this.jj_la1[13] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            paramOptimizer.binaryOP_param();
            term(paramOptimizer);
            try {
                boolean bool = true;
                if (token.kind == 4)
                    bool = false;
                paramOptimizer.binaryOP(bool, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void term(Optimizer paramOptimizer) throws ParseException, CompilationException {
        unary(paramOptimizer);
        while (true) {
            Token token;
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                default :
                    this.jj_la1[14] = this.jj_gen;
                    break;
                case 8 :
                case 9 :
                case 10 :
                    break;
            }
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 8 :
                    token = jj_consume_token(8);
                    break;
                case 9 :
                    token = jj_consume_token(9);
                    break;
                case 10 :
                    token = jj_consume_token(10);
                    break;
                default :
                    this.jj_la1[15] = this.jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
            paramOptimizer.binaryOP_param();
            unary(paramOptimizer);
            try {
                byte b = 2;
                if (token.kind == 9)
                    b = 3;
                if (token.kind == 10)
                    b = 4;
                paramOptimizer.binaryOP(b, false);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token, illegalStateException.getMessage());
            }
        }
    }

    public final void unary(Optimizer paramOptimizer) throws ParseException, CompilationException {
        Token token;
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 5 :
                token = jj_consume_token(5);
                element(paramOptimizer);
                try {
                    paramOptimizer.unary(0);
                } catch (IllegalStateException illegalStateException) {
                    throw new CompilationException(token, illegalStateException.getMessage());
                }
                return;
            case 6 :
                token = jj_consume_token(6);
                element(paramOptimizer);
                try {
                    paramOptimizer.unary(1);
                } catch (IllegalStateException illegalStateException) {
                    throw new CompilationException(token, illegalStateException.getMessage());
                }
                return;
            case 7 :
                token = jj_consume_token(7);
                paramOptimizer.logical_not_start();
                element(paramOptimizer);
                try {
                    paramOptimizer.logical_not();
                } catch (IllegalStateException illegalStateException) {
                    throw new CompilationException(token, illegalStateException.getMessage());
                }
                return;
        }
        this.jj_la1[16] = this.jj_gen;
        if (jj_2_1(3)) {
            jj_consume_token(40);
            Token token1 = jj_consume_token(35);
            jj_consume_token(41);
            element(paramOptimizer);
            Class clazz = null;
            for (byte b = 0; b < ExpressionImage.primitiveTypeNames.length && clazz == null; b++) {
                if (token1.image.equals(ExpressionImage.primitiveTypeNames[b]))
                    clazz = ExpressionImage.primitiveTypes[b];
            }
            if (clazz == null)
                throw new CompilationException(token1, "The string \"" + token1.image + "\" is not a valid primitive type.");
            try {
                paramOptimizer.convert(clazz);
            } catch (IllegalStateException illegalStateException) {
                throw new CompilationException(token1, illegalStateException.getMessage());
            }
        } else {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case 25 :
                case 26 :
                case 27 :
                case 31 :
                case 33 :
                case 34 :
                case 35 :
                case 40 :
                    element(paramOptimizer);
                    return;
            }
            this.jj_la1[17] = this.jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
        }
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
