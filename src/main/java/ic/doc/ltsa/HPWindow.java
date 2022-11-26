package ic.doc.ltsa;

import gov.nasa.arc.ase.util.graph.Graph;
import ic.doc.extension.Animator;
import ic.doc.extension.Exportable;
import ic.doc.extension.Initialisable;
import ic.doc.extension.LTSA;
import ic.doc.extension.LTSAButton;
import ic.doc.extension.LTSAPlugin;
import ic.doc.extension.PluginManager;
import ic.doc.extension.Relation;
import ic.doc.ltsa.dclap.Gr2PICT;
import ic.doc.ltsa.editor.ColoredEditorKit;
import ic.doc.ltsa.lts.Analyser;
import ic.doc.ltsa.lts.Automata;
import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.CompositeState;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.EventManager;
import ic.doc.ltsa.lts.LTSCanvas;
import ic.doc.ltsa.lts.LTSCompiler;
import ic.doc.ltsa.lts.LTSEvent;
import ic.doc.ltsa.lts.LTSException;
import ic.doc.ltsa.lts.LTSInput;
import ic.doc.ltsa.lts.LTSOutput;
import ic.doc.ltsa.lts.MenuDefinition;
import ic.doc.ltsa.lts.ProgressCheck;
import ic.doc.ltsa.lts.RunMenu;
import ic.doc.ltsa.lts.SuperTrace;
import ic.doc.ltsa.lts.SymbolTable;
import ic.doc.ltsa.lts.ltl.AssertDefinition;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.EditorKit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class HPWindow extends JFrame implements LTSA, LTSInput, LTSOutput, Runnable {
    private static final String VERSION = " j1.2 v14-10-99, amimation support";
    private static final String DEFAULT = "DEFAULT";
    JTextArea output;
    JEditorPane input;
    JEditorPane manual;
    AlphabetWindow alphabet;
    PrintWindow prints;
    LTSDrawWindow draws;
    JTabbedPane textIO;
    JToolBar tools;
    PluginManager o_plugin_manager;
    Map o_plugin_buttons;
    Map o_menus;
    JComboBox targetChoice;
    JPanel p;
    EventManager eman = new EventManager();
    JFrame animator = null;
    CompositeState current = null;
    String run_menu = "DEFAULT";
    String asserted = null;
    protected UndoableEditListener undoHandler = new UndoHandler(this);
    protected UndoManager undo = new UndoManager();
    JMenu file;
    JMenu edit;
    JMenu check;
    JMenu build;
    JMenu window;
    JMenu help;
    JMenu option;
    JMenuItem file_new;
    JMenuItem file_open;
    JMenuItem file_save;
    JMenuItem file_saveAs;
    JMenuItem file_export;
    JMenuItem file_exit;
    JMenuItem edit_cut;
    JMenuItem edit_copy;
    JMenuItem edit_paste;
    JMenuItem edit_undo;
    JMenuItem edit_redo;
    JMenuItem check_safe;
    JMenuItem check_progress;
    JMenuItem check_reachable;
    JMenuItem check_stop;
    JMenuItem build_parse;
    JMenuItem build_compile;
    JMenuItem build_compose;
    JMenuItem build_minimise;
    JMenuItem help_about;
    JMenuItem supertrace_options;
    JMenu check_run;
    JMenu file_example;
    JMenu check_liveness;
    JMenuItem default_run;
    JMenuItem[] run_items;
    JMenuItem[] assert_items;
    String[] run_names;
    String[] assert_names;
    boolean[] run_enabled;
    JCheckBoxMenuItem setWarnings;
    JCheckBoxMenuItem setWarningsAreErrors;
    JCheckBoxMenuItem setFair;
    JCheckBoxMenuItem setPartialOrder;
    JCheckBoxMenuItem setObsEquiv;
    JCheckBoxMenuItem setReduction;
    JCheckBoxMenuItem setBigFont;
    JCheckBoxMenuItem setDisplayName;
    JCheckBoxMenuItem setNewLabelFormat;
    JCheckBoxMenuItem setAutoRun;
    JCheckBoxMenuItem setMultipleLTS;
    JCheckBoxMenuItem help_manual;
    JCheckBoxMenuItem window_alpha;
    JCheckBoxMenuItem window_print;
    JCheckBoxMenuItem window_draw;
    JMenuBar mb;
    JCheckBoxMenuItem window_msc;
    Map o_plugins;
    JButton stopTool;
    JButton parseTool;
    JButton safetyTool;
    JButton progressTool;
    JButton copyTool;
    JButton cutTool;
    JButton pasteTool;
    JButton newFileTool;
    JButton openFileTool;
    JButton saveFileTool;
    JButton compileTool;
    JButton composeTool;
    JButton minimizeTool;
    JButton undoTool;
    JButton redoTool;
    int fPos = -1;
    String fSrc = "\n";
    Font fixed = new Font("Monospaced", 0, 12);
    Font big = new Font("Monospaced", 1, 18);
    private AppletButton isApplet = null;
    private static final int DO_safety = 1;
    private static final int DO_execute = 3;
    private static final int DO_reachable = 4;
    private static final int DO_compile = 5;
    private static final int DO_doComposition = 6;
    private static final int DO_minimiseComposition = 7;
    private static final int DO_progress = 8;
    private static final int DO_liveness = 9;
    private static final int DO_parse = 10;
    private int theAction;
    private Thread executer;
    private static final String fileType = "*.lts";
    private String openFile;
    String currentDirectory;
    private String savedText;
    private int tabindex;

    static void centre(Component paramComponent) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension1 = toolkit.getScreenSize();
        Dimension dimension2 = paramComponent.getSize();
        double d1 = (dimension1.getWidth() - dimension2.getWidth()) / 2.0D;
        double d2 = (dimension1.getHeight() - dimension2.getHeight()) / 2.0D;
        paramComponent.setLocation((int) d1, (int) d2);
    }

    void left(Component paramComponent) {
        Point point = getLocationOnScreen();
        point.translate(10, 100);
        paramComponent.setLocation(point);
    }

    protected JButton createTool(String paramString1, String paramString2, ActionListener paramActionListener) {
        JButton jButton = new JButton(this, new ImageIcon(getClass().getResource(paramString1))) {
            private final HPWindow this$0;

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

    void menuEnable(boolean paramBoolean) {
        String str = this.textIO.getTitleAt(this.tabindex = this.textIO.getSelectedIndex());
        boolean bool1 = (this.tabindex == 0) ? true : false;
        LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str);
        boolean bool2 = (this.isApplet == null) ? true : false;
        this.file_new.setEnabled((paramBoolean && (bool1 || (lTSAPlugin != null && lTSAPlugin.providesNewFile()))));
        this.file_example.setEnabled((paramBoolean && bool1));
        this.file_open.setEnabled((paramBoolean && (bool1 || (lTSAPlugin != null && lTSAPlugin.providesOpenFile()))));
        this.file_exit.setEnabled(paramBoolean);
        this.check_safe.setEnabled(paramBoolean);
        this.check_progress.setEnabled(paramBoolean);
        this.check_run.setEnabled(paramBoolean);
        this.check_reachable.setEnabled(paramBoolean);
        this.build_parse.setEnabled(paramBoolean);
        this.build_compile.setEnabled(paramBoolean);
        this.build_compose.setEnabled(paramBoolean);
        this.build_minimise.setEnabled(paramBoolean);
        this.parseTool.setEnabled(paramBoolean);
        this.safetyTool.setEnabled(paramBoolean);
        this.progressTool.setEnabled(paramBoolean);
        this.compileTool.setEnabled(paramBoolean);
        this.composeTool.setEnabled(paramBoolean);
        this.minimizeTool.setEnabled(paramBoolean);
        for (LTSAButton lTSAButton : this.o_plugin_buttons.keySet()) {
            LTSAPlugin lTSAPlugin1 = (LTSAPlugin) this.o_plugin_buttons.get(lTSAButton);
            if (lTSAPlugin1 != null && lTSAPlugin1.addAsTab()) {
                lTSAButton.setEnabled((lTSAPlugin == lTSAPlugin1));
                continue;
            }
            lTSAButton.setEnabled(true);
        }
    }

    public HPWindow(AppletButton paramAppletButton) {
        this.theAction = 0;
        this.openFile = "*.lts";
        this.savedText = "";
        this.tabindex = 0;
        this.isApplet = paramAppletButton;
        SymbolTable.init();
        getContentPane().setLayout(new BorderLayout());
        this.o_plugin_manager = new PluginManager(this);
        this.o_menus = new HashMap();
        this.textIO = new JTabbedPane();
        this.input = new JEditorPane();
        this.input.setEditorKit((EditorKit) new ColoredEditorKit());
        this.input.setFont(this.fixed);
        this.input.setBackground(Color.white);
        this.input.getDocument().addUndoableEditListener(this.undoHandler);
        this.undo.setLimit(10);
        this.input.setBorder(new EmptyBorder(0, 5, 0, 0));
        JScrollPane jScrollPane1 = new JScrollPane(this.input, 22, 30);
        this.textIO.addTab("Edit", jScrollPane1);
        this.output = new JTextArea("", 30, 100);
        this.output.setEditable(false);
        this.output.setFont(this.fixed);
        this.output.setBackground(Color.white);
        this.output.setLineWrap(true);
        this.output.setWrapStyleWord(true);
        this.output.setBorder(new EmptyBorder(0, 5, 0, 0));
        JScrollPane jScrollPane2 = new JScrollPane(this.output, 22, 31);
        this.textIO.addTab("Output", jScrollPane2);
        this.textIO.addChangeListener(new TabChange(this));
        getContentPane().add("Center", this.textIO);
        this.o_plugin_buttons = new HashMap();
        for (Iterator iterator1 = this.o_plugin_manager.getPluginIterator(); iterator1.hasNext();) {
            LTSAPlugin lTSAPlugin = iterator1.next();
            if (lTSAPlugin.addAsTab())
                this.textIO.addTab(lTSAPlugin.getName(), lTSAPlugin.getComponent());
        }
        this.mb = new JMenuBar();
        setJMenuBar(this.mb);
        this.file = new JMenu("File");
        this.mb.add(this.file);
        this.o_menus.put("File", this.file);
        this.file_new = new JMenuItem("New");
        this.file_new.addActionListener(new NewFileAction(this));
        this.file.add(this.file_new);
        this.file_open = new JMenuItem("Open...");
        this.file_open.addActionListener(new OpenFileAction(this));
        this.file.add(this.file_open);
        this.file_save = new JMenuItem("Save");
        this.file_save.addActionListener(new SaveFileAction(this));
        this.file.add(this.file_save);
        this.file_saveAs = new JMenuItem("Save as...");
        this.file_saveAs.addActionListener(new SaveAsFileAction(this));
        this.file.add(this.file_saveAs);
        this.file_export = new JMenuItem("Export...");
        this.file_export.addActionListener(new ExportFileAction(this));
        this.file.add(this.file_export);
        this.file_example = new JMenu("Examples");
        (new Examples(this.file_example, this)).getExamples();
        this.file.add(this.file_example);
        this.file_exit = new JMenuItem("Quit");
        this.file_exit.addActionListener(new ExitFileAction(this));
        this.file.add(this.file_exit);
        this.edit = new JMenu("Edit");
        this.mb.add(this.edit);
        this.o_menus.put("Edit", this.edit);
        this.edit_cut = new JMenuItem("Cut");
        this.edit_cut.addActionListener(new EditCutAction(this));
        this.edit.add(this.edit_cut);
        this.edit_copy = new JMenuItem("Copy");
        this.edit_copy.addActionListener(new EditCopyAction(this));
        this.edit.add(this.edit_copy);
        this.edit_paste = new JMenuItem("Paste");
        this.edit_paste.addActionListener(new EditPasteAction(this));
        this.edit.add(this.edit_paste);
        this.edit.addSeparator();
        this.edit_undo = new JMenuItem("Undo");
        this.edit_undo.addActionListener(new UndoAction(this));
        this.edit.add(this.edit_undo);
        this.edit_redo = new JMenuItem("Redo");
        this.edit_redo.addActionListener(new RedoAction(this));
        this.edit.add(this.edit_redo);
        this.check = new JMenu("Check");
        this.mb.add(this.check);
        this.o_menus.put("Check", this.check);
        this.check_safe = new JMenuItem("Safety");
        this.check_safe.addActionListener(new DoAction(this, 1));
        this.check.add(this.check_safe);
        this.check_progress = new JMenuItem("Progress");
        this.check_progress.addActionListener(new DoAction(this, 8));
        this.check.add(this.check_progress);
        this.check_liveness = new JMenu("LTL property");
        if (hasLTL2BuchiJar())
            this.check.add(this.check_liveness);
        this.check_run = new JMenu("Run");
        this.check.add(this.check_run);
        this.default_run = new JMenuItem("DEFAULT");
        this.default_run.addActionListener(new ExecuteAction(this, "DEFAULT"));
        this.check_run.add(this.default_run);
        this.check_reachable = new JMenuItem("Supertrace");
        this.check_reachable.addActionListener(new DoAction(this, 4));
        this.check.add(this.check_reachable);
        this.check_stop = new JMenuItem("Stop");
        this.check_stop.addActionListener(new StopAction(this));
        this.check_stop.setEnabled(false);
        this.check.add(this.check_stop);
        this.build = new JMenu("Build");
        this.mb.add(this.build);
        this.o_menus.put("Build", this.build);
        this.build_parse = new JMenuItem("Parse");
        this.build_parse.addActionListener(new DoAction(this, 10));
        this.build.add(this.build_parse);
        this.build_compile = new JMenuItem("Compile");
        this.build_compile.addActionListener(new DoAction(this, 5));
        this.build.add(this.build_compile);
        this.build_compose = new JMenuItem("Compose");
        this.build_compose.addActionListener(new DoAction(this, 6));
        this.build.add(this.build_compose);
        this.build_minimise = new JMenuItem("Minimise");
        this.build_minimise.addActionListener(new DoAction(this, 7));
        this.build.add(this.build_minimise);
        this.window = new JMenu("Window");
        this.mb.add(this.window);
        this.o_menus.put("Window", this.window);
        this.window_alpha = new JCheckBoxMenuItem("Alphabet");
        this.window_alpha.setSelected(false);
        this.window_alpha.addActionListener(new WinAlphabetAction(this));
        this.window.add(this.window_alpha);
        this.window_print = new JCheckBoxMenuItem("Transitions");
        this.window_print.setSelected(false);
        this.window_print.addActionListener(new WinPrintAction(this));
        this.window.add(this.window_print);
        this.window_draw = new JCheckBoxMenuItem("Draw");
        this.window_draw.setSelected(true);
        this.window_draw.addActionListener(new WinDrawAction(this));
        this.window.add(this.window_draw);
        this.help = new JMenu("Help");
        this.mb.add(this.help);
        this.o_menus.put("Help", this.help);
        this.help_about = new JMenuItem("About");
        this.help_about.addActionListener(new HelpAboutAction(this));
        this.help.add(this.help_about);
        this.help_manual = new JCheckBoxMenuItem("Manual");
        this.help_manual.setSelected(false);
        this.help_manual.addActionListener(new HelpManualAction(this));
        this.help.add(this.help_manual);
        OptionAction optionAction = new OptionAction(this);
        this.option = new JMenu("Options");
        this.mb.add(this.option);
        this.o_menus.put("Options", this.option);
        this.setWarnings = new JCheckBoxMenuItem("Display warning messages");
        this.setWarnings.addActionListener(optionAction);
        this.option.add(this.setWarnings);
        this.setWarnings.setSelected(true);
        this.setWarningsAreErrors = new JCheckBoxMenuItem("Treat warnings as errors");
        this.setWarningsAreErrors.addActionListener(optionAction);
        this.option.add(this.setWarningsAreErrors);
        this.setWarningsAreErrors.setSelected(false);
        this.setFair = new JCheckBoxMenuItem("Fair Choice for LTL check");
        this.setFair.addActionListener(optionAction);
        this.option.add(this.setFair);
        this.setFair.setSelected(true);
        this.setPartialOrder = new JCheckBoxMenuItem("Partial Order Reduction");
        this.setPartialOrder.addActionListener(optionAction);
        this.option.add(this.setPartialOrder);
        this.setPartialOrder.setSelected(false);
        this.setObsEquiv = new JCheckBoxMenuItem("Preserve OE for POR composition");
        this.setObsEquiv.addActionListener(optionAction);
        this.option.add(this.setObsEquiv);
        this.setObsEquiv.setSelected(true);
        this.setReduction = new JCheckBoxMenuItem("Enable Tau Reduction");
        this.setReduction.addActionListener(optionAction);
        this.option.add(this.setReduction);
        this.setReduction.setSelected(true);
        this.supertrace_options = new JMenuItem("Set Supertrace parameters");
        this.supertrace_options.addActionListener(new SuperTraceOptionListener(this));
        this.option.add(this.supertrace_options);
        this.option.addSeparator();
        this.setBigFont = new JCheckBoxMenuItem("Use big font");
        this.setBigFont.addActionListener(optionAction);
        this.option.add(this.setBigFont);
        this.setBigFont.setSelected(false);
        this.setDisplayName = new JCheckBoxMenuItem("Display name when drawing LTS");
        this.setDisplayName.addActionListener(optionAction);
        this.option.add(this.setDisplayName);
        this.setDisplayName.setSelected(true);
        this.setNewLabelFormat = new JCheckBoxMenuItem("Use V2.0 label format when drawing LTS");
        this.setNewLabelFormat.addActionListener(optionAction);
        this.option.add(this.setNewLabelFormat);
        this.setNewLabelFormat.setSelected(true);
        this.setMultipleLTS = new JCheckBoxMenuItem("Multiple LTS in Draw window");
        this.setMultipleLTS.addActionListener(optionAction);
        this.option.add(this.setMultipleLTS);
        this.setMultipleLTS.setSelected(false);
        this.option.addSeparator();
        this.setAutoRun = new JCheckBoxMenuItem("Auto run actions in Animator");
        this.setAutoRun.addActionListener(optionAction);
        this.option.add(this.setAutoRun);
        this.setAutoRun.setSelected(false);
        this.tools = new JToolBar();
        this.tools.setFloatable(false);
        this.tools.add(this.newFileTool = createTool("/ic/doc/ltsa/icon/new.gif", "New file", new NewFileAction(this)));
        this.tools.add(this.openFileTool = createTool("/ic/doc/ltsa/icon/open.gif", "Open file", new OpenFileAction(this)));
        this.tools.add(this.saveFileTool = createTool("/ic/doc/ltsa/icon/save.gif", "Save File", new SaveFileAction(this)));
        this.tools.addSeparator();
        this.tools.add(this.cutTool = createTool("/ic/doc/ltsa/icon/cut.gif", "Cut", new EditCutAction(this)));
        this.tools.add(this.copyTool = createTool("/ic/doc/ltsa/icon/copy.gif", "Copy", new EditCopyAction(this)));
        this.tools.add(this.pasteTool = createTool("/ic/doc/ltsa/icon/paste.gif", "Paste", new EditPasteAction(this)));
        this.tools.add(this.undoTool = createTool("/ic/doc/ltsa/icon/undo.gif", "Undo", new UndoAction(this)));
        this.tools.add(this.redoTool = createTool("/ic/doc/ltsa/icon/redo.gif", "Redo", new RedoAction(this)));
        this.tools.addSeparator();
        this.tools.add(this.parseTool = createTool("/ic/doc/ltsa/icon/parse.gif", "Parse", new DoAction(this, 10)));
        this.tools.add(this.compileTool = createTool("/ic/doc/ltsa/icon/compile.gif", "Compile", new DoAction(this, 5)));
        this.tools.add(this.composeTool = createTool("/ic/doc/ltsa/icon/compose.gif", "Compose", new DoAction(this, 6)));
        this.tools.add(this.minimizeTool = createTool("/ic/doc/ltsa/icon/minimize.gif", "Minimize", new DoAction(this, 7)));
        for (Iterator iterator2 = this.o_plugin_manager.getPluginIterator(); iterator2.hasNext();) {
            LTSAPlugin lTSAPlugin = iterator2.next();
            if (lTSAPlugin.addToolbarButtons()) {
                this.tools.addSeparator();
                List list = lTSAPlugin.getToolbarButtons();
                for (LTSAButton lTSAButton : list) {
                    this.tools.add((Component) lTSAButton);
                    this.o_plugin_buttons.put(lTSAButton, lTSAPlugin);
                }
                if (!iterator2.hasNext())
                    this.tools.addSeparator();
            }
            if (lTSAPlugin.addMenuItems()) {
                Map map = lTSAPlugin.getMenuItems();
                HashMap hashMap = new HashMap();
                for (Object object : map.keySet()) {
                    Object object1 = map.get(object);
                    if (hashMap.get(object1) == null)
                        hashMap.put(object1, new ArrayList());
                    List list = (List) hashMap.get(object1);
                    list.add(object);
                }
                for (String str : hashMap.keySet()) {
                    JMenu jMenu;
                    List list = (List) hashMap.get(str);
                    if (this.o_menus.get(str) == null) {
                        jMenu = new JMenu(str);
                        this.o_menus.put(str, jMenu);
                        this.mb.add(jMenu);
                    } else {
                        jMenu = (JMenu) this.o_menus.get(str);
                        jMenu.addSeparator();
                    }
                    for (Iterator iterator = list.iterator(); iterator.hasNext();)
                        jMenu.add(iterator.next());
                }
            }
        }
        this.targetChoice = new JComboBox();
        this.targetChoice.setEditable(false);
        this.targetChoice.addItem("DEFAULT");
        this.targetChoice.setToolTipText("Target Composition");
        this.targetChoice.setRequestFocusEnabled(false);
        this.targetChoice.addActionListener(new TargetAction(this));
        this.tools.add(this.targetChoice);
        this.tools.addSeparator();
        this.tools.add(this.safetyTool = createTool("/ic/doc/ltsa/icon/safety.gif", "Check safety", new DoAction(this, 1)));
        this.tools.add(this.progressTool = createTool("/ic/doc/ltsa/icon/progress.gif", "Check Progress", new DoAction(this, 8)));
        this.tools.add(this.stopTool = createTool("/ic/doc/ltsa/icon/stop.gif", "Stop", new StopAction(this)));
        this.stopTool.setEnabled(false);
        this.tools.addSeparator();
        this.tools.add(createTool("/ic/doc/ltsa/icon/alphabet.gif", "Run DEFAULT Animation", new ExecuteAction(this, "DEFAULT")));
        this.tools.add(createTool("/ic/doc/ltsa/icon/blanker.gif", "Blank Screen", new BlankAction(this)));
        this.tools.addSeparator();
        getContentPane().add("North", this.tools);
        menuEnable(true);
        this.file_save.setEnabled((this.isApplet == null));
        this.file_saveAs.setEnabled((this.isApplet == null));
        this.file_export.setEnabled((this.isApplet == null));
        this.saveFileTool.setEnabled((this.isApplet == null));
        updateDoState();
        LTSCanvas.displayName = this.setDisplayName.isSelected();
        LTSCanvas.newLabelFormat = this.setNewLabelFormat.isSelected();
        LTSDrawWindow.singleMode = !this.setMultipleLTS.isSelected();
        newDrawWindow(this.window_draw.isSelected());
        swapto(0);
        setEditControls(0);
        setDefaultCloseOperation(0);
        addWindowListener(new CloseWindow(this));
    }

    private void do_action(int paramInt) {
        menuEnable(false);
        this.check_stop.setEnabled(true);
        this.stopTool.setEnabled(true);
        this.theAction = paramInt;
        this.executer = new Thread(this);
        this.executer.setPriority(4);
        this.executer.start();
    }

    public void run() {
        try {
            switch (this.theAction) {
                case 1 :
                    showOutput();
                    safety();
                    break;
                case 3 :
                    execute();
                    break;
                case 4 :
                    showOutput();
                    reachable();
                    break;
                case 5 :
                    showOutput();
                    compile();
                    break;
                case 6 :
                    showOutput();
                    doComposition();
                    break;
                case 7 :
                    showOutput();
                    minimiseComposition();
                    break;
                case 8 :
                    showOutput();
                    progress();
                    break;
                case 9 :
                    showOutput();
                    liveness();
                    break;
                case 10 :
                    parse();
                    break;
            }
        } catch (Throwable throwable) {
            showOutput();
            outln("**** Runtime Exception: " + throwable);
            throwable.printStackTrace();
        }
        menuEnable(true);
        this.check_stop.setEnabled(false);
        this.stopTool.setEnabled(false);
    }

    class CloseWindow extends WindowAdapter {
        private final HPWindow this$0;

        CloseWindow(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.quitAll();
        }

        public void windowActivated(WindowEvent param1WindowEvent) {
            if (this.this$0.animator != null)
                this.this$0.animator.toFront();
        }
    }

    public void invalidateState() {
        this.current = null;
        this.targetChoice.removeAllItems();
        this.targetChoice.addItem("DEFAULT");
        this.check_run.removeAll();
        this.check_run.add(this.default_run);
        this.run_items = null;
        this.assert_items = null;
        this.run_names = null;
        this.check_liveness.removeAll();
        validate();
        this.eman.post(new LTSEvent(1, null));
        if (this.animator != null) {
            this.animator.dispose();
            this.animator = null;
        }
    }

    public void postState(CompositeState paramCompositeState) {
        if (this.animator != null) {
            this.animator.dispose();
            this.animator = null;
        }
        this.eman.post(new LTSEvent(1, paramCompositeState));
    }

    private void newFile() {
        if (checkSave()) {
            setTitle("LTS Analyser");
            this.savedText = "";
            this.openFile = "*.lts";
            this.input.setText("");
            swapto(0);
            this.output.setText("");
            invalidateState();
        }
        repaint();
    }

    public void newExample(String paramString1, String paramString2) {
        this.undo.discardAllEdits();
        this.input.getDocument().removeUndoableEditListener(this.undoHandler);
        if (checkSave()) {
            invalidateState();
            clearOutput();
            doOpenFile(paramString1, paramString2, true);
        }
        this.input.getDocument().addUndoableEditListener(this.undoHandler);
        updateDoState();
        repaint();
    }

    private void openAFile() {
        if (checkSave()) {
            invalidateState();
            clearOutput();
            FileDialog fileDialog = new FileDialog(this, "Select source file:");
            if (this.currentDirectory != null)
                fileDialog.setDirectory(this.currentDirectory);
            String str = this.textIO.getTitleAt(this.textIO.getSelectedIndex());
            LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str);
            if (lTSAPlugin != null && lTSAPlugin.getFileExtension() != null) {
                fileDialog.setFile("*." + lTSAPlugin.getFileExtension());
            } else {
                fileDialog.setFile("*.lts");
            }
            fileDialog.show();
            doOpenFile(this.currentDirectory = fileDialog.getDirectory(), fileDialog.getFile(), false);
        }
        repaint();
    }

    private void doOpenFile(String paramString1, String paramString2, boolean paramBoolean) {
        if (paramString2 != null)
            try {
                InputStream inputStream;
                this.openFile = paramString2;
                setTitle("LTSA - " + this.openFile);
                if (!paramBoolean) {
                    String str = this.textIO.getTitleAt(this.textIO.getSelectedIndex());
                    LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str);
                    if (lTSAPlugin != null && lTSAPlugin.providesOpenFile()) {
                        lTSAPlugin.openFile(new File(paramString1 + this.openFile));
                        return;
                    }
                    inputStream = new FileInputStream(paramString1 + this.openFile);
                } else {
                    inputStream = getClass().getResourceAsStream(paramString1 + this.openFile);
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        StringBuffer stringBuffer = new StringBuffer();
                        String str;
                        while ((str = bufferedReader.readLine()) != null)
                            stringBuffer.append(str + "\n");
                        this.savedText = stringBuffer.toString();
                        this.input.setText(this.savedText);
                        parse();
                    } catch (Exception exception) {
                        outln("Error reading file: " + exception);
                    }
                } catch (Exception exception) {
                    outln("Error creating InputStream: " + exception);
                }
            } catch (Exception exception) {
                outln("Error creating FileInputStream: " + exception);
            }
    }

    private void saveAsFile() {
        FileDialog fileDialog = new FileDialog(this, "Save file in:", 1);
        if (this.currentDirectory != null)
            fileDialog.setDirectory(this.currentDirectory);
        String str1 = this.textIO.getTitleAt(this.textIO.getSelectedIndex());
        LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str1);
        if (lTSAPlugin != null && lTSAPlugin.getFileExtension() != null) {
            fileDialog.setFile("*." + lTSAPlugin.getFileExtension());
        } else {
            fileDialog.setFile(this.openFile);
        }
        fileDialog.show();
        String str2 = fileDialog.getFile();
        if (str2 != null) {
            if (lTSAPlugin != null && lTSAPlugin.providesSaveFile()) {
                try {
                    lTSAPlugin.saveFile(new FileOutputStream(fileDialog.getDirectory() + fileDialog.getFile()));
                } catch (IOException iOException) {
                    outln("File not found : " + fileDialog.getDirectory() + fileDialog.getFile());
                }
                return;
            }
            this.currentDirectory = fileDialog.getDirectory();
            this.openFile = str2;
            setTitle("LTSA - " + this.openFile);
            saveFile();
        }
    }

    private void saveFile() {
        String str = this.textIO.getTitleAt(this.textIO.getSelectedIndex());
        LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str);
        if (lTSAPlugin != null && lTSAPlugin.providesSaveFile()) {
            saveAsFile();
        } else if (this.openFile != null && this.openFile.equals("*.lts")) {
            saveAsFile();
        } else if (this.openFile != null) {
            try {
                int i = this.openFile.indexOf('.', 0);
                if (i > 0) {
                    this.openFile = this.openFile.substring(0, i) + "." + "lts";
                } else {
                    this.openFile += ".lts";
                }
                String str1 = (this.currentDirectory == null) ? this.openFile : (this.currentDirectory + this.openFile);
                FileOutputStream fileOutputStream = new FileOutputStream(str1);
                PrintStream printStream = new PrintStream(fileOutputStream);
                this.savedText = this.input.getText();
                printStream.print(this.savedText);
                printStream.close();
                fileOutputStream.close();
                outln("Saved in: " + str1);
            } catch (IOException iOException) {
                outln("Error saving file: " + iOException);
            }
        }
    }

    private void exportFile() {
        String str1 = "Export as Aldebaran format (.aut) to:";
        FileDialog fileDialog = new FileDialog(this, str1, 1);
        if (this.current == null || this.current.composition == null) {
            JOptionPane.showMessageDialog(this, "No target composition to export");
            return;
        }
        String str2 = this.current.composition.name;
        fileDialog.setFile(str2 + ".aut");
        fileDialog.setDirectory(this.currentDirectory);
        fileDialog.show();
        String str3;
        if ((str3 = fileDialog.getFile()) != null)
            try {
                int i = str3.indexOf('.', 0);
                str3 = str3.substring(0, i) + ".aut";
                File file = new File(fileDialog.getDirectory(), str3);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PrintStream printStream = new PrintStream(fileOutputStream);
                this.current.composition.printAUT(printStream);
                printStream.close();
                fileOutputStream.close();
                outln("Exported to: " + fileDialog.getDirectory() + file);
            } catch (IOException iOException) {
                outln("Error exporting file: " + iOException);
            }
    }

    private boolean checkSave() {
        if (this.isApplet != null)
            return true;
        if (!this.savedText.equals(this.input.getText())) {
            int i = JOptionPane.showConfirmDialog(this, "Do you want to save the contents of " + this.openFile);
            if (i == 0) {
                saveFile();
                return true;
            }
            if (i == 1)
                return true;
            if (i == 2)
                return false;
        }
        return true;
    }

    private void doFont() {
        if (this.setBigFont.getState()) {
            this.input.setFont(this.big);
            this.output.setFont(this.big);
        } else {
            this.input.setFont(this.fixed);
            this.output.setFont(this.fixed);
        }
        pack();
        show();
    }

    private void quitAll() {
        if (this.isApplet != null) {
            dispose();
            this.isApplet.ended();
        } else if (checkSave()) {
            System.exit(0);
        }
    }

    class NewFileAction implements ActionListener {
        private final HPWindow this$0;

        NewFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            LTSAPlugin lTSAPlugin = this.this$0.o_plugin_manager.getPlugin(str);
            if (lTSAPlugin != null && lTSAPlugin.providesNewFile()) {
                lTSAPlugin.newFile();
                return;
            }
            this.this$0.undo.discardAllEdits();
            this.this$0.input.getDocument().removeUndoableEditListener(this.this$0.undoHandler);
            this.this$0.newFile();
            this.this$0.input.getDocument().addUndoableEditListener(this.this$0.undoHandler);
            this.this$0.updateDoState();
        }
    }

    class OpenFileAction implements ActionListener {
        private final HPWindow this$0;

        OpenFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.undo.discardAllEdits();
            this.this$0.input.getDocument().removeUndoableEditListener(this.this$0.undoHandler);
            this.this$0.openAFile();
            this.this$0.input.getDocument().addUndoableEditListener(this.this$0.undoHandler);
            this.this$0.updateDoState();
        }
    }

    class SaveFileAction implements ActionListener {
        private final HPWindow this$0;

        SaveFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            if (str.equals("Edit") || str.equals("Output")) {
                this.this$0.saveFile();
            } else if (str.equals("Alphabet")) {
                this.this$0.alphabet.saveFile();
            } else if (str.equals("Transitions")) {
                this.this$0.prints.saveFile(this.this$0.currentDirectory, ".txt");
            } else if (str.equals("Draw")) {
                this.this$0.draws.saveFile();
            } else {
                this.this$0.saveFile();
            }
        }
    }

    class SaveAsFileAction implements ActionListener {
        private final HPWindow this$0;

        SaveAsFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.saveAsFile();
        }
    }

    class ExportFileAction implements ActionListener {
        private final HPWindow this$0;

        ExportFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            if (str.equals("Edit")) {
                this.this$0.exportFile();
            } else if (str.equals("Transitions")) {
                this.this$0.prints.saveFile(this.this$0.currentDirectory, ".aut");
            }
        }
    }

    class ExitFileAction implements ActionListener {
        private final HPWindow this$0;

        ExitFileAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.quitAll();
        }
    }

    class DoAction implements ActionListener {
        int actionCode;
        private final HPWindow this$0;

        DoAction(HPWindow this$0, int param1Int) {
            this.this$0 = this$0;
            this.actionCode = param1Int;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.do_action(this.actionCode);
        }
    }

    class OptionAction implements ActionListener {
        private final HPWindow this$0;

        OptionAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            Object object = param1ActionEvent.getSource();
            if (object == this.this$0.setWarnings) {
                Diagnostics.warningFlag = this.this$0.setWarnings.isSelected();
            } else if (object == this.this$0.setWarningsAreErrors) {
                Diagnostics.warningsAreErrors = this.this$0.setWarningsAreErrors.isSelected();
            } else if (object == this.this$0.setFair) {
                ProgressCheck.strongFairFlag = this.this$0.setFair.isSelected();
            } else if (object == this.this$0.setPartialOrder) {
                Analyser.partialOrderReduction = this.this$0.setPartialOrder.isSelected();
            } else if (object == this.this$0.setObsEquiv) {
                Analyser.preserveObsEquiv = this.this$0.setObsEquiv.isSelected();
            } else if (object == this.this$0.setReduction) {
                CompositeState.reduceFlag = this.this$0.setReduction.isSelected();
            } else if (object == this.this$0.setBigFont) {
                AnimWindow.fontFlag = this.this$0.setBigFont.isSelected();
                AlphabetWindow.fontFlag = this.this$0.setBigFont.isSelected();
                if (this.this$0.alphabet != null)
                    this.this$0.alphabet.setBigFont(this.this$0.setBigFont.isSelected());
                PrintWindow.fontFlag = this.this$0.setBigFont.isSelected();
                if (this.this$0.prints != null)
                    this.this$0.prints.setBigFont(this.this$0.setBigFont.isSelected());
                LTSDrawWindow.fontFlag = this.this$0.setBigFont.isSelected();
                if (this.this$0.draws != null)
                    this.this$0.draws.setBigFont(this.this$0.setBigFont.isSelected());
                LTSCanvas.fontFlag = this.this$0.setBigFont.isSelected();
                this.this$0.doFont();
            } else if (object == this.this$0.setDisplayName) {
                if (this.this$0.draws != null)
                    this.this$0.draws.setDrawName(this.this$0.setDisplayName.isSelected());
                LTSCanvas.displayName = this.this$0.setDisplayName.isSelected();
            } else if (object == this.this$0.setMultipleLTS) {
                LTSDrawWindow.singleMode = !this.this$0.setMultipleLTS.isSelected();
                if (this.this$0.draws != null)
                    this.this$0.draws.setMode(LTSDrawWindow.singleMode);
            } else if (object == this.this$0.setNewLabelFormat) {
                if (this.this$0.draws != null)
                    this.this$0.draws.setNewLabelFormat(this.this$0.setNewLabelFormat.isSelected());
                LTSCanvas.newLabelFormat = this.this$0.setNewLabelFormat.isSelected();
            }
        }
    }

    class SuperTraceOptionListener implements ActionListener {
        private final HPWindow this$0;

        SuperTraceOptionListener(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.setSuperTraceOption();
        }
    }

    class WinAlphabetAction implements ActionListener {
        private final HPWindow this$0;

        WinAlphabetAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.newAlphabetWindow(this.this$0.window_alpha.isSelected());
        }
    }

    class WinPrintAction implements ActionListener {
        private final HPWindow this$0;

        WinPrintAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.newPrintWindow(this.this$0.window_print.isSelected());
        }
    }

    class WinDrawAction implements ActionListener {
        private final HPWindow this$0;

        WinDrawAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.newDrawWindow(this.this$0.window_draw.isSelected());
        }
    }

    class WinMSCAction implements ActionListener {
        private final HPWindow this$0;

        WinMSCAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.newMSCWindow(this.this$0.window_msc.isSelected());
        }
    }

    class HelpAboutAction implements ActionListener {
        private final HPWindow this$0;

        HelpAboutAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.aboutDialog();
        }
    }

    class BlankAction implements ActionListener {
        private final HPWindow this$0;

        BlankAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.blankit();
        }
    }

    class HelpManualAction implements ActionListener {
        private final HPWindow this$0;

        HelpManualAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.displayManual(this.this$0.help_manual.isSelected());
        }
    }

    class StopAction implements ActionListener {
        private final HPWindow this$0;

        StopAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (this.this$0.executer != null) {
                this.this$0.executer.stop();
                this.this$0.menuEnable(true);
                this.this$0.check_stop.setEnabled(false);
                this.this$0.stopTool.setEnabled(false);
                this.this$0.outln("\n\t-- stopped");
                this.this$0.executer = null;
            }
        }
    }

    class ExecuteAction implements ActionListener {
        String runtarget;
        private final HPWindow this$0;

        ExecuteAction(HPWindow this$0, String param1String) {
            this.this$0 = this$0;
            this.runtarget = param1String;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.run_menu = this.runtarget;
            this.this$0.do_action(3);
        }
    }

    class LivenessAction implements ActionListener {
        String asserttarget;
        private final HPWindow this$0;

        LivenessAction(HPWindow this$0, String param1String) {
            this.this$0 = this$0;
            this.asserttarget = param1String;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.asserted = this.asserttarget;
            this.this$0.do_action(9);
        }
    }

    class EditCutAction implements ActionListener {
        private final HPWindow this$0;

        EditCutAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            LTSAPlugin lTSAPlugin = this.this$0.o_plugin_manager.getPlugin(str);
            if (lTSAPlugin != null && lTSAPlugin.providesCut()) {
                lTSAPlugin.cut();
            } else {
                this.this$0.input.cut();
            }
        }
    }

    class EditCopyAction implements ActionListener {
        private final HPWindow this$0;

        EditCopyAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            LTSAPlugin lTSAPlugin = this.this$0.o_plugin_manager.getPlugin(str);
            if (lTSAPlugin != null && lTSAPlugin.providesCopy()) {
                lTSAPlugin.copy();
            } else if (str.equals("Edit")) {
                this.this$0.input.copy();
            } else if (str.equals("Output")) {
                this.this$0.output.copy();
            } else if (str.equals("Manual")) {
                this.this$0.manual.copy();
            } else if (str.equals("Alphabet")) {
                this.this$0.alphabet.copy();
            } else if (str.equals("Transitions")) {
                this.this$0.prints.copy();
            }
        }
    }

    class EditPasteAction implements ActionListener {
        private final HPWindow this$0;

        EditPasteAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = this.this$0.textIO.getTitleAt(this.this$0.textIO.getSelectedIndex());
            LTSAPlugin lTSAPlugin = this.this$0.o_plugin_manager.getPlugin(str);
            if (lTSAPlugin != null && lTSAPlugin.providesPaste()) {
                lTSAPlugin.paste();
            } else {
                this.this$0.input.paste();
            }
        }
    }

    class TargetAction implements ActionListener {
        private final HPWindow this$0;

        TargetAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            String str = (String) this.this$0.targetChoice.getSelectedItem();
            if (str == null)
                return;
            this.this$0.run_enabled = MenuDefinition.enabled(str);
            if (this.this$0.run_items != null && this.this$0.run_enabled != null && this.this$0.run_items.length == this.this$0.run_enabled.length)
                for (byte b = 0; b < this.this$0.run_items.length; b++)
                    this.this$0.run_items[b].setEnabled(this.this$0.run_enabled[b]);
        }
    }

    class UndoHandler implements UndoableEditListener {
        private final HPWindow this$0;

        UndoHandler(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void undoableEditHappened(UndoableEditEvent param1UndoableEditEvent) {
            this.this$0.undo.addEdit(param1UndoableEditEvent.getEdit());
            this.this$0.updateDoState();
        }
    }

    class UndoAction implements ActionListener {
        private final HPWindow this$0;

        UndoAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
                this.this$0.undo.undo();
            } catch (CannotUndoException cannotUndoException) {
            }
            this.this$0.updateDoState();
        }
    }

    class RedoAction implements ActionListener {
        private final HPWindow this$0;

        RedoAction(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
                this.this$0.undo.redo();
            } catch (CannotUndoException cannotUndoException) {
            }
            this.this$0.updateDoState();
        }
    }

    public void updateDoState() {
        this.edit_undo.setEnabled(this.undo.canUndo());
        this.undoTool.setEnabled(this.undo.canUndo());
        this.edit_redo.setEnabled(this.undo.canRedo());
        this.redoTool.setEnabled(this.undo.canRedo());
    }

    private void swapto(int paramInt) {
        if (paramInt == this.tabindex)
            return;
        this.textIO.setBackgroundAt(paramInt, Color.green);
        if (this.tabindex != paramInt && this.tabindex < this.textIO.getTabCount())
            this.textIO.setBackgroundAt(this.tabindex, Color.lightGray);
        this.tabindex = paramInt;
        setEditControls(this.tabindex);
        this.textIO.setSelectedIndex(paramInt);
    }

    public void swapto(String paramString) {
        swapto(this.textIO.indexOfTab(paramString));
    }

    class TabChange implements ChangeListener {
        private final HPWindow this$0;

        TabChange(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void stateChanged(ChangeEvent param1ChangeEvent) {
            int i = this.this$0.textIO.getSelectedIndex();
            if (i == this.this$0.tabindex)
                return;
            this.this$0.textIO.setBackgroundAt(i, Color.green);
            this.this$0.textIO.setBackgroundAt(this.this$0.tabindex, Color.lightGray);
            this.this$0.tabindex = i;
            this.this$0.setEditControls(this.this$0.tabindex);
        }
    }

    private void setEditControls(int paramInt) {
        boolean bool1 = (this.isApplet == null) ? true : false;
        String str = this.textIO.getTitleAt(paramInt = this.textIO.getSelectedIndex());
        boolean bool2 = (paramInt == 0) ? true : false;
        LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin(str);
        if (lTSAPlugin != null && lTSAPlugin.useOwnMenuBar()) {
            setJMenuBar(lTSAPlugin.getMenuBar());
            return;
        }
        setJMenuBar(this.mb);
        this.edit_cut.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesCut())));
        this.cutTool.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesCut())));
        this.edit_paste.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesPaste())));
        this.pasteTool.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesPaste())));
        this.edit_copy.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesCopy())));
        this.copyTool.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesCopy())));
        this.file_new.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesNewFile())));
        this.file_example.setEnabled(bool2);
        this.file_open.setEnabled((bool1 && (bool2 || (lTSAPlugin != null && lTSAPlugin.providesOpenFile()))));
        this.file_save.setEnabled((bool1 && (bool2 || (lTSAPlugin != null && lTSAPlugin.providesSaveFile()))));
        this.file_saveAs.setEnabled((bool1 && (bool2 || (lTSAPlugin != null && lTSAPlugin.providesSaveFile()))));
        this.file_export.setEnabled((bool1 && (bool2 || str.equals("Transitions"))));
        this.newFileTool.setEnabled((bool2 || (lTSAPlugin != null && lTSAPlugin.providesNewFile())));
        this.openFileTool.setEnabled((bool1 && (bool2 || (lTSAPlugin != null && lTSAPlugin.providesOpenFile()))));
        this.saveFileTool.setEnabled((bool1 && (bool2 || (lTSAPlugin != null && lTSAPlugin.providesSaveFile()))));
        this.edit_undo.setEnabled((bool2 && this.undo.canUndo()));
        this.undoTool.setEnabled((bool2 && this.undo.canUndo()));
        this.edit_redo.setEnabled((bool2 && this.undo.canRedo()));
        this.redoTool.setEnabled((bool2 && this.undo.canRedo()));
        if (bool2)
            this.input.requestFocusInWindow();
        for (LTSAButton lTSAButton : this.o_plugin_buttons.keySet()) {
            LTSAPlugin lTSAPlugin1 = (LTSAPlugin) this.o_plugin_buttons.get(lTSAButton);
            if (lTSAPlugin1 != null && lTSAPlugin1.addAsTab()) {
                lTSAButton.setEnabled((lTSAPlugin == lTSAPlugin1));
                continue;
            }
            lTSAButton.setEnabled(true);
        }
    }

    public void out(String paramString) {
        SwingUtilities.invokeLater(new OutputAppend(this, paramString));
    }

    public void outln(String paramString) {
        SwingUtilities.invokeLater(new OutputAppend(this, paramString + "\n"));
    }

    public void clearOutput() {
        SwingUtilities.invokeLater(new OutputClear(this));
    }

    public void showOutput() {
        SwingUtilities.invokeLater(new OutputShow(this));
    }

    class OutputAppend implements Runnable {
        String text;
        private final HPWindow this$0;

        OutputAppend(HPWindow this$0, String param1String) {
            this.this$0 = this$0;
            this.text = param1String;
        }

        public void run() {
            this.this$0.output.append(this.text);
        }
    }

    class OutputClear implements Runnable {
        private final HPWindow this$0;

        OutputClear(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.output.setText("");
        }
    }

    class OutputShow implements Runnable {
        private final HPWindow this$0;

        OutputShow(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void run() {
            this.this$0.swapto(1);
        }
    }

    public char nextChar() {
        this.fPos++;
        if (this.fPos < this.fSrc.length())
            return this.fSrc.charAt(this.fPos);
        return Character.MIN_VALUE;
    }

    public char backChar() {
        this.fPos--;
        if (this.fPos < 0) {
            this.fPos = 0;
            return Character.MIN_VALUE;
        }
        return this.fSrc.charAt(this.fPos);
    }

    public int getMarker() {
        return this.fPos;
    }

    private void compile() {
        clearOutput();
        if (!parse())
            return;
        this.current = docompile();
        if (this.current != null)
            postState(this.current);
    }

    private void displayError(LTSException paramLTSException) {
        if (paramLTSException.marker != null) {
            int i = ((Integer) paramLTSException.marker).intValue();
            byte b1 = 1;
            for (byte b2 = 0; b2 < i; b2++) {
                if (this.fSrc.charAt(b2) == '\n')
                    b1++;
            }
            outln("ERROR line:" + b1 + " - " + paramLTSException.getMessage());
            this.input.select(i, i + 1);
        } else {
            outln("ERROR - " + paramLTSException.getMessage());
        }
    }

    private CompositeState docompile() {
        this.fPos = -1;
        this.fSrc = this.input.getText();
        CompositeState compositeState = null;
        LTSCompiler lTSCompiler = new LTSCompiler(this, this, this.currentDirectory);
        try {
            compositeState = lTSCompiler.compile((String) this.targetChoice.getSelectedItem());
        } catch (LTSException lTSException) {
            displayError(lTSException);
        }
        return compositeState;
    }

    private Hashtable doparse() {
        this.fPos = -1;
        this.fSrc = this.input.getText();
        LTSCompiler lTSCompiler = new LTSCompiler(this, this, this.currentDirectory);
        Hashtable hashtable1 = new Hashtable();
        Hashtable hashtable2 = new Hashtable();
        try {
            lTSCompiler.parse(hashtable1, hashtable2);
        } catch (LTSException lTSException) {
            displayError(lTSException);
            return null;
        }
        return hashtable1;
    }

    private void compileIfChange() {
        String str = this.input.getText();
        if (this.current == null || !str.equals(this.fSrc) || !this.current.name.equals(this.targetChoice.getSelectedItem()))
            compile();
    }

    private void safety() {
        clearOutput();
        compileIfChange();
        if (this.current != null)
            this.current.analyse(this);
    }

    private void progress() {
        clearOutput();
        compileIfChange();
        if (this.current != null)
            this.current.checkProgress(this);
    }

    private void liveness() {
        clearOutput();
        compileIfChange();
        CompositeState compositeState = AssertDefinition.compile(this, this.asserted);
        if (this.current != null && compositeState != null) {
            this.current.checkLTL(this, compositeState);
            postState(this.current);
        }
    }

    private void minimiseComposition() {
        clearOutput();
        compileIfChange();
        if (this.current != null) {
            if (this.current.composition == null)
                this.current.compose(this);
            this.current.minimise(this);
            postState(this.current);
        }
    }

    private void doComposition() {
        clearOutput();
        compileIfChange();
        if (this.current != null) {
            this.current.compose(this);
            postState(this.current);
        }
    }

    public void compileNoClear() {
        if (!parse())
            return;
        this.current = docompile();
        if (this.current != null)
            postState(this.current);
    }

    private boolean checkReplay(Animator paramAnimator) {
        if (paramAnimator.hasErrorTrace()) {
            int i = JOptionPane.showConfirmDialog(this, "Do you want to replay the error trace?");
            if (i == 0)
                return true;
            if (i == 1)
                return false;
            if (i == 2)
                return false;
        }
        return false;
    }

    private void execute() {
        clearOutput();
        compileIfChange();
        if (this.current != null) {
            Analyser analyser = new Analyser(this.current, this, this.eman);
            boolean bool = checkReplay((Animator) analyser);
            if (this.animator != null) {
                this.animator.dispose();
                this.animator = null;
            }
            RunMenu runMenu = null;
            if (RunMenu.menus != null)
                runMenu = (RunMenu) RunMenu.menus.get(this.run_menu);
            if (runMenu != null && runMenu.isCustom()) {
                this.animator = createCustom((Animator) analyser, runMenu.params, runMenu.actions, runMenu.controls, bool);
            } else {
                this.animator = new AnimWindow((Animator) analyser, runMenu, this.setAutoRun.getState(), bool);
            }
            if (this.animator != null) {
                this.animator.pack();
                left(this.animator);
                this.animator.setVisible(true);
            }
        }
    }

    private JFrame createCustom(Animator paramAnimator, String paramString, Relation paramRelation1, Relation paramRelation2, boolean paramBoolean) {
        LTSAPlugin lTSAPlugin = this.o_plugin_manager.getPlugin("SceneBeans");
        if (lTSAPlugin != null) {
            System.err.println("x_scenebeans != null");
            try {
                Initialisable initialisable = (Initialisable) lTSAPlugin;
                initialisable.initialise(new Object[]{paramAnimator, paramString, paramRelation1, paramRelation2, new Boolean(paramBoolean), this.currentDirectory});
                return (JFrame) lTSAPlugin.getComponent();
            } catch (ClassCastException classCastException) {
            }
        }
        outln("** Failed to create instance of Scene Animator");
        return null;
    }

    private void reachable() {
        clearOutput();
        compileIfChange();
        if (this.current != null && this.current.machines.size() > 0) {
            Analyser analyser = new Analyser(this.current, this, null);
            SuperTrace superTrace = new SuperTrace((Automata) analyser, this);
            this.current.setErrorTrace(superTrace.getErrorTrace());
        }
    }

    private void newDrawWindow(boolean paramBoolean) {
        if (paramBoolean && this.textIO.indexOfTab("Draw") < 0) {
            this.draws = new LTSDrawWindow(this.current, this.eman);
            this.textIO.addTab("Draw", this.draws);
            swapto(this.textIO.indexOfTab("Draw"));
        } else if (!paramBoolean && this.textIO.indexOfTab("Draw") > 0) {
            swapto(0);
            this.textIO.removeTabAt(this.textIO.indexOfTab("Draw"));
            this.draws.removeClient();
            this.draws = null;
        }
    }

    private void newMSCWindow(boolean paramBoolean) {
        if (paramBoolean && this.textIO.indexOfTab("MSC Editor") < 0) {
            swapto(this.textIO.indexOfTab("MSC Editor"));
        } else if (!paramBoolean && this.textIO.indexOfTab("MSC Editor") > 0) {
            swapto(0);
            this.textIO.removeTabAt(this.textIO.indexOfTab("MSC Editor"));
        }
    }

    private void newPrintWindow(boolean paramBoolean) {
        if (paramBoolean && this.textIO.indexOfTab("Transitions") < 0) {
            this.prints = new PrintWindow(this.current, this.eman);
            this.textIO.addTab("Transitions", this.prints);
            swapto(this.textIO.indexOfTab("Transitions"));
        } else if (!paramBoolean && this.textIO.indexOfTab("Transitions") > 0) {
            swapto(0);
            this.textIO.removeTabAt(this.textIO.indexOfTab("Transitions"));
            this.prints.removeClient();
            this.prints = null;
        }
    }

    private void newAlphabetWindow(boolean paramBoolean) {
        if (paramBoolean && this.textIO.indexOfTab("Alphabet") < 0) {
            this.alphabet = new AlphabetWindow(this.current, this.eman);
            this.textIO.addTab("Alphabet", this.alphabet);
            swapto(this.textIO.indexOfTab("Alphabet"));
        } else if (!paramBoolean && this.textIO.indexOfTab("Alphabet") > 0) {
            swapto(0);
            this.textIO.removeTabAt(this.textIO.indexOfTab("Alphabet"));
            this.alphabet.removeClient();
            this.alphabet = null;
        }
    }

    private void aboutDialog() {
        LTSASplash lTSASplash = new LTSASplash(this);
        lTSASplash.setVisible(true);
    }

    private void blankit() {
        LTSABlanker lTSABlanker = new LTSABlanker(this);
        lTSABlanker.setVisible(true);
    }

    private void setSuperTraceOption() {
        try {
            String str = (String) JOptionPane.showInputDialog(this, "Enter Hashtable size (Kilobytes):", "Supertrace parameters", -1, null, null, "" + SuperTrace.getHashSize());
            if (str == null)
                return;
            SuperTrace.setHashSize(Integer.parseInt(str));
            str = (String) JOptionPane.showInputDialog(this, "Enter bound for search depth size:", "Supertrace parameters", -1, null, null, "" + SuperTrace.getDepthBound());
            if (str == null)
                return;
            SuperTrace.setDepthBound(Integer.parseInt(str));
        } catch (NumberFormatException numberFormatException) {
        }
    }

    public boolean parse() {
        String str = (String) this.targetChoice.getSelectedItem();
        Hashtable hashtable = doparse();
        if (hashtable == null)
            return false;
        this.targetChoice.removeAllItems();
        if (hashtable.size() == 0) {
            this.targetChoice.addItem("DEFAULT");
        } else {
            Enumeration enumeration = hashtable.keys();
            ArrayList arrayList = new ArrayList();
            while (enumeration.hasMoreElements())
                arrayList.add(enumeration.nextElement());
            Collections.sort(arrayList);
            for (Iterator iterator = arrayList.iterator(); iterator.hasNext();)
                this.targetChoice.addItem((String) iterator.next());
        }
        if (str != null && !str.equals("DEFAULT") && hashtable.containsKey(str))
            this.targetChoice.setSelectedItem(str);
        this.current = null;
        this.check_run.removeAll();
        this.run_names = MenuDefinition.names();
        this.run_enabled = MenuDefinition.enabled((String) this.targetChoice.getSelectedItem());
        this.check_run.add(this.default_run);
        if (this.run_names != null) {
            this.run_items = new JMenuItem[this.run_names.length];
            for (byte b = 0; b < this.run_names.length; b++) {
                this.run_items[b] = new JMenuItem(this.run_names[b]);
                this.run_items[b].setEnabled(this.run_enabled[b]);
                this.run_items[b].addActionListener(new ExecuteAction(this, this.run_names[b]));
                this.check_run.add(this.run_items[b]);
            }
        }
        this.check_liveness.removeAll();
        this.assert_names = AssertDefinition.names();
        if (this.assert_names != null) {
            this.assert_items = new JMenuItem[this.assert_names.length];
            for (byte b = 0; b < this.assert_names.length; b++) {
                this.assert_items[b] = new JMenuItem(this.assert_names[b]);
                this.assert_items[b].addActionListener(new LivenessAction(this, this.assert_names[b]));
                this.check_liveness.add(this.assert_items[b]);
            }
        }
        validate();
        return true;
    }

    private void displayManual(boolean paramBoolean) {
        if (paramBoolean && this.textIO.indexOfTab("Manual") < 0) {
            this.manual = new JEditorPane();
            this.manual.setEditable(false);
            this.manual.addHyperlinkListener(new Hyperactive(this));
            JScrollPane jScrollPane = new JScrollPane(this.manual, 22, 31);
            this.textIO.addTab("Manual", jScrollPane);
            swapto(this.textIO.indexOfTab("Manual"));
            URL uRL = getClass().getResource("doc/User-manual.html");
            try {
                this.manual.setPage(uRL);
            } catch (IOException iOException) {
                outln("" + iOException);
            }
        } else if (!paramBoolean && this.textIO.indexOfTab("Manual") > 0) {
            swapto(0);
            this.textIO.removeTabAt(this.textIO.indexOfTab("Manual"));
            this.manual = null;
        }
    }

    class Hyperactive implements HyperlinkListener {
        private final HPWindow this$0;

        Hyperactive(HPWindow this$0) {
            this.this$0 = this$0;
        }

        public void hyperlinkUpdate(HyperlinkEvent param1HyperlinkEvent) {
            if (param1HyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane jEditorPane = (JEditorPane) param1HyperlinkEvent.getSource();
                try {
                    URL uRL = param1HyperlinkEvent.getURL();
                    jEditorPane.setPage(uRL);
                } catch (Throwable throwable) {
                    this.this$0.outln("" + param1HyperlinkEvent);
                }
            }
        }
    }

    public static void main(String[] paramArrayOfString) {
        try {
            String str = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(str);
        } catch (Exception exception) {
        }
        HPWindow hPWindow = new HPWindow(null);
        hPWindow.setTitle("LTS Analyser");
        hPWindow.pack();
        centre(hPWindow);
        hPWindow.setVisible(true);
        if (paramArrayOfString.length > 0) {
            SwingUtilities.invokeLater(new ScheduleOpenFile(hPWindow, paramArrayOfString[0]));
        } else {
            hPWindow.currentDirectory = System.getProperty("user.dir");
        }
    }

    static class ScheduleOpenFile implements Runnable {
        HPWindow window;
        String arg;

        ScheduleOpenFile(HPWindow param1HPWindow, String param1String) {
            this.window = param1HPWindow;
            this.arg = param1String;
        }

        public void run() {
            this.window.doOpenFile("", this.arg, false);
        }
    }

    private boolean hasLTL2BuchiJar() {
        try {
            new Graph();
            return true;
        } catch (NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }

    public List getLTSNames() {
        ArrayList arrayList = new ArrayList();
        compileIfChange();
        if (this.current != null)
            for (Iterator iterator = this.current.machines.iterator(); iterator.hasNext();)
                arrayList.add(((CompactState) iterator.next()).name);
        return arrayList;
    }

    public String getCurrentDirectory() {
        return this.currentDirectory;
    }

    public void setCurrentDirectory(String paramString) {
        this.currentDirectory = paramString;
    }

    public UndoManager getUndoManager() {
        return this.undo;
    }

    public JEditorPane getInputPane() {
        return this.input;
    }

    public void setTargetChoice(String paramString) {
        this.targetChoice.setSelectedItem(paramString);
    }

    public boolean isCurrentStateNull() {
        return (this.current == null);
    }

    public boolean isCurrentStateComposed() {
        return (this.current.composition != null);
    }

    public void composeCurrentState() {
        this.current.compose(this);
    }

    public void analyseCurrentState() {
        this.current.analyse(this);
    }

    public Vector getCurrentStateErrorTrace() {
        return this.current.getErrorTrace();
    }

    public void postCurrentState() {
        postState(this.current);
    }

    public void exportGraphic(Exportable paramExportable) {
        FileDialog fileDialog = new FileDialog(this, "Save file in:", 1);
        fileDialog.show();
        String str = fileDialog.getFile();
        if (this.file != null)
            try {
                int i = str.indexOf('.', 0);
                str = str.substring(0, i) + "." + "pct";
                FileOutputStream fileOutputStream = new FileOutputStream(fileDialog.getDirectory() + str);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
                Rectangle rectangle = new Rectangle(0, 0, (this.input.getSize()).width, (this.input.getSize()).height);
                Gr2PICT gr2PICT = new Gr2PICT(byteArrayOutputStream, this.input.getGraphics(), rectangle);
                paramExportable.fileDraw((Graphics) gr2PICT);
                gr2PICT.finalize();
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException iOException) {
                System.out.println("Error saving file: " + iOException);
            }
    }

    public Animator getAnimator() {
        return (Animator) new Analyser(this.current, this, this.eman);
    }
}
