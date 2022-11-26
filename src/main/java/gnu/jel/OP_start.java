package gnu.jel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class OP_start extends OP {
    protected OP_function f = null;
    protected Object mf = null;
    protected int id = 999;

    OP_start(OP_function paramOP_function) {
        this.f = paramOP_function;
    }

    void compile(ExpressionImage paramExpressionImage) {
        if (this.mf != null)
            if (this.mf instanceof Method) {
                paramExpressionImage.asm_func_start((Method) this.mf, this.id);
            } else {
                paramExpressionImage.asm_load_field((Field) this.mf, this.id);
            }
        if (this.f instanceof OP_logical_not)
            paramExpressionImage.asm_logical_block();
    }

    OP_function getFunction() {
        return this.f;
    }

    void setMethod(Object paramObject, int paramInt) {
        this.mf = paramObject;
        this.id = paramInt;
    }

    public String toString() {
        return "[";
    }
}
