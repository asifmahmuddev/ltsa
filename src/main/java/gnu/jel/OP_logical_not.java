package gnu.jel;

class OP_logical_not extends OP_function {
    boolean canInterpret() {
        return !(!(this.prev instanceof OP_load) || !(this.prev.prev instanceof OP_start));
    }

    void compile(ExpressionImage paramExpressionImage) {
        paramExpressionImage.asm_logical_unblock_not();
    }

    void interpret(OPlist paramOPlist) throws Throwable {
        OP_load oP_load = (OP_load) this.prev;
        if (((Boolean) oP_load.what).booleanValue()) {
            oP_load.what = Boolean.FALSE;
        } else {
            oP_load.what = Boolean.TRUE;
        }
        paramOPlist.remove(this.prev.prev);
        paramOPlist.remove(this);
    }

    public String toString() {
        return "!]";
    }
}
