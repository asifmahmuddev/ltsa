package ic.doc.ltsa;

import ic.doc.ltsa.dclap.Gr2PICT;
import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.CompositeState;
import ic.doc.ltsa.lts.DrawMachine;
import ic.doc.ltsa.lts.EventClient;
import ic.doc.ltsa.lts.EventManager;
import ic.doc.ltsa.lts.LTSCanvas;
import ic.doc.ltsa.lts.LTSEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LTSDrawWindow extends JSplitPane implements EventClient {
    LTSCanvas output;
    EventManager eman;
    CompositeState cs;
    int[] lastEvent;
    int[] prevEvent;
    String lastName;
    int Nmach = 0;
    int hasC = 0;
    CompactState[] sm;
    boolean[] machineHasAction;
    boolean[] machineToDrawSet;
    public static boolean fontFlag = false;
    public static boolean singleMode = false;
    JList list;
    JScrollPane left;
    JScrollPane right;
    Font f1 = new Font("Monospaced", 0, 12);
    Font f2 = new Font("Monospaced", 1, 16);
    Font f3 = new Font("SansSerif", 0, 12);
    Font f4 = new Font("SansSerif", 1, 16);
    ImageIcon drawIcon = new ImageIcon(getClass().getResource("icon/draw.gif"));

    public LTSDrawWindow(CompositeState paramCompositeState, EventManager paramEventManager) {
        this.eman = paramEventManager;
        this.output = new LTSCanvas(singleMode);
        JPanel jPanel1 = new JPanel();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add("Center", (Component) this.output);
        this.right = new JScrollPane(jPanel1, 20, 30);
        this.list = new JList();
        this.list.setSelectionMode(0);
        this.list.addListSelectionListener(new PrintAction(this));
        this.list.setCellRenderer(new MyCellRenderer(this));
        this.left = new JScrollPane(this.list, 20, 30);
        JPanel jPanel2 = new JPanel(new BorderLayout());
        jPanel2.add("Center", this.right);
        JToolBar jToolBar = new JToolBar();
        jToolBar.setOrientation(1);
        jPanel2.add("West", jToolBar);
        jToolBar.add(createTool("icon/stretchHorizontal.gif", "Stretch Horizontal", new HStretchAction(this, 10)));
        jToolBar.add(createTool("icon/compressHorizontal.gif", "Compress Horizontal", new HStretchAction(this, -10)));
        jToolBar.add(createTool("icon/stretchVertical.gif", "Stretch Vertical", new VStretchAction(this, 10)));
        jToolBar.add(createTool("icon/compressVertical.gif", "Compress Vertical", new VStretchAction(this, -10)));
        if (paramEventManager != null)
            paramEventManager.addClient(this);
        new_machines(paramCompositeState);
        setLeftComponent(this.left);
        setRightComponent(jPanel2);
        setDividerLocation(200);
        setBigFont(fontFlag);
        validate();
        this.output.addKeyListener(new KeyPress(this));
        this.output.addMouseListener(new MyMouse(this));
    }

    class HStretchAction implements ActionListener {
        int increment;
        private final LTSDrawWindow this$0;

        HStretchAction(LTSDrawWindow this$0, int param1Int) {
            this.this$0 = this$0;
            this.increment = param1Int;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.output != null)
                this.this$0.output.stretchHorizontal(this.increment);
        }
    }

    class VStretchAction implements ActionListener {
        int increment;
        private final LTSDrawWindow this$0;

        VStretchAction(LTSDrawWindow this$0, int param1Int) {
            this.this$0 = this$0;
            this.increment = param1Int;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.output != null)
                this.this$0.output.stretchVertical(this.increment);
        }
    }

    class PrintAction implements ListSelectionListener {
        private final LTSDrawWindow this$0;

        PrintAction(LTSDrawWindow this$0) {
            this.this$0 = this$0;
        }

        public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            if (param1ListSelectionEvent.getValueIsAdjusting() && !LTSDrawWindow.singleMode)
                return;
            int i = this.this$0.list.getSelectedIndex();
            if (i < 0 || i >= this.this$0.Nmach)
                return;
            if (LTSDrawWindow.singleMode) {
                this.this$0.output.draw(i, this.this$0.sm[i], this.this$0.validMachine(i, this.this$0.prevEvent), this.this$0.validMachine(i, this.this$0.lastEvent), this.this$0.lastName);
            } else {
                if (!this.this$0.machineToDrawSet[i]) {
                    this.this$0.output.draw(i, this.this$0.sm[i], this.this$0.validMachine(i, this.this$0.prevEvent), this.this$0.validMachine(i, this.this$0.lastEvent), this.this$0.lastName);
                    this.this$0.machineToDrawSet[i] = true;
                } else {
                    this.this$0.output.clear(i);
                    this.this$0.machineToDrawSet[i] = false;
                }
                this.this$0.list.clearSelection();
            }
        }
    }

    private int validMachine(int paramInt, int[] paramArrayOfint) {
        if (paramArrayOfint != null && paramInt < this.Nmach - this.hasC)
            return paramArrayOfint[paramInt];
        return 0;
    }

    class KeyPress extends KeyAdapter {
        private final LTSDrawWindow this$0;

        KeyPress(LTSDrawWindow this$0) {
            this.this$0 = this$0;
        }

        public void keyPressed(KeyEvent param1KeyEvent) {
            if (this.this$0.output == null)
                return;
            int i = param1KeyEvent.getKeyCode();
            if (i == 37) {
                this.this$0.output.stretchHorizontal(-5);
            } else if (i == 39) {
                this.this$0.output.stretchHorizontal(5);
            } else if (i == 38) {
                this.this$0.output.stretchVertical(-5);
            } else if (i == 40) {
                this.this$0.output.stretchVertical(5);
            } else if (i == 8) {
                int j = this.this$0.output.clearSelected();
                if (j >= 0) {
                    this.this$0.machineToDrawSet[j] = false;
                    this.this$0.list.repaint();
                }
            }
        }
    }

    class MyMouse extends MouseAdapter {
        private final LTSDrawWindow this$0;

        MyMouse(LTSDrawWindow this$0) {
            this.this$0 = this$0;
        }

        public void mouseEntered(MouseEvent param1MouseEvent) {
            this.this$0.output.requestFocus();
        }
    }

    public void ltsAction(LTSEvent paramLTSEvent) {
        switch (paramLTSEvent.kind) {
            case 0 :
                this.prevEvent = this.lastEvent;
                this.lastEvent = (int[]) paramLTSEvent.info;
                this.lastName = paramLTSEvent.name;
                this.output.select(this.Nmach - this.hasC, this.prevEvent, this.lastEvent, paramLTSEvent.name);
                buttonHighlight(paramLTSEvent.name);
                break;
            case 1 :
                this.prevEvent = null;
                this.lastEvent = null;
                new_machines(this.cs = (CompositeState) paramLTSEvent.info);
                break;
        }
    }

    private void buttonHighlight(String paramString) {
        if (paramString == null && this.machineHasAction != null) {
            for (byte b = 0; b < this.machineHasAction.length; b++)
                this.machineHasAction[b] = false;
        } else if (this.machineHasAction != null) {
            for (byte b = 0; b < this.sm.length - this.hasC; b++)
                this.machineHasAction[b] = (!paramString.equals("tau") && this.sm[b].hasLabel(paramString));
        }
        this.list.repaint();
    }

    private void new_machines(CompositeState paramCompositeState) {
        this.hasC = (paramCompositeState != null && paramCompositeState.composition != null) ? 1 : 0;
        if (paramCompositeState != null && paramCompositeState.machines != null && paramCompositeState.machines.size() > 0) {
            this.sm = new CompactState[paramCompositeState.machines.size() + this.hasC];
            Enumeration enumeration = paramCompositeState.machines.elements();
            for (byte b1 = 0; enumeration.hasMoreElements(); b1++)
                this.sm[b1] = enumeration.nextElement();
            this.Nmach = this.sm.length;
            if (this.hasC == 1)
                this.sm[this.Nmach - 1] = paramCompositeState.composition;
            this.machineHasAction = new boolean[this.Nmach];
            this.machineToDrawSet = new boolean[this.Nmach];
        } else {
            this.Nmach = 0;
            this.machineHasAction = null;
            this.machineToDrawSet = null;
        }
        DefaultListModel defaultListModel = new DefaultListModel();
        for (byte b = 0; b < this.Nmach; b++) {
            if (this.hasC == 1 && b == this.Nmach - 1) {
                defaultListModel.addElement("||" + (this.sm[b]).name);
            } else {
                defaultListModel.addElement((this.sm[b]).name);
            }
        }
        this.list.setModel(defaultListModel);
        this.output.setMachines(this.Nmach);
    }

    protected JButton createTool(String paramString1, String paramString2, ActionListener paramActionListener) {
        JButton jButton = new JButton(this, new ImageIcon(getClass().getResource(paramString1))) {
            private final LTSDrawWindow this$0;

            public float getAlignmentY() {
                return 0.5F;
            }
        };
        jButton.setRequestFocusEnabled(false);
        jButton.setMargin(new Insets(0, 0, 0, 0));
        jButton.setToolTipText(paramString2);
        jButton.addActionListener(paramActionListener);
        return jButton;
    }

    public void setBigFont(boolean paramBoolean) {
        fontFlag = paramBoolean;
        this.output.setBigFont(paramBoolean);
    }

    public void setDrawName(boolean paramBoolean) {
        this.output.setDrawName(paramBoolean);
    }

    public void setNewLabelFormat(boolean paramBoolean) {
        this.output.setNewLabelFormat(paramBoolean);
    }

    public void setMode(boolean paramBoolean) {
        singleMode = paramBoolean;
        this.output.setMode(paramBoolean);
        this.list.clearSelection();
        if (this.Nmach > 0)
            this.machineToDrawSet = new boolean[this.Nmach];
        this.list.repaint();
    }

    public void removeClient() {
        if (this.eman != null)
            this.eman.removeClient(this);
    }

    class MyCellRenderer extends JLabel implements ListCellRenderer {
        private final LTSDrawWindow this$0;

        public MyCellRenderer(LTSDrawWindow this$0) {
            this.this$0 = this$0;
            setOpaque(true);
            setHorizontalTextPosition(2);
        }

        public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
            setFont(LTSDrawWindow.fontFlag ? this.this$0.f4 : this.this$0.f3);
            setText(param1Object.toString());
            setBackground(param1Boolean1 ? Color.blue : Color.white);
            setForeground(param1Boolean1 ? Color.white : Color.black);
            if (this.this$0.machineHasAction != null && this.this$0.machineHasAction[param1Int]) {
                setBackground(Color.red);
                setForeground(Color.white);
            }
            setForeground(param1Boolean1 ? Color.white : Color.black);
            setIcon((this.this$0.machineToDrawSet[param1Int] && !LTSDrawWindow.singleMode) ? this.this$0.drawIcon : null);
            return this;
        }
    }

    public void saveFile() {
        DrawMachine drawMachine = this.output.getDrawing();
        if (drawMachine == null) {
            JOptionPane.showMessageDialog(this, "No LTS picture selected to save");
            return;
        }
        FileDialog fileDialog = new FileDialog((Frame) getTopLevelAncestor(), "Save file in:", 1);
        if (this.Nmach > 0) {
            String str1 = (drawMachine.getMachine()).name;
            int i = str1.indexOf(':', 0);
            if (i > 0)
                str1 = str1.substring(0, i);
        }
        fileDialog.show();
        String str = fileDialog.getFile();
        if (str != null)
            try {
                int i = str.indexOf('.', 0);
                str = str.substring(0, i) + "." + "pct";
                FileOutputStream fileOutputStream = new FileOutputStream(fileDialog.getDirectory() + str);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
                Rectangle rectangle = new Rectangle(0, 0, (drawMachine.getSize()).width, (drawMachine.getSize()).height);
                Gr2PICT gr2PICT = new Gr2PICT(byteArrayOutputStream, this.output.getGraphics(), rectangle);
                drawMachine.fileDraw((Graphics) gr2PICT);
                gr2PICT.finalize();
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException iOException) {
                System.out.println("Error saving file: " + iOException);
            }
    }
}
