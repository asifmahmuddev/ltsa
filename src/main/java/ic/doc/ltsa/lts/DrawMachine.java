package ic.doc.ltsa.lts;

import ic.doc.extension.Exportable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.BitSet;
import javax.swing.JPanel;

public class DrawMachine implements Exportable {
    public static int MAXDRAWSTATES = 64;
    static final int STATESIZE = 30;
    Font labelFont;
    Font nameFont;
    Font stateFont = new Font("SansSerif", 1, 18);
    protected boolean displayName = false;
    protected boolean newLabelFormat = true;
    protected boolean selectedMachine = false;
    int SEPARATION;
    int ARCINC;
    int topX = 0;
    int topY = 0;
    int zeroX;
    int zeroY;
    int heightAboveCenter;
    int nameWidth = 0;
    Dimension size;
    private int errorState = 0;
    private int lastselected = -3;
    private int selected = 0;
    private String lastaction;
    CompactState mach = null;
    BitSet accepting;
    JPanel parent;
    private int[] arrowX;
    private int[] arrowY;

    public void setDrawName(boolean paramBoolean) {
        this.displayName = paramBoolean;
        this.size = computeDimension(this.mach);
    }

    public void setNewLabelFormat(boolean paramBoolean) {
        this.newLabelFormat = paramBoolean;
        if (this.newLabelFormat)
            initCompactLabels();
        this.size = computeDimension(this.mach);
    }

    public void setFonts(Font paramFont1, Font paramFont2) {
        this.nameFont = paramFont1;
        this.labelFont = paramFont2;
        this.size = computeDimension(this.mach);
    }

    public void setStretch(boolean paramBoolean, int paramInt1, int paramInt2) {
        if (paramBoolean) {
            this.SEPARATION = paramInt1;
            this.ARCINC = paramInt2;
        } else {
            if (this.SEPARATION + paramInt1 > 10)
                this.SEPARATION += paramInt1;
            if (this.ARCINC + paramInt2 > 5)
                this.ARCINC += paramInt2;
        }
        this.size = computeDimension(this.mach);
    }

    public void select(int paramInt1, int paramInt2, String paramString) {
        this.lastselected = paramInt1;
        this.selected = paramInt2;
        this.lastaction = paramString;
    }

    public void setPos(int paramInt1, int paramInt2) {
        this.topX = paramInt1;
        this.topY = paramInt2;
    }

    public boolean isSelected() {
        return this.selectedMachine;
    }

    public void setSelected(boolean paramBoolean) {
        this.selectedMachine = paramBoolean;
    }

    public Dimension getSize() {
        return this.size;
    }

    public void getRect(Rectangle paramRectangle) {
        paramRectangle.x = this.topX;
        paramRectangle.y = this.topY;
        paramRectangle.width = this.size.width;
        paramRectangle.height = this.size.height;
    }

    public CompactState getMachine() {
        return this.mach;
    }

    protected Dimension computeDimension(CompactState paramCompactState) {
        int i = 0;
        if (this.displayName) {
            Graphics graphics1 = this.parent.getGraphics();
            if (graphics1 != null) {
                graphics1.setFont(this.nameFont);
                FontMetrics fontMetrics = graphics1.getFontMetrics();
                this.nameWidth = fontMetrics.stringWidth(this.mach.name);
                i = fontMetrics.getHeight();
            } else {
                this.nameWidth = this.SEPARATION;
            }
        } else {
            this.nameWidth = 0;
        }
        if (paramCompactState.maxStates > MAXDRAWSTATES)
            return new Dimension(220 + this.nameWidth, 50);
        String str = null;
        if (!this.newLabelFormat) {
            EventState eventState = paramCompactState.states[paramCompactState.maxStates - 1];
            while (eventState != null) {
                EventState eventState1 = eventState;
                while (eventState1 != null) {
                    if (eventState1.next == paramCompactState.maxStates - 1)
                        if (str == null) {
                            str = paramCompactState.alphabet[eventState1.event];
                        } else {
                            String str1 = paramCompactState.alphabet[eventState1.event];
                            if (str1.length() > str.length())
                                str = str1;
                        }
                    eventState1 = eventState1.nondet;
                }
                eventState = eventState.list;
            }
        } else {
            str = this.labels[paramCompactState.maxStates][paramCompactState.maxStates];
        }
        int j = 10;
        if (str != null) {
            Graphics graphics1 = this.parent.getGraphics();
            if (graphics1 != null) {
                graphics1.setFont(this.labelFont);
                FontMetrics fontMetrics = graphics1.getFontMetrics();
                j = fontMetrics.stringWidth(str);
                j += this.SEPARATION / 3;
            } else {
                j = this.SEPARATION;
            }
        }
        this.errorState = 0;
        for (byte b1 = 0; b1 < paramCompactState.maxStates; b1++) {
            if (EventState.hasState(paramCompactState.states[b1], -1))
                this.errorState = 1;
        }
        int k = 0;
        byte b2 = 0;
        int m = 0;
        byte b3 = 0;
        for (byte b4 = 0; b4 < paramCompactState.maxStates; b4++) {
            int[] arrayOfInt = new int[paramCompactState.maxStates + 1];
            int i4 = 0;
            int i5 = 0;
            boolean bool1 = false;
            boolean bool2 = false;
            EventState eventState = paramCompactState.states[b4];
            while (eventState != null) {
                EventState eventState1 = eventState;
                while (eventState1 != null) {
                    arrayOfInt[eventState1.next + 1] = arrayOfInt[eventState1.next + 1] + 1;
                    int i6 = eventState1.next - b4;
                    if (i6 > k || (i6 == k && arrayOfInt[eventState1.next + 1] > b2)) {
                        k = i6;
                        i4 = eventState1.next + 1;
                        bool1 = true;
                    }
                    if (i6 < m || (i6 == m && arrayOfInt[eventState1.next + 1] > b3)) {
                        m = i6;
                        i5 = eventState1.next + 1;
                        bool2 = true;
                    }
                    eventState1 = eventState1.nondet;
                }
                eventState = eventState.list;
            }
            if (bool1)
                b2 = this.newLabelFormat ? 1 : arrayOfInt[i4];
            if (bool2)
                b3 = this.newLabelFormat ? 1 : arrayOfInt[i5];
        }
        if (paramCompactState.maxStates == 1)
            b2 = 0;
        int n = 10;
        Graphics graphics = this.parent.getGraphics();
        if (graphics != null) {
            graphics.setFont(this.labelFont);
            FontMetrics fontMetrics = graphics.getFontMetrics();
            n = fontMetrics.getHeight();
        }
        this.heightAboveCenter = (k != 0) ? (this.ARCINC * k / 2) : (15 + i);
        this.heightAboveCenter = this.heightAboveCenter + b2 * n + 10;
        int i1 = (m != 0) ? (this.ARCINC * Math.abs(m) / 2) : 15;
        i1 = i1 + b3 * n + 10;
        int i2 = (this.errorState == 0) ? (10 + this.nameWidth + 30 + j + (paramCompactState.maxStates - 1) * this.SEPARATION) : (40 + j + paramCompactState.maxStates * this.SEPARATION);
        int i3 = this.heightAboveCenter + i1;
        return new Dimension(i2, i3);
    }

    public void fileDraw(Graphics paramGraphics) {
        int i = this.topX;
        int j = this.topY;
        boolean bool = this.selectedMachine;
        this.topX = 0;
        this.topY = 0;
        this.selectedMachine = false;
        draw(paramGraphics);
        this.topX = i;
        this.topY = j;
        this.selectedMachine = bool;
    }

    public Graphics getGraphics() {
        return this.parent.getGraphics();
    }

    public void draw(Graphics paramGraphics) {
        CompactState compactState = this.mach;
        if (compactState == null)
            return;
        if (this.selectedMachine) {
            paramGraphics.setColor(Color.white);
            paramGraphics.fillRect(this.topX, this.topY, this.size.width, this.size.height);
        }
        int i = 0;
        if (this.displayName && this.errorState == 0)
            i = this.nameWidth;
        this.zeroX = this.topX + 10 + this.errorState * this.SEPARATION + i;
        this.zeroY = this.topY + this.heightAboveCenter - 15;
        if (compactState.maxStates > MAXDRAWSTATES) {
            paramGraphics.setColor(Color.black);
            paramGraphics.setFont(this.nameFont);
            paramGraphics.drawString(compactState.name + " -- too many states: " + compactState.maxStates, this.topX, this.topY + 20);
        } else {
            paramGraphics.setFont(this.nameFont);
            FontMetrics fontMetrics = paramGraphics.getFontMetrics();
            int j = fontMetrics.stringWidth(compactState.name);
            paramGraphics.setColor(Color.black);
            if (this.displayName)
                paramGraphics.drawString(compactState.name, this.zeroX - j, this.zeroY - 5);
            for (byte b1 = 0; b1 < compactState.maxStates; b1++) {
                int[] arrayOfInt = new int[compactState.maxStates + 1];
                EventState eventState = compactState.states[b1];
                while (eventState != null) {
                    EventState eventState1 = eventState;
                    String str = compactState.alphabet[eventState1.event];
                    if (str.charAt(0) != '@')
                        while (eventState1 != null) {
                            arrayOfInt[eventState1.next + 1] = arrayOfInt[eventState1.next + 1] + 1;
                            drawTransition(paramGraphics, b1, eventState1.next, str, arrayOfInt[eventState1.next + 1], (b1 == this.lastselected && eventState1.next == this.selected), false);
                            eventState1 = eventState1.nondet;
                        }
                    eventState = eventState.list;
                }
            }
            for (byte b2 = 0; b2 < compactState.maxStates; b2++) {
                int[] arrayOfInt = new int[compactState.maxStates + 1];
                EventState eventState = compactState.states[b2];
                while (eventState != null) {
                    EventState eventState1 = eventState;
                    String str = compactState.alphabet[eventState1.event];
                    if (str.charAt(0) != '@')
                        while (eventState1 != null) {
                            arrayOfInt[eventState1.next + 1] = arrayOfInt[eventState1.next + 1] + 1;
                            if (!this.newLabelFormat) {
                                drawTransition(paramGraphics, b2, eventState1.next, str, arrayOfInt[eventState1.next + 1], (b2 == this.lastselected && eventState1.next == this.selected), true);
                            } else if (arrayOfInt[eventState1.next + 1] == 1) {
                                drawTransition(paramGraphics, b2, eventState1.next, this.labels[b2 + 1][eventState1.next + 1], arrayOfInt[eventState1.next + 1],
                                    (b2 == this.lastselected && eventState1.next == this.selected), true);
                            }
                            eventState1 = eventState1.nondet;
                        }
                    eventState = eventState.list;
                }
            }
            for (int k = -this.errorState; k < compactState.maxStates; k++)
                drawState(paramGraphics, k, (k == this.selected));
        }
        if (this.selectedMachine) {
            paramGraphics.setColor(Color.gray);
            paramGraphics.drawRect(this.topX, this.topY, this.size.width, this.size.height);
        }
    }

    private void drawState(Graphics paramGraphics, int paramInt, boolean paramBoolean) {
        int i = this.zeroX + paramInt * this.SEPARATION;
        int j = this.zeroY;
        if (paramBoolean) {
            paramGraphics.setColor(Color.red);
        } else {
            paramGraphics.setColor(Color.cyan);
        }
        if (paramInt >= 0 && this.accepting.get(paramInt)) {
            paramGraphics.fillArc(i - 3, j - 3, 36, 36, 0, 360);
        } else {
            paramGraphics.fillArc(i, j, 30, 30, 0, 360);
        }
        paramGraphics.setColor(Color.black);
        paramGraphics.setFont(this.stateFont);
        if (paramInt >= 0 && this.accepting.get(paramInt))
            paramGraphics.drawArc(i - 3, j - 3, 36, 36, 0, 360);
        paramGraphics.drawArc(i, j, 30, 30, 0, 360);
        FontMetrics fontMetrics = paramGraphics.getFontMetrics();
        String str = (paramInt == this.mach.endseq) ? "E" : ("" + paramInt);
        int k = i + 15 - fontMetrics.stringWidth(str) / 2;
        int m = j + 15 + fontMetrics.getHeight() / 3;
        paramGraphics.drawString(str, k, m);
    }

    private void drawTransition(Graphics paramGraphics, int paramInt1, int paramInt2, String paramString, int paramInt3, boolean paramBoolean1, boolean paramBoolean2) {
        if (paramBoolean1) {
            paramGraphics.setColor(Color.red);
        } else {
            paramGraphics.setColor(Color.black);
        }
        byte b1 = (paramInt2 <= paramInt1) ? -1 : 1;
        int i = (paramInt2 < paramInt1) ? paramInt2 : paramInt1;
        int j = this.zeroX + i * this.SEPARATION + 15;
        int k = (paramInt2 != paramInt1) ? (this.SEPARATION * Math.abs(paramInt1 - paramInt2)) : (this.SEPARATION / 3);
        byte b2 = (paramInt2 != paramInt1) ? (this.ARCINC * Math.abs(paramInt1 - paramInt2)) : 25;
        int m = this.zeroY - (b2 - 30) / 2;
        if (paramInt3 == 1 && !paramBoolean2)
            if (paramInt1 != paramInt2) {
                paramGraphics.drawArc(j, m, k, b2, 0, 180 * b1);
                if (b1 > 0) {
                    drawArrow(paramGraphics, j + k / 2, m, arrowForward);
                } else {
                    drawArrow(paramGraphics, j + k / 2, m + b2 - 1, arrowBackward);
                }
            } else {
                paramGraphics.drawArc(j, m, k, b2, 0, 360);
                drawArrow(paramGraphics, j + k, m + b2 / 2, arrowDown);
            }
        if (!paramBoolean2)
            return;
        paramInt3++;
        paramGraphics.setFont(this.labelFont);
        FontMetrics fontMetrics = paramGraphics.getFontMetrics();
        int n = fontMetrics.getMaxAscent() / 3;
        int i1 = j + k / 2 - fontMetrics.stringWidth(paramString) / 2;
        if (paramInt2 == paramInt1)
            i1 = j + k + 2;
        int i2 = (b1 > 0) ? (m + n) : (m + b2 + n);
        if (paramInt2 == paramInt1)
            i2 = m + b2 / 2 + n;
        if (paramInt3 > 1)
            i2 -= (paramInt3 - 1) * fontMetrics.getHeight() * b1;
        paramGraphics.setColor(Color.white);
        paramGraphics.fillRect(i1, i2 - fontMetrics.getMaxAscent(), fontMetrics.stringWidth(paramString), fontMetrics.getHeight());
        if (paramBoolean1 && ((this.lastaction != null && this.lastaction.equals(paramString)) || this.newLabelFormat)) {
            paramGraphics.setColor(Color.red);
        } else {
            paramGraphics.setColor(Color.black);
        }
        paramGraphics.drawString(paramString, i1, i2);
    }

    public DrawMachine(CompactState paramCompactState, JPanel paramJPanel, Font paramFont1, Font paramFont2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
        this.arrowX = new int[3];
        this.arrowY = new int[3];
        this.mach = paramCompactState;
        this.parent = paramJPanel;
        this.nameFont = paramFont1;
        this.labelFont = paramFont2;
        this.displayName = paramBoolean1;
        this.newLabelFormat = paramBoolean2;
        this.SEPARATION = paramInt1;
        this.ARCINC = paramInt2;
        this.accepting = this.mach.accepting();
        if (this.newLabelFormat)
            initCompactLabels();
        this.size = computeDimension(this.mach);
    }

    private static int arrowForward = 1;
    private static int arrowBackward = 2;
    private static int arrowDown = 3;
    String[][] labels;

    private void drawArrow(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
        if (paramInt3 == arrowForward) {
            this.arrowX[0] = paramInt1 - 5;
            this.arrowY[0] = paramInt2 - 5;
            this.arrowX[1] = paramInt1 + 5;
            this.arrowY[1] = paramInt2;
            this.arrowX[2] = paramInt1 - 5;
            this.arrowY[2] = paramInt2 + 5;
        } else if (paramInt3 == arrowBackward) {
            this.arrowX[0] = paramInt1 + 5;
            this.arrowY[0] = paramInt2 - 5;
            this.arrowX[1] = paramInt1 - 5;
            this.arrowY[1] = paramInt2;
            this.arrowX[2] = paramInt1 + 5;
            this.arrowY[2] = paramInt2 + 5;
        } else if (paramInt3 == arrowDown) {
            this.arrowX[0] = paramInt1 - 5;
            this.arrowY[0] = paramInt2 - 5;
            this.arrowX[1] = paramInt1 + 5;
            this.arrowY[1] = paramInt2 - 5;
            this.arrowX[2] = paramInt1;
            this.arrowY[2] = paramInt2 + 5;
        }
        paramGraphics.fillPolygon(this.arrowX, this.arrowY, 3);
    }

    private void initCompactLabels() {
        if (this.mach == null)
            return;
        if (this.mach.maxStates > MAXDRAWSTATES)
            return;
        this.labels = new String[this.mach.maxStates + 1][this.mach.maxStates + 1];
        for (byte b = 0; b < this.mach.maxStates; b++) {
            EventState eventState = EventState.transpose(this.mach.states[b]);
            while (eventState != null) {
                String[] arrayOfString = EventState.eventsToNextNoAccept(eventState, this.mach.alphabet);
                Alphabet alphabet = new Alphabet(arrayOfString);
                this.labels[b + 1][eventState.next + 1] = alphabet.toString();
                eventState = eventState.list;
            }
        }
    }
}
