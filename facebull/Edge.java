
import java.util.ArrayList;

public class Edge implements Comparable<Edge> {

    public boolean traversed = false;
    public Node from, to;
    public int weight;
    public int name;
    boolean inCycle = false;

    Edge(int n, Node f, Node t, int w){
       name = n;
       from = f;
       to = t;
       weight = w;
    }

    public int compareTo(Edge e){
       return this.name - e.name;
    }

    public int getName() {
       return name;
    }

    public int getWeight() {
       return weight;
    }

    public boolean isInCycle() {
        return inCycle;
    }

    public void setInCycle(boolean flag) {
        inCycle = flag;
    }

    public boolean isTraversed() {
        return traversed;
    }
    public void setTraversed(boolean flag) {
        traversed = flag;
    }

    public static int getTotalWeight(ArrayList<Edge> edges) {
        int w = 0;
        for (Edge e: edges)
            w += e.weight;
        return w;

    }
}
