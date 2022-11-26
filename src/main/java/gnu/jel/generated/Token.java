package gnu.jel.generated;

public class Token {
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;

    public static final Token newToken(int paramInt) {
        switch (paramInt) {
        }
        return new Token();
    }

    public final String toString() {
        return this.image;
    }
}
