package gnu.jel;

class OP_param extends OP_start {
    Class typeInStack = null;
    boolean setbinaryparam = true;

    OP_param(OP_function paramOP_function, Class paramClass) {
        super(paramOP_function);
        this.typeInStack = paramClass;
    }

    void compile(ExpressionImage paramExpressionImage) {
        if (this.mf != null) {
            paramExpressionImage.asm_func_param();
        } else if (this.setbinaryparam && this.f instanceof OP_binary) {
            if (((OP_binary) this.f).logical) {
                paramExpressionImage.asm_logical_binary_param(((OP_binary) this.f).opc);
            } else {
                paramExpressionImage.asm_binary_param(((OP_binary) this.f).opc);
            }
        }
    }

    void setConvert(OPlist paramOPlist, Class paramClass) {
        if (paramClass != this.typeInStack) {
            if (this.prev instanceof OP_convert) {
                ((OP_convert) this.prev).setType(paramClass);
            } else {
                paramOPlist.addBefore(this, new OP_convert(paramClass));
            }
            this.typeInStack = paramClass;
        }
    }

    public String toString() {
        return this.setbinaryparam ? "," : ".";
    }
}
