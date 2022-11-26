package ic.doc.ltsa.lts;

public class LTSEvent {
    public int kind;
    public Object info;
    public String name;
    public static final int NEWSTATE = 0;
    public static final int INVALID = 1;
    public static final int KILL = 2;

    public LTSEvent(int paramInt, Object paramObject) {
        this.kind = paramInt;
        this.info = paramObject;
    }

    public LTSEvent(int paramInt, Object paramObject, String paramString) {
        this.kind = paramInt;
        this.info = paramObject;
        this.name = paramString;
    }
}
