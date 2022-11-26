package uk.ac.ic.doc.scenebeans;

public interface SceneGraphProcessor {
    void process(Primitive paramPrimitive);

    void process(CompositeNode paramCompositeNode);

    void process(Transform paramTransform);

    void process(Style paramStyle);

    void process(Input paramInput);
}
