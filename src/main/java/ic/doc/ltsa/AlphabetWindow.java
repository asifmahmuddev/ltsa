package ic.doc.ltsa;

import ic.doc.ltsa.lts.Alphabet;
import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.CompositeState;
import ic.doc.ltsa.lts.EventClient;
import ic.doc.ltsa.lts.EventManager;
import ic.doc.ltsa.lts.LTSEvent;
import ic.doc.ltsa.lts.LTSOutput;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AlphabetWindow extends JSplitPane implements LTSOutput, EventClient {
    public static boolean fontFlag = false;
    JTextArea output;
    JList list;
    JScrollPane left;
    JScrollPane right;
    EventManager eman;
    int Nmach;
    int selectedMachine = 0;
    Alphabet current = null;
    int expandLevel = 0;
    CompactState[] sm;
    Font f1 = new Font("Monospaced", 0, 12);
    Font f2 = new Font("Monospaced", 1, 18);
    Font f3 = new Font("SansSerif", 0, 12);
    Font f4 = new Font("SansSerif", 1, 18);
    AlphabetWindow thisWindow;
    private static final int MAXPRINT = 400;

    public AlphabetWindow(CompositeState paramCompositeState, EventManager paramEventManager) {
        this.eman = paramEventManager;
        this.thisWindow = this;
        this.output = new JTextArea(23, 50);
        this.output.setEditable(false);
        this.right = new JScrollPane(this.output, 20, 30);
        this.output.setBackground(Color.white);
        this.output.setBorder(new EmptyBorder(0, 5, 0, 0));
        this.list = new JList();
        this.list.setSelectionMode(0);
        this.list.addListSelectionListener(new PrintAction(this));
        this.left = new JScrollPane(this.list, 20, 30);
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add("Center", this.right);
        JToolBar jToolBar = new JToolBar();
        jToolBar.setOrientation(1);
        jPanel.add("West", jToolBar);
        jToolBar.add(createTool("icon/expanded.gif", "Expand Most", new ExpandMostAction(this)));
        jToolBar.add(createTool("icon/expand.gif", "Expand", new ExpandMoreAction(this)));
        jToolBar.add(createTool("icon/collapse.gif", "Collapse", new ExpandLessAction(this)));
        jToolBar.add(createTool("icon/collapsed.gif", "Most Concise", new ExpandLeastAction(this)));
        if (paramEventManager != null)
            paramEventManager.addClient(this);
        new_machines(paramCompositeState);
        setLeftComponent(this.left);
        setRightComponent(jPanel);
        setDividerLocation(150);
        setBigFont(fontFlag);
        validate();
    }

    class ExpandMoreAction implements ActionListener {
        private final AlphabetWindow this$0;

        ExpandMoreAction(AlphabetWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.current == null)
                return;
            if (this.this$0.expandLevel < this.this$0.current.maxLevel)
                this.this$0.expandLevel++;
            this.this$0.clearOutput();
            this.this$0.current.print(this.this$0.thisWindow, this.this$0.expandLevel);
        }
    }

    class ExpandLessAction implements ActionListener {
        private final AlphabetWindow this$0;

        ExpandLessAction(AlphabetWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.current == null)
                return;
            if (this.this$0.expandLevel > 0)
                this.this$0.expandLevel--;
            this.this$0.clearOutput();
            this.this$0.current.print(this.this$0.thisWindow, this.this$0.expandLevel);
        }
    }

    class ExpandMostAction implements ActionListener {
        private final AlphabetWindow this$0;

        ExpandMostAction(AlphabetWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.current == null)
                return;
            this.this$0.expandLevel = this.this$0.current.maxLevel;
            this.this$0.clearOutput();
            this.this$0.current.print(this.this$0.thisWindow, this.this$0.expandLevel);
        }
    }

    class ExpandLeastAction implements ActionListener {
        private final AlphabetWindow this$0;

        ExpandLeastAction(AlphabetWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.current == null)
                return;
            this.this$0.expandLevel = 0;
            this.this$0.clearOutput();
            this.this$0.current.print(this.this$0.thisWindow, this.this$0.expandLevel);
        }
    }

    class PrintAction implements ListSelectionListener {
        private final AlphabetWindow this$0;

        PrintAction(AlphabetWindow this$0) {
            this.this$0 = this$0;
        }

        public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            int i = this.this$0.list.getSelectedIndex();
            if (i < 0 || i >= this.this$0.Nmach)
                return;
            this.this$0.selectedMachine = i;
            this.this$0.clearOutput();
            this.this$0.current = new Alphabet(this.this$0.sm[i]);
            if (this.this$0.expandLevel > this.this$0.current.maxLevel)
                this.this$0.expandLevel = this.this$0.current.maxLevel;
            this.this$0.current.print(this.this$0.thisWindow, this.this$0.expandLevel);
        }
    }

    public void ltsAction(LTSEvent paramLTSEvent) {
        switch (paramLTSEvent.kind) {
            case 1 :
                new_machines((CompositeState) paramLTSEvent.info);
                break;
        }
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

    private void new_machines(CompositeState paramCompositeState) {
        byte b1 = (paramCompositeState != null && paramCompositeState.composition != null) ? 1 : 0;
        if (paramCompositeState != null && paramCompositeState.machines != null && paramCompositeState.machines.size() > 0) {
            this.sm = new CompactState[paramCompositeState.machines.size() + b1];
            Enumeration enumeration = paramCompositeState.machines.elements();
            for (byte b = 0; enumeration.hasMoreElements(); b++)
                this.sm[b] = enumeration.nextElement();
            this.Nmach = this.sm.length;
            if (b1 == 1)
                this.sm[this.Nmach - 1] = paramCompositeState.composition;
        } else {
            this.Nmach = 0;
        }
        DefaultListModel defaultListModel = new DefaultListModel();
        for (byte b2 = 0; b2 < this.Nmach; b2++) {
            if (b1 == 1 && b2 == this.Nmach - 1) {
                defaultListModel.addElement("||" + (this.sm[b2]).name);
            } else {
                defaultListModel.addElement((this.sm[b2]).name);
            }
        }
        this.list.setModel(defaultListModel);
        if (this.selectedMachine >= this.Nmach)
            this.selectedMachine = 0;
        this.current = null;
        clearOutput();
    }

    protected JButton createTool(String paramString1, String paramString2, ActionListener paramActionListener) {
        JButton jButton = new JButton(this, new ImageIcon(getClass().getResource(paramString1))) {
            private final AlphabetWindow this$0;

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
        if (fontFlag) {
            this.output.setFont(this.f2);
            this.list.setFont(this.f4);
        } else {
            this.output.setFont(this.f1);
            this.list.setFont(this.f3);
        }
    }

    public void removeClient() {
        if (this.eman != null)
            this.eman.removeClient(this);
    }

    public void copy() {
        this.output.copy();
    }

    public void saveFile() {
        FileDialog fileDialog = new FileDialog((Frame) getTopLevelAncestor(), "Save text in:", 1);
        if (this.Nmach > 0) {
            String str1 = (this.sm[this.selectedMachine]).name;
            int i = str1.indexOf(':', 0);
            if (i > 0)
                str1 = str1.substring(0, i);
            fileDialog.setFile(str1 + ".txt");
        }
        fileDialog.show();
        String str = fileDialog.getFile();
        if (str != null)
            try {
                int i = str.indexOf('.', 0);
                str = str.substring(0, i) + "." + "txt";
                FileOutputStream fileOutputStream = new FileOutputStream(fileDialog.getDirectory() + str);
                PrintStream printStream = new PrintStream(fileOutputStream);
                String str1 = this.output.getText();
                printStream.print(str1);
                printStream.close();
                fileOutputStream.close();
            } catch (IOException iOException) {
                outln("Error saving file: " + iOException);
            }
    }
}
