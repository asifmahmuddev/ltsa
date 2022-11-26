package ic.doc.ltsa.lts;

import java.util.Hashtable;
import java.util.Stack;

public class Expression {
    static Hashtable constants;

    private static String labelVar(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        if (paramStack == null)
            return null;
        if (paramStack.empty())
            return null;
        Symbol symbol = paramStack.peek();
        if (symbol.kind == 124) {
            if (paramHashtable1 != null) {
                Value value = (Value) paramHashtable1.get(symbol.toString());
                if (value != null && value.isLabel()) {
                    paramStack.pop();
                    return value.toString();
                }
            }
        } else if (symbol.kind == 123) {
            Value value = null;
            if (paramHashtable2 != null)
                value = (Value) paramHashtable2.get(symbol.toString());
            if (value == null)
                value = (Value) constants.get(symbol.toString());
            if (value != null && value.isLabel()) {
                paramStack.pop();
                return value.toString();
            }
        } else {
            if (symbol.kind == 98) {
                ActionLabels actionLabels = (ActionLabels) symbol.getAny();
                if (actionLabels.hasMultipleValues())
                    Diagnostics.fatal("label constants cannot be sets", symbol);
                actionLabels.initContext(paramHashtable1, paramHashtable2);
                paramStack.pop();
                return actionLabels.nextName();
            }
            if (symbol.kind == 68)
                return indexSet(paramStack, paramHashtable1, paramHashtable2);
        }
        return null;
    }

    protected static int countSet(Symbol paramSymbol, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        if (paramSymbol.kind != 98)
            Diagnostics.fatal("label set expected", paramSymbol);
        ActionLabels actionLabels = (ActionLabels) paramSymbol.getAny();
        actionLabels.initContext(paramHashtable1, paramHashtable2);
        byte b = 0;
        while (actionLabels.hasMoreNames()) {
            b++;
            actionLabels.nextName();
        }
        actionLabels.clearContext();
        return b;
    }

    protected static String indexSet(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        paramStack.pop();
        int i = eval(paramStack, paramHashtable1, paramHashtable2);
        Symbol symbol = paramStack.pop();
        if (symbol.kind != 98)
            Diagnostics.fatal("label set expected", symbol);
        ActionLabels actionLabels = (ActionLabels) symbol.getAny();
        actionLabels.initContext(paramHashtable1, paramHashtable2);
        int j = 0;
        String str = null;
        while (actionLabels.hasMoreNames()) {
            str = actionLabels.nextName();
            if (j == i)
                break;
            j++;
        }
        actionLabels.clearContext();
        if (j != i)
            Diagnostics.fatal("label set index expression out of range", symbol);
        return str;
    }

    public static int evaluate(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Stack stack = (Stack) paramStack.clone();
        return eval(stack, paramHashtable1, paramHashtable2);
    }

    public static Value getValue(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Stack stack = (Stack) paramStack.clone();
        return getVal(stack, paramHashtable1, paramHashtable2);
    }

    private static Value getVal(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        String str = labelVar(paramStack, paramHashtable1, paramHashtable2);
        if (str != null)
            return new Value(str);
        return new Value(eval(paramStack, paramHashtable1, paramHashtable2));
    }

    private static int eval(Stack paramStack, Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Value value1, value2, value3, value4;
        Symbol symbol = paramStack.pop();
        switch (symbol.kind) {
            case 25 :
                return symbol.intValue();
            case 124 :
                if (paramHashtable1 == null)
                    Diagnostics.fatal("no variables defined", symbol);
                value1 = (Value) paramHashtable1.get(symbol.toString());
                if (value1 == null)
                    Diagnostics.fatal("variable not defined- " + symbol, symbol);
                if (value1.isLabel())
                    Diagnostics.fatal("not integer variable- " + symbol, symbol);
                return value1.intValue();
            case 123 :
                value2 = null;
                if (paramHashtable2 != null)
                    value2 = (Value) paramHashtable2.get(symbol.toString());
                if (value2 == null)
                    value2 = (Value) constants.get(symbol.toString());
                if (value2 == null)
                    Diagnostics.fatal("constant or parameter not defined- " + symbol, symbol);
                if (value2.isLabel())
                    Diagnostics.fatal("not integer constant or parameter- " + symbol, symbol);
                return value2.intValue();
            case 73 :
                return countSet(paramStack.pop(), paramHashtable1, paramHashtable2);
            case 30 :
            case 31 :
            case 32 :
            case 33 :
            case 34 :
            case 35 :
            case 40 :
            case 41 :
            case 42 :
            case 43 :
            case 44 :
            case 46 :
            case 47 :
            case 48 :
            case 49 :
            case 50 :
            case 51 :
            case 52 :
                value3 = getVal(paramStack, paramHashtable1, paramHashtable2);
                value4 = getVal(paramStack, paramHashtable1, paramHashtable2);
                if (value3.isInt() && value4.isInt())
                    return exec_op(symbol.kind, value4.intValue(), value3.intValue());
                if (symbol.kind == 52 || symbol.kind == 44) {
                    if (symbol.kind == 52)
                        return value4.toString().equals(value3.toString()) ? 1 : 0;
                    return value4.toString().equals(value3.toString()) ? 0 : 1;
                }
                Diagnostics.fatal("invalid expression", symbol);
            case 29 :
                return eval(paramStack, paramHashtable1, paramHashtable2);
            case 28 :
                return -eval(paramStack, paramHashtable1, paramHashtable2);
            case 45 :
                return (eval(paramStack, paramHashtable1, paramHashtable2) > 0) ? 0 : 1;
        }
        Diagnostics.fatal("invalid expression", symbol);
        return 0;
    }

    private static int exec_op(int paramInt1, int paramInt2, int paramInt3) {
        switch (paramInt1) {
            case 30 :
                return paramInt2 + paramInt3;
            case 31 :
                return paramInt2 - paramInt3;
            case 32 :
                return paramInt2 * paramInt3;
            case 33 :
                return paramInt2 / paramInt3;
            case 34 :
                return paramInt2 % paramInt3;
            case 35 :
                return paramInt2 ^ paramInt3;
            case 43 :
                return paramInt2 & paramInt3;
            case 41 :
                return paramInt2 | paramInt3;
            case 48 :
                return paramInt2 << paramInt3;
            case 51 :
                return paramInt2 >> paramInt3;
            case 47 :
                return (paramInt2 < paramInt3) ? 1 : 0;
            case 46 :
                return (paramInt2 <= paramInt3) ? 1 : 0;
            case 50 :
                return (paramInt2 > paramInt3) ? 1 : 0;
            case 49 :
                return (paramInt2 >= paramInt3) ? 1 : 0;
            case 52 :
                return (paramInt2 == paramInt3) ? 1 : 0;
            case 44 :
                return (paramInt2 != paramInt3) ? 1 : 0;
            case 42 :
                return (paramInt2 != 0 && paramInt3 != 0) ? 1 : 0;
            case 40 :
                return (paramInt2 != 0 || paramInt3 != 0) ? 1 : 0;
        }
        return 0;
    }
}
