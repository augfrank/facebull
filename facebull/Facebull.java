import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class Facebull {
    long nodeCount;
    int circuitCount;
    int completeCircuitCount;
    int numNodes;
    int minWeight;
    Edge[] minCircuit;
    int minCircuitLength;
    Set<Node> nodeSet;

    public Facebull() {

    }

    public static AdjacencyList loadFile(String file) {
        BufferedReader reader;
        AdjacencyList graph = new AdjacencyList();
        try {
            String bufStr;
            reader = new BufferedReader(new FileReader(file));
            HashMap<String,Node> nodeMap = new HashMap<String,Node>();
            while ((bufStr = reader.readLine()) != null){
                String[] mach = bufStr.split("\\s+");
                if (mach.length < 4) {
                    continue;
                }
                Node n1, n2;
                Integer name;
                n1 = (Node)nodeMap.get(mach[1]);
                if (n1 == null) {
                    name = new Integer(mach[1].substring(1));
                    n1 = new Node(name.intValue());
                    nodeMap.put(mach[1], n1);
                }
                n2 = (Node)nodeMap.get(mach[2]);
                if (n2 == null) {
                    name = new Integer(mach[2].substring(1));
                    n2 = new Node(name.intValue());
                    nodeMap.put(mach[2], n2);
                }
                Long price = new Long(mach[3]);
                int w = price.intValue();
                name = new Integer(mach[0].substring(1));
                graph.addEdge(name.intValue(), n1, n2, w);
            }
            reader.close();
        }  catch (FileNotFoundException fnf) {
            System.out.println("Input file not found:" + file);
            graph = null;
        } catch (IOException ioe) {
            System.out.println("Unable to read input file:" + file);
            graph = null;
        }

        return graph;
    }

    static void printSolution(Edge[] minCircuit, int minCircuitLength) {
        ArrayList<Edge> machines = new ArrayList<Edge>();
        for (int i=0; i<minCircuitLength; i++) {
            machines.add(minCircuit[i]);
        }
        printSolution(machines);
    }

    static void printSolution(ArrayList<Edge> minCircuit) {
	int minWeight = 0;
	if (minCircuit == null) {
	    return;
	}
        for (int i=0; i<minCircuit.size(); i++) {
	    minWeight += minCircuit.get(i).weight;
        }
        Collections.sort(minCircuit);
        System.out.println(minWeight);
        for (int i=0; i<minCircuit.size(); i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(minCircuit.get(i).name);
        }
        System.out.println("");
    }
}
