import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Algorithm k-invert:
 * Start with any random order.  reverse the order of k consecutive nodes in
 * the path.  If it's an improvement keep it, otherwise discard.  Repeat.
 * 
 */
public class Kpermute {

    private AdjacencyList graph;
    private Random random = new Random(FloydWarshall.INFINITY);
    private int nNodes;

    public Kpermute(AdjacencyList g) {
	graph = g;
    }

    public ArrayList<Edge> solve(ArrayList<Integer> klist) {
        ArrayList<Node> nodeList = graph.getSortedNodeList();
        nNodes = nodeList.size();
        ArrayList<Edge> edges;
        int count = 0;
        final int limit = 10000;
        int K = 0;

        FloydWarshall fw = new FloydWarshall(graph);
        graph.clearVisited();

        int[] path = new int[nNodes];
        path = getRandomPath(nNodes);
        int minWeight = getWeight(fw, path);
        int[] newPath = null;
        Debug.println(2, "initial weight = " + getWeight(fw, path));
        printPath(path);

        count = limit+1;
        while (true) {
            if (count++ > limit) {
                if (klist.isEmpty())
                    break;
                K = klist.get(0);
                klist.remove(0);
                if (K > nNodes)
                    continue;
                count = 0;
                Debug.println(2, "K = " + K);
            }

            int[] nodes = getRandomNodes(nNodes, K);
            if (Debug.getLevel() > 2) {
                Debug.print(3, "permuting nodes: ");
                printPath(nodes);
            }

            PermutationGenerator pg = new PermutationGenerator(K);
            pg.getNext();           // skip first one
            while (pg.hasMore()) {
                int[] indices = pg.getNext();
                newPath = permutePath(path, nodes, indices, K);
                if (Debug.getLevel() > 2) {
                    Debug.print(3, "trying path: ");
                    printPath(newPath);
                    Debug.print(3, "indices: ");
                    printPath(indices);
                }

                int w = getWeight(fw, newPath);
                if (minWeight > w) {
                    minWeight = w;
                    Debug.println(2, "better weight = " + minWeight);
                    Debug.print(2, "path: ");
                    printPath(path);
                    path = newPath;
                    count = 0;
                }
            }
        }
        edges = getEdges(fw, path);
        return edges;
    }

    public boolean visitedAllNodes(ArrayList<Edge> path) {
        Set<Node> visitedNodeSet = new HashSet<Node>();
        for (Edge e : path) {
            visitedNodeSet.add(e.from);
        }
        return (visitedNodeSet.size() == nNodes);
    }

    public ArrayList<Edge> getEdges(FloydWarshall fw, int[] path) {
        Set<Edge> edgeSet = new HashSet<Edge>();
        ArrayList<Node> pathNodes = new ArrayList<Node>();
        Node n = graph.getNode(path[0]);
        pathNodes.add(graph.getNode(path[0]));
        for (int i=0; i<path.length; i++) {
            int to = i+1;
            if (i == (path.length - 1))
                to = 0;
            ArrayList<Node> nodes = fw.getShortestPath(path[i], path[to]);
            nodes.remove(0);
            pathNodes.addAll(nodes);
        }

        for (int i=0; i<pathNodes.size()-1; i++){
            edgeSet.add(graph.getEdge(pathNodes.get(i), pathNodes.get(i+1)));
        }
        return new ArrayList<Edge>(edgeSet);
    }

    public int getWeight(ArrayList<Edge> edges) {
        int w = 0;
        for (Edge e : edges)
            w += e.weight;
        return w;
    }

    public int getWeight(FloydWarshall fw, int[] path) {
        int w = 0;
        for (int i=0; i<path.length-1; i++){
            w += fw.getShortestDistance(path[i], path[i+1]);
        }
        w += fw.getShortestDistance(path[path.length-1], path[0]);
        return w;
    }

    public int[] permutePath(int[] path, int[] nodes, int[] perm, int k) {
        int n = path.length;
        int p[] = new int[n];
        System.arraycopy(path, 0, p, 0, n);
        if (k > n)
            k = n;
        for (int i=0; i<k; i++) {
            p[nodes[i]] = path[nodes[perm[i]]];
        }
        return p;
    }

    public int[] getRandomNodes(int n, int k) {
        int a[] = new int[n];
        for (int i=0; i<n; i++)
            a[i] = i;
        if (k > n)
            k = n;
        int m = k;
        int b[] = new int[k];
        while (k-- > 0) {
             int i = random.nextInt(n--);
             int j = a[i];
             a[i] = a[n];
             a[n] = j;
        }
        k = 0;
        while (k < m) {
            b[k++] = a[n++];
        }
        return b;
    }

    public int[] getRandomPath(int n) {
        int a[] = new int[n];
        for (int i=0; i<n; i++)
            a[i] = i;
        int k = n;
        while (k > 1) {
             int i = random.nextInt(k);
             int j = a[i];
             k--;
             a[i] = a[k];
             a[k] = j;
        }
        return a;
    }

    private void printPath(int[] path) {
        for (int i : path)
            Debug.print(2, graph.getNode(i).name + " ");
        Debug.println(2, "");
    }

    public static void main(String[] args) {
        Debug.setLevel(1);
	if (args.length < 1) {
	    System.out.println("Usage: Kpermute <inputfile> ...");
	    return;
	}

        for (int i=0; i<args.length; i++) {
            String file = args[i];
            Debug.println("\nfile: " + file);
            AdjacencyList graph = Facebull.loadFile(file);
            Debug.println(2, "lower bound = " + graph.getCircuitLowerBound());
            Kpermute solver = new Kpermute(graph);
            ArrayList<Integer> klist = new ArrayList<Integer>()
                {{ add(5); add(4); add(3); add(2); }};
            ArrayList<Edge> edges = solver.solve(klist);
            Facebull.printSolution(edges);
        }
    }
}
