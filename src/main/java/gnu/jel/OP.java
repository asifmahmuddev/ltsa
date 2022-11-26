package gnu.jel;

abstract class OP {
    protected OP next = null;
    protected OP prev = null;

    abstract void compile(ExpressionImage paramExpressionImage);

    public OP next() {
        return this.next;
    }

    boolean optimize(OPlist paramOPlist) throws Throwable {
        return false;
    }
}
