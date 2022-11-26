package gnu.jel;

class OP_binary extends OP_function {
    int opc = 999;
    boolean logical = false;

    boolean canInterpret() {
        return operandsReady(2, false);
    }

    void compile(ExpressionImage paramExpressionImage) {
        if (this.logical) {
            paramExpressionImage.asm_logical_binary(this.opc);
        } else {
            paramExpressionImage.asm_binary(this.opc);
        }
    }

    void interpret(OPlist paramOPlist) throws Throwable {
        if (this.logical) {
            OP_load oP_load3 = (OP_load) this.prev.prev;
            OP_load oP_load4 = (OP_load) this.prev.prev.prev.prev;
            boolean bool1 = ((Boolean) oP_load4.what).booleanValue();
            boolean bool2 = ((Boolean) oP_load3.what).booleanValue();
            switch (this.opc) {
                case 0 :
                    bool1 &= bool2;
                    break;
                case 1 :
                    bool1 |= bool2;
                    break;
            }
            if (bool1) {
                oP_load4.what = Boolean.TRUE;
            } else {
                oP_load4.what = Boolean.FALSE;
            }
            consume(paramOPlist, oP_load4, 2, false);
            return;
        }
        OP_load oP_load1 = (OP_load) this.prev.prev;
        OP_load oP_load2 = (OP_load) this.prev.prev.prev.prev;
        if (this.opc == 0 && oP_load2.type == Optimizer.string_class) {
            oP_load2.what = String.valueOf(oP_load2.what) + String.valueOf(oP_load1.what);
        } else {
            int i = ExpressionImage.primitiveID(oP_load2.type);
            Number number1 = OP_function.widen(oP_load2.what, i);
            Number number2 = OP_function.widen(oP_load1.what, i);
            boolean bool1 = false;
            boolean bool2 = false;
            if (OP_function.isFloat(i)) {
                double d1 = number1.doubleValue();
                double d2 = number2.doubleValue();
                boolean bool = false;
                switch (this.opc) {
                    case 0 :
                        d1 += d2;
                        break;
                    case 1 :
                        d1 -= d2;
                        break;
                    case 2 :
                        d1 *= d2;
                        break;
                    case 3 :
                        d1 /= d2;
                        break;
                    case 4 :
                        d1 %= d2;
                        break;
                    case 5 :
                        bool = true;
                        break;
                    case 6 :
                        bool = true;
                        break;
                    case 7 :
                        bool = true;
                        break;
                    case 8 :
                        bool1 = true;
                        bool2 = (d1 != d2) ? false : true;
                        break;
                    case 9 :
                        bool1 = true;
                        bool2 = (d1 == d2) ? false : true;
                        break;
                    case 10 :
                        bool1 = true;
                        bool2 = (d1 >= d2) ? false : true;
                        break;
                    case 11 :
                        bool1 = true;
                        bool2 = (d1 < d2) ? false : true;
                        break;
                    case 12 :
                        bool1 = true;
                        bool2 = (d1 <= d2) ? false : true;
                        break;
                    case 13 :
                        bool1 = true;
                        bool2 = (d1 > d2) ? false : true;
                        break;
                    default :
                        bool = true;
                        break;
                }
                if (!bool1) {
                    number1 = new Double(d1);
                } else if (bool2) {
                    number1 = new Long(1L);
                } else {
                    number1 = new Long(0L);
                }
            } else {
                long l1 = number1.longValue();
                long l2 = number2.longValue();
                switch (this.opc) {
                    case 0 :
                        l1 += l2;
                        break;
                    case 1 :
                        l1 -= l2;
                        break;
                    case 2 :
                        l1 *= l2;
                        break;
                    case 3 :
                        l1 /= l2;
                        break;
                    case 4 :
                        l1 %= l2;
                        break;
                    case 5 :
                        l1 &= l2;
                        break;
                    case 6 :
                        l1 |= l2;
                        break;
                    case 7 :
                        l1 ^= l2;
                        break;
                    case 8 :
                        bool1 = true;
                        l1 = (l1 == l2) ? 1L : 0L;
                        break;
                    case 9 :
                        bool1 = true;
                        l1 = (l1 != l2) ? 1L : 0L;
                        break;
                    case 10 :
                        bool1 = true;
                        l1 = (l1 < l2) ? 1L : 0L;
                        break;
                    case 11 :
                        bool1 = true;
                        l1 = (l1 >= l2) ? 1L : 0L;
                        break;
                    case 12 :
                        bool1 = true;
                        l1 = (l1 > l2) ? 1L : 0L;
                        break;
                    case 13 :
                        bool1 = true;
                        l1 = (l1 <= l2) ? 1L : 0L;
                        break;
                    case 14 :
                        l1 <<= (int) l2;
                        break;
                    case 15 :
                        l1 >>= (int) l2;
                        break;
                    case 16 :
                        if (i == 4) {
                            l1 = ((int) l1 >>> (int) l2);
                            break;
                        }
                        l1 >>>= (int) l2;
                        break;
                }
                number1 = new Long(l1);
            }
            if (bool1) {
                oP_load2.what = OP_function.narrow(number1, 0);
                oP_load2.type = boolean.class;
            } else {
                oP_load2.what = OP_function.narrow(number1, i);
            }
        }
        consume(paramOPlist, oP_load2, 2, false);
    }

    void setOperation(OPlist paramOPlist, int paramInt, boolean paramBoolean, Class paramClass) {
        this.logical = paramBoolean;
        this.opc = paramInt;
        if (paramClass != Optimizer.string_class) {
            byte b = 2;
            for (OP oP = this.prev; oP != null && b >= 0; oP = oP.prev) {
                if (oP instanceof OP_param) {
                    OP_param oP_param = (OP_param) oP;
                    if (oP_param.getFunction() == this)
                        oP_param.setConvert(paramOPlist, paramClass);
                }
            }
        }
    }

    public String toString() {
        return this.logical ? ExpressionImage.logicalSymbols[this.opc] : ExpressionImage.binarySymbols[this.opc];
    }
}
