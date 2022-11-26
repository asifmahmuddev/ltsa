package gnu.jel;

import gnu.jel.debug.Debug;

class OP_convert extends OP_unary_primitive {
    Class to;

    OP_convert(Class paramClass) {
        this.to = paramClass;
    }

    void compile(ExpressionImage paramExpressionImage) {
        paramExpressionImage.asm_convert(this.to);
    }

    void doOperation(OP_load paramOP_load) throws Throwable {
        if (paramOP_load.type == this.to)
            return;
        if ((paramOP_load.type == null || paramOP_load.what == null) && !this.to.isPrimitive())
            return;
        boolean bool1 = paramOP_load.type.isPrimitive();
        boolean bool2 = this.to.isPrimitive();
        if (!bool1 && !bool2) {
            Debug.assertTrue(this.to.isAssignableFrom(paramOP_load.type), "Class types not compatible.");
            return;
        }
        if (bool1 && bool2) {
            int i = ExpressionImage.primitiveID(paramOP_load.type);
            int j = ExpressionImage.primitiveID(this.to);
            paramOP_load.what = OP_function.narrow(OP_function.widen(paramOP_load.what, i), j);
            paramOP_load.type = this.to;
            return;
        }
    }

    void setType(Class paramClass) {
        this.to = paramClass;
    }

    public String toString() {
        return "(" + this.to.toString() + ")";
    }
}
