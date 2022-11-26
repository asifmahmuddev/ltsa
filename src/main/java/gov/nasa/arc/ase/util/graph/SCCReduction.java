package gov.nasa.arc.ase.util.graph;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

public class SCCReduction {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.util.graph.SCCReduction [<filename>]");
            return;
        }
        Graph graph = null;
        try {
            if (paramArrayOfString.length == 0) {
                graph = Graph.load();
            } else {
                graph = Graph.load(paramArrayOfString[0]);
            }
        } catch (IOException iOException) {
            System.out.println("Can't load the graph.");
            return;
        }
        graph = reduce(graph);
        graph.save();
    }

    private static boolean isAccepting(List paramList, Graph paramGraph) {
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (str1.equals("ba")) {
            if (str2.equals("nodes")) {
                for (Iterator iterator = paramList.iterator(); iterator.hasNext();) {
                    if (((Node) iterator.next()).getBooleanAttribute("accepting"))
                        return true;
                }
                return false;
            }
            if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Edge edge : node.getOutgoingEdges()) {
                        if (edge.getBooleanAttribute("accepting"))
                            return true;
                    }
                }
                return false;
            }
            throw new RuntimeException("invalid accepting type: " + str2);
        }
        if (str1.equals("gba")) {
            int i = paramGraph.getIntAttribute("nsets");
            BitSet bitSet = new BitSet(i);
            byte b = 0;
            if (str2.equals("nodes")) {
                for (Node node : paramList) {
                    for (byte b1 = 0; b1 < i; b1++) {
                        if (node.getBooleanAttribute("acc" + b1) && !bitSet.get(b1)) {
                            bitSet.set(b1);
                            b++;
                        }
                    }
                }
            } else if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Edge edge : node.getOutgoingEdges()) {
                        for (byte b1 = 0; b1 < i; b1++) {
                            if (edge.getBooleanAttribute("acc" + b1) && !bitSet.get(b1)) {
                                bitSet.set(b1);
                                b++;
                            }
                        }
                    }
                }
            } else {
                throw new RuntimeException("invalid accepting type: " + str2);
            }
            return (b == i);
        }
        throw new RuntimeException("invalid graph type: " + str1);
    }

    private static boolean isTransient(List paramList) {
        if (paramList.size() != 1)
            return false;
        Node node = paramList.get(0);
        Iterator iterator = node.getOutgoingEdges().iterator();
        while (iterator.hasNext()) {
            if (((Edge) iterator.next()).getNext() == node)
                return false;
        }
        return true;
    }

    private static boolean isTerminal(List paramList) {
        for (Node node : paramList) {
            for (Iterator iterator = node.getOutgoingEdges().iterator(); iterator.hasNext();) {
                if (!paramList.contains(((Edge) iterator.next()).getNext()))
                    return false;
            }
        }
        return true;
    }

    private static void clearAccepting(List paramList, Graph paramGraph) {
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (str1.equals("ba")) {
            if (str2.equals("nodes")) {
                for (Node node : paramList)
                    node.setBooleanAttribute("accepting", false);
            } else if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Edge edge : node.getOutgoingEdges())
                        edge.setBooleanAttribute("accepting", false);
                }
            } else {
                throw new RuntimeException("invalid accepting type: " + str2);
            }
        } else if (str1.equals("gba")) {
            int i = paramGraph.getIntAttribute("nsets");
            if (str2.equals("nodes")) {
                for (Node node : paramList) {
                    for (byte b = 0; b < i; b++)
                        node.setBooleanAttribute("acc" + b, false);
                }
            } else if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Edge edge : node.getOutgoingEdges()) {
                        for (byte b = 0; b < i; b++)
                            edge.setBooleanAttribute("acc" + b, false);
                    }
                }
            } else {
                throw new RuntimeException("invalid accepting type: " + str2);
            }
        } else {
            throw new RuntimeException("invalid graph type: " + str1);
        }
    }

    private static void clearExternalEdges(List paramList, Graph paramGraph) {
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (str1.equals("ba")) {
            if (!str2.equals("nodes"))
                if (str2.equals("edges")) {
                    for (Node node : paramList) {
                        for (Edge edge : node.getOutgoingEdges()) {
                            if (!paramList.contains(edge.getNext()))
                                edge.setBooleanAttribute("accepting", false);
                        }
                    }
                } else {
                    throw new RuntimeException("invalid accepting type: " + str2);
                }
        } else if (str1.equals("gba")) {
            int i = paramGraph.getIntAttribute("nsets");
            if (!str2.equals("nodes"))
                if (str2.equals("edges")) {
                    for (Node node : paramList) {
                        for (Edge edge : node.getOutgoingEdges()) {
                            if (!paramList.contains(edge.getNext()))
                                for (byte b = 0; b < i; b++)
                                    edge.setBooleanAttribute("acc" + b, false);
                        }
                    }
                } else {
                    throw new RuntimeException("invalid accepting type: " + str2);
                }
        } else {
            throw new RuntimeException("invalid graph type: " + str1);
        }
    }

    private static boolean anyAcceptingState(List paramList, Graph paramGraph) {
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (str1.equals("ba")) {
            if (str2.equals("nodes")) {
                for (Node node : paramList) {
                    if (node.getBooleanAttribute("accepting"))
                        return true;
                }
            } else if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Edge edge : node.getOutgoingEdges()) {
                        if (edge.getBooleanAttribute("accepting"))
                            return true;
                    }
                }
            } else {
                throw new RuntimeException("invalid accepting type: " + str2);
            }
        } else if (str1.equals("gba")) {
            int i = paramGraph.getIntAttribute("nsets");
            if (str2.equals("nodes")) {
                for (Node node : paramList) {
                    for (byte b = 0; b < i; b++) {
                        if (node.getBooleanAttribute("acc" + b))
                            return true;
                    }
                }
            } else if (str2.equals("edges")) {
                for (Node node : paramList) {
                    for (Iterator iterator = node.getOutgoingEdges().iterator(); iterator.hasNext();) {
                        Edge edge = iterator.next();
                        for (byte b = 0; b < i; b++) {
                            if (edge.getBooleanAttribute("acc" + iterator))
                                return true;
                        }
                    }
                }
            } else {
                throw new RuntimeException("invalid accepting type: " + str2);
            }
        } else {
            throw new RuntimeException("invalid graph type: " + str1);
        }
        return false;
    }

    public static Graph reduce(Graph paramGraph) {
        boolean bool1;
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        boolean bool2 = str2.equals("nodes");
        for (Iterator iterator = SCC.scc(paramGraph).iterator(); iterator.hasNext();)
            clearExternalEdges(iterator.next(), paramGraph);
        do {
            bool1 = false;
            List list = SCC.scc(paramGraph);
            for (List list1 : list) {
                boolean bool = isAccepting(list1, paramGraph);
                if (!bool && isTerminal(list1)) {
                    bool1 = true;
                    for (Iterator iterator1 = list1.iterator(); iterator1.hasNext();)
                        ((Node) iterator1.next()).remove();
                    continue;
                }
                if (isTransient(list1) || !bool) {
                    bool1 |= anyAcceptingState(list1, paramGraph);
                    clearAccepting(list1, paramGraph);
                }
            }
        } while (bool1);
        return paramGraph;
    }
}
