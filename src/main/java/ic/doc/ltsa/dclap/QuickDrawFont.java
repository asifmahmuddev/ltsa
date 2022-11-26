package ic.doc.ltsa.dclap;

class QuickDrawFont {
    int val;
    String name;

    QuickDrawFont(int paramInt, String paramString) {
        this.val = paramInt;
        this.name = paramString;
    }

    int fontval(String paramString) {
        if (this.name.equalsIgnoreCase(paramString))
            return this.val;
        return -1;
    }
}
