package ic.doc.ltsa.lts;

public class StackChecker {
    StateCodec coder;
    StackCheck checker;

    public StackChecker(StateCodec paramStateCodec, StackCheck paramStackCheck) {
        this.coder = paramStateCodec;
        this.checker = paramStackCheck;
    }

    public boolean onStack(int[] paramArrayOfint) {
        byte[] arrayOfByte = this.coder.encode(paramArrayOfint);
        if (arrayOfByte == null)
            return false;
        return this.checker.onStack(arrayOfByte);
    }
}
