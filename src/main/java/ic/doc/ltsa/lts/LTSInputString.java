package ic.doc.ltsa.lts;

public class LTSInputString implements LTSInput {
    private String fSrc;
    private int fPos;

    public LTSInputString(String paramString) {
        this.fSrc = paramString;
        this.fPos = -1;
    }

    public char nextChar() {
        this.fPos++;
        if (this.fPos < this.fSrc.length())
            return this.fSrc.charAt(this.fPos);
        return Character.MIN_VALUE;
    }

    public char backChar() {
        this.fPos--;
        if (this.fPos < 0) {
            this.fPos = 0;
            return Character.MIN_VALUE;
        }
        return this.fSrc.charAt(this.fPos);
    }

    public int getMarker() {
        return this.fPos;
    }
}
