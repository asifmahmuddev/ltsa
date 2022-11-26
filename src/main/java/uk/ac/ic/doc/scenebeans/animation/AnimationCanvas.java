package uk.ac.ic.doc.scenebeans.animation;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.bounds.DirtyBounds;

public class AnimationCanvas extends Canvas {
    private Animation _animation = null;
    private WindowTransform _root = new WindowTransform();
    private RenderingHints _hints = null;
    private Image _backbuffer;
    private Thread _runner;
    private long _frame_delay = 25L;
    private double _time_warp = 1.0D;
    private boolean _pause = false, _paused = false;
    private boolean _is_timing_adaptive = false;
    private boolean _is_update_pending = false;

    public AnimationCanvas() {
        enableEvents(1L);
        this._runner = new Thread(this) {
            private final AnimationCanvas this$0;

            public void run() {
                this.this$0.animateAnimation();
            }
        };
        this._runner.start();
    }

    public Animation getAnimation() {
        return this._animation;
    }

    public synchronized void setAnimation(Animation paramAnimation) {
        this._animation = paramAnimation;
        this._root.setTransformedGraph(paramAnimation);
        invalidate();
        repaint();
        notifyAll();
    }

    public SceneGraph getSceneGraph() {
        return (SceneGraph) this._root;
    }

    public synchronized long getFrameDelay() {
        return this._frame_delay;
    }

    public synchronized void setFrameDelay(long paramLong) {
        this._frame_delay = paramLong;
    }

    public synchronized double getTimeWarp() {
        return this._time_warp;
    }

    public synchronized void setTimeWarp(double paramDouble) {
        this._time_warp = paramDouble;
    }

    public boolean isTimingAdaptive() {
        return this._is_timing_adaptive;
    }

    public void setTimingAdaptive(boolean paramBoolean) {
        this._is_timing_adaptive = paramBoolean;
    }

    public RenderingHints getRenderingHints() {
        return this._hints;
    }

    public void setRenderingHints(RenderingHints paramRenderingHints) {
        this._hints = paramRenderingHints;
        repaint();
    }

    public boolean isPaused() {
        return this._paused;
    }

    public synchronized void setPaused(boolean paramBoolean) {
        this._pause = paramBoolean;
        this._paused = false;
        if (!paramBoolean)
            notifyAll();
    }

    public synchronized void waitPaused() throws InterruptedException {
        setPaused(true);
        for (; !isPaused(); wait());
    }

    public boolean isAnimationCentered() {
        return this._root.isCentered();
    }

    public synchronized void setAnimationCentered(boolean paramBoolean) {
        this._root.setCentered(paramBoolean);
        if (this._paused) {
            paintBackbuffer();
            repaint();
        }
    }

    public boolean isAnimationStretched() {
        return this._root.isStretched();
    }

    public synchronized void setAnimationStretched(boolean paramBoolean) {
        this._root.setStretched(paramBoolean);
        if (this._paused) {
            paintBackbuffer();
            repaint();
        }
    }

    public boolean isAnimationAspectFixed() {
        return this._root.isAspectFixed();
    }

    public synchronized void setAnimationAspectFixed(boolean paramBoolean) {
        this._root.setAspectFixed(paramBoolean);
        if (this._paused) {
            paintBackbuffer();
            repaint();
        }
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        if (this._animation == null)
            return new Dimension(256, 256);
        return new Dimension((int) Math.ceil(this._animation.getWidth()), (int) Math.ceil(this._animation.getHeight()));
    }

    public boolean isDoubleBuffered() {
        return true;
    }

    public synchronized void stop() {
        if (this._runner != null) {
            Thread thread = this._runner;
            this._runner = null;
            thread.interrupt();
        }
    }

    protected void finalize() throws Throwable {
        this._runner.interrupt();
        super.finalize();
    }

    void animateAnimation() {
        long l = System.currentTimeMillis();
        try {
            while (true) {
                synchronized (this) {
                    double d;
                    for (; this._animation == null; wait());
                    while (this._pause) {
                        this._paused = true;
                        notifyAll();
                        wait();
                        l = System.currentTimeMillis();
                    }
                    long l1 = System.currentTimeMillis();
                    if (isTimingAdaptive()) {
                        d = this._time_warp * (l1 - l) / 1000.0D;
                    } else {
                        d = this._time_warp * this._frame_delay / 1000.0D;
                    }
                    this._animation.performActivity(d);
                    paintAnimationFrame();
                    l = l1;
                }
                Thread.sleep(this._frame_delay);
            }
        } catch (InterruptedException interruptedException) {
            return;
        }
    }

    private void discardBackbuffer() {
        if (this._backbuffer != null) {
            this._backbuffer.flush();
            this._backbuffer = null;
        }
    }

    private Rectangle2D paintBackbuffer() {
        Graphics2D graphics2D = null;
        try {
            Rectangle2D rectangle2D = null;
            if (this._backbuffer == null) {
                this._backbuffer = createImage(getWidth(), getHeight());
                graphics2D = (Graphics2D) this._backbuffer.getGraphics();
            } else {
                graphics2D = (Graphics2D) this._backbuffer.getGraphics();
                rectangle2D = DirtyBounds.getBounds((SceneGraph) this._root, graphics2D);
                if (rectangle2D != null) {
                    graphics2D.clip(rectangle2D);
                } else {
                    return null;
                }
            }
            if (this._hints != null)
                graphics2D.setRenderingHints(this._hints);
            graphics2D.clearRect(0, 0, getWidth(), getHeight());
            this._root.draw(graphics2D);
            return rectangle2D;
        } finally {
            if (graphics2D != null)
                graphics2D.dispose();
        }
    }

    private void paintFrontbuffer(Graphics2D paramGraphics2D) {
        paramGraphics2D.drawImage(this._backbuffer, 0, 0, null);
    }

    public synchronized void update(Graphics paramGraphics) {
        paint(paramGraphics);
    }

    public synchronized void paint(Graphics paramGraphics) {
        if (this._backbuffer == null)
            paintBackbuffer();
        paintFrontbuffer((Graphics2D) paramGraphics);
    }

    private synchronized void paintAnimationFrame() {
        if (!isShowing())
            return;
        this._is_update_pending = false;
        Rectangle2D rectangle2D = paintBackbuffer();
        if (rectangle2D != null) {
            Graphics2D graphics2D = (Graphics2D) getGraphics();
            try {
                graphics2D.setClip(rectangle2D);
                paintFrontbuffer(graphics2D);
            } finally {
                graphics2D.dispose();
            }
        }
    }

    protected synchronized void processComponentEvent(ComponentEvent paramComponentEvent) {
        if (paramComponentEvent.getID() == 101 || paramComponentEvent.getID() == 102)
            this._root.setWindowSize(paramComponentEvent.getComponent().getWidth(), paramComponentEvent.getComponent().getHeight());
        if (paramComponentEvent.getID() == 101)
            discardBackbuffer();
        super.processComponentEvent(paramComponentEvent);
    }
}
