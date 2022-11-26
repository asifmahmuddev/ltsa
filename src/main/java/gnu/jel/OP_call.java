package gnu.jel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class OP_call extends OP_function {
    Method m = null;
    Field f = null;
    int nargs = 999;
    Class[] args = null;
    boolean interpretable = false;

    boolean canInterpret() {
        return !(!this.interpretable || !operandsReady(this.nargs, true));
    }

    void compile(ExpressionImage paramExpressionImage) {
        if (this.m != null)
            paramExpressionImage.asm_func_call();
    }

    private void fix_OP_start(OP paramOP, Object paramObject, int paramInt) {
        OP_start oP_start = null;
        while (paramOP != null && oP_start == null) {
            if (paramOP instanceof OP_start) {
                oP_start = (OP_start) paramOP;
                if (oP_start.getFunction() != this)
                    oP_start = null;
            }
            paramOP = paramOP.prev;
        }
        oP_start.setMethod(paramObject, paramInt);
    }

    void interpret(OPlist paramOPlist) throws Throwable {
        Object object;
        Class clazz;
        Object[] arrayOfObject = new Object[this.nargs];
        OP oP = this.prev;
        int i = this.nargs - 1;
        while (i >= 0) {
            arrayOfObject[i--] = ((OP_load) oP.prev).what;
            oP = oP.prev.prev;
        }
        if (this.m != null) {
            object = this.m.invoke(null, arrayOfObject);
            clazz = this.m.getReturnType();
        } else {
            object = this.f.get(null);
            clazz = this.f.getType();
        }
        OP_load oP_load = new OP_load(clazz, object);
        consume(paramOPlist, oP_load, this.nargs, true);
    }

    void setField(OPlist paramOPlist, Field paramField, int paramInt, boolean paramBoolean) {
        this.f = paramField;
        this.interpretable = paramBoolean;
        this.nargs = 0;
        fix_OP_start(this.prev, paramField, paramInt);
    }

    void setMethod(OPlist paramOPlist, Method paramMethod, int paramInt, boolean paramBoolean) {
        this.args = paramMethod.getParameterTypes();
        this.nargs = this.args.length;
        this.interpretable = paramBoolean;
        this.m = paramMethod;
        int i = this.args.length - 1;
        OP oP;
        for (oP = this.prev; oP != null && i >= 0; oP = oP.prev) {
            if (oP instanceof OP_param) {
                OP_param oP_param = (OP_param) oP;
                if (oP_param.getFunction() == this) {
                    oP_param.setConvert(paramOPlist, this.args[i--]);
                    oP_param.setMethod(paramMethod, paramInt);
                }
            }
        }
        fix_OP_start(oP, paramMethod, paramInt);
    }

    public String toString() {
        return (this.m != null) ? (String.valueOf(this.m.getName()) + "]") : (String.valueOf(this.f.getName()) + "&]");
    }
}
