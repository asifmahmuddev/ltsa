package ic.doc.ltsa.custom;

import ic.doc.extension.Animator;
import ic.doc.extension.Relation;
import java.util.BitSet;
import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public class SceneAnimationController implements Runnable, AnimationMessage, AnimationListener {
    Animator animator;
    OutputActionRegistry actions;
    ControlActionRegistry controls;
    String[] controlNames;
    volatile BitSet eligible;
    volatile boolean[] signalled;
    Thread ticker;
    boolean debug = false;
    boolean trace = false;
    boolean replayMode = false;
    Object canvas;
    public static int LIMIT = 300;

    public SceneAnimationController(Animator paramAnimator, Relation paramRelation1, Relation paramRelation2, boolean paramBoolean, Object paramObject) {
        this.animator = paramAnimator;
        this.replayMode = paramBoolean;
        this.canvas = paramObject;
        this.actions = new OutputActionRegistry(paramRelation1, this);
        this.controls = new ControlActionRegistry(paramRelation2, this);
    }

    public void setTrace(boolean paramBoolean) {
        this.trace = paramBoolean;
    }

    public void setDebug(boolean paramBoolean) {
        this.debug = paramBoolean;
    }

    public void registerAction(String paramString, AnimationAction paramAnimationAction) {
        this.actions.register(paramString, paramAnimationAction);
    }

    public void start() {
        this.eligible = this.animator.initialise(this.controls.getControls());
        this.trace = this.replayMode;
        this.controlNames = this.animator.getMenuNames();
        this.signalled = new boolean[this.controlNames.length];
        for (byte b = 0; b < this.signalled.length;) {
            this.signalled[b] = true;
            b++;
        }
        this.controls.initMap(this.controlNames);
    }

    public void stop() {
        if (this.ticker != null)
            this.ticker.interrupt();
    }

    public void restart() {
        if (this.ticker == null) {
            this.ticker = new Thread(this);
            this.ticker.start();
        }
    }

    void doReplay() throws InterruptedException {
        while (this.animator.traceChoice()) {
            this.eligible = this.animator.traceStep();
            String str = this.animator.actionNameChosen();
            int i = this.controls.controlled(str);
            if (i > 0)
                for (; !this.signalled[i]; this.canvas.wait());
            this.actions.doAction(this.animator.actionNameChosen());
            if (this.animator.isError()) {
                this.animator.message("Animation - ERROR state reached");
                return;
            }
        }
        this.animator.message("Animation - end of Replay");
    }

    void doActions() throws InterruptedException {
        try {
            while (true) {
                doNonControlActions();
                if (empty(this.eligible)) {
                    this.animator.message("Animation - STOP state reached");
                    return;
                }
                int i = -1;
                for (; (i = getValidControl()) < 0; this.canvas.wait());
                doMenuStep(i);
            }
        } catch (AnimationException animationException) {
            this.animator.message("Animation - ERROR state reached " + animationException);
            return;
        }
    }

    int getValidControl() {
        for (byte b = 0; b < this.signalled.length; b++) {
            if (this.signalled[b] && this.eligible.get(b))
                return b;
        }
        return -1;
    }

    void doMenuStep(int paramInt) throws AnimationException {
        this.eligible = this.animator.menuStep(paramInt);
        this.actions.doAction(this.animator.actionNameChosen());
        if (this.animator.isError())
            throw new AnimationException(this);
    }

    void doNonControlActions() throws AnimationException {
        byte b = 0;
        while (this.animator.nonMenuChoice()) {
            this.eligible = this.animator.singleStep();
            this.actions.doAction(this.animator.actionNameChosen());
            if (this.animator.isError())
                throw new AnimationException(this);
            b++;
            if (b > LIMIT)
                throw new AnimationException(this, "immediate action LIMIT exceeded");
        }
    }

    private boolean empty(BitSet paramBitSet) {
        for (byte b = 0; b < paramBitSet.size(); b++) {
            if (paramBitSet.get(b))
                return false;
        }
        return true;
    }

    public void traceMsg(String paramString) {
        if (this.trace)
            this.animator.message(paramString);
    }

    public void debugMsg(String paramString) {
        if (this.debug)
            this.animator.message(paramString);
    }

    public void signalControl(String paramString) {
        synchronized (this.canvas) {
            if (paramString.charAt(0) != '~') {
                this.controls.mapControl(paramString, this.signalled, true);
                this.canvas.notifyAll();
            } else {
                this.controls.mapControl(paramString.substring(1), this.signalled, false);
            }
        }
    }

    public void clearControl(String paramString) {
        this.controls.mapControl(paramString, this.signalled, false);
    }

    public void run() {
        try {
            synchronized (this.canvas) {
                if (!this.replayMode) {
                    doActions();
                } else {
                    doReplay();
                }
            }
        } catch (InterruptedException interruptedException) {
        }
        this.ticker = null;
    }

    public void animationEvent(AnimationEvent paramAnimationEvent) {
        signalControl(paramAnimationEvent.getName());
    }

    public class AnimationException extends Exception {
        private final SceneAnimationController this$0;

        public AnimationException(SceneAnimationController this$0) {
            this.this$0 = this$0;
        }

        public AnimationException(SceneAnimationController this$0, String param1String) {
            super(param1String);
            this.this$0 = this$0;
        }
    }
}
