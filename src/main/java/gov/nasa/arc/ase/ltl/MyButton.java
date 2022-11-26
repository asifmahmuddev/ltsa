package gov.nasa.arc.ase.ltl;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.MouseEvent;

class MyButton extends Button {
    Frame f;
    String label;
    Window win;

    public MyButton(String paramString, Frame paramFrame) {
        super(paramString);
        this.label = paramString;
        this.f = paramFrame;
        enableEvents(16L);
    }

    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }

    protected void processMouseEvent(MouseEvent paramMouseEvent) {
        if (LTL2BuchiApplet.showHelp)
            if (paramMouseEvent.getID() == 504) {
                int i = (this.f.getBounds()).x + (this.f.getBounds()).width;
                if (this.label.equals("Show GEN")) {
                    this.win = new Window(this.f);
                    TextArea textArea = new TextArea("Show Generalized buchi automaton", 1, 30, 3);
                    textArea.setBackground(Color.yellow);
                    this.win.setLocation(i - 240, (getLocationOnScreen()).y);
                    this.win.add(textArea);
                    this.win.pack();
                    this.win.show();
                } else if (this.label.equals("Show SSR")) {
                    this.win = new Window(this.f);
                    TextArea textArea = new TextArea("Show Super Set Reduced gba", 1, 25, 3);
                    textArea.setBackground(Color.yellow);
                    this.win.setLocation(i - 200, (getLocationOnScreen()).y);
                    this.win.add(textArea);
                    this.win.pack();
                    this.win.show();
                } else if (this.label.equals("Show DEG")) {
                    this.win = new Window(this.f);
                    TextArea textArea = new TextArea("Show Degeneralized buchi automaton", 1, 33, 3);
                    textArea.setBackground(Color.yellow);
                    this.win.setLocation(i - 260, (getLocationOnScreen()).y);
                    this.win.add(textArea);
                    this.win.pack();
                    this.win.show();
                } else if (this.label.equals("Show SCC")) {
                    this.win = new Window(this.f);
                    TextArea textArea = new TextArea("Show Strongly Connected Component reduced ba", 1, 42, 3);
                    textArea.setBackground(Color.yellow);
                    this.win.setLocation(i - 325, (getLocationOnScreen()).y);
                    this.win.add(textArea);
                    this.win.pack();
                    this.win.show();
                } else if (this.label.equals("Show SFS")) {
                    this.win = new Window(this.f);
                    TextArea textArea = new TextArea("Show Strong Fair Simulation reduced ba", 1, 35, 3);
                    textArea.setBackground(Color.yellow);
                    this.win.setLocation(i - 275, (getLocationOnScreen()).y);
                    this.win.add(textArea);
                    this.win.pack();
                    this.win.show();
                }
            } else if (paramMouseEvent.getID() == 505) {
                this.win.dispose();
            }
    }

    public static void main(String[] paramArrayOfString) {
        Frame frame = new Frame("MyButton");
        MyButton myButton = new MyButton("Show GEN", frame);
        frame.add(myButton);
        frame.pack();
        frame.show();
    }
}
