package uk.ac.ic.doc.scenebeans;

public class Switch extends CompositeBase {
    private int _current = 0;

    public int getCurrent() {
        return this._current;
    }

    public void setCurrent(int paramInt) {
        this._current = paramInt;
        setDirty(true);
    }

    public int getVisibleSubgraphCount() {
        return 1;
    }

    public SceneGraph getVisibleSubgraph(int paramInt) {
        if (paramInt == 0)
            return getSubgraph(this._current);
        throw new IndexOutOfBoundsException("invalid subgraph index");
    }
}
