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
                // Add each edge from current node i, excluding itself, to the linked list
                if (i == j) continue;
                cur_node.add(new WeightedEdge<Integer,Double>(i, j, distances[i][j]));
            }
        }

        // Run prims to get minimum spanning tree
        prims(0);

        // Run makeKCluster to calculate cost and populate clusters
        makeKCluster(k);
    }

    // implement Prim's algorithm to find a MST of the graph.
    // in my implementation I used the mstAdjList field to store this.
    private void prims(int start){
        boolean[] known = new boolean[adjList.size()];
        Arrays.fill(known, false);
        int size = 1;

        // Initialize priority queue with start node's edges
        known[start] = true;
        PriorityQueue<WeightedEdge<Integer, Double>> queue = new PriorityQueue<>();
        for (WeightedEdge<Integer, Double> edge : adjList.get(start)) {
            queue.add(edge);
        }

        // Loop through priority queue until every node is added
        while(size != adjList.size()) {
            // Get cheapest edge
            WeightedEdge<Integer, Double> cur_edge = queue.remove();
            // If edge destination is known skip it
            if (known[cur_edge.destination]) continue;
            // Otherwise mark it known and add to mst
            known[cur_edge.destination] = true;
            mstAdjList.get(cur_edge.source).add(cur_edge);
            // Also add mirrored edge to destination index
            WeightedEdge<Integer, Double> mirror_edge = new WeightedEdge<Integer,Double>(cur_edge.destination, cur_edge.source, cur_edge.weight);
            mstAdjList.get(mirror_edge.source).add(mirror_edge);
            // Increment size so we can break the loop when done
            size++;
            // Add each of the destination's edges to the priority queue if they aren't known
            for (WeightedEdge<Integer, Double> edge : adjList.get(cur_edge.destination)) {
                if (known[edge.destination]) continue;
                queue.add(edge);
            }
        }
    }


    // After making the minimum spanning tree, use this method to
    // remove its k-1 heaviest edges, then assign integers
    // to clusters based on which nodes are still connected by
    // the remaining MST edges.
    private void makeKCluster(int k){
        // Create an array of each edge to be sorted. 
        // Purposely add both partner edges so we properly remove pairs
        ArrayList<WeightedEdge<Integer, Double>> edges = new ArrayList<>(2 * mstAdjList.size());
        for (List<WeightedEdge<Integer, Double>> node : mstAdjList) {
            for (WeightedEdge<Integer, Double> edge : node) {
                edges.add(edge);
            }
        }
        // Sort edges in descending order
        Collections.sort(edges, Collections.reverseOrder());

        // Remove the most expensive edge until there are no more edges to remove
        int edges_left = k - 1;
        for (int i = 0; edges_left != 0; i++) {
            WeightedEdge<Integer, Double> cur_edge = edges.get(i);
            // If cur_edge isn't in mst anymore than skip this because we already did it
            if (!mstAdjList.get(cur_edge.source).contains(cur_edge)) {
                continue;
            }
            // If this is the last edge set its weight as cost
            edges_left--;
            if (edges_left == 0) {
                cost = cur_edge.weight;
            }
            // Remove current edge from mstAdjList
            mstAdjList.get(cur_edge.source).remove(cur_edge);
            // Remove the matching edge
            for (WeightedEdge<Integer, Double> mirror_edge : mstAdjList.get(cur_edge.destination)) {
                if (mirror_edge.destination == cur_edge.source) {
                    mstAdjList.get(cur_edge.destination).remove(mirror_edge);
                    break;
                }
            }
        }

        // BFS The fractured mst to find clusters
        boolean[] known = new boolean[mstAdjList.size()];
        Arrays.fill(known, false);
        int cluster = 0;
        // Attempt a BFS on each node if it isn't known yet
        for (int i = 0; i < mstAdjList.size(); i++) {
            if (known[i]) continue;
            ClusterBreadthFirstSearch(i, known, cluster);
            cluster++;
        }
    }

    // Perform a breadth first search on the given node and add any unknown nodes to the given cluster
    private void ClusterBreadthFirstSearch(int node, boolean[] known, int cluster) {
        // Initialize queue for traversal
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(node);
        // Repeatedly add nodes to given cluster until we traverse the whole forest
        List<Integer> cur_cluster = clusters.get(cluster);
        while (!queue.isEmpty()) {
            int cur_node = queue.remove();
            if (known[cur_node]) continue;
            cur_cluster.add(cur_node);
            known[cur_node] = true;
            // Add each of cur_node's edge's destinations to queue if not known
            for (WeightedEdge<Integer, Double> edge : mstAdjList.get(cur_node)) {
                if (known[edge.destination]) continue;
                queue.add(edge.destination);
            }
        }
    }

    public List<List<Integer>> getClusters(){
        return clusters;
    }

    public double getCost(){
        return cost;
    }

}
