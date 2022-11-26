package gnu.jel.generated;

import java.io.IOException;

public class ECTokenManager implements ECConstants {
    static final long[] jjbitVec0 = new long[]{0, 0, -1L, -1L};
    static final int[] jjnextStates = new int[]{30, 31, 36, 37, 40, 41, 8, 19, 20, 22, 10, 12, 45, 47, 2, 4, 5, 8, 19, 20, 24, 22, 32, 33, 8, 40, 41, 8, 6, 7, 13, 14, 16, 21, 23, 25, 34, 35, 38, 39,
        42, 43};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, "+", "-", "~", "!", "*", "/", "%", "&", "&&", "|", "||", "^", "==", "!=", "<", ">=", ">", "<=", "<<", ">>",
        ">>>", "false", "true", null, null, null, null, null, null, null, null, null, null, null, "?", ":", "(", ")", "[", "]", ","};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    static final long[] jjtoToken = new long[]{34972039643121L};
    static final long[] jjtoSkip = new long[]{14L};
    private ASCII_CharStream input_stream;
    private final int[] jjrounds = new int[48];
    private final int[] jjstateSet = new int[96];
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public ECTokenManager(ASCII_CharStream paramASCII_CharStream) {
        this.input_stream = paramASCII_CharStream;
    }

    public ECTokenManager(ASCII_CharStream paramASCII_CharStream, int paramInt) {
        this(paramASCII_CharStream);
        SwitchTo(paramInt);
    }

    public void ReInit(ASCII_CharStream paramASCII_CharStream) {
        this.jjmatchedPos = this.jjnewStateCnt = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = paramASCII_CharStream;
        ReInitRounds();
    }

    public void ReInit(ASCII_CharStream paramASCII_CharStream, int paramInt) {
        ReInit(paramASCII_CharStream);
        SwitchTo(paramInt);
    }

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        byte b = 48;
        while (b-- > 0)
            this.jjrounds[b] = Integer.MIN_VALUE;
    }

    public void SwitchTo(int paramInt) {
        if (paramInt >= 1 || paramInt < 0)
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + paramInt + ". State unchanged.", 2);
        this.curLexState = paramInt;
    }

    public final Token getNextToken() {
        Object object = null;
        int i = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            } catch (IOException iOException) {
                this.jjmatchedKind = 0;
                return jjFillToken();
            }
            try {
                this.input_stream.backup(0);
                while (this.curChar <= ' ' && (0x100002200L & 1L << this.curChar) != 0L)
                    this.curChar = this.input_stream.BeginToken();
            } catch (IOException iOException) {
                continue;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            i = jjMoveStringLiteralDfa0_0();
            if (this.jjmatchedKind != Integer.MAX_VALUE) {
                if (this.jjmatchedPos + 1 < i)
                    this.input_stream.backup(i - this.jjmatchedPos - 1);
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L)
                    return jjFillToken();
                continue;
            }
            int j = this.input_stream.getEndLine();
            int k = this.input_stream.getEndColumn();
            String str = null;
            boolean bool = false;
            this.input_stream.backup(1);
            str = (i <= 1) ? "" : this.input_stream.GetImage();
            throw new TokenMgrError(bool, this.curLexState, j, k, str, this.curChar, 0);
        }
    }

    private final void jjAddStates(int paramInt1, int paramInt2) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[paramInt1];
        } while (paramInt1++ != paramInt2);
    }

    private final void jjCheckNAdd(int paramInt) {
        if (this.jjrounds[paramInt] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = paramInt;
            this.jjrounds[paramInt] = this.jjround;
        }
    }

    private final void jjCheckNAddStates(int paramInt) {
        jjCheckNAdd(jjnextStates[paramInt]);
        jjCheckNAdd(jjnextStates[paramInt + 1]);
    }

    private final void jjCheckNAddStates(int paramInt1, int paramInt2) {
        do {
            jjCheckNAdd(jjnextStates[paramInt1]);
        } while (paramInt1++ != paramInt2);
    }

    private final void jjCheckNAddTwoStates(int paramInt1, int paramInt2) {
        jjCheckNAdd(paramInt1);
        jjCheckNAdd(paramInt2);
    }

    private final Token jjFillToken() {
        Token token = Token.newToken(this.jjmatchedKind);
        token.kind = this.jjmatchedKind;
        String str = jjstrLiteralImages[this.jjmatchedKind];
        token.image = (str == null) ? this.input_stream.GetImage() : str;
        token.beginLine = this.input_stream.getBeginLine();
        token.beginColumn = this.input_stream.getBeginColumn();
        token.endLine = this.input_stream.getEndLine();
        token.endColumn = this.input_stream.getEndColumn();
        return token;
    }

    private final int jjMoveNfa_0(int paramInt1, int paramInt2) {
        int i = 0;
        this.jjnewStateCnt = 48;
        int j = 1;
        this.jjstateSet[0] = paramInt1;
        int k = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE)
                ReInitRounds();
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--j]) {
                        case 0 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                jjCheckNAddStates(0, 6);
                            } else if (this.curChar == '$') {
                                if (k > 35)
                                    k = 35;
                                jjCheckNAdd(28);
                            } else if (this.curChar == '"') {
                                jjCheckNAddStates(7, 9);
                            } else if (this.curChar == '\'') {
                                jjAddStates(10, 11);
                            } else if (this.curChar == '.') {
                                jjCheckNAdd(4);
                            }
                            if ((0x3FE000000000000L & l) != 0L) {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddTwoStates(1, 2);
                                break;
                            }
                            if (this.curChar == '0') {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddStates(12, 14);
                            }
                            break;
                        case 1 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddTwoStates(1, 2);
                            }
                            break;
                        case 3 :
                            if (this.curChar == '.')
                                jjCheckNAdd(4);
                            break;
                        case 4 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddStates(15, 17);
                            }
                            break;
                        case 6 :
                            if ((0x280000000000L & l) != 0L)
                                jjCheckNAdd(7);
                            break;
                        case 7 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddTwoStates(7, 8);
                            }
                            break;
                        case 9 :
                            if (this.curChar == '\'')
                                jjAddStates(10, 11);
                            break;
                        case 10 :
                            if ((0xFFFFFF7FFFFFDBFFL & l) != 0L)
                                jjCheckNAdd(11);
                            break;
                        case 11 :
                            if (this.curChar == '\'' && k > 33)
                                k = 33;
                            break;
                        case 13 :
                            if ((0x8400000000L & l) != 0L)
                                jjCheckNAdd(11);
                            break;
                        case 14 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAddTwoStates(15, 11);
                            break;
                        case 15 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAdd(11);
                            break;
                        case 16 :
                            if ((0xF000000000000L & l) != 0L)
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        case 17 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAdd(15);
                            break;
                        case 18 :
                            if (this.curChar == '"')
                                jjCheckNAddStates(7, 9);
                            break;
                        case 19 :
                            if ((0xFFFFFFFBFFFFDBFFL & l) != 0L)
                                jjCheckNAddStates(7, 9);
                            break;
                        case 21 :
                            if ((0x8400000000L & l) != 0L)
                                jjCheckNAddStates(7, 9);
                            break;
                        case 22 :
                            if (this.curChar == '"' && k > 34)
                                k = 34;
                            break;
                        case 23 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAddStates(18, 21);
                            break;
                        case 24 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAddStates(7, 9);
                            break;
                        case 25 :
                            if ((0xF000000000000L & l) != 0L)
                                this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        case 26 :
                            if ((0xFF000000000000L & l) != 0L)
                                jjCheckNAdd(24);
                            break;
                        case 27 :
                            if (this.curChar == '$') {
                                if (k > 35)
                                    k = 35;
                                jjCheckNAdd(28);
                            }
                            break;
                        case 28 :
                            if ((0x3FF001000000000L & l) != 0L) {
                                if (k > 35)
                                    k = 35;
                                jjCheckNAdd(28);
                            }
                            break;
                        case 29 :
                            if ((0x3FF000000000000L & l) != 0L)
                                jjCheckNAddStates(0, 6);
                            break;
                        case 30 :
                            if ((0x3FF000000000000L & l) != 0L)
                                jjCheckNAddTwoStates(30, 31);
                            break;
                        case 31 :
                            if (this.curChar == '.') {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 32 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddStates(22, 24);
                            }
                            break;
                        case 34 :
                            if ((0x280000000000L & l) != 0L)
                                jjCheckNAdd(35);
                            break;
                        case 35 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddTwoStates(35, 8);
                            }
                            break;
                        case 36 :
                            if ((0x3FF000000000000L & l) != 0L)
                                jjCheckNAddTwoStates(36, 37);
                            break;
                        case 38 :
                            if ((0x280000000000L & l) != 0L)
                                jjCheckNAdd(39);
                            break;
                        case 39 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 31)
                                    k = 31;
                                jjCheckNAddTwoStates(39, 8);
                            }
                            break;
                        case 40 :
                            if ((0x3FF000000000000L & l) != 0L)
                                jjCheckNAddStates(25, 27);
                            break;
                        case 42 :
                            if ((0x280000000000L & l) != 0L)
                                jjCheckNAdd(43);
                            break;
                        case 43 :
                            if ((0x3FF000000000000L & l) != 0L)
                                jjCheckNAddTwoStates(43, 8);
                            break;
                        case 44 :
                            if (this.curChar == '0') {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddStates(12, 14);
                            }
                            break;
                        case 46 :
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddTwoStates(46, 2);
                            }
                            break;
                        case 47 :
                            if ((0xFF000000000000L & l) != 0L) {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddTwoStates(47, 2);
                            }
                            break;
                    }
                } while (j != i);
            } else if (this.curChar < '') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--j]) {
                        case 0 :
                        case 28 :
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (k > 35)
                                    k = 35;
                                jjCheckNAdd(28);
                            }
                            break;
                        case 2 :
                            if ((0x100000001000L & l) != 0L && k > 27)
                                k = 27;
                            break;
                        case 5 :
                            if ((0x2000000020L & l) != 0L)
                                jjAddStates(28, 29);
                            break;
                        case 8 :
                            if ((0x5000000050L & l) != 0L && k > 31)
                                k = 31;
                            break;
                        case 10 :
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
                                jjCheckNAdd(11);
                            break;
                        case 12 :
                            if (this.curChar == '\\')
                                jjAddStates(30, 32);
                            break;
                        case 13 :
                            if ((0x14404410000000L & l) != 0L)
                                jjCheckNAdd(11);
                            break;
                        case 19 :
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
                                jjCheckNAddStates(7, 9);
                            break;
                        case 20 :
                            if (this.curChar == '\\')
                                jjAddStates(33, 35);
                            break;
                        case 21 :
                            if ((0x14404410000000L & l) != 0L)
                                jjCheckNAddStates(7, 9);
                            break;
                        case 33 :
                            if ((0x2000000020L & l) != 0L)
                                jjAddStates(36, 37);
                            break;
                        case 37 :
                            if ((0x2000000020L & l) != 0L)
                                jjAddStates(38, 39);
                            break;
                        case 41 :
                            if ((0x2000000020L & l) != 0L)
                                jjAddStates(40, 41);
                            break;
                        case 45 :
                            if ((0x100000001000000L & l) != 0L)
                                jjCheckNAdd(46);
                            break;
                        case 46 :
                            if ((0x7E0000007EL & l) != 0L) {
                                if (k > 27)
                                    k = 27;
                                jjCheckNAddTwoStates(46, 2);
                            }
                            break;
                    }
                } while (j != i);
            } else {
                int m = (this.curChar & 0xFF) >> 6;
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--j]) {
                        case 10 :
                            if ((jjbitVec0[m] & l) != 0L)
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        case 19 :
                            if ((jjbitVec0[m] & l) != 0L)
                                jjAddStates(7, 9);
                            break;
                    }
                } while (j != i);
            }
            if (k != Integer.MAX_VALUE) {
                this.jjmatchedKind = k;
                this.jjmatchedPos = paramInt2;
                k = Integer.MAX_VALUE;
            }
            paramInt2++;
            if ((j = this.jjnewStateCnt) == (i = 48 - (this.jjnewStateCnt = i)))
                return paramInt2;
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException iOException) {
                return paramInt2;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '!' :
                this.jjmatchedKind = 7;
                return jjMoveStringLiteralDfa1_0(131072L);
            case '%' :
                return jjStopAtPos(0, 10);
            case '&' :
                this.jjmatchedKind = 11;
                return jjMoveStringLiteralDfa1_0(4096L);
            case '(' :
                return jjStopAtPos(0, 40);
            case ')' :
                return jjStopAtPos(0, 41);
            case '*' :
                return jjStopAtPos(0, 8);
            case '+' :
                return jjStopAtPos(0, 4);
            case ',' :
                return jjStopAtPos(0, 44);
            case '-' :
                return jjStopAtPos(0, 5);
            case '/' :
                return jjStopAtPos(0, 9);
            case ':' :
                return jjStopAtPos(0, 39);
            case '<' :
                this.jjmatchedKind = 18;
                return jjMoveStringLiteralDfa1_0(6291456L);
            case '=' :
                return jjMoveStringLiteralDfa1_0(65536L);
            case '>' :
                this.jjmatchedKind = 20;
                return jjMoveStringLiteralDfa1_0(25690112L);
            case '?' :
                return jjStopAtPos(0, 38);
            case '[' :
                return jjStopAtPos(0, 42);
            case ']' :
                return jjStopAtPos(0, 43);
            case '^' :
                return jjStopAtPos(0, 15);
            case 'f' :
                return jjMoveStringLiteralDfa1_0(33554432L);
            case 't' :
                return jjMoveStringLiteralDfa1_0(67108864L);
            case '|' :
                this.jjmatchedKind = 13;
                return jjMoveStringLiteralDfa1_0(16384L);
            case '~' :
                return jjStopAtPos(0, 6);
        }
        return jjMoveNfa_0(0, 0);
    }

    private final int jjMoveStringLiteralDfa1_0(long paramLong) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(0, paramLong);
            return 1;
        }
        switch (this.curChar) {
            case '&' :
                if ((paramLong & 0x1000L) != 0L)
                    return jjStopAtPos(1, 12);
                break;
            case '<' :
                if ((paramLong & 0x400000L) != 0L)
                    return jjStopAtPos(1, 22);
                break;
            case '=' :
                if ((paramLong & 0x10000L) != 0L)
                    return jjStopAtPos(1, 16);
                if ((paramLong & 0x20000L) != 0L)
                    return jjStopAtPos(1, 17);
                if ((paramLong & 0x80000L) != 0L)
                    return jjStopAtPos(1, 19);
                if ((paramLong & 0x200000L) != 0L)
                    return jjStopAtPos(1, 21);
                break;
            case '>' :
                if ((paramLong & 0x800000L) != 0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 1;
                }
                return jjMoveStringLiteralDfa2_0(paramLong, 16777216L);
            case 'a' :
                return jjMoveStringLiteralDfa2_0(paramLong, 33554432L);
            case 'r' :
                return jjMoveStringLiteralDfa2_0(paramLong, 67108864L);
            case '|' :
                if ((paramLong & 0x4000L) != 0L)
                    return jjStopAtPos(1, 14);
                break;
        }
        return jjStartNfa_0(0, paramLong);
    }

    private final int jjMoveStringLiteralDfa2_0(long paramLong1, long paramLong2) {
        if ((paramLong2 &= paramLong1) == 0L)
            return jjStartNfa_0(0, paramLong1);
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(1, paramLong2);
            return 2;
        }
        switch (this.curChar) {
            case '>' :
                if ((paramLong2 & 0x1000000L) != 0L)
                    return jjStopAtPos(2, 24);
                break;
            case 'l' :
                return jjMoveStringLiteralDfa3_0(paramLong2, 33554432L);
            case 'u' :
                return jjMoveStringLiteralDfa3_0(paramLong2, 67108864L);
        }
        return jjStartNfa_0(1, paramLong2);
    }

    private final int jjMoveStringLiteralDfa3_0(long paramLong1, long paramLong2) {
        if ((paramLong2 &= paramLong1) == 0L)
            return jjStartNfa_0(1, paramLong1);
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(2, paramLong2);
            return 3;
        }
        switch (this.curChar) {
            case 'e' :
                if ((paramLong2 & 0x4000000L) != 0L)
                    return jjStartNfaWithStates_0(3, 26, 28);
                break;
            case 's' :
                return jjMoveStringLiteralDfa4_0(paramLong2, 33554432L);
        }
        return jjStartNfa_0(2, paramLong2);
    }

    private final int jjMoveStringLiteralDfa4_0(long paramLong1, long paramLong2) {
        if ((paramLong2 &= paramLong1) == 0L)
            return jjStartNfa_0(2, paramLong1);
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            jjStopStringLiteralDfa_0(3, paramLong2);
            return 4;
        }
        switch (this.curChar) {
            case 'e' :
                if ((paramLong2 & 0x2000000L) != 0L)
                    return jjStartNfaWithStates_0(4, 25, 28);
                break;
        }
        return jjStartNfa_0(3, paramLong2);
    }

    private final int jjStartNfaWithStates_0(int paramInt1, int paramInt2, int paramInt3) {
        this.jjmatchedKind = paramInt2;
        this.jjmatchedPos = paramInt1;
        try {
            this.curChar = this.input_stream.readChar();
        } catch (IOException iOException) {
            return paramInt1 + 1;
        }
        return jjMoveNfa_0(paramInt3, paramInt1 + 1);
    }

    private final int jjStartNfa_0(int paramInt, long paramLong) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(paramInt, paramLong), paramInt + 1);
    }

    private final int jjStopAtPos(int paramInt1, int paramInt2) {
        this.jjmatchedKind = paramInt2;
        this.jjmatchedPos = paramInt1;
        return paramInt1 + 1;
    }

    private final int jjStopStringLiteralDfa_0(int paramInt, long paramLong) {
        switch (paramInt) {
            case 0 :
                if ((paramLong & 0x6000000L) != 0L) {
                    this.jjmatchedKind = 35;
                    return 28;
                }
                return -1;
            case 1 :
                if ((paramLong & 0x6000000L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 35;
                        this.jjmatchedPos = 1;
                    }
                    return 28;
                }
                return -1;
            case 2 :
                if ((paramLong & 0x6000000L) != 0L) {
                    this.jjmatchedKind = 35;
                    this.jjmatchedPos = 2;
                    return 28;
                }
                return -1;
            case 3 :
                if ((paramLong & 0x2000000L) != 0L) {
                    this.jjmatchedKind = 35;
                    this.jjmatchedPos = 3;
                    return 28;
                }
                return ((paramLong & 0x4000000L) != 0L) ? 28 : -1;
        }
        return -1;
    }
}
