package ic.doc.ltsa;

import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.CompositeState;
import ic.doc.ltsa.lts.EventClient;
import ic.doc.ltsa.lts.EventManager;
import ic.doc.ltsa.lts.LTSEvent;
import ic.doc.ltsa.lts.LTSOutput;
import ic.doc.ltsa.lts.PrintTransitions;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PrintWindow extends JSplitPane implements LTSOutput, EventClient {
    public static boolean fontFlag = false;
    JTextArea output;
    JList list;
    JScrollPane left;
    JScrollPane right;
    EventManager eman;
    int Nmach;
    int selectedMachine = 0;
    CompactState[] sm;
    Font f1 = new Font("Monospaced", 0, 12);
    Font f2 = new Font("Monospaced", 1, 18);
    Font f3 = new Font("SansSerif", 0, 12);
    Font f4 = new Font("SansSerif", 1, 18);
    PrintWindow thisWindow;
    private static final int MAXPRINT = 400;

    public PrintWindow(CompositeState paramCompositeState, EventManager paramEventManager) {
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
        if (paramEventManager != null)
            paramEventManager.addClient(this);
        new_machines(paramCompositeState);
        setLeftComponent(this.left);
        setRightComponent(this.right);
        setDividerLocation(200);
        setBigFont(fontFlag);
        validate();
    }

    class PrintAction implements ListSelectionListener {
        private final PrintWindow this$0;

        PrintAction(PrintWindow this$0) {
            this.this$0 = this$0;
        }

        public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            int i = this.this$0.list.getSelectedIndex();
            if (i < 0 || i >= this.this$0.Nmach)
                return;
            this.this$0.selectedMachine = i;
            this.this$0.clearOutput();
            PrintTransitions printTransitions = new PrintTransitions(this.this$0.sm[this.this$0.selectedMachine]);
            printTransitions.print(this.this$0.thisWindow, 400);
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
        clearOutput();
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

    public void saveFile(String paramString1, String paramString2) {
        String str1;
        if (paramString2.equals(".txt")) {
            str1 = "Save text in:";
        } else {
            str1 = "Save as Aldebaran format (.aut) in:";
        }
        FileDialog fileDialog = new FileDialog((Frame) getTopLevelAncestor(), str1, 1);
        if (this.Nmach > 0) {
            String str = (this.sm[this.selectedMachine]).name;
            int i = str.indexOf(':', 0);
            if (i > 0)
                str = str.substring(0, i);
            fileDialog.setFile(str + paramString2);
            fileDialog.setDirectory(paramString1);
        }
        fileDialog.show();
        String str2;
        if ((str2 = fileDialog.getFile()) != null)
            try {
                int i = str2.indexOf('.', 0);
                str2 = str2.substring(0, i) + paramString2;
                File file = new File(fileDialog.getDirectory(), str2);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PrintStream printStream = new PrintStream(fileOutputStream);
                if (paramString2.equals(".txt")) {
                    String str = this.output.getText();
                    printStream.print(str);
                } else if (this.Nmach > 0) {
                    this.sm[this.selectedMachine].printAUT(printStream);
                }
                printStream.close();
                fileOutputStream.close();
            } catch (IOException iOException) {
                outln("Error saving file: " + iOException);
            }
    }
}
