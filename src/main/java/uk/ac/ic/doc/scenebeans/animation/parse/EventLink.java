package uk.ac.ic.doc.scenebeans.animation.parse;

import uk.ac.ic.doc.scenebeans.animation.EventInvoker;

public class EventLink {
    private Object _source;
    private String _source_id;
    private EventInvoker _invoker;

    public EventLink(Object paramObject, String paramString, EventInvoker paramEventInvoker) {
        this._source = paramObject;
        this._source_id = paramString;
        this._invoker = paramEventInvoker;
    }

    public Object getSource() {
        return this._source;
    }

    public String getSourceID() {
        return this._source_id;
    }

    public EventInvoker getInvoker() {
        return this._invoker;
    }
}
