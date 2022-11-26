package gov.nasa.arc.ase.util.graph;

public interface Visitor {
    void visitNode(Node paramNode);

    void visitEdge(Edge paramEdge);
}
