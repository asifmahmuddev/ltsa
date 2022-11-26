package gnu.jel;

import gnu.jel.debug.Tester;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EmptyStackException;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class ExpressionImage {
    public static final int BI_PL = 0;
    public static final int BI_MI = 1;
    public static final int BI_MU = 2;
    public static final int BI_DI = 3;
    public static final int BI_RE = 4;
    public static final int BI_AN = 5;
    public static final int BI_OR = 6;
    public static final int BI_XO = 7;
    public static final int BI_EQ = 8;
    public static final int BI_NE = 9;
    public static final int BI_LT = 10;
    public static final int BI_GE = 11;
    public static final int BI_GT = 12;
    public static final int BI_LE = 13;
    public static final int BI_LS = 14;
    public static final int BI_RSS = 15;
    public static final int BI_RUS = 16;
    public static final String[] binaryNames = new String[]{"add", "substract", "multiply", "divide", "remainder", "bitwise and", "bitwise or", "bitwise xor", "equal", "not equal", "less",
        "greater or equal", "greater", "less or equal", "left shift", "signed right shift", "unsigned right shift"};
    public static final String[] binarySymbols = new String[]{"+", "-", "*", "/", "%", "&", "|", "^", "==", "!=", "<", ">=", ">", "<=", "<<", ">>", ">>>"};
    public static final int LOG_AN = 0;
    public static final int LOG_OR = 1;
    public static final int LOG_NO = 2;
    public static final String[] logicalNames = new String[]{"logical and", "logical or", "logical not"};
    public static final String[] logicalSymbols = new String[]{"&&", "||", "!"};
    public static final int UN_NE = 0;
    public static final int UN_NO = 1;
    public static final String[] unaryNames = new String[]{"negate", "bitwise complement"};
    public static final String[] unarySymbols = new String[]{"-", "~"};
    private static final byte[] prologue = new byte[]{-54, -2, -70, -66, 3, 45};
    private int poolEntries = 1;
    private static final byte[] cp_middle = new byte[]{7, 1, 1, 26, 103, 110, 117, 47, 106, 101, 108, 47, 67, 111, 109, 112, 105, 108, 101, 100, 69, 120, 112, 114, 101, 115, 115, 105, 111, 110, 7, 3,
        1, 1, 101, 1, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 6, 60, 105, 110, 105, 116, 62, 1, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110,
        103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 86, 1, 10, 69, 120, 99, 101, 112, 116, 105, 111, 110, 115, 1, 4, 67, 111, 100, 101, 1, 3, 40, 41, 86, 12, 7, 11, 10, 4, 12, 12, 5, 6, 9, 2, 14, 1,
        7, 103, 101, 116, 84, 121, 112, 101, 1, 3, 40, 41, 73, 1, 19, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 84, 104, 114, 111, 119, 97, 98, 108, 101, 7, 18};
    private static final byte[] intermezzo1 = new byte[]{0, 33, 2, 4, 1, 2, 5, 6, 3, 1, 7, 8, 2, 9, 2, 10, 22, 2, 2, 10, 42, -73, 13, 42, 43, -75, 15, -79, 1, 16, 17, 2, 9, 2, 10, 15, 1, 1, 3, 16};
    private byte thetype = 99;
    private static final byte[] intermezzo2 = new byte[]{-84, 1};
    short nameIdx = 9999;
    short signIdx = 9999;
    private static final byte[] intermezzo3 = new byte[]{0, 2, 9, 4, 1, 19, 10};
    int max_stack = 2;
    int max_locals = 2;
    private static final byte[] konetc = new byte[6];
    public static final Class[] primitiveTypes = new Class[]{boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class, void.class};
    private static final char[] primitiveCodes = new char[]{'Z', 'B', 'C', 'S', 'I', 'J', 'F', 'D', 'V'};
    public static final String[] primitiveTypeNames = new String[]{"boolean", "byte", "char", "short", "int", "long", "float", "double"};
    private static Class sb_class = null;
    private static Class tsb_class = null;
    private static Class string_class = null;
    private static Constructor sb_constructor = null;
    private static Method[] sb_append_primitive = new Method[primitiveTypes.length - 1];
    private static Method sb_append_String = null;
    private static Method sb_append_Object = null;
    private static Method sb_toString = null;
    private static final byte[] returns = new byte[]{-84, -84, -84, -84, -84, -83, -82, -81};
    private static final byte[] cvt_wide = new byte[]{Byte.MIN_VALUE, 64, 96, 80, 120, 92, 94, 95};
    private static final int[][] cvt1 = new int[][]{{0, 255, 255, 255, 255, 255, 255, 255}, {145, 145, 145, 145, 136, 139, 142}, {146, 146, 146, 146, 136, 139, 142},
        {147, 147, 147, 147, 136, 139, 142}, {0, 0, 0, 0, 0, 136, 139, 142}, {133, 133, 133, 133, 133, 140, 143}, {134, 134, 134, 134, 134, 137, 144}, {135, 135, 135, 135, 135, 138, 141}};
    private static final int[][] cvt2 = new int[][]{new int[8], {0, 0, 0, 0, 0, 145, 145, 145}, {0, 0, 0, 0, 0, 146, 146, 146}, {0, 0, 0, 0, 0, 147, 147, 147}, new int[8], new int[8], new int[8],
        new int[8]};
    private static final byte[] stkoccup = new byte[]{1, 1, 1, 1, 1, 2, 1, 2};
    private static final int[] load_ints = new int[]{2, 3, 4, 5, 6, 7, 8};
    private static final int[] load_long_ints1 = new int[]{2, 9, 10, 5, 6, 7, 8};
    private static final int[] load_long_ints2 = new int[]{133, 133, 133, 133, 133};
    private static final int[][] binary_promotions = new int[][]{{0, 255, 255, 255, 255, 255, 255, 255}, {255, 4, 4, 4, 4, 5, 6, 7}, {255, 4, 4, 4, 4, 5, 6, 7}, {255, 4, 4, 4, 4, 5, 6, 7},
        {255, 4, 4, 4, 4, 5, 6, 7}, {255, 5, 5, 5, 5, 5, 6, 7}, {255, 6, 6, 6, 6, 6, 6, 7}, {255, 7, 7, 7, 7, 7, 7, 7}};
    private static final int[] unary_promotions = new int[]{255, 4, 4, 4, 4, 5, 6, 7};
    private static final int[][][] ops = new int[][][]{{{255, 96, 255, 96, 96, 97, 98, 99}, {255, 100, 255, 100, 100, 101, 102, 103}, {255, 104, 255, 104, 104, 105, 106, 107},
        {255, 108, 255, 108, 108, 109, 110, 111}, {255, 112, 255, 112, 112, 113, 114, 115}, {126, 126, 255, 126, 126, 127, 255, 255}, {128, 128, 255, 128, 128, 129, 255, 255},
        {130, 130, 255, 130, 130, 131, 255, 255}, {0, 0, 0, 0, 0, 148, 150, 152}, {0, 0, 0, 0, 0, 148, 150, 152}, {0, 0, 0, 0, 0, 148, 150, 152}, {0, 0, 0, 0, 0, 148, 149, 151},
        {0, 0, 0, 0, 0, 148, 149, 151}, {0, 0, 0, 0, 0, 148, 150, 152}, {120, 120, 120, 120, 120, 121, 255, 255}, {122, 122, 122, 122, 122, 123, 255, 255}, {124, 124, 124, 124, 124, 125, 255, 255}}};
    private static final boolean[] is_promotion_binary = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true};
    private static final int[][] openjumps = new int[][]{new int[8], new int[8], new int[8], new int[8], new int[8], new int[8], new int[8], new int[8], {159, 159, 159, 159, 159, 153, 153, 153},
        {160, 160, 160, 160, 160, 154, 154, 154}, {161, 161, 161, 161, 161, 155, 155, 155}, {162, 162, 162, 162, 162, 156, 156, 156}, {163, 163, 163, 163, 163, 157, 157, 157},
        {164, 164, 164, 164, 164, 158, 158, 158}, new int[8], new int[8], new int[8]};
    private static final int[] norm = new int[]{0, 145, 146, 147};
    private static final int[][][] una = new int[][][]{{{116, 116, 255, 116, 116, 117, 118, 119}, {255, 2, 2, 2, 2, 2, 255, 255}}, {new int[8], {0, 130, 130, 130, 130, 133}},
        {new int[8], {0, 0, 0, 0, 0, 131}}};
    private static final int[][] una_excess_stack = new int[][]{new int[8], {0, 1, 1, 1, 1, 2}};
    private static final byte[] stor = new byte[]{54, 54, 54, 54, 54, 55, 56, 57, 58};
    private static final byte[] load = new byte[]{21, 21, 21, 21, 21, 22, 23, 24, 25};
    private static final byte[] arrload = new byte[]{51, 51, 52, 53, 46, 47, 48, 49, 50};
    private static final byte CONSTANT_Class = 7;
    private static final byte CONSTANT_Fieldref = 9;
    private static final byte CONSTANT_Methodref = 10;
    private static final byte CONSTANT_InterfaceMethodref = 11;
    private static final byte CONSTANT_String = 8;
    private static final byte CONSTANT_Integer = 3;
    private static final byte CONSTANT_Float = 4;
    private static final byte CONSTANT_Long = 5;
    private static final byte CONSTANT_Double = 6;
    private static final byte CONSTANT_NameAndType = 12;
    private static final byte CONSTANT_Utf8 = 1;
    private ByteArrayOutputStream constPool = new ByteArrayOutputStream();
    private DataOutputStream constPoolData = new DataOutputStream(this.constPool);
    private Hashtable Items = new Hashtable();
    private Hashtable UTFs = new Hashtable();
    private PatchableByteArrayOutputStream methodText = new PatchableByteArrayOutputStream();
    private DataOutputStream methodTextData = new DataOutputStream(this.methodText);
    private boolean classFinished = false;
    private int currSSW = 0;
    private Vector objectPool = new Vector();
    private Stack typesStk = new Stack();
    int jump_progress = 0;
    private Stack functionParams = new Stack();
    private Stack functionINTS = new Stack();
    private Stack functionRet = new Stack();
    private IntegerStack jumps0 = new IntegerStack();
    private IntegerStack jumps1 = new IntegerStack();
    private IntegerStack jumps = new IntegerStack();
    private IntegerStack blocks0 = new IntegerStack();
    private IntegerStack blocks1 = new IntegerStack();
    private IntegerStack branchStack = new IntegerStack();
    private boolean invert_next_jump = false;

    public ExpressionImage() {
        this.classFinished = false;
        this.poolEntries++;
        try {
            this.constPoolData.write(cp_middle);
            this.poolEntries += 18;
        } catch (IOException iOException) {
        }
    }

    private static void appendParametersSignature(StringBuffer paramStringBuffer, Class[] paramArrayOfClass) {
        paramStringBuffer.append('(');
        for (byte b = 0; b < paramArrayOfClass.length; b++)
            paramStringBuffer.append(getSignature(paramArrayOfClass[b]));
        paramStringBuffer.append(')');
    }

    public void asm_binary(int paramInt) {
        ensure_value();
        Class clazz1 = this.typesStk.peek();
        Object object = this.typesStk.pop();
        Class clazz2 = this.typesStk.peek();
        this.typesStk.push(object);
        if (paramInt == 0 && clazz2 == tsb_class) {
            codeOP(182);
            if (clazz1.isPrimitive()) {
                codeINDEX(getMethodIndex(sb_append_primitive[primitiveID(clazz1)]));
            } else if (clazz1 == string_class) {
                codeINDEX(getMethodIndex(sb_append_String));
            } else {
                codeINDEX(getMethodIndex(sb_append_Object));
            }
            this.typesStk.pop();
            this.currSSW -= stackSpace(clazz1);
            return;
        }
        if (!clazz2.isPrimitive() || !clazz1.isPrimitive())
            return;
        int i = primitiveID(clazz2);
        int j = primitiveID(clazz1);
        int k = i;
        if (is_promotion_binary[paramInt] && i != j)
            return;
        if (!is_promotion_binary[paramInt]) {
            k = unary_promotions[i];
            asm_convert(int.class);
            clazz1 = this.typesStk.peek();
            j = primitiveID(clazz1);
        }
        if (ops[0][paramInt][k] == 255)
            return;
        this.typesStk.pop();
        this.currSSW -= stkoccup[j];
        this.typesStk.pop();
        this.currSSW -= stkoccup[i];
        for (byte b = 0; b < ops.length; b++) {
            int m = ops[b][paramInt][k];
            if (m > 0)
                codeOP(m);
        }
        this.jump_progress = openjumps[paramInt][k];
        if (this.jump_progress == 0) {
            this.typesStk.push(primitiveTypes[k]);
            this.currSSW += stkoccup[k];
        }
    }

    public void asm_binary_param(int paramInt) {
        ensure_value();
        if (paramInt == 0 && this.typesStk.peek() == string_class) {
            codeOP(187);
            codeINDEX(getClassIndex(sb_class));
            codeOP(90);
            codeOP(90);
            codeOP(87);
            codeOP(183);
            codeINDEX(getMethodIndex(sb_constructor));
            this.typesStk.pop();
            this.typesStk.push(tsb_class);
            this.currSSW += 3;
            ensureStack();
            this.currSSW -= 3;
        }
    }

    public void asm_branch_end() {
        ensure_value();
        landLabel(this.jumps);
    }

    public void asm_branch_start_false() {
        ensure_value();
        unblockLabels(this.jumps0, this.blocks0);
        int i = this.branchStack.pop();
        while (i < this.typesStk.size())
            this.currSSW -= stackSpace(this.typesStk.pop());
        codeOP(167);
        mkLabel(this.jumps);
        landLabels(this.jumps0, this.blocks0);
    }

    public void asm_branch_start_true() {
        ensure_jump();
        if (!this.invert_next_jump)
            this.jump_progress = invert_jump_bytecode(this.jump_progress);
        this.invert_next_jump = false;
        codeOP(this.jump_progress);
        this.jump_progress = 0;
        mkLabel(this.jumps0);
        landLabels(this.jumps1, this.blocks1);
        blockLabels(this.jumps0, this.blocks0);
        this.branchStack.push(this.typesStk.size());
    }

    public boolean asm_convert(Class paramClass) {
        Class clazz = this.typesStk.peek();
        if (paramClass == clazz)
            return true;
        boolean bool1 = clazz.isPrimitive();
        boolean bool2 = paramClass.isPrimitive();
        if (bool1 && bool2) {
            int i = primitiveID(clazz);
            int j = primitiveID(paramClass);
            int k = cvt1[j][i];
            int m = cvt2[j][i];
            if (k == 255)
                return false;
            this.currSSW = this.currSSW + stkoccup[j] - stkoccup[i];
            ensureStack();
            if (k != 0)
                codeOP(k);
            if (m != 0)
                codeOP(m);
            this.typesStk.pop();
            this.typesStk.push(paramClass);
            return true;
        }
        if (bool1 ^ bool2)
            return false;
        this.typesStk.pop();
        this.typesStk.push(paramClass);
        return true;
    }

    public void asm_func_call() {
        asm_logical_unblock();
        Class[] arrayOfClass = this.functionParams.pop();
        int[] arrayOfInt = this.functionINTS.pop();
        Class clazz = this.functionRet.pop();
        int i = arrayOfInt[3];
        if (i != arrayOfInt[2])
            return;
        boolean bool = (arrayOfInt[1] != 1) ? false : true;
        int j = bool ? 0 : 1;
        checkAlter();
        if (bool) {
            codeOP(184);
        } else {
            codeOP(182);
        }
        codeINDEX(arrayOfInt[0]);
        for (byte b = 0; b < arrayOfClass.length; b++) {
            j += stackSpace(arrayOfClass[b]);
            this.typesStk.pop();
        }
        this.currSSW -= j;
        this.typesStk.push(clazz);
        this.currSSW += stackSpace(clazz);
        ensureStack();
    }

    public void asm_func_param() {
        ensure_value();
        asm_logical_unblock();
        normalize_tsb();
        Class[] arrayOfClass = this.functionParams.peek();
        int[] arrayOfInt = this.functionINTS.peek();
        int i = arrayOfInt[3];
        boolean bool = asm_convert(arrayOfClass[i]);
        if (!bool)
            return;
        arrayOfInt[4] = this.currSSW;
        arrayOfInt[3] = arrayOfInt[3] + 1;
        asm_logical_block();
    }

    public void asm_func_start(Method paramMethod, int paramInt) {
        Class[] arrayOfClass = paramMethod.getParameterTypes();
        this.functionParams.push(arrayOfClass);
        boolean bool = Modifier.isStatic(paramMethod.getModifiers());
        int[] arrayOfInt = {getMethodIndex(paramMethod), bool ? 1 : 0, arrayOfClass.length, this.currSSW};
        this.functionINTS.push(arrayOfInt);
        this.functionRet.push(paramMethod.getReturnType());
        if (!bool) {
            loadThisPointer(paramInt, getClassIndex(paramMethod.getDeclaringClass()));
            arrayOfInt[4] = this.currSSW;
        }
        asm_logical_block();
    }

    public void asm_get_array_element() {
        Class clazz1 = this.typesStk.pop();
        Class clazz2 = this.typesStk.pop();
        Class clazz = clazz2.getComponentType();
        int i = 8;
        byte b = 1;
        if (clazz.isPrimitive()) {
            i = primitiveID(clazz);
            b = stkoccup[i];
        }
        codeOP(arrload[i]);
        this.currSSW -= 2;
        this.currSSW += b;
        ensureStack();
        this.typesStk.push(clazz);
    }

    public void asm_load_field(Field paramField, int paramInt) {
        boolean bool = Modifier.isStatic(paramField.getModifiers());
        char c = '²';
        if (!bool) {
            loadThisPointer(paramInt, getClassIndex(paramField.getDeclaringClass()));
            c = '´';
        }
        checkAlter();
        codeOP(c);
        codeINDEX(getFieldIndex(paramField));
        Class clazz = paramField.getType();
        this.typesStk.push(clazz);
        this.currSSW += stackSpace(clazz);
        if (!bool)
            this.currSSW--;
        ensureStack();
    }

    public void asm_load_object(Object paramObject) {
        if (paramObject == null) {
            this.typesStk.push(null);
            codeOP(1);
            this.currSSW++;
            ensureStack();
        } else if (paramObject instanceof String) {
            this.typesStk.push(string_class);
            int i = getStringIndex((String) paramObject);
            if (i < 255) {
                codeOP(18);
                codeOP(i);
            } else {
                codeOP(19);
                codeINDEX(i);
            }
            this.currSSW++;
            ensureStack();
        } else {
            int i = this.objectPool.indexOf(paramObject);
            if (i == -1) {
                this.objectPool.addElement(paramObject);
                i = this.objectPool.size() - 1;
            }
            this.typesStk.push(paramObject.getClass());
            this.currSSW += 2;
            ensureStack();
            codeOP(42);
            codeOP(180);
            codeINDEX(15);
            if (i < 255) {
                codeOP(16);
                codeOP(i);
            } else {
                int k = getIntIndex(new Integer(i));
                codeOP(19);
                codeINDEX(k);
            }
            codeOP(50);
            int j = getClassIndex(paramObject.getClass());
            codeOP(192);
            codeINDEX(j);
            this.currSSW--;
        }
    }

    public void asm_load_primitive(Object paramObject) {
        checkAlter();
        int i = 0;
        int j = 0;
        int k = -1;
        byte b = -1;
        if (paramObject instanceof Double) {
            Double double_ = (Double) paramObject;
            double d = double_.doubleValue();
            if (d == 0.0D) {
                i = 14;
            } else if (d == 1.0D) {
                i = 15;
            } else {
                k = getDoubleIndex(double_);
            }
            b = 7;
        } else if (paramObject instanceof Long) {
            Long long_ = (Long) paramObject;
            long l = long_.longValue();
            if (l >= -1L && l <= 5L) {
                i = load_long_ints1[(int) l + 1];
                j = load_long_ints2[(int) l + 1];
            } else {
                k = getLongIndex(long_);
            }
            b = 5;
        } else if (paramObject instanceof Float) {
            Float float_ = (Float) paramObject;
            float f = float_.floatValue();
            if (f == 0.0D) {
                i = 11;
            } else if (f == 1.0D) {
                i = 12;
            } else if (f == 2.0D) {
                i = 13;
            }
            k = getFloatIndex(float_);
            b = 6;
        } else if (paramObject instanceof Integer || paramObject instanceof Byte || paramObject instanceof Short || paramObject instanceof Character) {
            int m;
            b = 4;
            if (paramObject instanceof Short) {
                b = 3;
            } else if (paramObject instanceof Character) {
                b = 2;
            } else if (paramObject instanceof Byte) {
                b = 1;
            }
            if (b != 2) {
                m = ((Number) paramObject).intValue();
            } else {
                m = ((Character) paramObject).charValue();
            }
            if (m >= -1 && m <= 5) {
                i = load_ints[m + 1];
            } else if (b == 4) {
                k = getIntIndex((Integer) paramObject);
            } else {
                k = getIntIndex(new Integer(m));
            }
        } else if (paramObject instanceof Boolean) {
            b = 0;
            if (((Boolean) paramObject).booleanValue()) {
                i = 4;
            } else {
                i = 3;
            }
        }
        byte b1 = stkoccup[b];
        if (i == 0) {
            byte b2 = 19;
            if (b1 == 2)
                b2 = 20;
            if (b2 == 19 && k < 255) {
                codeOP(b2 - 1);
                codeOP(k);
            } else {
                codeOP(b2);
                codeINDEX(k);
            }
        } else {
            codeOP(i);
            if (j != 0)
                codeOP(j);
        }
        this.currSSW += b1;
        ensureStack();
        this.typesStk.push(primitiveTypes[b]);
    }

    public void asm_logical_binary(int paramInt) {
        switch (paramInt) {
            case 0 :
                asm_logical_binary_and();
                break;
            case 1 :
                asm_logical_binary_or();
                break;
        }
    }

    private final void asm_logical_binary_and() {
        unblockLabels(this.jumps0, this.blocks0);
    }

    private final void asm_logical_binary_or() {
        unblockLabels(this.jumps1, this.blocks1);
    }

    public void asm_logical_binary_param(int paramInt) {
        switch (paramInt) {
            case 0 :
                asm_logical_binary_param_and();
                break;
            case 1 :
                asm_logical_binary_param_or();
                break;
        }
    }

    private final void asm_logical_binary_param_and() {
        logical_param(true, this.jumps0, this.jumps1, this.blocks0, this.blocks1);
    }

    private final void asm_logical_binary_param_or() {
        logical_param(false, this.jumps1, this.jumps0, this.blocks1, this.blocks0);
    }

    public void asm_logical_block() {
        blockLabels(this.jumps0, this.blocks0);
        blockLabels(this.jumps1, this.blocks1);
    }

    private void asm_logical_unblock() {
        unblockLabels(this.jumps0, this.blocks0);
        unblockLabels(this.jumps1, this.blocks1);
    }

    public void asm_logical_unblock_not() {
        ensure_jump();
        IntegerStack.swap(this.jumps1, this.blocks1.pop(), this.jumps0, this.blocks0.pop());
        this.invert_next_jump ^= 0x1;
    }

    public void asm_return() {
        checkAlter();
        ensure_value();
        normalize_tsb();
        Class clazz = null;
        boolean bool = false;
        try {
            clazz = this.typesStk.peek();
        } catch (EmptyStackException emptyStackException) {
            asm_load_object(null);
            bool = true;
        }
        int i = primitiveTypes.length;
        byte b = -80;
        if (!bool) {
            i++;
            if (clazz != null && clazz.isPrimitive()) {
                i = primitiveID(clazz);
                b = returns[i];
            }
        }
        StringBuffer stringBuffer1 = new StringBuffer("evaluate");
        StringBuffer stringBuffer2 = new StringBuffer("([Ljava/lang/Object;)");
        if (i < primitiveTypes.length) {
            stringBuffer1.append('_');
            stringBuffer1.append(primitiveTypeNames[i]);
            stringBuffer2.append(primitiveCodes[i]);
        } else {
            stringBuffer2.append("Ljava/lang/Object;");
        }
        this.nameIdx = (short) getUTFIndex(stringBuffer1.toString());
        this.signIdx = (short) getUTFIndex(stringBuffer2.toString());
        this.thetype = (byte) i;
        this.typesStk.pop();
        codeOP(b);
        this.classFinished = true;
    }

    public void asm_throw_return() {
        checkAlter();
        Class clazz = null;
        try {
            clazz = this.typesStk.pop();
        } catch (EmptyStackException emptyStackException) {
        }
        codeOP(191);
        this.classFinished = true;
    }

    public void asm_unary(int paramInt) {
        ensure_value();
        Class clazz = this.typesStk.peek();
        if (!clazz.isPrimitive())
            return;
        int i = primitiveID(clazz);
        if (una[0][paramInt][i] == 255)
            return;
        for (byte b = 0; b < una.length; b++) {
            int k = una[b][paramInt][i];
            if (k > 0)
                codeOP(k);
        }
        int j = una_excess_stack[paramInt][i];
        this.currSSW += j;
        ensureStack();
        this.currSSW -= j;
        this.typesStk.pop();
        this.typesStk.push(primitiveTypes[unary_promotions[i]]);
    }

    private final void blockLabels(IntegerStack paramIntegerStack1, IntegerStack paramIntegerStack2) {
        paramIntegerStack2.push(paramIntegerStack1.size());
    }

    public static boolean canConvert(Class paramClass1, Class paramClass2) {
        boolean bool1 = paramClass1.isPrimitive();
        boolean bool2 = paramClass2.isPrimitive();
        if (bool2 && bool1) {
            int i = primitiveID(paramClass1);
            int j = primitiveID(paramClass2);
            return !(cvt1[j][i] == 255);
        }
        return (bool2 ^ bool1) ? false : paramClass2.isAssignableFrom(paramClass1);
    }

    public static boolean canConvertByWidening(Class paramClass1, Class paramClass2) {
        boolean bool1 = paramClass1.isPrimitive();
        boolean bool2 = paramClass2.isPrimitive();
        if (bool2 && bool1) {
            int i = primitiveID(paramClass1);
            int j = primitiveID(paramClass2);
            return !((cvt_wide[j] & 128 >> i) <= 0);
        }
        return (bool2 ^ bool1) ? false : paramClass2.isAssignableFrom(paramClass1);
    }

    public static boolean canGenerateBinary(int paramInt, Class paramClass1, Class paramClass2) {
        if (paramInt == 0 && (paramClass1 == string_class || paramClass1 == tsb_class))
            return true;
        if (!paramClass1.isPrimitive() || !paramClass2.isPrimitive())
            return false;
        int i = primitiveID(paramClass1);
        int j = primitiveID(paramClass2);
        if (is_promotion_binary[paramInt]) {
            int k = binary_promotions[i][j];
            return (k == 255) ? false : (!(ops[0][paramInt][k] == 255));
        }
        return !(i < 1 || i > 5 || j < 1 || j > 5);
    }

    public static boolean canGenerateUnary(int paramInt, Class paramClass) {
        return !paramClass.isPrimitive() ? false : (!(una[0][paramInt][primitiveID(paramClass)] == 255));
    }

    private final void checkAlter() {
    }

    private final void codeINDEX(int paramInt) {
        try {
            this.methodTextData.writeShort(paramInt);
        } catch (IOException iOException) {
        }
    }

    private final void codeOP(int paramInt) {
        try {
            this.methodTextData.write(paramInt);
        } catch (IOException iOException) {
        }
    }

    static void dumpImage(ExpressionImage paramExpressionImage) {
    }

    private final void ensureStack() {
        if (this.currSSW > this.max_stack)
            this.max_stack = this.currSSW;
    }

    private final void ensure_jump() {
        if (this.jump_progress != 0)
            return;
        Class clazz = this.typesStk.pop();
        this.currSSW -= stkoccup[0];
        this.jump_progress = 157;
    }

    private final void ensure_value() {
        if (this.jump_progress == 0 && noPendingJumps())
            return;
        asm_branch_start_true();
        asm_load_primitive(Boolean.TRUE);
        asm_branch_start_false();
        asm_load_primitive(Boolean.FALSE);
        asm_branch_end();
    }

    public static Class getBinaryPromoted(Class paramClass1, Class paramClass2) {
        int i = primitiveID(paramClass1);
        int j = primitiveID(paramClass2);
        int k = binary_promotions[i][j];
        return (k == 255) ? null : primitiveTypes[k];
    }

    public ExpressionBits getBits() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.write(prologue);
            dataOutputStream.writeShort(this.poolEntries);
            byte[] arrayOfByte1 = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            this.constPool.writeTo(dataOutputStream);
            dataOutputStream.write(intermezzo1);
            dataOutputStream.write(this.thetype);
            dataOutputStream.write(intermezzo2);
            dataOutputStream.writeShort(this.nameIdx);
            dataOutputStream.writeShort(this.signIdx);
            dataOutputStream.write(intermezzo3);
            int i = this.methodText.size();
            dataOutputStream.writeInt(i + 12);
            dataOutputStream.writeShort(this.max_stack);
            dataOutputStream.writeShort(this.max_locals);
            dataOutputStream.writeInt(i);
            this.methodText.writeTo(dataOutputStream);
            dataOutputStream.write(konetc);
            byte[] arrayOfByte2 = byteArrayOutputStream.toByteArray();
            Object[] arrayOfObject = new Object[this.objectPool.size()];
            this.objectPool.copyInto(arrayOfObject);
            return new ExpressionBits(arrayOfByte1, arrayOfByte2, arrayOfObject);
        } catch (IOException iOException) {
            return null;
        }
    }

    private int getClassIndex(Class paramClass) {
        Integer integer = (Integer) this.Items.get(paramClass);
        if (integer == null) {
            String str1 = paramClass.getName();
            String str2 = toHistoricalForm(str1);
            int i = getUTFIndex(str2);
            integer = new Integer(this.poolEntries++);
            writeClassInfo(i);
            this.Items.put(paramClass, integer);
        }
        return integer.intValue();
    }

    private int getDoubleIndex(Double paramDouble) {
        Integer integer = (Integer) this.Items.get(paramDouble);
        if (integer == null) {
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(6);
                this.constPoolData.writeDouble(paramDouble.doubleValue());
                this.poolEntries++;
            } catch (IOException iOException) {
            }
            this.Items.put(paramDouble, integer);
        }
        return integer.intValue();
    }

    public CompiledExpression getExpression() {
        return getBits().getExpression();
    }

    private int getFieldIndex(Field paramField) {
        Integer integer = (Integer) this.Items.get(paramField);
        if (integer == null) {
            integer = new Integer(writeFIMRef(paramField.getName(), getSignature(paramField.getType()), paramField.getDeclaringClass(), 9));
            this.Items.put(paramField, integer);
        }
        return integer.intValue();
    }

    private int getFloatIndex(Float paramFloat) {
        Integer integer = (Integer) this.Items.get(paramFloat);
        if (integer == null) {
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(4);
                this.constPoolData.writeFloat(paramFloat.floatValue());
            } catch (IOException iOException) {
            }
            this.Items.put(paramFloat, integer);
        }
        return integer.intValue();
    }

    public byte[] getImage() {
        return getBits().getImage();
    }

    private int getIntIndex(Integer paramInteger) {
        Integer integer = (Integer) this.Items.get(paramInteger);
        if (integer == null) {
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(3);
                this.constPoolData.writeInt(paramInteger.intValue());
            } catch (IOException iOException) {
            }
            this.Items.put(paramInteger, integer);
        }
        return integer.intValue();
    }

    private int getLongIndex(Long paramLong) {
        Integer integer = (Integer) this.Items.get(paramLong);
        if (integer == null) {
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(5);
                this.constPoolData.writeLong(paramLong.longValue());
                this.poolEntries++;
            } catch (IOException iOException) {
            }
            this.Items.put(paramLong, integer);
        }
        return integer.intValue();
    }

    private int getMethodIndex(Constructor paramConstructor) {
        Integer integer = (Integer) this.Items.get(paramConstructor);
        if (integer == null) {
            integer = new Integer(writeFIMRef("<init>", getSignature(paramConstructor), paramConstructor.getDeclaringClass(), 10));
            this.Items.put(paramConstructor, integer);
        }
        return integer.intValue();
    }

    private int getMethodIndex(Method paramMethod) {
        Integer integer = (Integer) this.Items.get(paramMethod);
        if (integer == null) {
            Class clazz = paramMethod.getDeclaringClass();
            byte b = 10;
            if (clazz.isInterface())
                b = 11;
            integer = new Integer(writeFIMRef(paramMethod.getName(), getSignature(paramMethod), paramMethod.getDeclaringClass(), b));
            this.Items.put(paramMethod, integer);
        }
        return integer.intValue();
    }

    public static String getSignature(Class paramClass) {
        return paramClass.isPrimitive()
            ? String.valueOf(primitiveCodes[primitiveID(paramClass)])
            : (paramClass.isArray() ? (String.valueOf('[') + getSignature(paramClass.getComponentType())) : (String.valueOf('L') + toHistoricalForm(paramClass.getName()) + ';'));
    }

    public static String getSignature(Constructor paramConstructor) {
        StringBuffer stringBuffer = new StringBuffer();
        appendParametersSignature(stringBuffer, paramConstructor.getParameterTypes());
        stringBuffer.append('V');
        return stringBuffer.toString();
    }

    public static String getSignature(Method paramMethod) {
        StringBuffer stringBuffer = new StringBuffer();
        appendParametersSignature(stringBuffer, paramMethod.getParameterTypes());
        stringBuffer.append(getSignature(paramMethod.getReturnType()));
        return stringBuffer.toString();
    }

    private int getStringIndex(String paramString) {
        Integer integer = (Integer) this.Items.get(paramString);
        if (integer == null) {
            int i = getUTFIndex(paramString);
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(8);
                this.constPoolData.writeShort(i);
            } catch (IOException iOException) {
            }
            this.Items.put(paramString, integer);
        }
        return integer.intValue();
    }

    private int getUTFIndex(String paramString) {
        Integer integer = (Integer) this.UTFs.get(paramString);
        if (integer == null) {
            integer = new Integer(this.poolEntries++);
            try {
                checkAlter();
                this.constPoolData.write(1);
                this.constPoolData.writeUTF(paramString);
            } catch (IOException iOException) {
            }
            this.UTFs.put(paramString, integer);
        }
        return integer.intValue();
    }

    static int getUnaryPromoted(int paramInt) {
        return unary_promotions[paramInt];
    }

    public static Class getUnaryPromoted(Class paramClass) {
        int i = unary_promotions[primitiveID(paramClass)];
        return (i == 255) ? null : primitiveTypes[i];
    }

    private static final byte invert_jump_bytecode(int paramInt) {
        return (byte) ((paramInt - 1 ^ 0x1) + 1);
    }

    public static final boolean isPromotionBinary(int paramInt) {
        return is_promotion_binary[paramInt];
    }

    private final void landLabel(IntegerStack paramIntegerStack) {
        int i = this.methodText.size();
        int j = paramIntegerStack.pop();
        this.methodText.patchAddress(j, i - j + 1);
    }

    private final void landLabels(IntegerStack paramIntegerStack1, IntegerStack paramIntegerStack2) {
        int i = 0;
        if (paramIntegerStack2.size() > 0)
            i = paramIntegerStack2.peek();
        while (paramIntegerStack1.size() > i)
            landLabel(paramIntegerStack1);
    }

    private void loadThisPointer(int paramInt1, int paramInt2) {
        checkAlter();
        this.currSSW += 2;
        ensureStack();
        codeOP(43);
        if (paramInt1 < 255) {
            codeOP(16);
            codeOP(paramInt1);
        } else {
            int i = getIntIndex(new Integer(paramInt1));
            codeOP(19);
            codeINDEX(i);
        }
        codeOP(50);
        codeOP(192);
        codeINDEX(paramInt2);
        this.currSSW--;
    }

    private void logical_param(boolean paramBoolean, IntegerStack paramIntegerStack1, IntegerStack paramIntegerStack2, IntegerStack paramIntegerStack3, IntegerStack paramIntegerStack4) {
        ensure_jump();
        if (this.invert_next_jump ^ paramBoolean)
            this.jump_progress = invert_jump_bytecode(this.jump_progress);
        this.invert_next_jump = false;
        codeOP(this.jump_progress);
        this.jump_progress = 0;
        mkLabel(paramIntegerStack1);
        landLabels(paramIntegerStack2, paramIntegerStack4);
        blockLabels(paramIntegerStack1, paramIntegerStack3);
    }

    public static void main(String[] paramArrayOfString) {
        Tester tester = new Tester(System.out);
        test(tester);
        tester.summarize();
    }

    private final void mkLabel(IntegerStack paramIntegerStack) {
        int i = this.methodText.size();
        paramIntegerStack.push(i);
        try {
            this.methodTextData.writeShort(0);
        } catch (IOException iOException) {
        }
    }

    private final boolean noPendingJumps() {
        int i = 0;
        if (this.blocks0.size() > 0)
            i = this.blocks0.peek();
        int j = 0;
        if (this.blocks1.size() > 0)
            j = this.blocks1.peek();
        return !(this.jumps0.size() != i || this.jumps1.size() != j);
    }

    private void normalize_tsb() {
        Class clazz = this.typesStk.peek();
        if (clazz != tsb_class)
            return;
        codeOP(182);
        codeINDEX(getMethodIndex(sb_toString));
        this.typesStk.pop();
        this.typesStk.push(string_class);
    }

    static final int primitiveID(Class paramClass) {
        for (byte b = 0; b < primitiveTypes.length && primitiveTypes[b] != paramClass; b++);
        return b;
    }

    private static final int stackSpace(Class paramClass) {
        return paramClass.isPrimitive() ? stkoccup[primitiveID(paramClass)] : 1;
    }

    public static void test(Tester paramTester) {
    }

    private static void testArrLoad(Object paramObject1, int paramInt, Object paramObject2, String paramString, Tester paramTester) {
    }

    private static void testComparizons(Object paramObject1, Object paramObject2, String paramString, Tester paramTester) {
    }

    private static String toHistoricalForm(String paramString) {
        StringBuffer stringBuffer = new StringBuffer(paramString);
        int i = paramString.length();
        for (int j = paramString.indexOf('.'); j > 0 && j < i; j = paramString.indexOf('.', j + 1))
            stringBuffer.setCharAt(j, '/');
        return stringBuffer.toString();
    }

    private static boolean transmitPrimitive(Object paramObject1, Object paramObject2) throws Throwable {
        return true;
    }

    private final void unblockLabels(IntegerStack paramIntegerStack1, IntegerStack paramIntegerStack2) {
        paramIntegerStack2.pop_throw();
    }

    private void writeClassInfo(int paramInt) {
        try {
            checkAlter();
            this.constPoolData.write(7);
            this.constPoolData.writeShort(paramInt);
        } catch (IOException iOException) {
        }
    }

    private int writeFIMRef(String paramString1, String paramString2, Class paramClass, int paramInt) {
        int i = getUTFIndex(paramString1);
        int j = getUTFIndex(paramString2);
        int k = getClassIndex(paramClass);
        int m = this.poolEntries++;
        try {
            checkAlter();
            this.constPoolData.write(12);
            this.constPoolData.writeShort(i);
            this.constPoolData.writeShort(j);
        } catch (IOException iOException) {
        }
        int n = this.poolEntries++;
        try {
            checkAlter();
            this.constPoolData.write(paramInt);
            this.constPoolData.writeShort(k);
            this.constPoolData.writeShort(m);
        } catch (IOException iOException) {
        }
        return n;
    }

    static {
        try {
            sb_class = Class.forName("java.lang.StringBuffer");
            tsb_class = Class.forName("gnu.jel.TempStringBuffer");
            string_class = Class.forName("java.lang.String");
            Class[] arrayOfClass = new Class[0];
            sb_toString = sb_class.getMethod("toString", arrayOfClass);
            arrayOfClass = new Class[1];
            arrayOfClass[0] = string_class;
            sb_constructor = sb_class.getConstructor(arrayOfClass);
            sb_append_String = sb_class.getMethod("append", arrayOfClass);
            arrayOfClass[0] = Class.forName("java.lang.Object");
            sb_append_Object = sb_class.getMethod("append", arrayOfClass);
            for (byte b = 0; b < primitiveTypes.length - 1; b++) {
                arrayOfClass[0] = primitiveTypes[(b == 1) ? 4 : ((b == 3) ? 4 : b)];
                sb_append_primitive[b] = sb_class.getMethod("append", arrayOfClass);
            }
        } catch (NoSuchMethodException noSuchMethodException) {
        } catch (ClassNotFoundException classNotFoundException) {
        }
    }
}
