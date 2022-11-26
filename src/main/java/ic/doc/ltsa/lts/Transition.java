package ic.doc.ltsa.lts;

public class Transition {
    int from;
    int to;
    Symbol event;

    Transition() {
    }

    Transition(int paramInt) {
        this.from = paramInt;
    }

    Transition(int paramInt1, Symbol paramSymbol, int paramInt2) {
        this.from = paramInt1;
        this.to = paramInt2;
        this.event = paramSymbol;
    }

    public String toString() {
        return "" + this.from + " " + this.event + " " + this.to;
    }
}
