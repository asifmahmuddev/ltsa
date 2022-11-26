package ic.doc.ltsa.custom;

import ic.doc.extension.Animator;
import ic.doc.extension.Relation;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import uk.ac.ic.doc.scenebeans.animation.Animation;
import uk.ac.ic.doc.scenebeans.animation.AnimationCanvas;
import uk.ac.ic.doc.scenebeans.animation.CommandException;
import uk.ac.ic.doc.scenebeans.animation.parse.XMLAnimationParser;
import uk.ac.ic.doc.scenebeans.input.MouseDispatcher;

public class SceneAnimator extends CustomAnimator implements AnimationControl {
    SceneAnimationController tac;
    MenuBar mb;
    Menu run;
    MenuItem pause;
    MenuItem resume;
    Menu trace;
    CheckboxMenuItem setTrace;
    CheckboxMenuItem setDebug;
    Scrollbar bar;
    Animator animator;
    Relation buttonControls;
    AnimationCanvas _canvas;
    MouseDispatcher _dispatcher;

    public SceneAnimator() {
        setTitle("SceneBean Animator");
        addWindowListener(new MyWindow(this));
        getContentPane().setLayout(new BorderLayout());
        this._canvas = new AnimationCanvas();
        this._canvas.setBackground(Color.white);
        this._canvas.setAnimationStretched(true);
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this._canvas.setRenderingHints(renderingHints);
        this._canvas.setFont(new Font("SansSerif", 1, 14));
        getContentPane().add("Center", (Component) this._canvas);
        this.bar = new Scrollbar(1, 25, 1, 1, 32);
        this._canvas.setFrameDelay(40L);
        getContentPane().add("East", this.bar);
        this.bar.addAdjustmentListener(new AdjustmentListener(this) {
            private final SceneAnimator this$0;

            public void adjustmentValueChanged(AdjustmentEvent param1AdjustmentEvent) {
                this.this$0._canvas.setTimeWarp((33 - param1AdjustmentEvent.getValue()) * 0.125D);
            }
        });
        MenuBar menuBar = new MenuBar();
        setMenuBar(menuBar);
        this.run = new Menu("Run");
        menuBar.add(this.run);
        this.pause = new MenuItem("Pause");
        this.run.add(this.pause);
        this.resume = new MenuItem("Resume");
        this.run.add(this.resume);
        this.pause.setEnabled(true);
        this.resume.setEnabled(false);
        RunMenu runMenu = new RunMenu(this);
        this.pause.addActionListener(runMenu);
        this.resume.addActionListener(runMenu);
        this.trace = new Menu("Trace");
        menuBar.add(this.trace);
        this.setTrace = new CheckboxMenuItem("Trace");
        this.trace.add(this.setTrace);
        this.setTrace.setState(false);
        this.setDebug = new CheckboxMenuItem("Debug");
        this.trace.add(this.setDebug);
        this.setDebug.setState(false);
        CheckItem checkItem = new CheckItem(this);
        this.setDebug.addItemListener(checkItem);
        this.setTrace.addItemListener(checkItem);
        this._dispatcher = new MouseDispatcher(this._canvas.getSceneGraph(), this._canvas);
        this._dispatcher.attachTo((Component) this._canvas);
    }

    public void init(Animator paramAnimator, File paramFile, Relation paramRelation1, Relation paramRelation2, boolean paramBoolean) {
        if (paramBoolean)
            setTitle("Custom Animator - Replay Mode");
        this.setTrace.setState(paramBoolean);
        this.animator = paramAnimator;
        if (paramRelation1 == null || paramRelation2 == null) {
            this.animator.message("Animator - must have 'controls' and 'actions'");
            dispose();
            return;
        }
        try {
            XMLAnimationParser xMLAnimationParser = new XMLAnimationParser(paramFile, (Component) this._canvas);
            Animation animation = xMLAnimationParser.parseAnimation();
            this._canvas.setAnimation(animation);
            this.buttonControls = paramRelation2.inverse();
            Iterator iterator = animation.getEventNames().iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                this.buttonControls.remove(str);
            }
            Relation relation = new Relation();
            relation.union(paramRelation1);
            relation.union(this.buttonControls.inverse());
            this.tac = new SceneAnimationController(paramAnimator, relation, paramRelation2, paramBoolean, this._canvas);
            iterator = animation.getCommandNames().iterator();
            while (iterator.hasNext())
                registerAction(iterator.next());
            if (this.buttonControls.size() > 0)
                createButtons(this.buttonControls);
            animation.addAnimationListener(this.tac);
            invalidate();
            pack();
            this.tac.start();
            clearButtons(this.buttonControls);
            this.tac.restart();
        } catch (Exception exception) {
            this.animator.message("XML-" + exception);
            exception.printStackTrace();
            dispose();
        }
    }

    protected void createButtons(Relation paramRelation) {
        Panel panel = new Panel();
        getContentPane().add("South", panel);
        Enumeration enumeration = paramRelation.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            Button button = new Button(str);
            button.setBackground(Color.green);
            button.addActionListener(new ButtonAction(this, str));
            registerButtonClearAction(str, button);
            panel.add(button);
        }
    }

    protected void clearButtons(Relation paramRelation) {
        Enumeration enumeration = paramRelation.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            clearControl(str);
        }
    }

    public void stop() {
        if (this.tac != null)
            this.tac.stop();
        if (this._canvas != null)
            this._canvas.stop();
    }

    public void registerAction(String paramString) {
        this.tac.registerAction(paramString, new CommandAction(this, paramString));
    }

    class CommandAction implements AnimationAction {
        String name;
        private final SceneAnimator this$0;

        CommandAction(SceneAnimator this$0, String param1String) {
            this.this$0 = this$0;
            this.name = param1String;
        }

        public void action() {
            try {
                synchronized (this.this$0._canvas) {
                    this.this$0._canvas.getAnimation().invokeCommand(this.name);
                }
            } catch (CommandException commandException) {
                System.out.println("Animation" + commandException);
            }
        }
    }

    public void registerButtonClearAction(String paramString, Button paramButton) {
        this.tac.registerAction(paramString, new ButtonClearAction(this, paramString, paramButton));
    }

    class ButtonClearAction implements AnimationAction {
        String name;
        Button button;
        private final SceneAnimator this$0;

        ButtonClearAction(SceneAnimator this$0, String param1String, Button param1Button) {
            this.this$0 = this$0;
            this.name = param1String;
            this.button = param1Button;
        }

        public void action() {
            this.button.setBackground(Color.green);
            this.this$0.clearControl(this.name);
        }
    }

    public void signalControl(String paramString) {
        this.tac.signalControl(paramString);
    }

    public void clearControl(String paramString) {
        this.tac.clearControl(paramString);
    }

    class MyWindow extends WindowAdapter {
        private final SceneAnimator this$0;

        MyWindow(SceneAnimator this$0) {
            this.this$0 = this$0;
        }

        public void windowClosing(WindowEvent param1WindowEvent) {
            this.this$0.dispose();
        }
    }

    class CheckItem implements ItemListener {
        private final SceneAnimator this$0;

        CheckItem(SceneAnimator this$0) {
            this.this$0 = this$0;
        }

        public void itemStateChanged(ItemEvent param1ItemEvent) {
            if (param1ItemEvent.getSource() == this.this$0.setTrace) {
                this.this$0.tac.setTrace(this.this$0.setTrace.getState());
            } else if (param1ItemEvent.getSource() == this.this$0.setDebug) {
                this.this$0.tac.setDebug(this.this$0.setDebug.getState());
            }
        }
    }

    class RunMenu implements ActionListener {
        private final SceneAnimator this$0;

        RunMenu(SceneAnimator this$0) {
            this.this$0 = this$0;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            if (param1ActionEvent.getSource() == this.this$0.pause) {
                if (this.this$0.tac != null)
                    this.this$0.tac.stop();
                this.this$0.pause.setEnabled(false);
                this.this$0.resume.setEnabled(true);
            } else if (param1ActionEvent.getSource() == this.this$0.resume) {
                if (this.this$0.tac != null)
                    this.this$0.tac.restart();
                this.this$0.pause.setEnabled(true);
                this.this$0.resume.setEnabled(false);
            }
        }
    }

    class ButtonAction implements ActionListener {
        String name;
        private final SceneAnimator this$0;

        public ButtonAction(SceneAnimator this$0, String param1String) {
            this.this$0 = this$0;
            this.name = param1String;
        }

        public void actionPerformed(ActionEvent param1ActionEvent) {
            Button button = (Button) param1ActionEvent.getSource();
            button.setBackground(Color.red);
            this.this$0.signalControl(this.name);
        }
    }
}
