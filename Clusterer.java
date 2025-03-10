import java.util.*;

public class Clusterer {
    private List<List<WeightedEdge<Integer, Double>>> adjList; // the adjacency list of the original graph
    private List<List<WeightedEdge<Integer, Double>>> mstAdjList; // the adjacency list of the minimum spanning tree
    private List<List<Integer>> clusters; // a list of k points, each representing one of the clusters.
    private double cost; // the distance between the closest pair of clusters

    public Clusterer(double[][] distances, int k){
        // Initialize minimum spanning stree adjancency list with n items, will be populated by
        mstAdjList = new ArrayList<>(distances.length);
        for (int i = 0; i < distances.length; i++) {
            mstAdjList.add(new LinkedList<WeightedEdge<Integer, Double>>());
        }

        // Initialize the clusters list with k items
        clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            clusters.add(new LinkedList<Integer>());
        }

        // Populate adjacency list
        adjList = new ArrayList<>(distances.length);
        for (int i = 0; i < distances.length; i++) {
            // Create linked list for current node
            adjList.add(new LinkedList<WeightedEdge<Integer, Double>>());
            List<WeightedEdge<Integer, Double>> cur_node = adjList.get(i);
            for (int j = 0; j < distances[i].length; j++) {
                // Add each edge from current node i to the linked list
                cur_node.add(new WeightedEdge<Integer,Double>(i, j, distances[i][j]));
            }
        }      
    }

    // implement Prim's algorithm to find a MST of the graph.
    // in my implementation I used the mstAdjList field to store this.
    private void prims(int start){
        //TODO
    }


    // After making the minimum spanning tree, use this method to
    // remove its k-1 heaviest edges, then assign integers
    // to clusters based on which nodes are still connected by
    // the remaining MST edges.
    private void makeKCluster(int k){
        //TODO
    }

    public List<List<Integer>> getClusters(){
        return clusters;
    }

    public double getCost(){
        return cost;
    }

}
