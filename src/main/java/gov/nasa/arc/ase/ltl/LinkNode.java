package gov.nasa.arc.ase.ltl;

class LinkNode {
    private Node node;
    private LinkNode next;

    public LinkNode(Node paramNode, LinkNode paramLinkNode) {
        this.node = paramNode;
        this.next = paramLinkNode;
    }

    public Node getNode() {
        return this.node;
    }

    public LinkNode getNext() {
        return this.next;
    }

    public void LinkWith(LinkNode paramLinkNode) {
        this.next = paramLinkNode;
    }
}
