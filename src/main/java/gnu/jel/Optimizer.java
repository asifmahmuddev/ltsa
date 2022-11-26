package gnu.jel;

import gnu.jel.debug.Tester;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

public class Optimizer {
    static Class string_class = null;
    private Library lib;
    private Stack types = new Stack();
    protected OPlist code = new OPlist();
    private Stack functions = new Stack();
    private Stack functionsDescriptors = new Stack();
    private boolean finished = false;
    static {
        try {
            string_class = Class.forName("java.lang.String");
        } catch (ClassNotFoundException classNotFoundException) {
        }
    }

    public Optimizer(Library paramLibrary) {
        this.lib = paramLibrary;
    }

    public void binaryOP(int paramInt, boolean paramBoolean) throws IllegalStateException {
        seeNonFinished();
        OP_binary oP_binary1 = this.functions.peek();
        int[] arrayOfInt = this.functionsDescriptors.peek();
        OP_param oP_param = new OP_param(oP_binary1, this.types.peek());
        oP_param.setbinaryparam = false;
        this.code.addLast(oP_param);
        Class clazz1 = (Class) this.types.pop();
        Class clazz2 = (Class) this.types.pop();
        String str = null;
        boolean bool = false;
        if (!paramBoolean) {
            if (!ExpressionImage.canGenerateBinary(paramInt, clazz2, clazz1)) {
                bool = true;
                str = "Types " + clazz2.toString() + " and " + clazz1.toString() + " are not suitable " + "for the operation \"" + ExpressionImage.binaryNames[paramInt] + "\"";
            }
        } else if (clazz2 != boolean.class || clazz1 != boolean.class) {
            bool = true;
            str = "Types " + clazz2.toString() + " and " + clazz1.toString() + " are not suitable for \"" + ExpressionImage.logicalNames[paramInt] + "\" operation. Both operands must be boolean.";
        }
        if (bool) {
            this.types.push(clazz2);
            this.types.push(clazz1);
            throw new IllegalStateException(str);
        }
        Class clazz3 = boolean.class;
        Class clazz4 = boolean.class;
        if (!paramBoolean) {
            if (ExpressionImage.isPromotionBinary(paramInt)) {
                if (paramInt == 0 && clazz2 == string_class) {
                    clazz3 = string_class;
                } else {
                    clazz3 = ExpressionImage.getBinaryPromoted(clazz2, clazz1);
                }
            } else {
                clazz3 = clazz4 = ExpressionImage.getUnaryPromoted(clazz2);
            }
            clazz4 = clazz3;
            if (paramInt >= 8 && paramInt <= 13)
                clazz4 = boolean.class;
        }
        OP_binary oP_binary2 = oP_binary1;
        this.code.addLast(oP_binary2);
        oP_binary2.setOperation(this.code, paramInt, paramBoolean, clazz3);
        this.types.push(clazz4);
        this.functions.pop();
        this.functionsDescriptors.pop();
    }

    public void binaryOP_param() {
        seeNonFinished();
        OP_binary oP_binary = new OP_binary();
        this.functions.push(oP_binary);
        int[] arrayOfInt = new int[1];
        this.functionsDescriptors.push(arrayOfInt);
        this.code.addLast(new OP_param(oP_binary, this.types.peek()));
    }

    public CompiledExpression compile() {
        return compileBits().getExpression();
    }

    public ExpressionBits compileBits() {
        seeFinished();
        ExpressionImage expressionImage = new ExpressionImage();
        this.code.compile(expressionImage);
        expressionImage.asm_return();
        return expressionImage.getBits();
    }

    public void conditional_end() throws IllegalStateException {
        seeNonFinished();
        OP_conditional oP_conditional = this.functions.peek();
        int[] arrayOfInt = this.functionsDescriptors.peek();
        arrayOfInt[0] = arrayOfInt[0] + 1;
        for (OP oP = this.code.getLast();; oP = oP.prev) {
            if (oP instanceof OP_start && ((OP_start) oP).getFunction() == oP_conditional) {
                oP_conditional.setFalseList(this.code.cut_end(oP.next));
                this.code.remove(oP);
                Class clazz1 = this.types.pop();
                Class clazz2 = this.types.pop();
                Class clazz3 = null;
                if (clazz2.isPrimitive() && clazz1.isPrimitive()) {
                    clazz3 = ExpressionImage.getBinaryPromoted(clazz2, clazz1);
                } else if (clazz2 == clazz1) {
                    clazz3 = clazz2;
                } else if (ExpressionImage.canConvertByWidening(clazz2, clazz1)) {
                    clazz3 = clazz1;
                } else if (ExpressionImage.canConvertByWidening(clazz1, clazz2)) {
                    clazz3 = clazz2;
                }
                if (clazz3 == null)
                    throw new IllegalStateException("Operands of conditional have types " + clazz2.getName() + " and " + clazz1.getName() + " . These types are not compatible.");
                oP_conditional.setType(clazz3);
                this.types.push(clazz3);
                this.code.addLast(oP_conditional);
                this.functions.pop();
                this.functionsDescriptors.pop();
                return;
            }
        }
    }

    public void conditional_false() {
        seeNonFinished();
        OP_conditional oP_conditional = this.functions.peek();
        int[] arrayOfInt = this.functionsDescriptors.peek();
        arrayOfInt[0] = arrayOfInt[0] + 1;
        for (OP oP = this.code.getLast();; oP = oP.prev) {
            if (oP instanceof OP_start && ((OP_start) oP).getFunction() == oP_conditional) {
                oP_conditional.setTrueList(this.code.cut_end(oP.next));
                return;
            }
        }
    }

    public void conditional_true() throws IllegalStateException {
        if (this.types.pop() != boolean.class)
            throw new IllegalStateException("The first operand of conditional must be of boolean type.");
        OP_conditional oP_conditional = new OP_conditional();
        this.functions.push(oP_conditional);
        int[] arrayOfInt = new int[1];
        this.functionsDescriptors.push(arrayOfInt);
        this.code.addLast(new OP_start(oP_conditional));
    }

    public void convert(Class paramClass) throws IllegalStateException {
        convert(paramClass, false);
    }

    public void convert(Class paramClass, boolean paramBoolean) throws IllegalStateException {
        seeNonFinished();
        Class clazz = this.types.peek();
        if (!ExpressionImage.canConvert(clazz, paramClass))
            throw new IllegalStateException("Can not convert " + clazz.toString() + " to " + paramClass.toString() + ".");
        if (paramBoolean && !ExpressionImage.canConvertByWidening(clazz, paramClass))
            throw new IllegalStateException("You must specify narrowing conversion from " + clazz.toString() + " to " + paramClass.toString() + " explicitly");
        if (clazz != paramClass) {
            OP oP = this.code.getLast();
            if (oP instanceof OP_convert) {
                ((OP_convert) oP).setType(paramClass);
            } else {
                this.code.addLast(new OP_convert(paramClass));
            }
        }
        this.types.pop();
        this.types.push(paramClass);
    }

    public void finish() {
        seeNonFinished();
        this.finished = true;
    }

    public void function_call(String paramString) throws IllegalStateException {
        Object object;
        seeNonFinished();
        OP_call oP_call1 = this.functions.peek();
        int[] arrayOfInt = this.functionsDescriptors.peek();
        int i = arrayOfInt[0];
        Class[] arrayOfClass = new Class[i];
        for (int j = i - 1; j >= 0; j--)
            arrayOfClass[j] = this.types.pop();
        try {
            object = this.lib.getMethod(paramString, arrayOfClass);
        } catch (NoSuchMethodException noSuchMethodException) {
            for (byte b = 0; b < i; b++)
                this.types.push(arrayOfClass[b]);
            throw new IllegalStateException(noSuchMethodException.getMessage());
        }
        OP_call oP_call2 = oP_call1;
        this.code.addLast(oP_call2);
        if (object instanceof Method) {
            oP_call2.setMethod(this.code, (Method) object, this.lib.getDynamicMethodClassID(object), this.lib.isStateless(object));
            this.types.push(((Method) object).getReturnType());
        } else {
            oP_call2.setField(this.code, (Field) object, this.lib.getDynamicMethodClassID(object), this.lib.isStateless(object));
            this.types.push(((Field) object).getType());
        }
        this.functions.pop();
        this.functionsDescriptors.pop();
    }

    public boolean function_param() {
        seeNonFinished();
        OP_function oP_function = this.functions.peek();
        int[] arrayOfInt = this.functionsDescriptors.peek();
        arrayOfInt[0] = arrayOfInt[0] + 1;
        this.code.addLast(new OP_param(oP_function, this.types.peek()));
        return true;
    }

    public void function_start() {
        seeNonFinished();
        OP_call oP_call = new OP_call();
        this.functions.push(oP_call);
        int[] arrayOfInt = new int[1];
        this.functionsDescriptors.push(arrayOfInt);
        this.code.addLast(new OP_start(oP_call));
    }

    public void load(byte paramByte) {
        load(byte.class, new Byte(paramByte));
    }

    public void load(char paramChar) {
        load(char.class, new Character(paramChar));
    }

    public void load(double paramDouble) {
        load(double.class, new Double(paramDouble));
    }

    public void load(float paramFloat) {
        load(float.class, new Float(paramFloat));
    }

    public void load(int paramInt) {
        load(int.class, new Integer(paramInt));
    }

    public void load(long paramLong) {
        load(long.class, new Long(paramLong));
    }

    private void load(Class paramClass, Object paramObject) {
        seeNonFinished();
        OP_load oP_load = new OP_load(paramClass, paramObject);
        this.types.push(paramClass);
        this.code.addLast(oP_load);
    }

    public void load(String paramString) {
        load(string_class, paramString);
    }

    public void load(short paramShort) {
        load(short.class, new Short(paramShort));
    }

    public void load(boolean paramBoolean) {
        load(boolean.class, new Boolean(paramBoolean));
    }

    public void load_array() {
        convert(int.class, true);
        Class clazz1 = this.types.pop();
        Class clazz2 = this.types.pop();
        Class clazz = clazz2.getComponentType();
        this.types.push(clazz);
        OP_load_array oP_load_array = new OP_load_array();
        this.code.addLast(oP_load_array);
    }

    public void load_array_param() {
        Class clazz = this.types.peek();
        if (clazz.getComponentType() == null)
            throw new IllegalStateException("The class " + clazz.toString() + " is not an array.");
    }

    public void logical_not() throws IllegalStateException {
        if (this.types.peek() != boolean.class)
            throw new IllegalStateException("You tried to use logical complement on " + ((Class) this.types.peek()).getName() + ". This operation is supported only" + " on booleans.");
        OP_logical_not oP_logical_not = this.functions.pop();
        int[] arrayOfInt = this.functionsDescriptors.pop();
        this.code.addLast(oP_logical_not);
    }

    public void logical_not_start() {
        seeNonFinished();
        OP_logical_not oP_logical_not = new OP_logical_not();
        this.functions.push(oP_logical_not);
        int[] arrayOfInt = new int[1];
        this.functionsDescriptors.push(arrayOfInt);
        this.code.addLast(new OP_start(oP_logical_not));
    }

    public static void main(String[] paramArrayOfString) {
    }

    public void optimize(int paramInt) {
        seeFinished();
        while (paramInt > 0 && optimizeIteration(this.code))
            paramInt--;
    }

    protected static boolean optimizeIteration(OPlist paramOPlist) {
        return paramOPlist.optimize();
    }

    private void seeFinished() {
        if (!this.finished)
            throw new IllegalStateException("Attempt to instantiate unfinished function.");
    }

    private void seeNonFinished() {
        if (this.finished)
            throw new IllegalStateException("Attempt to modify finished function.");
    }

    public static void test(Tester paramTester) {
    }

    public String toString() {
        return this.code.toString();
    }

    public void unary(int paramInt) {
        seeNonFinished();
        Class clazz = this.types.peek();
        if (!ExpressionImage.canGenerateUnary(paramInt, clazz))
            throw new IllegalStateException("Unary " + ExpressionImage.unaryNames[paramInt] + " is not supported on " + clazz.getName() + "'s");
        this.code.addLast(new OP_unary(paramInt));
    }
}
