package gov.nasa.arc.ase.util;

import java.util.Collection;

public class Tree {
    private TreeNode root = null;

    public TreeNode getRoot() {
        return this.root;
    }

    void setRoot(TreeNode paramTreeNode) {
        this.root = paramTreeNode;
    }

    public Collection get(TreeNode paramTreeNode) {
        return this.root.get(paramTreeNode);
    }

    public String toString() {
        if (this.root == null)
            return "-";
        return "\\" + this.root.toString("   ");
    }
}
