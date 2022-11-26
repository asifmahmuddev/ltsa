package ic.doc.ltsa.lts;

import java.awt.Color;

public class Symbol {
    public int kind;
    public int startPos;
    public int endPos = -1;
    private String string;
    private int longValue;
    private Object any;
    private Color commentColor;
    private Color upperColor;
    public static final int CONSTANT = 1;
    public static final int PROPERTY = 2;
    public static final int RANGE = 3;
    public static final int IF = 4;
    public static final int THEN = 5;
    public static final int ELSE = 6;
    public static final int FORALL = 7;
    public static final int WHEN = 8;
    public static final int SET = 9;
    public static final int PROGRESS = 10;
    public static final int MENU = 11;
    public static final int ANIMATION = 12;
    public static final int ACTIONS = 13;
    public static final int CONTROLS = 14;
    public static final int DETERMINISTIC = 15;
    public static final int MINIMAL = 16;
    public static final int COMPOSE = 17;
    public static final int TARGET = 18;
    public static final int IMPORT = 19;
    public static final int UNTIL = 20;
    public static final int ASSERT = 21;
    public static final int PREDICATE = 22;
    public static final int NEXTTIME = 23;
    public static final int INIT = 24;
    public static final int BOOLEAN_TYPE = 102;
    public static final int DOUBLE_TYPE = 103;
    public static final int INT_TYPE = 104;
    public static final int STRING_TYPE = 105;
    public static final int UNKNOWN_TYPE = 106;
    public static final int UPPERIDENT = 123;
    public static final int IDENTIFIER = 124;
    public static final int INT_VALUE = 25;
    public static final int DOUBLE_VALUE = 26;
    public static final int STRING_VALUE = 27;
    public static final int UNARY_MINUS = 28;
    public static final int UNARY_PLUS = 29;
    public static final int PLUS = 30;
    public static final int MINUS = 31;
    public static final int STAR = 32;
    public static final int DIVIDE = 33;
    public static final int MODULUS = 34;
    public static final int CIRCUMFLEX = 35;
    public static final int SINE = 36;
    public static final int QUESTION = 37;
    public static final int COLON = 38;
    public static final int COMMA = 39;
    public static final int OR = 40;
    public static final int BITWISE_OR = 41;
    public static final int AND = 42;
    public static final int BITWISE_AND = 43;
    public static final int NOT_EQUAL = 44;
    public static final int PLING = 45;
    public static final int LESS_THAN_EQUAL = 46;
    public static final int LESS_THAN = 47;
    public static final int SHIFT_LEFT = 48;
    public static final int GREATER_THAN_EQUAL = 49;
    public static final int GREATER_THAN = 50;
    public static final int SHIFT_RIGHT = 51;
    public static final int EQUALS = 52;
    public static final int LROUND = 53;
    public static final int RROUND = 54;
    public static final int LCURLY = 60;
    public static final int RCURLY = 61;
    public static final int LSQUARE = 62;
    public static final int RSQUARE = 63;
    public static final int BECOMES = 64;
    public static final int SEMICOLON = 65;
    public static final int DOT = 66;
    public static final int DOT_DOT = 67;
    public static final int AT = 68;
    public static final int ARROW = 69;
    public static final int BACKSLASH = 70;
    public static final int COLON_COLON = 71;
    public static final int QUOTE = 72;
    public static final int HASH = 73;
    public static final int EVENTUALLY = 74;
    public static final int ALWAYS = 75;
    public static final int EQUIVALENT = 76;
    public static final int LABELCONST = 98;
    public static final int EOFSYM = 99;
    public static final int COMMENT = 100;

    public void setString(String paramString) {
        this.string = paramString;
    }

    public void setValue(int paramInt) {
        this.longValue = paramInt;
    }

    public int intValue() {
        return this.longValue;
    }

    public void setAny(Object paramObject) {
        this.any = paramObject;
    }

    public Object getAny() {
        return this.any;
    }

    public boolean isScalarType() {
        switch (this.kind) {
            case 102 :
            case 103 :
            case 104 :
            case 105 :
                return true;
        }
        return false;
    }

    public Symbol() {
        this.commentColor = new Color(102, 153, 153);
        this.upperColor = new Color(0, 0, 160);
        this.kind = 106;
    }

    public Symbol(Symbol paramSymbol) {
        this.commentColor = new Color(102, 153, 153);
        this.upperColor = new Color(0, 0, 160);
        this.kind = paramSymbol.kind;
        this.startPos = paramSymbol.startPos;
        this.endPos = paramSymbol.endPos;
        this.string = paramSymbol.string;
        this.longValue = paramSymbol.longValue;
        this.any = paramSymbol.any;
    }

    public Symbol(int paramInt) {
        this.commentColor = new Color(102, 153, 153);
        this.upperColor = new Color(0, 0, 160);
        this.kind = paramInt;
        this.startPos = -1;
        this.string = null;
        this.longValue = 0;
    }

    public Symbol(int paramInt, String paramString) {
        this.commentColor = new Color(102, 153, 153);
        this.upperColor = new Color(0, 0, 160);
        this.kind = paramInt;
        this.startPos = -1;
        this.string = paramString;
        this.longValue = 0;
    }

    public Symbol(int paramInt1, int paramInt2) {
        this.commentColor = new Color(102, 153, 153);
        this.upperColor = new Color(0, 0, 160);
        this.kind = paramInt1;
        this.startPos = -1;
        this.string = null;
        this.longValue = paramInt2;
    }

    public Color getColor() {
        if (this.kind > 0 && this.kind <= 24)
            return Color.blue;
        if (this.kind == 100)
            return this.commentColor;
        if (this.kind == 25 || this.kind == 27)
            return Color.red;
        if (this.kind == 123)
            return this.upperColor;
        return Color.black;
    }

    public String toString() {
        switch (this.kind) {
            case 1 :
                return "const";
            case 2 :
                return "property";
            case 3 :
                return "range";
            case 4 :
                return "if";
            case 5 :
                return "then";
            case 6 :
                return "else";
            case 7 :
                return "forall";
            case 8 :
                return "when";
            case 9 :
                return "set";
            case 10 :
                return "progress";
            case 11 :
                return "menu";
            case 12 :
                return "animation";
            case 13 :
                return "actions";
            case 14 :
                return "controls";
            case 15 :
                return "determinstic";
            case 16 :
                return "minimal";
            case 17 :
                return "compose";
            case 18 :
                return "target";
            case 19 :
                return "import";
            case 20 :
                return "U";
            case 21 :
                return "assert";
            case 22 :
                return "state";
            case 23 :
                return "X";
            case 24 :
                return "initially";
            case 102 :
                return "boolean";
            case 103 :
                return "double";
            case 104 :
                return "int";
            case 105 :
                return "string";
            case 106 :
                return "unknown";
            case 123 :
                return this.string;
            case 124 :
                return this.string;
            case 98 :
                return this.string;
            case 25 :
                return this.longValue + "";
            case 27 :
                return this.string;
            case 28 :
                return "-";
            case 29 :
                return "+";
            case 30 :
                return "+";
            case 31 :
                return "-";
            case 32 :
                return "*";
            case 33 :
                return "/";
            case 34 :
                return "%";
            case 35 :
                return "^";
            case 36 :
                return "~";
            case 37 :
                return "?";
            case 38 :
                return ":";
            case 71 :
                return "::";
            case 39 :
                return ",";
            case 40 :
                return "||";
            case 41 :
                return "|";
            case 42 :
                return "&&";
            case 43 :
                return "&";
            case 44 :
                return "!=";
            case 45 :
                return "!";
            case 46 :
                return "<=";
            case 47 :
                return "<";
            case 48 :
                return "<<";
            case 49 :
                return ">=";
            case 50 :
                return ">";
            case 51 :
                return ">>";
            case 52 :
                return "==";
            case 53 :
                return "(";
            case 54 :
                return ")";
            case 72 :
                return "'";
            case 73 :
                return "#";
            case 74 :
                return "<>";
            case 75 :
                return "[]";
            case 76 :
                return "<->";
            case 60 :
                return "{";
            case 61 :
                return "}";
            case 62 :
                return "[";
            case 63 :
                return "]";
            case 64 :
                return "=";
            case 65 :
                return ";";
            case 66 :
                return ".";
            case 67 :
                return "..";
            case 68 :
                return "@";
            case 69 :
                return "->";
            case 70 :
                return "\\";
            case 99 :
                return "EOF";
        }
        return "ERROR";
    }
}
