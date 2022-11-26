package ic.doc.ltsa.dclap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;

public class Gr2PICT extends Graphics {
    public static final int CLONE = 49;
    protected static final int PAGEHEIGHT = 792;
    protected static final int PAGEWIDTH = 612;
    protected DataOutputStream os;
    protected Color clr = Color.black;
    protected Font font = new Font("Serif", 0, 12);
    protected Rectangle clipr = new Rectangle(-30000, -30000, 60000, 60000);
    protected Point origin = new Point(0, 0);
    protected boolean trouble = false;
    protected Graphics g;
    private int fAlign;

    public Gr2PICT(OutputStream paramOutputStream, Graphics paramGraphics, Rectangle paramRectangle) {
        this.fAlign = 0;
        this.os = new DataOutputStream(paramOutputStream);
        this.trouble = false;
        this.g = paramGraphics;
        emitHeader(paramRectangle.width, paramRectangle.height);
    }

    public Gr2PICT(OutputStream paramOutputStream, Graphics paramGraphics, int paramInt) {
        this.fAlign = 0;
        this.os = new DataOutputStream(paramOutputStream);
        this.trouble = false;
        this.g = paramGraphics;
        Rectangle rectangle = paramGraphics.getClipRect();
        if (rectangle == null)
            rectangle = new Rectangle(0, 0, 612, 792);
        if (paramInt != 49)
            emitHeader(rectangle.width, rectangle.height);
    }

    protected void emitbyte(int paramInt) {
        try {
            this.os.writeByte(paramInt);
            this.fAlign++;
        } catch (IOException iOException) {
            this.trouble = true;
        }
    }

    protected void emitword(int paramInt) {
        try {
            this.os.writeShort(paramInt);
        } catch (IOException iOException) {
            this.trouble = true;
        }
    }

    protected void emitint(int paramInt) {
        try {
            this.os.writeInt(paramInt);
        } catch (IOException iOException) {
            this.trouble = true;
        }
    }

    protected void emitstring(String paramString) {
        try {
            this.os.writeBytes(paramString);
            this.fAlign += paramString.length();
        } catch (IOException iOException) {
            this.trouble = true;
        }
    }

    protected final void emitop(int paramInt) {
        if ((this.fAlign & 0x1) == 1)
            emitbyte(0);
        emitword(paramInt);
    }

    protected final void emitcolor(Color paramColor) {
        emitword(paramColor.getRed() << 8);
        emitword(paramColor.getGreen() << 8);
        emitword(paramColor.getBlue() << 8);
    }

    protected final void emitrect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitword(paramInt2);
        emitword(paramInt1);
        emitword(paramInt2 + paramInt4);
        emitword(paramInt1 + paramInt3);
    }

    protected final void emitroundrect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
        emitop(11);
        emitword(paramInt7);
        emitword(paramInt6);
        emitop(paramInt1);
        emitrect(paramInt2, paramInt3, paramInt4, paramInt5);
    }

    protected void emitpolygon(Polygon paramPolygon) {
        int i = 10 + paramPolygon.npoints * 4;
        emitword(i);
        Rectangle rectangle = paramPolygon.getBounds();
        emitrect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        for (byte b = 0; b < paramPolygon.npoints; b++) {
            emitword(paramPolygon.ypoints[b]);
            emitword(paramPolygon.xpoints[b]);
        }
    }

    protected void emitcomment(int paramInt1, int paramInt2, String paramString) {
        if (paramInt2 == 0) {
            emitop(160);
            emitword(paramInt1);
        } else {
            emitop(161);
            emitword(paramInt1);
            emitword(paramString.length());
            emitstring(paramString);
        }
    }

    public final void beginPicGroup() {
        emitcomment(140, 0, null);
    }

    public final void endPicGroup() {
        emitcomment(141, 0, null);
    }

    public void laserLine(int paramInt1, int paramInt2) {
        emitop(161);
        emitword(182);
        emitword(4);
        emitword(paramInt1);
        emitword(paramInt2);
    }

    protected void emitHeader(int paramInt1, int paramInt2) {
        try {
            char c = 'Ȁ';
            byte[] arrayOfByte = new byte[c];
            this.os.write(arrayOfByte, 0, c);
        } catch (IOException iOException) {
            this.trouble = true;
        }
        boolean bool = false;
        emitword(bool);
        emitrect(0, 0, paramInt1, paramInt2);
        emitop(17);
        emitword(767);
        emitop(3072);
        emitint(-1);
        for (byte b = 0; b < 4;) {
            emitword(-1);
            emitword(0);
            b++;
        }
        emitint(-1);
        emitop(30);
        clipRect(this.clipr.x, this.clipr.y, this.clipr.width, this.clipr.height);
        beginPicGroup();
    }

    public Graphics create() {
        Gr2PICT gr2PICT = new Gr2PICT(this.os, this.g, 49);
        gr2PICT.font = this.font;
        gr2PICT.clipr = this.clipr;
        gr2PICT.clr = this.clr;
        return gr2PICT;
    }

    public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        Graphics graphics = create();
        graphics.translate(paramInt1, paramInt2);
        graphics.clipRect(0, 0, paramInt3, paramInt4);
        return graphics;
    }

    public void translate(int paramInt1, int paramInt2) {
        this.origin.x = paramInt1;
        this.origin.y = paramInt2;
        emitop(12);
        emitword(-paramInt1);
        emitword(-paramInt2);
    }

    public Color getColor() {
        return this.clr;
    }

    public void setColor(Color paramColor) {
        if (paramColor != null)
            this.clr = paramColor;
        emitop(26);
        emitcolor(this.clr);
    }

    public void setPaintMode() {
        emitop(8);
        emitword(8);
    }

    public void setXORMode(Color paramColor) {
        emitop(8);
        emitword(10);
        if (paramColor != null) {
            emitop(28);
            emitop(29);
            emitcolor(paramColor);
        }
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font paramFont) {
        if (paramFont != null) {
            this.font = paramFont;
            String str = this.font.getName();
            int i = QD.getQuickDrawFontNum(str);
            if (i >= 0) {
                emitop(3);
                emitword(i);
            } else {
                emitop(44);
                int m = str.length() + 1 + 2 + 2;
                emitword(m);
                emitword(QD.fontnum++);
                emitstring(str);
            }
            int j = 0;
            int k = this.font.getStyle();
            if ((k & 0x1) != 0)
                j |= 0x1;
            if ((k & 0x2) != 0)
                j |= 0x2;
            emitop(4);
            emitbyte(j);
            emitop(13);
            emitword(this.font.getSize());
        }
    }

    public FontMetrics getFontMetrics() {
        return getFontMetrics(getFont());
    }

    public FontMetrics getFontMetrics(Font paramFont) {
        return this.g.getFontMetrics(paramFont);
    }

    public Rectangle getClipRect() {
        return this.clipr;
    }

    public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.clipr = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
        emitop(1);
        byte b = 10;
        emitword(b);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    }

    public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(32);
        emitword(paramInt2);
        emitword(paramInt1);
        emitword(paramInt4);
        emitword(paramInt3);
    }

    public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(49);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(48);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(50);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        emitroundrect(64, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        emitroundrect(65, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    }

    public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
        Color color1 = getColor();
        Color color2 = color1.brighter();
        Color color3 = color1.darker();
        setColor(paramBoolean ? color2 : color3);
        drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4);
        drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2);
        setColor(paramBoolean ? color3 : color2);
        drawLine(paramInt1 + 1, paramInt2 + paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
        drawLine(paramInt1 + paramInt3, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
        setColor(color1);
    }

    public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
        Color color1 = getColor();
        Color color2 = color1.brighter();
        Color color3 = color1.darker();
        if (!paramBoolean)
            setColor(color3);
        fillRect(paramInt1 + 1, paramInt2 + 1, paramInt3 - 2, paramInt4 - 2);
        setColor(paramBoolean ? color2 : color3);
        drawLine(paramInt1, paramInt2, paramInt1, paramInt2 + paramInt4 - 1);
        drawLine(paramInt1 + 1, paramInt2, paramInt1 + paramInt3 - 2, paramInt2);
        setColor(paramBoolean ? color3 : color2);
        drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
        drawLine(paramInt1 + paramInt3 - 1, paramInt2, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 1);
        setColor(color1);
    }

    public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(80);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        emitop(81);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        emitop(96);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
        emitword(paramInt5 - 90);
        emitword(paramInt6);
    }

    public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        emitop(97);
        emitrect(paramInt1, paramInt2, paramInt3, paramInt4);
        emitword(paramInt5 + 90);
        emitword(paramInt6);
    }

    public void drawPolygon(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
        drawPolygon(new Polygon(paramArrayOfint1, paramArrayOfint2, paramInt));
    }

    public void drawPolygon(Polygon paramPolygon) {
        emitop(112);
        emitpolygon(paramPolygon);
    }

    public void fillPolygon(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
        fillPolygon(new Polygon(paramArrayOfint1, paramArrayOfint2, paramInt));
    }

    public void fillPolygon(Polygon paramPolygon) {
        emitop(113);
        emitpolygon(paramPolygon);
    }

    public void drawString(String paramString, int paramInt1, int paramInt2) {
        emitop(40);
        emitword(paramInt2);
        emitword(paramInt1);
        emitbyte(paramString.length());
        emitstring(paramString);
    }

    public void drawChars(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        drawString(new String(paramArrayOfchar, paramInt1, paramInt2), paramInt3, paramInt4);
    }

    public void drawBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        drawString(new String(paramArrayOfbyte, 0, paramInt1, paramInt2), paramInt3, paramInt4);
    }

    public boolean doImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver, Color paramColor) {
        return true;
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
        return doImage(paramImage, paramInt1, paramInt2, 0, 0, paramImageObserver, null);
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) {
        return doImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver, null);
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
        return doImage(paramImage, paramInt1, paramInt2, 0, 0, paramImageObserver, paramColor);
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
        return doImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver, paramColor);
    }

    public void dispose() {
        endPicGroup();
        emitop(255);
        try {
            this.os.flush();
        } catch (IOException iOException) {
            this.trouble = true;
        }
    }

    public void finalize() {
        super.finalize();
        dispose();
    }

    public String toString() {
        return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

    public boolean checkError() {
        return this.trouble;
    }

    public Rectangle getClipBounds() {
        return null;
    }

    public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    }

    public Shape getClip() {
        return null;
    }

    public void setClip(Shape paramShape) {
    }

    public void drawPolyline(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8,
        ImageObserver paramImageObserver) {
        return false;
    }

    public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor,
        ImageObserver paramImageObserver) {
        return false;
    }

    public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) {
    }
}
