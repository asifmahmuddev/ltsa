package uk.ac.ic.doc.scenebeans.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CompositeCommand implements Command {
    private List _actions;

    public CompositeCommand() {
        this._actions = new ArrayList();
    }

    public CompositeCommand(List paramList) {
        this._actions = paramList;
    }

    public void addCommand(Command paramCommand) {
        this._actions.add(paramCommand);
    }

    public void removeCommand(Command paramCommand) {
        this._actions.remove(paramCommand);
    }

    public void removeCommand(int paramInt) {
        this._actions.remove(paramInt);
    }

    public int getCommandCount() {
        return this._actions.size();
    }

    public List getCommands() {
        return Collections.unmodifiableList(this._actions);
    }

    public Command getCommand(int paramInt) {
        return this._actions.get(paramInt);
    }

    public void invoke() throws CommandException {
        for (Iterator iterator = this._actions.iterator(); iterator.hasNext();)
            ((Command) iterator.next()).invoke();
    }
}
