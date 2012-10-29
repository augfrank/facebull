import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * A directed graph represented as an adjacency list.
 * Both forward adjacency (outgoing edges) and reverse adjacency (incoming
 * edges) lists are maintained.  Thiese are implemented using HashMaps so that
 * retrieving either type of list for a given node is a constant time operation.
 *
 */
public class AdjacencyList {

    // Adjacency map: maps a Node to its outgoing Edges
    HashMap<Node, ArrayList<Edge>> adjacencies =
        new HashMap<Node, ArrayList<Edge>>();

    // Inverse adjacency map: maps a Node to its incoming Edges
    HashMap<Node, ArrayList<Edge>> invAdjacencies =
        new HashMap<Node, ArrayList<Edge>>();

    private int seq = 0;
    HashMap<Node,Integer> nodeMap = new HashMap<Node,Integer>();
    ArrayList<Node> nodeList = new ArrayList<Node>();

    boolean keepSorted = true;

    public AdjacencyList() {
    }

    public AdjacencyList(boolean sort) {
        keepSorted = sort;
    }

    public void addEdge(int name, Node source, Node target, int weight) {
        addEdge(new Edge(name, source, target, weight));
    }

    public void addEdge(Edge e) {
        Debug.println(4, "add edge " + e.name + ": " + e.from.name + "->" +
            e.to.name + " w=" + e.weight);

        // eliminate redundant edges, keeping only the minimum Edge one
        if (getEdge(e.from, e.to) != null) {
            if (getEdge(e.from, e.to).weight <= e.weight)
                return;
            removeEdge(getEdge(e.from, e.to));
        }

        // Add edge to adjacency map for quick look up
        if (!adjacencies.containsKey(e.from)) {
            adjacencies.put(e.from, new ArrayList<Edge>());
        }
        
        ArrayList<Edge> list = adjacencies.get(e.from);
        insert(list, e);        // keep list sorted

        // Add edge to inverse adjacency map for quick look up
        if (!invAdjacencies.containsKey(e.to)) {
            invAdjacencies.put(e.to, new ArrayList<Edge>());
        }
        list = invAdjacencies.get(e.to);
        insert(list, e);        // keep list sorted

        addNode(e.from);
        addNode(e.to);
    }

    public int getCircuitLowerBound() {
        int w = 0;
        for (Node n: nodeList) {
            Edge e1, e2;
            ArrayList<Edge> inEdges = getInEdges(n);
            ArrayList<Edge> outEdges = getOutEdges(n);
            if (inEdges.size() > 0 && outEdges.size() > 0) {
                e1 = inEdges.get(0);
                e2 = outEdges.get(0);
                w += (e1.weight + e2.weight) / 2;
            }
        }
        return w;
    }

    public int getNodeIndex(Node n) {
	return (nodeMap.get(n));
    }

    public Node getNode(int index) {
	return (nodeList.get(index));
    }

    public void removeEdge(Edge e) {
        ArrayList<Edge> list = adjacencies.get(e.from);
        if (list != null) {
            list.remove(e);
        }

        list = invAdjacencies.get(e.to);
        if (list != null) {
            list.remove(e);
        }
    }

    public ArrayList<Edge> getOutEdges(Node source){
        ArrayList<Edge> edges = adjacencies.get(source);
        if (edges == null)
            edges = new ArrayList<Edge>();
        return edges;
    }

    public ArrayList<Edge> getInEdges(Node target){
        ArrayList<Edge> edges = invAdjacencies.get(target);
        if (edges == null)
            edges = new ArrayList<Edge>();
        return edges;
    }

    public Edge getEdge(Node source, Node target) {
        ArrayList<Edge> adjEdges = adjacencies.get(source);
        if (adjEdges == null) return null;
        for (Edge e: adjEdges) {
            if (e.to.name == target.name) {
                return e;
            }
        }
        return null;
    }

    public ArrayList<Edge> getAllEdges(){
       ArrayList<Edge> edges = new ArrayList<Edge>();
       for(List<Edge> e : adjacencies.values()){
           edges.addAll(e);
       }
       return edges;
    }

    public AdjacencyList getCopy(){
       AdjacencyList list = new AdjacencyList();
       for (List<Edge> el : adjacencies.values()) {
           for (Edge e : el) {
            list.addEdge(e.name, e.from, e.to, e.weight);
           }
       }
       return list;
    }

    public HashSet<Node> getNodeSet() {
       return new HashSet<Node>(nodeList);
    }

    public Set<Node> getSourceNodeSet(){
       return adjacencies.keySet();
    }

    public Set<Node> getTargetNodeSet(){
       return invAdjacencies.keySet();
    }

    /**
     * @return  set of leaf nodes, defined as Nodes having zero outgoing edges
     */
    public HashSet<Node> getLeafSet() {
        HashSet<Node> leaves = new HashSet<Node>(nodeList.size());
        for (Node n: nodeList) {
            if (getOutEdges(n).size() == 0)
                leaves.add(n);
        }
        return leaves;
    }
    /**
     * @return  node list for the graph in index order.  should be treated as
     *          readonly, as any modifications to it will modify the graph!
     */
    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    /**
     * @return  node list for the graph sorted by Node.name.  This list
     *          can be modified without modifying the graph!
     */   public ArrayList<Node> getSortedNodeList() {
        ArrayList<Node> nList = new ArrayList<Node>(nodeList);
        Collections.sort(nList);
        return nList;
    }

    public void clearVisited() {
	for (Node n : getNodeSet()) {
            n.visited = false;
        }
    }

    public void printGraph() {
	if (adjacencies.size() == 0)
            return;

        ArrayList<Edge> edges = getAllEdges();
        Debug.println("Graph: " + nodeList.size() + " nodes, " +
            edges.size() + " edges");
        Debug.print("  nodes: ");
	for (Node n : getNodeSet()) {
	    Debug.print(n.name + " ");
	}
        Debug.println("");

        int totalWeight = 0;
	for (Edge e : edges) {
	    Debug.println("  edge " + e.name + ": " + e.from.name +
                "-->" + e.to.name + " weight:" + e.weight);
            totalWeight += e.weight;
	}
	Debug.println("END: total weight = " + totalWeight);
    }

    /**
     * insert edge, keeping list sorted in ascending order of Edge.
     * keeping edge lists sorted makes some algorithms faster.
     */
    private void insert(ArrayList<Edge> edges, Edge e) {
        int n = edges.size();
        int i = 0;
        while (i < n) {
            if (e.weight < edges.get(i).weight)
                break;
            i++;
        }
        edges.add(i, e);
    }

    private void addNode(Node n) {
	if (nodeMap.get(n) != null) {
	    return;		// already added
	}
	nodeList.add(n);
	nodeMap.put(n, seq++);
    }

}
