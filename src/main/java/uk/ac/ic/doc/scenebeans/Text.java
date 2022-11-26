package uk.ac.ic.doc.scenebeans;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

public class Text extends SceneGraphBase implements Primitive {
    private String _text;
    private GlyphVector _glyphs;
    private Shape _last_drawn;

    public Text() {
        this._text = "";
    }

    public Text(String paramString) {
        this._text = paramString;
    }

    public String getText() {
        return this._text;
    }

    public void setText(String paramString) {
        this._text = paramString;
        setDirty(true);
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        return getGlyphs(paramGraphics2D).getOutline();
    }

    public Shape getLastDrawnShape() {
        return this._last_drawn;
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public void draw(Graphics2D paramGraphics2D) {
        GlyphVector glyphVector = getGlyphs(paramGraphics2D);
        paramGraphics2D.drawGlyphVector(glyphVector, 0.0F, 0.0F);
        this._last_drawn = glyphVector.getOutline();
        setDirty(false);
    }

    private GlyphVector getGlyphs(Graphics2D paramGraphics2D) {
        if (this._glyphs == null || !this._glyphs.getFont().equals(paramGraphics2D.getFont())) {
            Font font = paramGraphics2D.getFont();
            FontRenderContext fontRenderContext = paramGraphics2D.getFontRenderContext();
            this._glyphs = font.createGlyphVector(fontRenderContext, this._text);
        }
        return this._glyphs;
    }

    public void setDirty(boolean paramBoolean) {
        if (paramBoolean)
            this._glyphs = null;
        super.setDirty(paramBoolean);
    }

    class TextAdapter implements StringBehaviourListener, DoubleBehaviourListener {
        private final Text this$0;

        TextAdapter(Text this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(String param1String) {
            this.this$0.setText(param1String);
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setText(Double.toString(param1Double));
        }
    }

    public StringBehaviourListener newTextAdapter() {
        return new TextAdapter(this);
    }
}
