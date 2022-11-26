package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.scenebeans.cag.CAGDirty;
import uk.ac.ic.doc.scenebeans.cag.CAGProcessor;
import uk.ac.ic.doc.scenebeans.cag.CAGSetDirty;

public abstract class CAGComposite extends PrimitiveBase implements CompositeNode {
    private Area _area;
    private List _args = new ArrayList();

    public void draw(Graphics2D paramGraphics2D) {
        super.draw(paramGraphics2D);
        CAGSetDirty.setChildrenDirty(this, false);
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        if (this._area == null || isDirty())
            this._area = calculateArea(paramGraphics2D);
        return this._area;
    }

    public boolean isDirty() {
        return (super.isDirty() || CAGDirty.areChildrenDirty(this));
    }

    public int getSubgraphCount() {
        return this._args.size();
    }

    public SceneGraph getSubgraph(int paramInt) {
        return this._args.get(paramInt);
    }

    public int getVisibleSubgraphCount() {
        return 0;
    }

    public SceneGraph getVisibleSubgraph(int paramInt) {
        throw new IndexOutOfBoundsException("subgraph index out of range");
    }

    public int getLastDrawnSubgraphCount() {
        return 0;
    }

    public SceneGraph getLastDrawnSubgraph(int paramInt) {
        throw new IndexOutOfBoundsException("last-drawn subgraph index out of range");
    }

    public void addSubgraph(SceneGraph paramSceneGraph) {
        this._args.add(paramSceneGraph);
        setDirty(true);
    }

    public void removeSubgraph(SceneGraph paramSceneGraph) {
        this._args.remove(paramSceneGraph);
        setDirty(true);
    }

    public void removeSubgraph(int paramInt) {
        this._args.remove(paramInt);
        setDirty(true);
    }

    private Area calculateArea(Graphics2D paramGraphics2D) {
        CAGProcessor cAGProcessor = newCAGProcessor(paramGraphics2D);
        for (byte b = 0; b < getSubgraphCount(); b++)
            getSubgraph(b).accept((SceneGraphProcessor) cAGProcessor);
        return cAGProcessor.getArea();
    }

    protected abstract CAGProcessor newCAGProcessor(Graphics2D paramGraphics2D);
}
