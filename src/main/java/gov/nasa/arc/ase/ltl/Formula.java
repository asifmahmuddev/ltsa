package gov.nasa.arc.ase.ltl;

import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Vector;

public class Formula implements Comparable {
    private char content;
    private boolean literal;
    private Formula left;
    private Formula right;
    private int id;
    private int untils_index;
    private int rightOfUntil_index;
    private String name;
    public static Vector untils = new Vector();
    public static Formula[] until_forms;
    private static int nId = 0;
    private static final int P_ALL = 0;
    private static final int P_OR = 1;
    private static final int P_AND = 2;
    private static final int P_UNTIL = 3;
    private static final int P_WUNTIL = 3;
    private static final int P_RELEASE = 4;
    private static final int P_WRELEASE = 4;
    private static final int P_IMPLIES = 5;
    private static final int P_NOT = 6;
    private static final int P_NEXT = 6;
    private static final int P_ALWAYS = 6;
    private static final int P_EVENTUALLY = 6;

    private Formula(char paramChar, boolean paramBoolean, Formula paramFormula1, Formula paramFormula2, String paramString) {
        this.id = nId++;
        this.content = paramChar;
        this.literal = paramBoolean;
        this.left = paramFormula1;
        this.right = paramFormula2;
        this.name = paramString;
        this.rightOfUntil_index = -1;
        this.untils_index = -1;
    }

    public static void reset_static() {
        untils = new Vector();
        until_forms = null;
    }

    public int compareTo(Object paramObject) {
        Formula formula = (Formula) paramObject;
        return this.id - formula.id;
    }

    public boolean is_literal() {
        return this.literal;
    }

    public boolean is_right_of_until() {
        return (this.rightOfUntil_index >= 0);
    }

    public int get_untils_index() {
        return this.untils_index;
    }

    public int get_rightOfUntils_index() {
        return this.rightOfUntil_index;
    }

    public char getContent() {
        return this.content;
    }

    public String getName() {
        return this.name;
    }

    public Formula getSub1() {
        if (this.content == 'V')
            return this.right;
        return this.left;
    }

    public Formula getSub2() {
        if (this.content == 'V')
            return this.left;
        return this.right;
    }

    public int processRightUntils() {
        boolean bool = false;
        if (getContent() == 'U') {
            untils.add(this);
            this.untils_index = this.right.rightOfUntil_index = untils.size() - 1;
        }
        if (this.left != null)
            this.left.processRightUntils();
        if (this.right != null)
            this.right.processRightUntils();
        return untils.size();
    }

    public Formula getNext() {
        switch (this.content) {
            case 'U' :
            case 'W' :
                return this;
            case 'V' :
                return this;
            case 'O' :
                return null;
        }
        return null;
    }

    public void addLeft(Formula paramFormula) {
        this.left = paramFormula;
    }

    public void addRight(Formula paramFormula) {
        this.right = paramFormula;
    }

    public Formula negate() {
        return Not(this);
    }

    public boolean is_special_case_of_V(TreeSet paramTreeSet) {
        Formula formula = Release(False(), this);
        if (paramTreeSet.contains(formula))
            return true;
        return false;
    }

    public boolean is_synt_implied(TreeSet paramTreeSet1, TreeSet paramTreeSet2) {
        if (getContent() == 't')
            return true;
        if (paramTreeSet1.contains(this))
            return true;
        if (!is_literal()) {
            boolean bool1, bool2, bool3;
            Formula formula1 = getSub1();
            Formula formula2 = getSub2();
            Formula formula3 = getNext();
            if (formula2 != null) {
                bool2 = formula2.is_synt_implied(paramTreeSet1, paramTreeSet2);
            } else {
                bool2 = true;
            }
            if (formula1 != null) {
                bool1 = formula1.is_synt_implied(paramTreeSet1, paramTreeSet2);
            } else {
                bool1 = true;
            }
            if (formula3 != null) {
                if (paramTreeSet2 != null) {
                    bool3 = paramTreeSet2.contains(formula3);
                } else {
                    bool3 = false;
                }
            } else {
                bool3 = true;
            }
            switch (getContent()) {
                case 'O' :
                case 'U' :
                case 'W' :
                    return (bool2 || (bool1 && bool3));
                case 'V' :
                    return ((bool1 && bool2) || (bool1 && bool3));
                case 'X' :
                    if (formula1 != null) {
                        if (paramTreeSet2 != null)
                            return paramTreeSet2.contains(formula1);
                        return false;
                    }
                    return true;
                case 'A' :
                    return (bool2 && bool1);
            }
            System.out.println("Default case of switch at Form.synt_implied");
            return false;
        }
        return false;
    }

    public String toString(boolean paramBoolean) {
        if (!paramBoolean)
            return toString();
        switch (this.content) {
            case 'A' :
                return "( " + this.left.toString(true) + " /\\ " + this.right.toString(true) + " )[" + this.id + "]";
            case 'O' :
                return "( " + this.left.toString(true) + " \\/ " + this.right.toString(true) + " )[" + this.id + "]";
            case 'U' :
                return "( " + this.left.toString(true) + " U " + this.right.toString(true) + " )[" + this.id + "]";
            case 'V' :
                return "( " + this.left.toString(true) + " V " + this.right.toString(true) + " )[" + this.id + "]";
            case 'W' :
                return "( " + this.left.toString(true) + " W " + this.right.toString(true) + " )[" + this.id + "]";
            case 'X' :
                return "( () " + this.left.toString(true) + " )[" + this.id + "]";
            case 'N' :
                return "( ! " + this.left.toString(true) + " )[" + this.id + "]";
            case 't' :
                return "( true )[" + this.id + "]";
            case 'f' :
                return "( false )[" + this.id + "]";
            case 'p' :
                return "( \"" + this.name + "\" )[" + this.id + "]";
        }
        return "( " + this.content + " )[" + this.id + "]";
    }

    public String toString() {
        switch (this.content) {
            case 'A' :
                return "( " + this.left.toString() + " /\\ " + this.right.toString() + " )";
            case 'O' :
                return "( " + this.left.toString() + " \\/ " + this.right.toString() + " )";
            case 'U' :
                return "( " + this.left.toString() + " U " + this.right.toString() + " )";
            case 'V' :
                return "( " + this.left.toString() + " V " + this.right.toString() + " )";
            case 'W' :
                return "( " + this.left.toString() + " W " + this.right.toString() + " )";
            case 'X' :
                return "( () " + this.left.toString() + " )";
            case 'N' :
                return "( ! " + this.left.toString() + " )";
            case 't' :
                return "( true )";
            case 'f' :
                return "( false )";
            case 'p' :
                return "( \"" + this.name + "\" )";
        }
        return (new Character(this.content)).toString();
    }

    public static Formula parse(String paramString) throws ParseErrorException {
        Input input = new Input(paramString);
        return parse(input, 0);
    }

    private static Formula parse(Input paramInput, int paramInt) throws ParseErrorException {
        try {
            Formula formula;
            StringBuffer stringBuffer;
            for (; paramInput.get() == ' '; paramInput.skip());
            char c;
            switch (c = paramInput.get()) {
                case ')' :
                case '/' :
                case 'M' :
                case 'U' :
                case 'V' :
                case 'W' :
                case '\\' :
                    throw new ParseErrorException("invalid character: " + c);
                case '!' :
                    paramInput.skip();
                    formula = Not(parse(paramInput, 6));
                    break;
                case 'X' :
                    paramInput.skip();
                    formula = Next(parse(paramInput, 6));
                    break;
                case '[' :
                    paramInput.skip();
                    if (paramInput.get() != ']')
                        throw new ParseErrorException("expected ]");
                    paramInput.skip();
                    formula = Always(parse(paramInput, 6));
                    break;
                case '<' :
                    paramInput.skip();
                    if (paramInput.get() != '>')
                        throw new ParseErrorException("expected >");
                    paramInput.skip();
                    formula = Eventually(parse(paramInput, 6));
                    break;
                case '(' :
                    paramInput.skip();
                    formula = parse(paramInput, 0);
                    if (paramInput.get() != ')')
                        throw new ParseErrorException("invalid character: " + c);
                    paramInput.skip();
                    break;
                case '"' :
                    stringBuffer = new StringBuffer();
                    paramInput.skip();
                    while ((c = paramInput.get()) != '"') {
                        stringBuffer.append(c);
                        paramInput.skip();
                    }
                    paramInput.skip();
                    formula = Proposition(stringBuffer.toString());
                    break;
                default :
                    if (Character.isJavaIdentifierStart(c)) {
                        StringBuffer stringBuffer1 = new StringBuffer();
                        stringBuffer1.append(c);
                        paramInput.skip();
                        try {
                            while (Character.isJavaIdentifierPart(c = paramInput.get()) && !is_reserved_char(c)) {
                                stringBuffer1.append(c);
                                paramInput.skip();
                            }
                        } catch (EndOfInputException endOfInputException) {
                        }
                        String str = stringBuffer1.toString();
                        if (str.equals("true")) {
                            formula = True();
                            break;
                        }
                        if (str.equals("false")) {
                            formula = False();
                            break;
                        }
                        formula = Proposition(stringBuffer1.toString());
                        break;
                    }
                    throw new ParseErrorException("invalid character: " + c);
            }
            try {
                for (; paramInput.get() == ' '; paramInput.skip());
                c = paramInput.get();
            } catch (EndOfInputException endOfInputException) {
                return formula;
            }
            while (true) {
                switch (c) {
                    case '/' :
                        if (paramInt > 2)
                            return formula;
                        paramInput.skip();
                        if (paramInput.get() != '\\')
                            throw new ParseErrorException("expected \\");
                        paramInput.skip();
                        formula = And(formula, parse(paramInput, 2));
                        break;
                    case '\\' :
                        if (paramInt > 1)
                            return formula;
                        paramInput.skip();
                        if (paramInput.get() != '/')
                            throw new ParseErrorException("expected /");
                        paramInput.skip();
                        formula = Or(formula, parse(paramInput, 1));
                        break;
                    case 'U' :
                        if (paramInt > 3)
                            return formula;
                        paramInput.skip();
                        formula = Until(formula, parse(paramInput, 3));
                        break;
                    case 'W' :
                        if (paramInt > 3)
                            return formula;
                        paramInput.skip();
                        formula = WUntil(formula, parse(paramInput, 3));
                        break;
                    case 'V' :
                        if (paramInt > 4)
                            return formula;
                        paramInput.skip();
                        formula = Release(formula, parse(paramInput, 4));
                        break;
                    case 'M' :
                        if (paramInt > 4)
                            return formula;
                        paramInput.skip();
                        formula = WRelease(formula, parse(paramInput, 4));
                        break;
                    case '-' :
                        if (paramInt > 5)
                            return formula;
                        paramInput.skip();
                        if (paramInput.get() != '>')
                            throw new ParseErrorException("expected >");
                        paramInput.skip();
                        formula = Implies(formula, parse(paramInput, 5));
                        break;
                    case ')' :
                        return formula;
                    default :
                        throw new ParseErrorException("invalid character: " + c);
                }
                try {
                    for (; paramInput.get() == ' '; paramInput.skip());
                    c = paramInput.get();
                } catch (EndOfInputException endOfInputException) {
                    break;
                }
            }
            return formula;
        } catch (EndOfInputException endOfInputException) {
            throw new ParseErrorException("unexpected end of input");
        }
    }

    private static Formula And(Formula paramFormula1, Formula paramFormula2) {
        if (paramFormula1.id < paramFormula2.id)
            return unique(new Formula('A', false, paramFormula1, paramFormula2, null));
        return unique(new Formula('A', false, paramFormula2, paramFormula1, null));
    }

    private static Formula Or(Formula paramFormula1, Formula paramFormula2) {
        if (paramFormula1.id < paramFormula2.id)
            return unique(new Formula('O', false, paramFormula1, paramFormula2, null));
        return unique(new Formula('O', false, paramFormula2, paramFormula1, null));
    }

    private static Formula Until(Formula paramFormula1, Formula paramFormula2) {
        return unique(new Formula('U', false, paramFormula1, paramFormula2, null));
    }

    private static Formula WUntil(Formula paramFormula1, Formula paramFormula2) {
        return unique(new Formula('W', false, paramFormula1, paramFormula2, null));
    }

    private static Formula Release(Formula paramFormula1, Formula paramFormula2) {
        return unique(new Formula('V', false, paramFormula1, paramFormula2, null));
    }

    private static Formula Proposition(String paramString) {
        return unique(new Formula('p', true, null, null, paramString));
    }

    private static Formula Not(Formula paramFormula) {
        if (paramFormula.literal) {
            switch (paramFormula.content) {
                case 't' :
                    return False();
                case 'f' :
                    return True();
                case 'N' :
                    return paramFormula.left;
            }
            return unique(new Formula('N', true, paramFormula, null, null));
        }
        switch (paramFormula.content) {
            case 'A' :
                return Or(Not(paramFormula.left), Not(paramFormula.right));
            case 'O' :
                return And(Not(paramFormula.left), Not(paramFormula.right));
            case 'U' :
                return Release(Not(paramFormula.left), Not(paramFormula.right));
            case 'V' :
                return Until(Not(paramFormula.left), Not(paramFormula.right));
            case 'W' :
                return WRelease(Not(paramFormula.left), Not(paramFormula.right));
            case 'N' :
                return paramFormula.left;
            case 'X' :
                return Next(Not(paramFormula.left));
        }
        throw new ParserInternalError();
    }

    private static Formula Next(Formula paramFormula) {
        return unique(new Formula('X', false, paramFormula, null, null));
    }

    private static Formula Always(Formula paramFormula) {
        return unique(new Formula('V', false, False(), paramFormula, null));
    }

    private static Formula Eventually(Formula paramFormula) {
        return unique(new Formula('U', false, True(), paramFormula, null));
    }

    private static Formula WRelease(Formula paramFormula1, Formula paramFormula2) {
        return unique(new Formula('U', false, paramFormula2, And(paramFormula1, paramFormula2), null));
    }

    private static Formula Implies(Formula paramFormula1, Formula paramFormula2) {
        return Or(Not(paramFormula1), paramFormula2);
    }

    private static Formula True() {
        return unique(new Formula('t', true, null, null, null));
    }

    private static Formula False() {
        return unique(new Formula('f', true, null, null, null));
    }

    public static class EndOfInputException extends Exception {
    }

    private static Hashtable ht = new Hashtable();

    private static Formula unique(Formula paramFormula) {
        String str = paramFormula.toString();
        if (ht.containsKey(str))
            return (Formula) ht.get(str);
        ht.put(str, paramFormula);
        return paramFormula;
    }

    private static class Input {
        private StringBuffer sb;

        public Input(String param1String) {
            this.sb = new StringBuffer(param1String);
        }

        public char get() throws Formula.EndOfInputException {
            try {
                return this.sb.charAt(0);
            } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                throw new Formula.EndOfInputException();
            }
        }

        public void skip() throws Formula.EndOfInputException {
            try {
                this.sb.deleteCharAt(0);
            } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                throw new Formula.EndOfInputException();
            }
        }
    }

    public static boolean is_reserved_char(char paramChar) {
        switch (paramChar) {
            case ' ' :
            case '(' :
            case ')' :
            case '-' :
            case '<' :
            case '>' :
            case 'M' :
            case 'U' :
            case 'V' :
            case 'W' :
            case 'X' :
            case '[' :
            case ']' :
                return true;
        }
        return false;
    }

    public Formula rewrite(Formula paramFormula1, Formula paramFormula2) {
        switch (this.content) {
            case 'A' :
            case 'O' :
            case 'U' :
            case 'V' :
            case 'W' :
                this.left = this.left.rewrite(paramFormula1, paramFormula2);
                this.right = this.right.rewrite(paramFormula1, paramFormula2);
                break;
            case 'N' :
            case 'X' :
                this.left = this.left.rewrite(paramFormula1, paramFormula2);
                break;
        }
        if (match(paramFormula1)) {
            Formula formula = paramFormula2.rewrite();
            clearMatches();
            return formula;
        }
        clearMatches();
        return this;
    }

    private Formula rewrite() {
        if (this.content == 'p')
            return getMatch(this.name);
        switch (this.content) {
            case 'A' :
                return And(this.left.rewrite(), this.right.rewrite());
            case 'O' :
                return Or(this.left.rewrite(), this.right.rewrite());
            case 'U' :
                return Until(this.left.rewrite(), this.right.rewrite());
            case 'V' :
                return Release(this.left.rewrite(), this.right.rewrite());
            case 'W' :
                return WUntil(this.left.rewrite(), this.right.rewrite());
            case 'X' :
                return Next(this.left.rewrite());
            case 'N' :
                return Not(this.left.rewrite());
            case 't' :
                return True();
            case 'f' :
                return False();
        }
        throw new RuntimeException("code should not be reached");
    }

    private static Hashtable matches = new Hashtable();

    private void clearMatches() {
        matches = new Hashtable();
    }

    private void addMatch(String paramString, Formula paramFormula) {
        matches.put(paramString, paramFormula);
    }

    private Formula getMatch(String paramString) {
        return (Formula) matches.get(paramString);
    }

    private boolean match(Formula paramFormula) {
        if (paramFormula.content == 'p') {
            Formula formula = getMatch(paramFormula.name);
            if (formula == null) {
                addMatch(paramFormula.name, this);
                return true;
            }
            return (formula == this);
        }
        if (paramFormula.content != this.content)
            return false;
        Hashtable hashtable = (Hashtable) matches.clone();
        switch (this.content) {
            case 'A' :
            case 'O' :
                if (this.left.match(paramFormula.left) && this.right.match(paramFormula.right))
                    return true;
                matches = hashtable;
                if (this.right.match(paramFormula.left) && this.left.match(paramFormula.right))
                    return true;
                matches = hashtable;
                return false;
            case 'U' :
            case 'V' :
            case 'W' :
                if (this.left.match(paramFormula.left) && this.right.match(paramFormula.right))
                    return true;
                matches = hashtable;
                return false;
            case 'N' :
            case 'X' :
                if (this.left.match(paramFormula.left))
                    return true;
                matches = hashtable;
                return false;
            case 'f' :
            case 't' :
                return true;
        }
        throw new RuntimeException("code should not be reached");
    }

    public int size() {
        switch (this.content) {
            case 'A' :
            case 'O' :
            case 'U' :
            case 'V' :
            case 'W' :
                return this.left.size() + this.right.size() + 1;
            case 'N' :
            case 'X' :
                return this.left.size() + 1;
        }
        return 0;
    }
}
