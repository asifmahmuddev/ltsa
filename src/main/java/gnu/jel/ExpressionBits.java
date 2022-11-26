package gnu.jel;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

public class ExpressionBits implements Serializable {
    static final long serialVersionUID = 5432046992123704544L;
    private static volatile transient long expressionUID = 0L;
    private byte[] beforeName = null;
    private byte[] afterName = null;
    private Object[] objectConstants = null;
    private static final String classNamePrefix = "gnu/jel/generated/E_";
    private String name = null;

    ExpressionBits(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, Object[] paramArrayOfObject) {
        this.beforeName = paramArrayOfbyte1;
        this.afterName = paramArrayOfbyte2;
        this.objectConstants = paramArrayOfObject;
    }

    public CompiledExpression getExpression() {
        byte[] arrayOfByte = getImage();
        try {
            ExpressionLoader expressionLoader = new ExpressionLoader(this.name, arrayOfByte);
            Class clazz = expressionLoader.loadClass(this.name);
            Constructor[] arrayOfConstructor = (Constructor[]) clazz.getConstructors();
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = this.objectConstants;
            return arrayOfConstructor[0].newInstance(arrayOfObject);
        } catch (Exception exception) {
            return null;
        }
    }

    public byte[] getImage() {
        this.name = "gnu/jel/generated/E_" + Long.toString(expressionUID++);
        int i = this.beforeName.length + this.afterName.length + 30 + "gnu/jel/generated/E_".length();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(i);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.write(this.beforeName);
            dataOutputStream.write(1);
            dataOutputStream.writeUTF(this.name);
            dataOutputStream.write(this.afterName);
        } catch (IOException iOException) {
        }
        return byteArrayOutputStream.toByteArray();
    }
}
