package gnu.jel;

class OP_load_array extends OP_function {
    boolean canInterpret() {
        return false;
    }

    void compile(ExpressionImage paramExpressionImage) {
        paramExpressionImage.asm_get_array_element();
    }

    void interpret(OPlist paramOPlist) {
    }

    public String toString() {
        return "{}";
    }
}
