package ic.doc.ltsa.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class ColoredContext extends StyleContext implements ViewFactory {
    class ColoredView extends PlainView {
        private ColoredScanner lexer;
        private boolean lexerValid;
        private final ColoredContext this$0;

        ColoredView(ColoredContext this$0, Element param1Element) {
            super(param1Element);
            this.this$0 = this$0;
            ColoredDocument coloredDocument = (ColoredDocument) getDocument();
            this.lexer = coloredDocument.getScanner();
            this.lexerValid = false;
        }

        public void paint(Graphics param1Graphics, Shape param1Shape) {
            super.paint(param1Graphics, param1Shape);
            this.lexerValid = false;
        }

        protected int drawUnselectedText(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) throws BadLocationException {
            Document document = getDocument();
            Color color = null;
            int i = param1Int3;
            while (param1Int3 < param1Int4) {
                updateScanner(param1Int3);
                int j = Math.min(this.lexer.getEndOffset(), param1Int4);
                j = (j <= param1Int3) ? param1Int4 : j;
                Color color1 = this.lexer.getColor();
                if (color1 != color && color != null) {
                    param1Graphics.setColor(color);
                    Segment segment1 = getLineBuffer();
                    document.getText(i, param1Int3 - i, segment1);
                    param1Int1 = Utilities.drawTabbedText(segment1, param1Int1, param1Int2, param1Graphics, this, i);
                    i = param1Int3;
                }
                color = color1;
                param1Int3 = j;
            }
            param1Graphics.setColor(color);
            Segment segment = getLineBuffer();
            document.getText(i, param1Int4 - i, segment);
            param1Int1 = Utilities.drawTabbedText(segment, param1Int1, param1Int2, param1Graphics, this, i);
            return param1Int1;
        }

        void updateScanner(int param1Int) {
            try {
                if (!this.lexerValid) {
                    ColoredDocument coloredDocument = (ColoredDocument) getDocument();
                    this.lexer.setRange(coloredDocument.getScannerStart(param1Int), coloredDocument.getLength());
                    this.lexerValid = true;
                }
                for (; this.lexer.getEndOffset() <= param1Int; this.lexer.next());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public View create(Element paramElement) {
        return new ColoredView(this, paramElement);
    }
}
