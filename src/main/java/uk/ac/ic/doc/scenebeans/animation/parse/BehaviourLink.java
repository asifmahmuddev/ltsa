package uk.ac.ic.doc.scenebeans.animation.parse;

public class BehaviourLink {
    private Object _behaviour;
    private String _behaviour_id;
    private Object _facet;
    private String _facet_name;
    private Object _animated;
    private Object _listener;
    private String _property_name;

    public BehaviourLink(Object paramObject1, String paramString1, Object paramObject2, Object paramObject3, String paramString2) {
        this._behaviour = paramObject1;
        this._behaviour_id = paramString1;
        this._facet = paramObject1;
        this._facet_name = null;
        this._animated = paramObject2;
        this._listener = paramObject3;
        this._property_name = paramString2;
    }

    public BehaviourLink(Object paramObject1, String paramString1, Object paramObject2, String paramString2, Object paramObject3, Object paramObject4, String paramString3) {
        this._behaviour = paramObject1;
        this._behaviour_id = paramString1;
        this._facet = paramObject2;
        this._facet_name = paramString2;
        this._animated = paramObject3;
        this._listener = paramObject4;
        this._property_name = paramString3;
    }

    public Object getBehaviour() {
        return this._behaviour;
    }

    public String getBehaviourID() {
        return this._behaviour_id;
    }

    public boolean isBehaviourFacetted() {
        return (this._facet_name != null);
    }

    public Object getFacet() {
        return this._facet;
    }

    public String getFacetName() {
        return this._facet_name;
    }

    public Object getAnimated() {
        return this._animated;
    }

    public Object getListener() {
        return this._listener;
    }

    public String getPropertyName() {
        return this._property_name;
    }
}
