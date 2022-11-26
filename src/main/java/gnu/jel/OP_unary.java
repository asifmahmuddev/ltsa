package gnu.jel;

class OP_unary extends OP_unary_primitive {
    int opc;

    OP_unary(int paramInt) {
        this.opc = paramInt;
    }

    void compile(ExpressionImage paramExpressionImage) {
        paramExpressionImage.asm_unary(this.opc);
    }

    void doOperation(OP_load paramOP_load) throws Throwable {
        int i = ExpressionImage.primitiveID(paramOP_load.type);
        Class clazz = ExpressionImage.getUnaryPromoted(paramOP_load.type);
        int j = ExpressionImage.primitiveID(clazz);
        Number number = OP_function.widen(paramOP_load.what, i);
        switch (this.opc) {
            case 0 :
                if (OP_function.isFloat(i)) {
                    number = new Double(-number.doubleValue());
                    break;
                }
                number = new Long(-number.longValue());
                break;
            case 1 :
                number = new Long(number.longValue() ^ 0xFFFFFFFFFFFFFFFFL);
                break;
        }
        paramOP_load.what = OP_function.narrow(number, j);
        paramOP_load.type = clazz;
    }

    public String toString() {
        return ExpressionImage.unarySymbols[this.opc];
    }
}
