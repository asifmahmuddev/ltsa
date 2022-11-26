package ic.doc.ltsa.lts;

class Value {
    private int val;
    private String sval;
    private boolean sonly;

    public Value(int paramInt) {
        this.val = paramInt;
        this.sonly = false;
        this.sval = String.valueOf(paramInt);
    }

    public Value(String paramString) {
        this.sval = paramString;
        try {
            this.val = Integer.parseInt(paramString);
            this.sonly = false;
        } catch (NumberFormatException numberFormatException) {
            this.sonly = true;
        }
    }

    public String toString() {
        return this.sval;
    }

    public int intValue() {
        return this.val;
    }

    public boolean isInt() {
        return !this.sonly;
    }

    public boolean isLabel() {
        return this.sonly;
    }
}
