package gov.nasa.arc.ase.util.graph;

import java.io.IOException;

public class SM2DG {
    public static void main(String[] paramArrayOfString) {
        try {
            Graph graph = null;
            switch (paramArrayOfString.length) {
                case 0 :
                    graph = Graph.load("out.sm");
                    graph.save(1);
                    return;
                case 1 :
                    graph = Graph.load(paramArrayOfString[0]);
                    graph.save(1);
                    return;
                case 2 :
                    graph = Graph.load(paramArrayOfString[0]);
                    graph.save(paramArrayOfString[1], 1);
                    return;
            }
            System.err.println("usage:\nSM2DG [<infile> [<outfile>]]\n\n");
            System.exit(1);
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
}
