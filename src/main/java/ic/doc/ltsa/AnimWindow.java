package ic.doc.ltsa;

import ic.doc.extension.Animator;
import ic.doc.ltsa.lts.RunMenu;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.BitSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AnimWindow extends JFrame {
    public static boolean fontFlag = false;
    JTextArea output;
    Animator animator;
    JCheckBox[] choices;
    Font f1;
    BitSet actions;
    Color priority;
    BitSet pactions;
    String[] modelAlphabet;
    JButton step;
    JButton run;
    public boolean autoRun = false;
    private static final int STEPLIMIT = 64;
    protected boolean traceMode = false;

    AnimWindow(Animator paramAnimator, RunMenu paramRunMenu, boolean paramBoolean1, boolean paramBoolean2) {
        this.autoRun = paramBoolean1;
        this.traceMode = paramBoolean2;
        if (fontFlag) {
            this.f1 = new Font("SansSerif", 1, 16);
        } else {
            this.f1 = new Font("SansSerif", 1, 12);
        }
        this.animator = paramAnimator;
        setDefaultCloseOperation(2);
        setBackground(Color.white);
        getContentPane().setLayout(new BoxLayout(getContentPane(), 0));
        this.output = new JTextArea("", 15, 15);
        this.output.setEditable(false);
        this.output.setFont(this.f1);
        this.output.setBackground(Color.white);
        this.output.setBorder(new EmptyBorder(0, 5, 0, 0));
        JScrollPane jScrollPane1 = new JScrollPane(this.output, 22, 30);
        getContentPane().add(jScrollPane1);
        if (paramRunMenu == null) {
            this.actions = paramAnimator.initialise(null);
        } else {
            this.actions = paramAnimator.initialise(paramRunMenu.alphabet);
        }
        if (this.traceMode) {
            setTitle("Replay Animator");
        } else {
            setTitle("Animator");
        }
        this.step = new JButton("Step");
        this.step.setFont(this.f1);
        this.step.addActionListener(new AnimAction(this, this.traceMode ? -3 : -1));
        this.run = new JButton("Run");
        this.run.addActionListener(new AnimAction(this, this.traceMode ? -4 : -2));
        this.run.setFont(this.f1);
        Box box1 = Box.createHorizontalBox();
        box1.add(this.run);
        box1.add(this.step);
        String[] arrayOfString = paramAnimator.getMenuNames();
        this.modelAlphabet = paramAnimator.getAllNames();
        if (paramAnimator.getPriority()) {
            this.priority = Color.cyan;
        } else {
            this.priority = Color.pink;
        }
        this.pactions = paramAnimator.getPriorityActions();
        this.choices = new JCheckBox[arrayOfString.length];
        Box box2 = Box.createVerticalBox();
        for (byte b = 1; b < arrayOfString.length; b++) {
            box2.add(this.choices[b] = new JCheckBox(arrayOfString[b], null, this.actions.get(b)));
            this.choices[b].setFont(this.f1);
            this.choices[b].addActionListener(new AnimAction(this, b));
            if (this.traceMode)
                this.choices[b].setEnabled(false);
            if (this.pactions != null && this.pactions.get(b))
                this.choices[b].setBackground(this.priority);
        }
        box2.add(Box.createHorizontalStrut(10));
        boolean bool = (paramAnimator.nonMenuChoice() || this.traceMode) ? true : false;
        this.step.setEnabled(bool);
        this.run.setEnabled(bool);
        if (empty(this.actions) && !bool)
            outln("STOP");
        JScrollPane jScrollPane2 = new JScrollPane(box2, 20, 31);
        jScrollPane2.setBorder(new EmptyBorder(0, 0, 0, 0));
        Box box3 = Box.createVerticalBox();
        box3.add(box1);
        box3.add(jScrollPane2);
        getContentPane().add(box3);
        box2.setBackground(Color.white);
        validate();
        if (this.autoRun)
            dostep(-2);
    }

    private void dostep(int paramInt) {
        if (this.animator.isError())
            return;
        if (paramInt == -1) {
            this.actions = this.animator.singleStep();
            outAction();
        } else if (paramInt == -2) {
            this.actions = multiStep((BitSet) null);
        } else if (paramInt == -3) {
            this.actions = this.animator.traceStep();
            outAction();
        } else if (paramInt == -4) {
            this.actions = multiTraceStep((BitSet) null);
        } else if (!this.choices[paramInt].isSelected()) {
            System.out.println("******* About to do animator.menuStep(" + paramInt + ") ******");
            this.actions = this.animator.menuStep(paramInt);
            outAction();
        }
        if (this.actions == null)
            return;
        if (this.autoRun && !this.traceMode)
            this.actions = multiStep(this.actions);
        for (byte b = 1; b < this.choices.length; b++)
            this.choices[b].setSelected(this.actions.get(b));
        if (!this.traceMode) {
            boolean bool = this.animator.nonMenuChoice();
            this.step.setEnabled(bool);
            this.run.setEnabled(bool);
            if (empty(this.actions) && !bool && !this.animator.isError())
                if (this.animator.isEnd()) {
                    outln("END");
                } else {
                    outln("STOP");
                }
        } else {
            boolean bool = this.animator.traceChoice();
            this.step.setEnabled(bool);
            this.run.setEnabled(bool);
            if (!bool && !this.animator.isError())
                if (empty(this.actions)) {
                    if (this.animator.isEnd()) {
                        outln("END");
                    } else {
                        outln("STOP");
                    }
                } else {
                    outln("DIVERGED FROM TRACE");
                }
        }
        repaint();
    }

    private BitSet multiStep(BitSet paramBitSet) {
        byte b = 0;
        while (this.animator.nonMenuChoice()) {
            paramBitSet = this.animator.singleStep();
            outAction();
            if (++b > 64) {
                outln("LOOP");
                return paramBitSet;
            }
        }
        return paramBitSet;
    }

    private BitSet multiTraceStep(BitSet paramBitSet) {
        while (this.animator.traceChoice()) {
            paramBitSet = this.animator.traceStep();
            outAction();
        }
        return paramBitSet;
    }

    public void out(String paramString) {
        this.output.append(paramString);
    }

    public void outln(String paramString) {
        this.output.append(paramString + "\n");
    }

    public void clearOutput() {
        this.output.setText("");
    }

    private boolean empty(BitSet paramBitSet) {
        for (byte b = 0; b < paramBitSet.size(); b++) {
            if (paramBitSet.get(b))
                return false;
        }
        return true;
    }

    private void outAction() {
        outln(" " + this.modelAlphabet[this.animator.actionChosen()]);
        if (this.animator.isError())
            outln("ERROR");
    }

    class AnimAction implements ActionListener {
        int choice;
        private final AnimWindow this$0;

        AnimAction(AnimWindow this$0, int param1Int) {
            this.this$0 = this$0;
            this.choice = param1Int;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.dostep(this.choice);
        }
    }
}
