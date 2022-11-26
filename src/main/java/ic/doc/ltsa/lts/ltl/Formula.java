package ic.doc.ltsa.lts.ltl;

public abstract class Formula implements Comparable {
    private int id = -1;

    public void setId(int paramInt) {
        this.id = paramInt;
    }

    public int getId() {
        return this.id;
    }

    private int untilsIndex = -1;
    private int rightOfUntilIndex = -1;

    boolean isRightOfUntil() {
        return (this.rightOfUntilIndex >= 0);
    }

    int getUI() {
        return this.untilsIndex;
    }

    void setUI(int paramInt) {
        this.untilsIndex = paramInt;
    }

    int getRofUI() {
        return this.rightOfUntilIndex;
    }

    void setRofUI(int paramInt) {
        this.rightOfUntilIndex = paramInt;
    }

    public int compareTo(Object paramObject) {
        return this.id - ((Formula) paramObject).id;
    }

    abstract Formula accept(Visitor paramVisitor);

    boolean isLiteral() {
        return false;
    }

    Formula getSub1() {
        return accept(Sub1.get());
    }

    Formula getSub2() {
        return accept(Sub2.get());
    }
}
