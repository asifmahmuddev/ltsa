package ic.doc.ltsa.lts;

public class Lex {
    private LTSInput input;
    private Symbol symbol;
    private char ch;
    private boolean eoln;
    private boolean newSymbols = true;
    private Symbol current;
    private Symbol buffer;

    public Lex(LTSInput paramLTSInput) {
        this(paramLTSInput, true);
    }

    private void error(String paramString) {
        Diagnostics.fatal(paramString, new Integer(this.input.getMarker()));
    }

    private void next_ch() {
        this.ch = this.input.nextChar();
        this.eoln = (this.ch == '\n' || this.ch == '\000');
    }

    private void back_ch() {
        this.ch = this.input.backChar();
        this.eoln = (this.ch == '\n' || this.ch == '\000');
    }

    private void in_comment() {
        if (this.ch == '/') {
            do {
                next_ch();
            } while (!this.eoln);
        } else {
            while (true) {
                next_ch();
                if (this.ch == '*' || this.ch == '\000') {
                    do {
                        next_ch();
                    } while (this.ch == '*' && this.ch != '\000');
                    if (this.ch == '/' || this.ch == '\000')
                        break;
                }
            }
            next_ch();
        }
        if (!this.newSymbols) {
            this.symbol.kind = 100;
            back_ch();
        }
    }

    private boolean isodigit(char paramChar) {
        return (paramChar >= '0' && paramChar <= '7');
    }

    private boolean isxdigit(char paramChar) {
        return ((paramChar >= '0' && paramChar <= '9') || (paramChar >= 'A' && paramChar <= 'F') || (paramChar >= 'a' && paramChar <= 'f'));
    }

    private boolean isbase(char paramChar, int paramInt) {
        switch (paramInt) {
            case 10 :
                return Character.isDigit(paramChar);
            case 16 :
                return isxdigit(paramChar);
            case 8 :
                return isodigit(paramChar);
        }
        return true;
    }

    private void in_number() {
        long l = 0L;
        int i = 0;
        byte b = 10;
        this.symbol.kind = 25;
        if (this.ch == '0') {
            next_ch();
            if (this.ch == 'x' || this.ch == 'X') {
                b = 16;
                next_ch();
            } else {
                b = 8;
            }
        } else {
            b = 10;
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (isbase(this.ch, b)) {
            stringBuffer.append(this.ch);
            switch (b) {
                case 8 :
                case 10 :
                    i = this.ch - 48;
                    break;
                case 16 :
                    if (Character.isUpperCase(this.ch)) {
                        i = this.ch - 65 + 10;
                        break;
                    }
                    if (Character.isLowerCase(this.ch)) {
                        i = this.ch - 97 + 10;
                        break;
                    }
                    i = this.ch - 48;
                    break;
            }
            if (l * b > (Integer.MAX_VALUE - i)) {
                error("Integer Overflow");
                l = 2147483647L;
                break;
            }
            l = l * b + i;
            next_ch();
        }
        this.symbol.setValue((int) l);
        back_ch();
    }

    private void in_escseq() {
        while (this.ch == '\\') {
            int i;
            next_ch();
            switch (this.ch) {
                case 'a' :
                    this.ch = 'a';
                case 'b' :
                    this.ch = '\b';
                case 'f' :
                    this.ch = '\f';
                case 'n' :
                    this.ch = '\n';
                case 'r' :
                    this.ch = '\r';
                case 't' :
                    this.ch = '\t';
                case '\\' :
                    this.ch = '\\';
                case '\'' :
                    this.ch = '\'';
                case '"' :
                    this.ch = '"';
                case '?' :
                    this.ch = '?';
                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                    i = this.ch - 48;
                    next_ch();
                    if (isodigit(this.ch)) {
                        i = i * 8 + this.ch - 48;
                        next_ch();
                        if (isodigit(this.ch))
                            i = i * 8 + this.ch - 48;
                    }
                    this.ch = (char) i;
                case 'X' :
                case 'x' :
                    i = 0;
                    next_ch();
                    if (!isxdigit(this.ch)) {
                        error("hex digit expected after \\x");
                    } else {
                        int j = 0;
                        while (isxdigit(this.ch) && j < 2) {
                            j++;
                            if (Character.isDigit(this.ch)) {
                                i = i * 16 + this.ch - 48;
                            } else if (Character.isUpperCase(this.ch)) {
                                i = i * 16 + this.ch - 65;
                            } else {
                                i = i * 16 + this.ch - 97;
                            }
                            next_ch();
                        }
                    }
                    this.ch = (char) i;
            }
        }
    }

    private void in_string() {
        char c = this.ch;
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            next_ch();
            boolean bool;
            if (bool = (this.ch != c && !this.eoln) ? true : false)
                stringBuffer.append(this.ch);
            if (!bool) {
                this.symbol.setString(stringBuffer.toString());
                if (this.eoln == true)
                    error("No closing character for string constant");
                this.symbol.kind = 27;
                return;
            }
        }
    }

    private void in_identifier() {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            stringBuffer.append(this.ch);
            next_ch();
            if (!Character.isLetterOrDigit(this.ch) && this.ch != '_') {
                String str = stringBuffer.toString();
                this.symbol.setString(str);
                Object object = SymbolTable.get(str);
                if (object == null) {
                    if (Character.isUpperCase(str.charAt(0))) {
                        this.symbol.kind = 123;
                    } else {
                        this.symbol.kind = 124;
                    }
                } else {
                    this.symbol.kind = ((Integer) object).intValue();
                }
                back_ch();
                return;
            }
        }
    }

    public Symbol in_sym() {
        next_ch();
        if (this.newSymbols)
            this.symbol = new Symbol();
        boolean bool = true;
        while (bool) {
            bool = false;
            this.symbol.startPos = this.input.getMarker();
            switch (this.ch) {
                case '\000' :
                    this.symbol.kind = 99;
                    continue;
                case '\t' :
                case '\n' :
                case '\f' :
                case '\r' :
                case ' ' :
                    while (Character.isWhitespace(this.ch))
                        next_ch();
                    bool = true;
                    continue;
                case '/' :
                    next_ch();
                    if (this.ch == '/' || this.ch == '*') {
                        in_comment();
                        if (this.newSymbols)
                            bool = true;
                        continue;
                    }
                    this.symbol.kind = 33;
                    back_ch();
                    continue;
                case 'A' :
                case 'B' :
                case 'C' :
                case 'D' :
                case 'E' :
                case 'F' :
                case 'G' :
                case 'H' :
                case 'I' :
                case 'J' :
                case 'K' :
                case 'L' :
                case 'M' :
                case 'N' :
                case 'O' :
                case 'P' :
                case 'Q' :
                case 'R' :
                case 'S' :
                case 'T' :
                case 'U' :
                case 'V' :
                case 'W' :
                case 'X' :
                case 'Y' :
                case 'Z' :
                case '_' :
                case 'a' :
                case 'b' :
                case 'c' :
                case 'd' :
                case 'e' :
                case 'f' :
                case 'g' :
                case 'h' :
                case 'i' :
                case 'j' :
                case 'k' :
                case 'l' :
                case 'm' :
                case 'n' :
                case 'o' :
                case 'p' :
                case 'q' :
                case 'r' :
                case 's' :
                case 't' :
                case 'u' :
                case 'v' :
                case 'w' :
                case 'x' :
                case 'y' :
                case 'z' :
                    in_identifier();
                    continue;
                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                case '8' :
                case '9' :
                    in_number();
                    continue;
                case '#' :
                    this.symbol.kind = 73;
                    continue;
                case '\'' :
                    this.symbol.kind = 72;
                    continue;
                case '"' :
                    in_string();
                    continue;
                case '+' :
                    this.symbol.kind = 30;
                    continue;
                case '*' :
                    this.symbol.kind = 32;
                    continue;
                case '%' :
                    this.symbol.kind = 34;
                    continue;
                case '^' :
                    this.symbol.kind = 35;
                    continue;
                case '~' :
                    this.symbol.kind = 36;
                    continue;
                case '?' :
                    this.symbol.kind = 37;
                    continue;
                case ',' :
                    this.symbol.kind = 39;
                    continue;
                case '(' :
                    this.symbol.kind = 53;
                    continue;
                case ')' :
                    this.symbol.kind = 54;
                    continue;
                case '{' :
                    this.symbol.kind = 60;
                    continue;
                case '}' :
                    this.symbol.kind = 61;
                    continue;
                case ']' :
                    this.symbol.kind = 63;
                    continue;
                case ';' :
                    this.symbol.kind = 65;
                    continue;
                case '@' :
                    this.symbol.kind = 68;
                    continue;
                case '\\' :
                    this.symbol.kind = 70;
                    continue;
                case '[' :
                    next_ch();
                    if (this.ch == ']') {
                        this.symbol.kind = 75;
                        continue;
                    }
                    this.symbol.kind = 62;
                    back_ch();
                    continue;
                case '|' :
                    next_ch();
                    if (this.ch == '|') {
                        this.symbol.kind = 40;
                        continue;
                    }
                    this.symbol.kind = 41;
                    back_ch();
                    continue;
                case '&' :
                    next_ch();
                    if (this.ch == '&') {
                        this.symbol.kind = 42;
                        continue;
                    }
                    this.symbol.kind = 43;
                    back_ch();
                    continue;
                case '!' :
                    next_ch();
                    if (this.ch == '=') {
                        this.symbol.kind = 44;
                        continue;
                    }
                    this.symbol.kind = 45;
                    back_ch();
                    continue;
                case '<' :
                    next_ch();
                    if (this.ch == '=') {
                        this.symbol.kind = 46;
                        continue;
                    }
                    if (this.ch == '<') {
                        this.symbol.kind = 48;
                        continue;
                    }
                    if (this.ch == '>') {
                        this.symbol.kind = 74;
                        continue;
                    }
                    if (this.ch == '-') {
                        next_ch();
                        if (this.ch == '>') {
                            this.symbol.kind = 76;
                            continue;
                        }
                        this.symbol.kind = 47;
                        back_ch();
                        back_ch();
                        continue;
                    }
                    this.symbol.kind = 47;
                    back_ch();
                    continue;
                case '>' :
                    next_ch();
                    if (this.ch == '=') {
                        this.symbol.kind = 49;
                        continue;
                    }
                    if (this.ch == '>') {
                        this.symbol.kind = 51;
                        continue;
                    }
                    this.symbol.kind = 50;
                    back_ch();
                    continue;
                case '=' :
                    next_ch();
                    if (this.ch == '=') {
                        this.symbol.kind = 52;
                        continue;
                    }
                    this.symbol.kind = 64;
                    back_ch();
                    continue;
                case '.' :
                    next_ch();
                    if (this.ch == '.') {
                        this.symbol.kind = 67;
                        continue;
                    }
                    this.symbol.kind = 66;
                    back_ch();
                    continue;
                case '-' :
                    next_ch();
                    if (this.ch == '>') {
                        this.symbol.kind = 69;
                        continue;
                    }
                    this.symbol.kind = 31;
                    back_ch();
                    continue;
                case ':' :
                    next_ch();
                    if (this.ch == ':') {
                        this.symbol.kind = 71;
                        continue;
                    }
                    this.symbol.kind = 38;
                    back_ch();
                    continue;
            }
            error("unexpected character encountered");
        }
        this.symbol.endPos = this.input.getMarker();
        return this.symbol;
    }

    public Lex(LTSInput paramLTSInput, boolean paramBoolean) {
        this.current = null;
        this.buffer = null;
        this.input = paramLTSInput;
        this.newSymbols = paramBoolean;
        if (!paramBoolean)
            this.symbol = new Symbol();
    }

    public Symbol next_symbol() {
        if (this.buffer == null) {
            this.current = in_sym();
        } else {
            this.current = this.buffer;
            this.buffer = null;
        }
        return this.current;
    }

    public void push_symbol() {
        this.buffer = this.current;
    }

    public Symbol current() {
        return this.current;
    }
}
