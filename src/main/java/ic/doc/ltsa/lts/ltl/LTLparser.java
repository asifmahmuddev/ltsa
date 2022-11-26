package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.Lex;
import ic.doc.ltsa.lts.Symbol;

public class LTLparser {
    private Lex lex;
    private FormulaFactory fac;
    private Symbol current;

    public LTLparser(Lex paramLex) {
        this.lex = paramLex;
        this.fac = new FormulaFactory();
    }

    public FormulaFactory parse() {
        this.current = modify(this.lex.current());
        if (this.current == null)
            next_symbol();
        this.fac.setFormula(ltl_unary());
        return this.fac;
    }

    private Symbol next_symbol() {
        return this.current = modify(this.lex.next_symbol());
    }

    private void push_symbol() {
        this.lex.push_symbol();
    }

    private void current_is(int paramInt, String paramString) {
        if (this.current.kind != paramInt)
            Diagnostics.fatal(paramString, this.current);
    }

    private Symbol modify(Symbol paramSymbol) {
        if (paramSymbol.kind != 123)
            return paramSymbol;
        if (paramSymbol.toString().equals("X")) {
            Symbol symbol = new Symbol(paramSymbol);
            symbol.kind = 23;
            return symbol;
        }
        if (paramSymbol.toString().equals("U")) {
            Symbol symbol = new Symbol(paramSymbol);
            symbol.kind = 20;
            return symbol;
        }
        return paramSymbol;
    }

    private Formula ltl_unary() {
        Formula formula;
        Symbol symbol = this.current;
        switch (this.current.kind) {
            case 23 :
            case 45 :
            case 74 :
            case 75 :
                next_symbol();
                return this.fac.make(null, symbol, ltl_unary());
            case 123 :
                next_symbol();
                return this.fac.make(symbol);
            case 53 :
                next_symbol();
                formula = ltl_or();
                current_is(54, ") expected to end LTL expression");
                next_symbol();
                return formula;
        }
        Diagnostics.fatal("syntax error in LTL expression", this.current);
        return null;
    }

    private Formula ltl_and() {
        Formula formula = ltl_unary();
        while (this.current.kind == 42) {
            Symbol symbol = this.current;
            next_symbol();
            Formula formula1 = ltl_unary();
            formula = this.fac.make(formula, symbol, formula1);
        }
        return formula;
    }

    private Formula ltl_or() {
        Formula formula = ltl_binary();
        while (this.current.kind == 40) {
            Symbol symbol = this.current;
            next_symbol();
            Formula formula1 = ltl_binary();
            formula = this.fac.make(formula, symbol, formula1);
        }
        return formula;
    }

    private Formula ltl_binary() {
        Formula formula = ltl_and();
        if (this.current.kind == 20 || this.current.kind == 69 || this.current.kind == 76) {
            Symbol symbol = this.current;
            next_symbol();
            Formula formula1 = ltl_and();
            formula = this.fac.make(formula, symbol, formula1);
        }
        return formula;
    }
}
