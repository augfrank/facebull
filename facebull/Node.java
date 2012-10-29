public class Node implements Comparable<Node> {

    public int name;
    public boolean visited = false;  // used for Kosaraju's algorithm
                                     // and Edmonds's algorithm
    public int lowlink = -1;         // used for Tarjan's algorithm
    public int index = -1;           // used for Tarjan's algorithm

    Node() {

    }

    Node(int n){
       name = n;
    }

    public int compareTo(Node n){
       return this.name - n.name;
    }

    public void reset() {
        index = lowlink = -1;
    }
}
