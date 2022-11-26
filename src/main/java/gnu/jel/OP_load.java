package gnu.jel;

class OP_load extends OP {
    Class type;
    Object what;
    static final char[] typecodes = new char[]{'Z', 'B', 'C', 'S', 'I', 'J', 'F', 'D'};

    OP_load(Class paramClass, Object paramObject) {
        this.type = paramClass;
        this.what = paramObject;
    }

    void compile(ExpressionImage paramExpressionImage) {
        if (this.type == null || this.what == null) {
            paramExpressionImage.asm_load_object(null);
            return;
        }
        if (this.type.isPrimitive()) {
            paramExpressionImage.asm_load_primitive(this.what);
        } else {
            paramExpressionImage.asm_load_object(this.what);
        }
    }

    public String toString() {
        return this.type.isPrimitive()
            ? (String.valueOf(this.what.toString()) + typecodes[ExpressionImage.primitiveID(this.type)])
            : ((this.what instanceof String) ? ("\"" + this.what + "\"") : this.what.toString());
    }
}
