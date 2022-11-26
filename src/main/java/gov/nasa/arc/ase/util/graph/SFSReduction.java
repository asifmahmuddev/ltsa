package gov.nasa.arc.ase.util.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

public class SFSReduction {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.util.graph.SFSReduction [<filename>]");
            return;
        }
        Graph graph1 = null;
        try {
            if (paramArrayOfString.length == 0) {
                graph1 = Graph.load();
            } else {
                graph1 = Graph.load(paramArrayOfString[0]);
            }
        } catch (IOException iOException) {
            System.out.println("Can't load the graph.");
            return;
        }
        Graph graph2 = reduce(graph1);
        graph2.save();
    }

    private static boolean isAccepting(Node paramNode) {
        return paramNode.getBooleanAttribute("accepting");
    }

    private static String subterm(String paramString1, String paramString2) {
        String str1, str2;
        if (paramString1.equals("-") && paramString2.equals("-"))
            return "true";
        if (paramString1.equals("-"))
            return paramString1;
        if (paramString2.equals("-"))
            return paramString2;
        if (paramString1.indexOf("true") != -1 && paramString2.indexOf("true") != -1)
            return "true";
        if (paramString1.indexOf("true") != -1)
            return paramString1;
        if (paramString2.indexOf("true") != -1)
            return paramString2;
        if (paramString1.length() <= paramString2.length()) {
            str1 = paramString1;
            str2 = paramString2;
        } else {
            str1 = paramString2;
            str2 = paramString1;
        }
        StringTokenizer stringTokenizer1 = new StringTokenizer(str1, "&");
        StringTokenizer stringTokenizer2 = new StringTokenizer(str2, "&");
        LinkedList linkedList = new LinkedList();
        while (stringTokenizer2.hasMoreTokens()) {
            String str = stringTokenizer2.nextToken();
            linkedList.add(str);
        }
        while (stringTokenizer1.hasMoreTokens()) {
            String str = stringTokenizer1.nextToken();
            if (!linkedList.contains(str))
                return "false";
        }
        if (paramString1.length() == paramString2.length())
            return "true";
        return str1;
    }

    private static ITypeNeighbor iDominates(ITypeNeighbor paramITypeNeighbor1, ITypeNeighbor paramITypeNeighbor2, boolean[][] paramArrayOfboolean) {
        String str1 = paramITypeNeighbor1.getTransition();
        String str2 = paramITypeNeighbor2.getTransition();
        int i = paramITypeNeighbor1.getColor();
        int j = paramITypeNeighbor2.getColor();
        String str3 = subterm(str1, str2);
        if (str3 == str1) {
            if (paramArrayOfboolean[j - 1][i - 1])
                return paramITypeNeighbor1;
            return null;
        }
        if (str3 == str2) {
            if (paramArrayOfboolean[i - 1][j - 1])
                return paramITypeNeighbor2;
            return null;
        }
        if (str3.equals("true")) {
            if (paramArrayOfboolean[j - 1][i - 1])
                return paramITypeNeighbor1;
            if (paramArrayOfboolean[i - 1][j - 1])
                return paramITypeNeighbor2;
        }
        return null;
    }

    private static boolean iDominateSet(TreeSet paramTreeSet1, TreeSet paramTreeSet2, boolean[][] paramArrayOfboolean) {
        TreeSet treeSet = new TreeSet(paramTreeSet2);
        for (Iterator iterator = treeSet.iterator(); iterator.hasNext();) {
            ITypeNeighbor iTypeNeighbor = iterator.next();
            for (ITypeNeighbor iTypeNeighbor1 : paramTreeSet1) {
                ITypeNeighbor iTypeNeighbor2 = iDominates(iTypeNeighbor1, iTypeNeighbor, paramArrayOfboolean);
                if (iTypeNeighbor2 == iTypeNeighbor1) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (treeSet.size() == 0)
            return true;
        return false;
    }

    private static TreeSet getPrevN(Node paramNode, boolean[][] paramArrayOfboolean) {
        List list = paramNode.getOutgoingEdges();
        LinkedList linkedList = new LinkedList();
        TreeSet treeSet = new TreeSet();
        for (Edge edge : list) {
            ITypeNeighbor iTypeNeighbor = new ITypeNeighbor(edge.getNext().getIntAttribute("_prevColor"), edge.getGuard());
            linkedList.add(iTypeNeighbor);
        }
        if (linkedList.size() == 0)
            return treeSet;
        do {
            boolean bool = false;
            ITypeNeighbor iTypeNeighbor = linkedList.removeFirst();
            for (Iterator iterator = linkedList.iterator(); iterator.hasNext();) {
                ITypeNeighbor iTypeNeighbor1 = iterator.next();
                ITypeNeighbor iTypeNeighbor2 = iDominates(iTypeNeighbor, iTypeNeighbor1, paramArrayOfboolean);
                if (iTypeNeighbor2 == iTypeNeighbor)
                    iterator.remove();
                if (iTypeNeighbor2 == iTypeNeighbor1) {
                    bool = true;
                    break;
                }
            }
            if (bool)
                continue;
            treeSet.add(iTypeNeighbor);
        } while (linkedList.size() > 0);
        return treeSet;
    }

    private static Graph reachabilityGraph(Graph paramGraph) {
        Vector vector1 = new Vector();
        Vector vector2 = new Vector();
        vector1.add(paramGraph.getInit());
        while (!vector1.isEmpty()) {
            Node node = vector1.firstElement();
            vector2.add(node);
            if (node != null) {
                List list1 = node.getOutgoingEdges();
                for (Edge edge : list1) {
                    Node node1 = edge.getNext();
                    if (!vector1.contains(node1) && !vector2.contains(node1))
                        vector1.add(node1);
                }
            }
            if (vector1.remove(0) != node)
                System.out.println("ERROR");
        }
        List list = paramGraph.getNodes();
        if (list != null)
            for (Node node : list) {
                if (!vector2.contains(node))
                    paramGraph.removeNode(node);
            }
        return paramGraph;
    }

    public static Graph reduce(Graph paramGraph) {
        int i;
        Graph graph;
        int j = 1;
        int k = 4;
        int m = j;
        TreeSet treeSet = null;
        LinkedList linkedList = null;
        boolean bool1 = false;
        boolean bool2 = false;
        List list = paramGraph.getNodes();
        for (Node node : list) {
            node.setIntAttribute("_prevColor", 1);
            if (isAccepting(node)) {
                node.setIntAttribute("_currColor", 1);
                bool1 = true;
                continue;
            }
            node.setIntAttribute("_currColor", 2);
            bool2 = true;
        }
        if (bool1 && bool2) {
            i = 2;
        } else {
            i = 1;
        }
        boolean[][] arrayOfBoolean = new boolean[2][2];
        for (byte b = 0; b < 2; b++) {
            for (byte b1 = 0; b1 < 2; b1++) {
                if (b >= b1) {
                    arrayOfBoolean[b][b1] = true;
                } else {
                    arrayOfBoolean[b][b1] = false;
                }
            }
        }
        while (i != j || k != m) {
            for (Node node : list)
                node.setIntAttribute("_prevColor", node.getIntAttribute("_currColor"));
            boolean[][] arrayOfBoolean1 = arrayOfBoolean;
            j = i;
            linkedList = new LinkedList();
            treeSet = new TreeSet();
            for (Node node : list) {
                ColorPair colorPair = new ColorPair(node.getIntAttribute("_prevColor"), getPrevN(node, arrayOfBoolean1));
                linkedList.add(new Pair(node.getId(), colorPair));
                treeSet.add(colorPair);
            }
            i = treeSet.size();
            if (j == i)
                break;
            LinkedList linkedList1 = new LinkedList();
            for (ColorPair colorPair : treeSet)
                linkedList1.add(colorPair);
            for (Pair pair : linkedList) {
                ColorPair colorPair = (ColorPair) pair.getElement();
                paramGraph.getNode(pair.getValue()).setIntAttribute("_currColor", linkedList1.indexOf(colorPair) + 1);
            }
            arrayOfBoolean = new boolean[i][i];
            for (Iterator iterator = linkedList.iterator(); iterator.hasNext();) {
                ColorPair colorPair = (ColorPair) ((Pair) iterator.next()).getElement();
                for (Iterator iterator1 = linkedList.iterator(); iterator1.hasNext();) {
                    ColorPair colorPair1 = (ColorPair) ((Pair) iterator1.next()).getElement();
                    boolean bool3 = arrayOfBoolean1[colorPair1.getColor() - 1][colorPair.getColor() - 1];
                    boolean bool4 = iDominateSet(colorPair.getIMaxSet(), colorPair1.getIMaxSet(), arrayOfBoolean1);
                    if (bool3 && bool4) {
                        arrayOfBoolean[linkedList1.indexOf(colorPair1)][linkedList1.indexOf(colorPair)] = true;
                        continue;
                    }
                    arrayOfBoolean[linkedList1.indexOf(colorPair1)][linkedList1.indexOf(colorPair)] = false;
                }
            }
        }
        if (linkedList == null) {
            graph = paramGraph;
        } else {
            graph = new Graph();
            Node[] arrayOfNode = new Node[i];
            for (byte b1 = 0; b1 < i; b1++) {
                Node node = new Node(graph);
                arrayOfNode[b1] = node;
            }
            for (Pair pair : linkedList) {
                int n = pair.getValue();
                ColorPair colorPair = (ColorPair) pair.getElement();
                if (treeSet.contains(colorPair)) {
                    treeSet.remove(colorPair);
                    TreeSet treeSet1 = colorPair.getIMaxSet();
                    int i1 = colorPair.getColor();
                    Node node = arrayOfNode[i1 - 1];
                    for (ITypeNeighbor iTypeNeighbor : treeSet1) {
                        int i2 = iTypeNeighbor.getColor() - 1;
                        Edge edge = new Edge(node, arrayOfNode[i2], iTypeNeighbor.getTransition());
                    }
                    if (paramGraph.getInit().getId() == n)
                        graph.setInit(node);
                    if (isAccepting(paramGraph.getNode(n)))
                        node.setBooleanAttribute("accepting", true);
                }
            }
        }
        return reachabilityGraph(graph);
    }
}
