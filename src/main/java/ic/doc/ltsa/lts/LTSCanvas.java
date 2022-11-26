package ic.doc.ltsa.lts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class LTSCanvas extends JPanel implements Scrollable {
    public static boolean fontFlag = false;
    public static boolean displayName = false;
    public static boolean newLabelFormat = true;
    Dimension initial = new Dimension(10, 10);
    Font nameFont;
    Font labelFont;
    static final int SEPARATION = 80;
    static final int ARCINC = 30;
    protected boolean singleMode = false;
    DrawMachine[] drawing;
    DrawMachine focus;
    protected MouseInputListener mouse;
    private Rectangle rr;
    private int maxUnitIncrement;

    public void setMode(boolean paramBoolean) {
        if (paramBoolean == this.singleMode)
            return;
        this.focus = null;
        if (this.drawing != null) {
            int i = this.drawing.length;
            this.drawing = new DrawMachine[i];
        }
        this.singleMode = paramBoolean;
        if (!this.singleMode) {
            this.mouse = new MyMouse(this);
            addMouseListener(this.mouse);
            addMouseMotionListener(this.mouse);
        } else if (this.mouse != null) {
            removeMouseListener(this.mouse);
            removeMouseMotionListener(this.mouse);
            this.mouse = null;
        }
        setPreferredSize(this.initial);
        revalidate();
        repaint();
    }

    public void setBigFont(boolean paramBoolean) {
        if (paramBoolean) {
            this.labelFont = new Font("Serif", 1, 14);
            this.nameFont = new Font("SansSerif", 1, 18);
        } else {
            this.labelFont = new Font("Serif", 0, 12);
            this.nameFont = new Font("SansSerif", 1, 14);
        }
        if (this.drawing != null)
            for (byte b = 0; b < this.drawing.length; b++) {
                if (this.drawing[b] != null)
                    this.drawing[b].setFonts(this.nameFont, this.labelFont);
            }
        repaint();
    }

    public void setDrawName(boolean paramBoolean) {
        displayName = paramBoolean;
        if (this.drawing != null)
            for (byte b = 0; b < this.drawing.length; b++) {
                if (this.drawing[b] != null)
                    this.drawing[b].setDrawName(displayName);
            }
        repaint();
    }

    public void setNewLabelFormat(boolean paramBoolean) {
        newLabelFormat = paramBoolean;
        if (this.drawing != null)
            for (byte b = 0; b < this.drawing.length; b++) {
                if (this.drawing[b] != null)
                    this.drawing[b].setNewLabelFormat(newLabelFormat);
            }
        repaint();
    }

    public void setMachines(int paramInt) {
        this.focus = null;
        if (paramInt > 0) {
            this.drawing = new DrawMachine[paramInt];
        } else {
            this.drawing = null;
        }
        setPreferredSize(this.initial);
        revalidate();
        repaint();
    }

    public void draw(int paramInt1, CompactState paramCompactState, int paramInt2, int paramInt3, String paramString) {
        if (paramCompactState == null || paramInt1 >= this.drawing.length) {
            this.drawing = null;
            repaint();
            return;
        }
        if (this.drawing[paramInt1] == null)
            this.drawing[paramInt1] = new DrawMachine(paramCompactState, this, this.nameFont, this.labelFont, displayName, newLabelFormat, 80, 30);
        if (this.singleMode)
            this.focus = this.drawing[paramInt1];
        this.drawing[paramInt1].select(paramInt2, paramInt3, paramString);
        Dimension dimension1 = this.drawing[paramInt1].getSize();
        Dimension dimension2 = getPreferredSize();
        setPreferredSize(new Dimension(Math.max(dimension2.width, dimension1.width), Math.max(dimension2.height, dimension1.height)));
        revalidate();
        repaint();
    }

    public void clear(int paramInt) {
        this.drawing[paramInt] = null;
        repaint();
    }

    public int clearSelected() {
        if (this.focus == null || this.singleMode || this.drawing == null)
            return -1;
        byte b;
        for (b = 0; this.drawing[b] != this.focus; b++);
        this.focus = null;
        this.drawing[b] = null;
        repaint();
        return b;
    }

    public LTSCanvas(boolean paramBoolean) {
        this.rr = new Rectangle();
        this.maxUnitIncrement = 1;
        setBigFont(fontFlag);
        setBackground(Color.white);
        this.singleMode = paramBoolean;
        if (!this.singleMode) {
            this.mouse = new MyMouse(this);
            addMouseListener(this.mouse);
            addMouseMotionListener(this.mouse);
        }
    }

    public void stretchHorizontal(int paramInt) {
        if (this.focus != null) {
            this.focus.setStretch(false, paramInt, 0);
            this.focus.getRect(this.rr);
            Dimension dimension = getPreferredSize();
            setPreferredSize(new Dimension(Math.max(dimension.width, this.rr.x + this.rr.width), Math.max(dimension.height, this.rr.y + this.rr.height)));
            revalidate();
            repaint();
        }
    }

    public void stretchVertical(int paramInt) {
        if (this.focus != null) {
            this.focus.setStretch(false, 0, paramInt);
            this.focus.getRect(this.rr);
            Dimension dimension = getPreferredSize();
            setPreferredSize(new Dimension(Math.max(dimension.width, this.rr.x + this.rr.width), Math.max(dimension.height, this.rr.y + this.rr.height)));
            revalidate();
            repaint();
        }
    }

    public void select(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2, String paramString) {
        if (this.drawing == null)
            return;
        for (byte b = 0; b < paramInt; b++) {
            if (this.drawing[b] != null) {
                boolean bool1 = (paramArrayOfint1 != null) ? paramArrayOfint1[b] : false;
                boolean bool2 = (paramArrayOfint2 != null) ? paramArrayOfint2[b] : false;
                this.drawing[b].select(bool1, bool2, paramString);
            }
        }
        repaint();
    }

    public DrawMachine getDrawing() {
        return this.focus;
    }

    public void paintComponent(Graphics paramGraphics) {
        super.paintComponent(paramGraphics);
        Graphics2D graphics2D = (Graphics2D) paramGraphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        if (this.drawing != null && !this.singleMode)
            for (byte b = 0; b < this.drawing.length; b++) {
                if (this.drawing[b] != null && (this.drawing[b] != this.focus || this.focus == null))
                    this.drawing[b].draw(paramGraphics);
            }
        if (this.focus != null)
            this.focus.draw(paramGraphics);
    }

    class MyMouse extends MouseInputAdapter {
        Point start;
        Rectangle r;
        private final LTSCanvas this$0;

        MyMouse(LTSCanvas this$0) {
            this.this$0 = this$0;
            this.start = null;
            this.r = new Rectangle();
        }

        public void mousePressed(MouseEvent param1MouseEvent) {
            if (this.this$0.drawing != null) {
                if (this.this$0.focus != null) {
                    this.this$0.focus.setSelected(false);
                    this.this$0.focus = null;
                    this.this$0.repaint();
                }
                for (byte b = 0; b < this.this$0.drawing.length; b++) {
                    if (this.this$0.drawing[b] != null) {
                        this.this$0.drawing[b].getRect(this.r);
                        if (this.r.contains(param1MouseEvent.getPoint())) {
                            this.this$0.focus = this.this$0.drawing[b];
                            this.this$0.focus.setSelected(true);
                            this.start = param1MouseEvent.getPoint();
                            this.this$0.repaint();
                            return;
                        }
                    }
                }
            }
        }

        public void mouseDragged(MouseEvent param1MouseEvent) {
            if (this.this$0.focus != null) {
                this.this$0.focus.getRect(this.r);
                Point point = param1MouseEvent.getPoint();
                if (this.start != null) {
                    double d1 = point.getX() - this.start.getX();
                    int i = (int) (this.r.x + d1);
                    double d2 = point.getY() - this.start.getY();
                    int j = (int) (this.r.y + d2);
                    this.this$0.focus.setPos((i > 0) ? i : 0, (j > 0) ? j : 0);
                    this.start = point;
                    this.this$0.repaint();
                }
            }
        }

        public void mouseReleased(MouseEvent param1MouseEvent) {
            this.start = null;
            if (this.this$0.focus != null) {
                this.this$0.focus.getRect(this.r);
                if (!this.r.contains(param1MouseEvent.getPoint())) {
                    this.this$0.focus.setSelected(false);
                    this.this$0.focus = null;
                    this.this$0.repaint();
                } else {
                    Dimension dimension = this.this$0.getPreferredSize();
                    this.this$0.setPreferredSize(new Dimension(Math.max(dimension.width, this.r.x + this.r.width), Math.max(dimension.height, this.r.y + this.r.height)));
                    this.this$0.revalidate();
                }
            }
        }
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
        return this.maxUnitIncrement;
    }

    public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
        if (paramInt1 == 0)
            return paramRectangle.width - 80;
        return paramRectangle.height - 30;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int paramInt) {
        this.maxUnitIncrement = paramInt;
    }

    public boolean isFocusTraversable() {
        return true;
    }
}
