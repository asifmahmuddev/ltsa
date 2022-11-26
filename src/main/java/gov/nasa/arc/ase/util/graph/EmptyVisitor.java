package gov.nasa.arc.ase.util.graph;

public class EmptyVisitor implements Visitor {
    protected Object arg;

    public EmptyVisitor() {
    }

    public EmptyVisitor(Object paramObject) {
        this.arg = paramObject;
    }

    public void visitNode(Node paramNode) {
    }

    public void visitEdge(Edge paramEdge) {
    }
}
