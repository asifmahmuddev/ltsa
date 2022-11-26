package uk.ac.ic.doc.scenebeans.cag;

import uk.ac.ic.doc.scenebeans.CAGComposite;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public class CAGDirty implements SceneGraphProcessor {
    private boolean _is_dirty = false;

    public static boolean areChildrenDirty(CAGComposite paramCAGComposite) {
        CAGDirty cAGDirty = new CAGDirty();
        for (byte b = 0; b < paramCAGComposite.getSubgraphCount(); b++) {
            paramCAGComposite.getSubgraph(b).accept(cAGDirty);
            if (cAGDirty.isDirty())
                return true;
        }
        return false;
    }

    public boolean isDirty() {
        return this._is_dirty;
    }

    public void process(Primitive paramPrimitive) {
        this._is_dirty = paramPrimitive.isDirty();
    }

    public void process(Transform paramTransform) {
        if (paramTransform.isDirty()) {
            this._is_dirty = true;
        } else {
            paramTransform.getTransformedGraph().accept(this);
        }
    }

    public void process(Input paramInput) {
        if (paramInput.isDirty()) {
            this._is_dirty = true;
        } else {
            paramInput.getSensitiveGraph().accept(this);
        }
    }

    public void process(Style paramStyle) {
        if (paramStyle.isDirty()) {
            this._is_dirty = true;
        } else {
            paramStyle.getStyledGraph().accept(this);
        }
    }

    public void process(CompositeNode paramCompositeNode) {
        if (paramCompositeNode.isDirty()) {
            this._is_dirty = true;
        } else {
            for (byte b = 0; b < paramCompositeNode.getVisibleSubgraphCount(); b++) {
                paramCompositeNode.getVisibleSubgraph(b).accept(this);
                if (this._is_dirty)
                    return;
            }
        }
    }
}
