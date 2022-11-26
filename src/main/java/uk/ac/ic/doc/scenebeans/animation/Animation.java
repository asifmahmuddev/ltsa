package uk.ac.ic.doc.scenebeans.animation;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Layered;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.activity.Activity;
import uk.ac.ic.doc.scenebeans.activity.ActivityBase;
import uk.ac.ic.doc.scenebeans.activity.ActivityList;
import uk.ac.ic.doc.scenebeans.activity.ActivityRunner;

public class Animation extends ActivityBase implements CompositeNode, Serializable, ActivityRunner {
    private ActivityList _activities = ActivityList.EMPTY;
    private Layered _layers = new Layered();
    private Map _commands = new HashMap();
    private Set _event_names = new HashSet();
    private double _width = 0.0D;
    private double _height = 0.0D;
    private boolean _is_animated = false;
    private boolean _is_dirty = false;

    public double getWidth() {
        return this._width;
    }

    public void setWidth(double paramDouble) {
        this._width = paramDouble;
    }

    public double getHeight() {
        return this._height;
    }

    public void setHeight(double paramDouble) {
        this._height = paramDouble;
    }

    public boolean isDirty() {
        return this._is_dirty;
    }

    public void setDirty(boolean paramBoolean) {
        this._is_dirty = paramBoolean;
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public int getSubgraphCount() {
        return this._layers.getSubgraphCount();
    }

    public SceneGraph getSubgraph(int paramInt) {
        return this._layers.getSubgraph(paramInt);
    }

    public int getVisibleSubgraphCount() {
        return this._layers.getVisibleSubgraphCount();
    }

    public SceneGraph getVisibleSubgraph(int paramInt) {
        return this._layers.getVisibleSubgraph(paramInt);
    }

    public int getLastDrawnSubgraphCount() {
        return this._layers.getLastDrawnSubgraphCount();
    }

    public SceneGraph getLastDrawnSubgraph(int paramInt) {
        return this._layers.getLastDrawnSubgraph(paramInt);
    }

    public void addSubgraph(SceneGraph paramSceneGraph) {
        this._layers.addSubgraph(paramSceneGraph);
    }

    public void removeSubgraph(SceneGraph paramSceneGraph) {
        this._layers.removeSubgraph(paramSceneGraph);
    }

    public void removeSubgraph(int paramInt) {
        this._layers.removeSubgraph(paramInt);
    }

    public void draw(Graphics2D paramGraphics2D) {
        this._layers.draw(paramGraphics2D);
    }

    public synchronized void addActivity(Activity paramActivity) {
        if (paramActivity.getActivityRunner() != this) {
            paramActivity.setActivityRunner(this);
            this._activities = this._activities.add(paramActivity);
        }
    }

    public synchronized void removeActivity(Activity paramActivity) {
        if (paramActivity.getActivityRunner() == this) {
            paramActivity.setActivityRunner(null);
            this._activities = this._activities.remove(paramActivity);
        }
    }

    public boolean isFinite() {
        return false;
    }

    public void reset() {
        for (Iterator iterator = this._activities.iterator(); iterator.hasNext();)
            ((Activity) iterator.next()).reset();
    }

    public void performActivity(double paramDouble) {
        this._activities.performActivities(paramDouble);
    }

    public synchronized void addCommand(String paramString, Command paramCommand) {
        this._commands.put(paramString, paramCommand);
    }

    public synchronized void removeCommand(String paramString) {
        this._commands.remove(paramString);
    }

    public synchronized Set getCommandNames() {
        return Collections.unmodifiableSet(this._commands.keySet());
    }

    public synchronized Command getCommand(String paramString) {
        return (Command) this._commands.get(paramString);
    }

    public synchronized void invokeCommand(String paramString) throws CommandException {
        Command command = (Command) this._commands.get(paramString);
        if (command != null) {
            command.invoke();
        } else {
            throw new CommandException("unknown command \"" + paramString + "\"");
        }
    }

    public Set getEventNames() {
        return Collections.unmodifiableSet(this._event_names);
    }

    public void addEventName(String paramString) {
        this._event_names.add(paramString);
    }

    public void removeEventName(String paramString) {
        this._event_names.remove(paramString);
    }

    final void announceAnimationEvent(String paramString) {
        postActivityComplete(paramString);
    }
}
