package ic.doc.ltsa.editor;

import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;

public class ColoredDocument extends PlainDocument {
    ColoredScanner scanner;

    public ColoredDocument() {
        super(new GapContent(1024));
        this.scanner = new ColoredScanner(this);
        putProperty("lineLimit", new Integer(256));
        putProperty("tabSize", new Integer(4));
    }

    public ColoredScanner getScanner() {
        return this.scanner;
    }

    public int getScannerStart(int paramInt) {
        return 0;
    }
}
