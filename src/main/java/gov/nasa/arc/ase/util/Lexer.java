package gov.nasa.arc.ase.util;

public class Lexer {
    public static final char STRING = 'S';
    public static final char INTEGER = 'I';
    public static final char END = 'E';
    public static final char ID = 'i';
    public static final char LOGICAND = 'a';
    public static final char LOGICOR = 'o';
    public static final char EQUAL = 'e';
    public static final char LESSEQUAL = 'l';
    public static final char GREATEREQUAL = 'g';
    private char[] data;
    private int length;
    private int offset;
    private StringBuffer string;

    public Lexer(String paramString) {
        this.length = paramString.length();
        this.data = new char[this.length];
        for (byte b = 0; b < this.length; b++)
            this.data[b] = paramString.charAt(b);
        this.offset = 0;
    }

    public char lex() throws LexerException {
        try {
            while (true) {
                switch (this.data[this.offset]) {
                    case '\'' :
                        this.offset++;
                        return parseQuoted('\'');
                    case '"' :
                        this.offset++;
                        return parseQuoted('"');
                    case ',' :
                        this.offset++;
                        return ',';
                    case ';' :
                        this.offset++;
                        return ';';
                    case ':' :
                        this.offset++;
                        return ':';
                    case '.' :
                        this.offset++;
                        return '.';
                    case '(' :
                        this.offset++;
                        return '(';
                    case ')' :
                        this.offset++;
                        return ')';
                    case ' ' :
                        this.offset++;
                        continue;
                    case '\t' :
                        this.offset++;
                        continue;
                    case '=' :
                        this.offset++;
                        if (this.data[this.offset] == '=') {
                            this.offset++;
                            return 'e';
                        }
                        throw new LexerException("Unexpected character '" + this.data[this.offset] + "'");
                    case '<' :
                        this.offset++;
                        if (this.data[this.offset] == '=') {
                            this.offset++;
                            return 'l';
                        }
                        return '<';
                    case '>' :
                        this.offset++;
                        if (this.data[this.offset] == '=') {
                            this.offset++;
                            return 'g';
                        }
                        return '>';
                    case '&' :
                        this.offset++;
                        if (this.data[this.offset] == '&') {
                            this.offset++;
                            return 'a';
                        }
                        return '&';
                    case '|' :
                        this.offset++;
                        if (this.data[this.offset] == '|') {
                            this.offset++;
                            return 'o';
                        }
                        return '|';
                }
                break;
            }
            if (Character.isDigit(this.data[this.offset]))
                return parseInt();
            if (Character.isJavaIdentifierStart(this.data[this.offset]))
                return parseId();
            throw new LexerException("invalid character '" + this.data[this.offset] + "'");
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return 'E';
        }
    }

    public String getStringValue() {
        return this.string.toString();
    }

    public int getIntValue() {
        return Integer.parseInt(this.string.toString());
    }

    private char parseQuoted(char paramChar) {
        this.string = new StringBuffer();
        try {
            while (this.data[this.offset] != paramChar)
                this.string.append(this.data[this.offset++]);
            this.offset++;
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        }
        return 'S';
    }

    private char parseInt() {
        this.string = new StringBuffer();
        try {
            while (Character.isDigit(this.data[this.offset]))
                this.string.append(this.data[this.offset++]);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        }
        return 'I';
    }

    private char parseId() {
        this.string = new StringBuffer();
        try {
            while (Character.isJavaIdentifierPart(this.data[this.offset]))
                this.string.append(this.data[this.offset++]);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        }
        return 'i';
    }
}
