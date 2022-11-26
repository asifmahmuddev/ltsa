package gov.nasa.arc.ase.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TreeNode {
    private Tree tree;
    private TreeNode parent;
    private Collection children;

    public TreeNode(Tree paramTree) {
        this.tree = paramTree;
        this.parent = null;
        this.children = new ArrayList();
        paramTree.setRoot(this);
    }

    public TreeNode(TreeNode paramTreeNode) {
        this.tree = paramTreeNode.tree;
        this.parent = paramTreeNode;
        this.children = new ArrayList();
        paramTreeNode.addChild(this);
    }

    private void addChild(TreeNode paramTreeNode) {
        this.children.add(paramTreeNode);
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public Tree getTree() {
        return this.tree;
    }

    public Collection children() {
        return this.children;
    }

    public Collection below() {
        ArrayList arrayList = new ArrayList();
        for (TreeNode treeNode : this.children) {
            arrayList.add(treeNode);
            arrayList.addAll(treeNode.below());
        }
        return arrayList;
    }

    public Collection above() {
        ArrayList arrayList = new ArrayList();
        if (this.parent != null) {
            arrayList.add(this.parent);
            arrayList.addAll(this.parent.above());
        }
        return arrayList;
    }

    public Collection get(TreeNode paramTreeNode) {
        ArrayList arrayList = new ArrayList();
        if (equals(paramTreeNode))
            arrayList.add(this);
        for (TreeNode treeNode : this.children)
            arrayList.addAll(treeNode.get(paramTreeNode));
        return arrayList;
    }

    public String toString(String paramString) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(' ');
        stringBuffer.append(this);
        stringBuffer.append('\n');
        for (Iterator iterator = this.children.iterator(); iterator.hasNext();) {
            TreeNode treeNode = iterator.next();
            stringBuffer.append(paramString);
            if (iterator.hasNext()) {
                stringBuffer.append('+');
                stringBuffer.append(treeNode.toString(paramString + "|  "));
                continue;
            }
            stringBuffer.append('\\');
            stringBuffer.append(treeNode.toString(paramString + "   "));
        }
        return stringBuffer.toString();
    }
}
