
import java.util.Comparator;

public class EdgeWeightComparator implements Comparator<Edge> {

    public int compare(Edge e1, Edge e2){
       return e1.weight - e2.weight;
    }

    public boolean equals(Edge e1, Edge e2) {
       return e1.weight == e2.weight;
    }
}
