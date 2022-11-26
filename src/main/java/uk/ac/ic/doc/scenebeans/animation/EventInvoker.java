package uk.ac.ic.doc.scenebeans.animation;

import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public class EventInvoker implements AnimationListener {
    private String _event_name;
    private Command _command;

    public EventInvoker(String paramString, Command paramCommand) {
        this._event_name = paramString;
        this._command = paramCommand;
    }

    public String getEventName() {
        return this._event_name;
    }

    public void setEventName(String paramString) {
        this._event_name = paramString;
    }

    public Command getCommand() {
        return this._command;
    }

    public void setCommand(Command paramCommand) {
        this._command = paramCommand;
    }

    public void animationEvent(AnimationEvent paramAnimationEvent) {
        if (this._event_name.equals(paramAnimationEvent.getName()))
            try {
                this._command.invoke();
            } catch (CommandException commandException) {
            }
    }
}
