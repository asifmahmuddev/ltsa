package uk.ac.ic.doc.scenebeans.cag;

import uk.ac.ic.doc.scenebeans.CAGComposite;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public class CAGSetDirty implements SceneGraphProcessor {
    private boolean _is_dirty;

    public CAGSetDirty(boolean paramBoolean) {
        this._is_dirty = paramBoolean;
    }

    public static void setChildrenDirty(CAGComposite paramCAGComposite, boolean paramBoolean) {
        CAGSetDirty cAGSetDirty = new CAGSetDirty(paramBoolean);
        for (byte b = 0; b < paramCAGComposite.getSubgraphCount(); b++)
            paramCAGComposite.getSubgraph(b).accept(cAGSetDirty);
    }

    public void process(Primitive paramPrimitive) {
        paramPrimitive.setDirty(this._is_dirty);
    }

    public void process(Transform paramTransform) {
        paramTransform.setDirty(this._is_dirty);
        paramTransform.getTransformedGraph().accept(this);
    }

    public void process(Input paramInput) {
        paramInput.setDirty(this._is_dirty);
        paramInput.getSensitiveGraph().accept(this);
    }

    public void process(Style paramStyle) {
        paramStyle.setDirty(this._is_dirty);
        paramStyle.getStyledGraph().accept(this);
    }

    public void process(CompositeNode paramCompositeNode) {
        paramCompositeNode.setDirty(this._is_dirty);
        for (byte b = 0; b < paramCompositeNode.getSubgraphCount(); b++)
            paramCompositeNode.getSubgraph(b).accept(this);
    }
}
