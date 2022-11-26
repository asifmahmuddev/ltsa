package uk.ac.ic.doc.scenebeans.event;

import java.util.EventObject;

public class AnimationEvent extends EventObject {
    private String _name;

    public AnimationEvent(Object paramObject, String paramString) {
        super(paramObject);
        this._name = paramString;
    }

    public String getName() {
        return this._name;
    }
}
