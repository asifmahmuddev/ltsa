package uk.ac.ic.doc.scenebeans.animation;

public class AnnounceCommand implements Command {
    private Animation _animation;
    private String _event;

    public AnnounceCommand(Animation paramAnimation, String paramString) {
        this._animation = paramAnimation;
        this._event = paramString;
    }

    public Animation getAnimation() {
        return this._animation;
    }

    public void setAnimation(Animation paramAnimation) {
        this._animation = paramAnimation;
    }

    public String getEventName() {
        return this._event;
    }

    public void setEventName(String paramString) {
        this._event = paramString;
    }

    public void invoke() throws CommandException {
        this._animation.announceAnimationEvent(this._event);
    }
}
