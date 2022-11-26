package gnu.jel;

abstract class OP_unary_primitive extends OP_function {
    boolean canInterpret() {
        return this.prev instanceof OP_load;
    }

    abstract void doOperation(OP_load paramOP_load) throws Throwable;

    void interpret(OPlist paramOPlist) throws Throwable {
        OP_load oP_load = (OP_load) this.prev;
        doOperation(oP_load);
        paramOPlist.remove(this);
    }
}
