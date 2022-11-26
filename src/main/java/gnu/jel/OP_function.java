package gnu.jel;

abstract class OP_function extends OP {
    abstract boolean canInterpret();

    void consume(OPlist paramOPlist, OP_load paramOP_load, int paramInt, boolean paramBoolean) {
        int i = paramInt * 2 + 1;
        if (paramBoolean)
            i++;
        OP oP = this.next;
        OP_function oP_function = this;
        for (byte b = 0; b < i; b++) {
            OP oP2 = oP_function.prev;
            paramOPlist.remove(oP_function);
            OP oP1 = oP2;
        }
        if (oP != null) {
            paramOPlist.addBefore(oP, paramOP_load);
        } else {
            paramOPlist.addLast(paramOP_load);
        }
    }

    abstract void interpret(OPlist paramOPlist) throws Throwable;

    static boolean isFloat(int paramInt) {
        return !(paramInt <= 5);
    }

    static Object narrow(Number paramNumber, int paramInt) {
        switch (paramInt) {
            case 0 :
                return (paramNumber.longValue() != 0L) ? Boolean.TRUE : Boolean.FALSE;
            case 1 :
                return new Byte(paramNumber.byteValue());
            case 2 :
                return new Character((char) (int) paramNumber.longValue());
            case 3 :
                return new Short(paramNumber.shortValue());
            case 4 :
                return new Integer(paramNumber.intValue());
            case 5 :
                return new Long(paramNumber.longValue());
            case 6 :
                return new Float(paramNumber.floatValue());
            case 7 :
                return new Double(paramNumber.doubleValue());
        }
        return null;
    }

    boolean operandsReady(int paramInt, boolean paramBoolean) {
        boolean bool = true;
        OP oP = this.prev;
        for (byte b = 0; b < paramInt && bool; b++) {
            bool = (!(oP instanceof OP_param) || !(oP.prev instanceof OP_load)) ? false : true;
            oP = oP.prev.prev;
        }
        if (paramBoolean)
            bool = (!bool || !(oP instanceof OP_start)) ? false : true;
        return bool;
    }

    public boolean optimize(OPlist paramOPlist) throws Throwable {
        boolean bool = canInterpret();
        if (bool)
            interpret(paramOPlist);
        return bool;
    }

    static Number widen(Object paramObject, int paramInt) {
        switch (paramInt) {
            case 0 :
                return ((Boolean) paramObject).booleanValue() ? new Long(1L) : new Long(0L);
            case 1 :
                return (Number) paramObject;
            case 2 :
                return new Long(((Character) paramObject).charValue());
            case 3 :
            case 4 :
            case 5 :
            case 6 :
            case 7 :
                return (Number) paramObject;
        }
        return new Long(0L);
    }
}
