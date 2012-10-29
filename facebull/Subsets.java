import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Generate all possible subsets of edges, with some pruning
 */
public class Subsets {
    final int INFINITY = Integer.MAX_VALUE;
    AdjacencyList graph;
    ArrayList<Edge> allEdges;
    Edge[] allEdgesArray;
    ArrayList<Node> nodeList;
    long nodeCount;
    int minWeight;
    int nNodes;
    int nEdges;
    int maxEdges;
    int weights[];
    int index;
    int[] minEdges;
    int minEdgesLength;
    HashMap<Node, ArrayList<Node>> adj = new HashMap<Node, ArrayList<Node>>();
    ArrayList<Node> stack = new ArrayList<Node>();
    ArrayList<ArrayList<Node>> adjLists;
    HashMap<Node, Integer> nodeIndex = new HashMap<Node, Integer>();

    public Subsets(AdjacencyList g) {
	graph = g;
        minWeight = INFINITY;
        nodeList = graph.getNodeList();
        nNodes = nodeList.size();
        allEdges = graph.getAllEdges();
        nEdges = allEdges.size();
        Collections.sort(allEdges, new EdgeWeightComparator());
        allEdgesArray = new Edge[nEdges];
        allEdgesArray = allEdges.toArray(allEdgesArray);
        weights = new int[nEdges];
        for (int i=0; i<nEdges; i++) {
            weights[i] = allEdges.get(i).weight;
        }
        for (int i=0; i<nNodes; i++) {
            Node n = nodeList.get(i);
            nodeIndex.put(n, graph.getNodeIndex(n));
            adj.put(n, new ArrayList<Node>());
        }
    }

    public void solve(int weight) {
        minWeight = weight;
        solve();
    }

    public void solve() {
        maxEdges = Math.min(2*nNodes, nEdges);
        int[] edges = new int[maxEdges];
        minEdges = new int[maxEdges];

        long start = System.currentTimeMillis();
        for (int m = nNodes; m <= maxEdges; m++) {
            Debug.println(2, "trying " + m + " edges... ");
            search(m, 0, 0, edges, 0, new HashSet<Node>());
        }
        long elapsed = System.currentTimeMillis() - start;
        
        if (Debug.isOn()) {
            double t = elapsed/1000.0;
            Debug.println("elapsed time = " + t);
        }
        ArrayList<Edge> edgeList = new ArrayList<Edge>();
        for (int i=0; i<minEdgesLength; i++) {
            edgeList.add(allEdges.get(minEdges[i]));
        }
        Facebull.printSolution(edgeList);
    }

    public void search(int m, int level, int weight, int edges[], int start, 
        HashSet<Node> inSet) {
        int end = nEdges-(m-level);
        for (int i=start; i<=end; i++) {
            if (Debug.debugLevel > 2 && level <= 2) {
                int j = level;
                while (j-- > 0) Debug.print(3, "  ");
                Debug.println(3, "  starting with " + i);
            }
            int w = weight + weights[i];
            if (w > minWeight)
                return;
            edges[level] = i;
            HashSet<Node> inSet2 = new HashSet<Node>(inSet);
            inSet2.add(allEdgesArray[i].to);
            if (level == m-1) {
                if (inSet2.size() == nNodes && isSCC(edges, level+1)) {
                    // found solution
                    for (int s=0; s<=level; s++) {
                        Debug.print(edges[s] + " ");
                    }
                    Debug.println("w="+w);
                    if (minWeight >= w)
                        minWeight = w;
                    minEdgesLength = level+1;
                    System.arraycopy(edges, 0, minEdges, 0, minEdgesLength);
                }            
            } else {
                search(m, level+1, w, edges, i+1, inSet2);
            }
        }
    }

    private boolean isSCC(int edges[], int len) {
        adj.clear();
        for (int i=0; i<len; i++) {
            Edge e = allEdgesArray[edges[i]];
            if (!adj.containsKey(e.from)) {
                adj.put(e.from, new ArrayList<Node>());
            }
            ArrayList<Node> list = adj.get(e.from);
            list.add(e.to);
        }
        for (Node n : nodeList) {
            n.reset();
            if (adj.get(n) == null || adj.get(n).size() == 0)
                return false;
        }
        Node v = nodeList.get(0);
        index = 0;
        stack.clear();
        return checkSCC(v);
    }

    private boolean checkSCC(Node v) {
        boolean ret = true;
        v.index = index;
        v.lowlink = index;
        index++;
        stack.add(v);
        ArrayList<Node> nodes = adj.get(v);
        if ((nodes == null) || (nodes.size() == 0))
            return false;
        for (Node n : nodes) {
           if (n.index == -1) {
               ret = checkSCC(n);
               if (!ret)
                   return false;
               if (v.lowlink > n.lowlink)
                   v.lowlink = n.lowlink;
           } else if (stack.contains(n)) {
               if (v.lowlink > n.index)
                   v.lowlink = n.index;
           }
        }
        if (v.lowlink == v.index){
            Node n;
            int i = 0;
            do {
                n = stack.remove(stack.size()-1);
                i++;
            } while (n != v);
            if (i != nNodes)
                ret = false;
        }
        return ret;
    }

    public static void main(String[] args) {
        Debug.setLevel(0);
	if (args.length < 1) {
	    System.out.println("Usage: Subsets <inputfile> ...");
	    return;
	}
        for (int i=0; i<args.length; i++) {
            String file = args[i];
            Debug.println("\nfile: " + file);
            AdjacencyList graph = Facebull.loadFile(file);
            Kpermute ksolver = new Kpermute(graph);
            ArrayList<Integer> klist = new ArrayList<Integer>()
                {{ add(5); add(4); add(3); add(2); }};
            ArrayList<Edge> edges = ksolver.solve(klist);
            int w = 0;
            for (Edge e : edges)
                w += e.weight;

            Subsets solver = new Subsets(graph);
            solver.solve(w);
        }
    }
}
