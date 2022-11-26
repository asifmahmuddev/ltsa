package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;

public class Font extends StyleBase {
    private java.awt.Font _font;

    public Font() {
        this._font = null;
    }

    public Font(java.awt.Font paramFont, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._font = paramFont;
    }

    public java.awt.Font getFont() {
        return this._font;
    }

    public void setFont(java.awt.Font paramFont) {
        this._font = paramFont;
        setDirty(true);
    }

    public Style.Change changeStyle(Graphics2D paramGraphics2D) {
        java.awt.Font font1 = paramGraphics2D.getFont();
        java.awt.Font font2 = this._font;
        paramGraphics2D.setFont(font2);
        return new Style.Change(this, font1, font2) {
            private final java.awt.Font val$old_font;
            private final java.awt.Font val$new_font;
            private final Font this$0;

            public void restoreStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setFont(this.val$old_font);
            }

            public void reapplyStyle(Graphics2D param1Graphics2D) {
                if (this.val$new_font != null)
                    param1Graphics2D.setFont(this.val$new_font);
            }
        };
    }
}
