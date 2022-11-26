package gnu.jel;

import gnu.jel.generated.ParseException;
import gnu.jel.generated.Token;
import gnu.jel.generated.TokenMgrError;

public class CompilationException extends Exception {
    int col;

    public CompilationException(int paramInt, String paramString) {
        super(paramString);
        this.col = paramInt;
    }

    public CompilationException(ParseException paramParseException) {
        super("Encountered unexpected " + ((paramParseException.currentToken.next.kind != 0) ? ("\"" + paramParseException.currentToken.next + "\"") : "end of expression") + " .");
        this.col = paramParseException.currentToken.next.beginColumn;
    }

    public CompilationException(Token paramToken, String paramString) {
        super(paramString);
        this.col = paramToken.beginColumn;
    }

    public CompilationException(TokenMgrError paramTokenMgrError) {
        super("Encountered unexpected " + (paramTokenMgrError.isEOF ? "end of expression" : ("'" + paramTokenMgrError.encountered + "'")) + " .");
        this.col = paramTokenMgrError.col;
    }

    public int getColumn() {
        return this.col;
    }
}
