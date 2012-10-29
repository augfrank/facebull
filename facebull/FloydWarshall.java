import java.util.ArrayList;
import java.util.Arrays;

public class FloydWarshall {
    AdjacencyList graph;
    int[][] D = null;
    Node[][] P;

    public static final int INFINITY = Integer.MAX_VALUE;

    public FloydWarshall(AdjacencyList g) {
        graph = g;
    }

    public int getShortestDistance(Node source, Node target){
	int i = graph.getNodeIndex(source);
	int j = graph.getNodeIndex(target);
	return getShortestDistance(i, j);
    }
    
    public int getShortestDistance(int sourceIndex, int targetIndex){
        if (D == null)
            init();
	return D[sourceIndex][targetIndex];
    }

    public ArrayList<Node> getShortestPath(Node source, Node target) {
	int i = graph.getNodeIndex(source);
	int j = graph.getNodeIndex(target);
	return getShortestPath(i, j);
    }

    public ArrayList<Node> getShortestPath(int sourceIndex, int targetIndex){
        if (D == null)
            init();
	Node source = graph.getNode(sourceIndex);
	Node target = graph.getNode(targetIndex);
        if (D[sourceIndex][targetIndex] == INFINITY){
           return new ArrayList<Node>();
        }
        ArrayList<Node> path = getIntermediatePath(source, target);
        path.add(0, source);
        path.add(target);
        return path;
    }

    private ArrayList<Node> getIntermediatePath(Node source, Node target){
        if (D == null)
            init();
	int i = graph.getNodeIndex(source);
	int j = graph.getNodeIndex(target);
        if (P[i][j] == null){
           return new ArrayList<Node>();
        }
        ArrayList<Node> path = new ArrayList<Node>();
        path.addAll(getIntermediatePath(source, P[i][j]));
        path.add(P[i][j]);
        path.addAll(getIntermediatePath(P[i][j], target));
        return path;
    }

    public int[][] getMatrix() {
        if (D == null)
            init();
        return D;
    }

    private void init() {
        ArrayList<Node> nodeList = graph.getNodeList();
        int n = nodeList.size();
        D = new int[n][n];
        P = new Node[n][n];

        for (int i=0; i<n; i++) {
            Arrays.fill(D[i], INFINITY);
        }
        for (Edge e : graph.getAllEdges()) {
	    int i = graph.getNodeIndex(e.from);
	    int j = graph.getNodeIndex(e.to);
            D[i][j] = e.weight;
        }

        for (int k=0; k<n; k++) {
           for (int i=0; i<n; i++) {
               for (int j=0; j<n; j++) {
                   if (D[i][k] != INFINITY && D[k][j] != INFINITY &&
                         D[i][k]+D[k][j] < D[i][j]) {
                       D[i][j] = D[i][k]+D[k][j];
                       P[i][j] = graph.getNode(k);
                   }
               }
           }
        }
    }
}

