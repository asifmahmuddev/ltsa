package ic.doc.ltsa.editor;

import ic.doc.ltsa.lts.LTSInput;
import ic.doc.ltsa.lts.Lex;
import ic.doc.ltsa.lts.Symbol;
import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

public class ColoredScanner implements LTSInput {
    private Document doc;
    private int d_Pos;
    private Segment d_text;
    private int d_offset;
    private Lex lex;
    private Symbol current;

    ColoredScanner(Document paramDocument) {
        this.d_text = new Segment();
        this.doc = paramDocument;
        this.d_Pos = -1;
        this.d_offset = 0;
        if (this.doc.getLength() > 0)
            try {
                paramDocument.getText(0, this.doc.getLength(), this.d_text);
            } catch (BadLocationException badLocationException) {
            }
        this.lex = new Lex(this, false);
    }

    public void next() {
        try {
            this.current = this.lex.in_sym();
        } catch (Exception exception) {
        }
    }

    public void setRange(int paramInt1, int paramInt2) throws BadLocationException {
        this.doc.getText(paramInt1, paramInt2 - paramInt1, this.d_text);
        this.d_Pos = -1;
        this.d_offset = paramInt1;
        this.current = null;
    }

    public final int getStartOffset() {
        if (this.current == null)
            return this.d_offset + this.d_Pos;
        return this.current.startPos + this.d_offset;
    }

    public final int getEndOffset() {
        if (this.current == null)
            return this.d_offset + this.d_Pos + 1;
        return this.current.endPos + this.d_offset + 1;
    }

    public Color getColor() {
        if (this.current == null)
            return Color.black;
        return this.current.getColor();
    }

    public char nextChar() {
        this.d_Pos++;
        if (this.d_Pos < this.d_text.count)
            return this.d_text.array[this.d_Pos];
        return Character.MIN_VALUE;
    }

    public char backChar() {
        this.d_Pos--;
        if (this.d_Pos < 0) {
            this.d_Pos = -1;
            return Character.MIN_VALUE;
        }
        return this.d_text.array[this.d_Pos];
    }

    public int getMarker() {
        return this.d_Pos;
    }
}
