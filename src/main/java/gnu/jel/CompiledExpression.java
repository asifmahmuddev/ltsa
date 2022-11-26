package gnu.jel;

public abstract class CompiledExpression {
    public Object evaluate(Object[] paramArrayOfObject) throws Throwable {
        Byte byte_;
        Character character;
        Short short_;
        Integer integer;
        Long long_;
        Float float_;
        Double double_;
        int i = getType();
        Boolean bool = null;
        switch (i) {
            case 0 :
                bool = new Boolean(evaluate_boolean(paramArrayOfObject));
                break;
            case 1 :
                byte_ = new Byte(evaluate_byte(paramArrayOfObject));
                break;
            case 2 :
                character = new Character(evaluate_char(paramArrayOfObject));
                break;
            case 3 :
                short_ = new Short(evaluate_short(paramArrayOfObject));
                break;
            case 4 :
                integer = new Integer(evaluate_int(paramArrayOfObject));
                break;
            case 5 :
                long_ = new Long(evaluate_long(paramArrayOfObject));
                break;
            case 6 :
                float_ = new Float(evaluate_float(paramArrayOfObject));
                break;
            case 7 :
                double_ = new Double(evaluate_double(paramArrayOfObject));
                break;
        }
        return double_;
    }

    public boolean evaluate_boolean(Object[] paramArrayOfObject) throws Throwable {
        return false;
    }

    public byte evaluate_byte(Object[] paramArrayOfObject) throws Throwable {
        return 0;
    }

    public char evaluate_char(Object[] paramArrayOfObject) throws Throwable {
        return '?';
    }

    public double evaluate_double(Object[] paramArrayOfObject) throws Throwable {
        return 0.0D;
    }

    public float evaluate_float(Object[] paramArrayOfObject) throws Throwable {
        return 0.0F;
    }

    public int evaluate_int(Object[] paramArrayOfObject) throws Throwable {
        return 0;
    }

    public long evaluate_long(Object[] paramArrayOfObject) throws Throwable {
        return 0L;
    }

    public short evaluate_short(Object[] paramArrayOfObject) throws Throwable {
        return 0;
    }

    public abstract int getType();
}
