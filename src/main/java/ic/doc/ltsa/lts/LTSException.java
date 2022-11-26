package ic.doc.ltsa.lts;

public class LTSException extends RuntimeException {
    public Object marker;

    public LTSException(String paramString) {
        super(paramString);
        this.marker = null;
    }

    public LTSException(String paramString, Object paramObject) {
        super(paramString);
        this.marker = paramObject;
    }
}
