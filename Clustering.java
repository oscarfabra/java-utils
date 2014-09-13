/**
 * $Id: Clustering.java, v 1.0 20/07/14 20:38 oscarfabra Exp $
 * {@code Clustering} is a class that implements a greedy algorithm for
 * computing the max-spacing k-clustering of a distance function represented
 * as a complete graph with edge costs.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 20/07/14
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Class that implements a greedy algorithm for computing the max-spacing
 * k-clustering of a distance function represented as a complete graph with
 * edge costs.
 */
public class Clustering
{
    //-------------------------------------------------------------------------
    // CLASS VARIABLES
    //-------------------------------------------------------------------------

    // Maps the vertices that comprise each cluster. Uses list-chaining to
    // assign the vertices of each cluster.
    private static Map<Integer,List<Integer>> clusterVertices;

    // Maps the cluster of each vertex. In this case there's no need for
    // chaining since each vertex can only belong to one cluster.
    private static Map<Integer,Integer> vertexCluster;

    // Stores the number of clusters in any given moment.
    // clusterNumber = clustersVertices.getKeys().size()
    private static int clustersNumber;

    // Stores the distance of the closest pair of separated points
    private static int closestPairSpacing;

    // Stores the index of the distances array that is being considered
    private static int currentDistanceIndex;

    // Stores those pair of nodes (edges) with distances between them less than
    // the given distance
    private static Map<Integer, Edge> pairs;

    // Heap to store the absolute difference of each pair of nodes whose
    // distance is less than the given distance
    private static PriorityQueue<Integer> heap;

    // Maps an edge's id with its key in the heap. Uses chaining given
    // that many edges may contain the same key (distance between points)
    private static Map<Integer,List<Integer>> heapKeyPairs;

    // Maps heap keys with their respective edge. In this case there's no need
    // for chaining since each key contains one single edge
    private static Map<Integer,Integer> pairHeapKey;

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    private Clustering(){ }         // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Finds the largest value of k such that there is a k-clustering with
     * spacing at least s taking into account the hamming distance between each
     * of the nodes.
     * @param nodes Matrix with the associated bits for each node.
     * @param spacing Minimum spacing to look for.
     * @return Largest value of k for a k-clustering with spacing at least s.
     */
    public static int findMaxClustering(int [][] nodes, int n, int bits,
                                        int spacing)
    {
        // Initializes data structures
        Clustering.clusterVertices = new HashMap<Integer, List<Integer>>(n);
        Clustering.vertexCluster = new HashMap<Integer, Integer>(n);
        Clustering.pairs = new HashMap<Integer, Edge>(n * 2);
        Clustering.heap = new PriorityQueue<Integer>(n * 2);
        Clustering.heapKeyPairs = new HashMap<Integer, List<Integer>>(n * 2);
        Clustering.pairHeapKey = new HashMap<Integer, Integer>(n * 2);

        // Builds and ASCII-characters encoding for faster
        int [] bitsSum = Clustering.findSumOfBits(nodes, n, bits);

        // Initializes points putting each of them on a separate cluster
        Clustering.initClusters(n);

        // Initializes pairs map and heap only with those pairs whose hamming
        // distance in between is strictly less than the given spacing
        Clustering.initPairsAndHeap(nodes, bitsSum, n, bits, spacing);

        // Finds the largest value of k such that there is a k-clustering with
        // spacing at least s
        System.out.println("Merging clusters and updating heap in order to" +
                " find k...");
        while(!Clustering.heap.isEmpty() && Clustering.clustersNumber > 1)
        {
            // Gets the closest pair of separated points from the heap
            Edge closestPair = Clustering.extractFromHeap();

            // Merges the clusters that contain p and q into a single one
            Integer p = Integer.valueOf(closestPair.getTail());
            Integer q = Integer.valueOf(closestPair.getHead());
            int clusterOfP = Clustering.vertexCluster.get(p);
            int clusterOfQ = Clustering.vertexCluster.get(q);
            int newCluster = Clustering.mergeClusters(clusterOfP, clusterOfQ);

            // Removes from heap pairs of points that belong to same cluster
            Clustering.updateHeap(newCluster);

            // Prints message in standard output for logging purposes
            if(Clustering.clustersNumber % 100 == 0)
            {
                System.out.println("-- [" + Clustering.clustersNumber +
                        " clusters left, " + Clustering.heap.size() +
                        " elements remaining in heap, so far]");
            }
        }
        System.out.println("...k found.");

        // Returns the largest value of k
        return Clustering.clustersNumber;
    }

    /**
     * Finds the maximum spacing of a k-clustering of the distance function
     * represented in the given graph.
     * @param graph Graph with the distance function to examine.
     * @param k Number of clusters desired.
     * @return Maximum spacing of a k-clustering of the given graph.
     */
    public static int findMaxSpacing(Graph graph, int k)
    {
        // Initializes data structures
        int m = graph.getM();
        int n = graph.getN();
        Clustering.clusterVertices = new HashMap<Integer, List<Integer>>(m);
        Clustering.vertexCluster = new HashMap<Integer, Integer>(n);
        int [] distances = new int[m];

        // Sorts the distances in order to identify the closest pair of points
        QuickEdges.sortEdges(graph, distances);

        // Initializes points putting each of them on a separate cluster
        Clustering.clustersNumber = 1;
        for(Integer vertexId: graph.getVertexKeys())
        {
            Clustering.addToCluster(vertexId, Clustering.clustersNumber++);
        }
        Clustering.clustersNumber--;

        // Repeats until there are only k clusters
        Clustering.currentDistanceIndex = 0;
        int [] pAndQ;
        while(Clustering.clustersNumber > k)
        {
            // Gets the closest pair of points, guaranteeing that they belong
            // to different clusters
            pAndQ = Clustering.updateClosestPairSpacing(graph, distances);

            // Merges the clusters that contain p and q into a single one
            Integer p = Integer.valueOf(pAndQ[0]);
            Integer q = Integer.valueOf(pAndQ[1]);
            int clusterOfP = Clustering.vertexCluster.get(p);
            int clusterOfQ = Clustering.vertexCluster.get(q);
            Clustering.mergeClusters(clusterOfP, clusterOfQ);
        }

        // Finds and updates the closest pair spacing
        Clustering.updateClosestPairSpacing(graph, distances);

        // Returns the distance between the closest pair of separated points
        return Clustering.closestPairSpacing;
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Stores the sum of bits of each node for faster search of hamming
     * distances.
     * @param nodes Matrix with the associated bits for each node.
     * @param n Number of nodes.
     * @param bits Number of bits per node.
     * @return Array with the sum of the bits of each node.
     */
    private static int[] findSumOfBits(int[][] nodes, int n, int bits)
    {
        System.out.println("Finding sum of bits for each node...");
        int [] bitsSum = new int[n];
        for(int i = 0; i < n; i++)
        {
            // Finds the sum of bits for node in position i
            int sum = 0;
            for(int j = 0; j < bits; j++)
            {
                sum += nodes[i][j];
            }
            bitsSum[i] = sum;
            // Message in standard output for logging purposes
            if((i + 1) % 5000 == 0)
            {
                System.out.println("-- " + (i + 1) + " sums so far.");
            }
        }
        System.out.println("...sums of bits found.");
        return bitsSum;
    }

    /**
     * Initializes the pairs map and the heap only with those pairs whose
     * distance in between is strictly less than the given spacing.
     * @param nodes Matrix with the associated bits for each node.
     * @param bitsSum Sum of the bits of each node.
     * @param n Number of nodes.
     * @param bits Number of bits per node.
     * @param spacing Minimum spacing to look for.
     */
    private static void initPairsAndHeap(int[][] nodes, int[] bitsSum,
                                         int n, int bits, int spacing)
    {
        System.out.println("Initializing pairs with hamming distance < given" +
                " spacing, and heap...");
        int newEdgeId = 1;
        for(int i = 0; i < n - 1; i++)
        {
            for(int j = i + 1; j < n; j++)
            {
                // If difference is > spacing, we know distance will be too
                int difference = Math.abs(bitsSum[i] - bitsSum[j]);
                if(difference < spacing)
                {
                    int distance = Clustering.closestPairHammingDistance(nodes,
                            i, j, bits, spacing);
                    if(distance < spacing)
                    {
                        // Creates new edge and adds it to the heap
                        Edge edge = new Edge(newEdgeId, i + 1, j + 1, distance);
                        Clustering.pairs.put(newEdgeId, edge);
                        Clustering.addToHeap(distance, newEdgeId++);

                        // Message in standard output for logging purposes
                        if(newEdgeId % 200 == 0)
                        {
                            System.out.println("-- " + newEdgeId + " pairs " +
                                    "w/ hamming distance < spacing, so far.");
                        }
                    }
                }
            }
        }
        System.out.println("...pairs and heap initialized.");
    }

    /**
     * Initializes points putting each of them on a separate cluster.
     * @param n Number of nodes.
     */
    private static void initClusters(int n)
    {
        System.out.println("Initializing clusters...");
        Clustering.clustersNumber = 1;
        for(int i = 0; i < n; i++)
        {
            Clustering.addToCluster(i+1, Clustering.clustersNumber++);
            // Message in standard output for logging purposes
            if((i + 1) % 5000 == 0)
            {
                System.out.println("-- " + (i + 1) + " clusters so far.");
            }
        }
        Clustering.clustersNumber--;
        System.out.println("...clusters initialized.");
    }

    /**
     * Updates the heap removing from it the edges whose both points belong to
     * the same new cluster.
     * <b>Pre:</b> Given cluster has just been expanded with new points.
     * @param newCluster Id of the new (big) cluster that was just merged.
     */
    private static void updateHeap(int newCluster)
    {
        // Initializes auxiliary data structures
        int m = Clustering.pairs.size();
        int initialCap = (m > 1)? (m / 2) : 1;
        Map<Integer, Edge> auxPairs = new HashMap<Integer, Edge>(initialCap);
        PriorityQueue<Integer> auxHeap = new PriorityQueue<Integer>(initialCap);
        Map<Integer, List<Integer>> auxHeapKeyPairs =
                new HashMap<Integer, List<Integer>>(initialCap);
        Map<Integer, Integer> auxPairHeapKey =
                new HashMap<Integer, Integer>(initialCap);

        // Given that pairs collection contains the edges whose ids are in the
        // heap, we can walk through it to add only the corresponding pairs
        for(Integer edgeId : Clustering.pairs.keySet())
        {
            Edge edge = Clustering.pairs.get(edgeId);
            Integer p = edge.getTail();
            Integer q = edge.getHead();
            if(!(Clustering.vertexCluster.get(p) == newCluster &&
                    Clustering.vertexCluster.get(q) == newCluster))
            {
                // Adds value to auxPairs
                auxPairs.put(edgeId, edge);

                // Adds value to auxHeap updating the corresponding hashmaps
                int auxDistance = edge.getCost();
                auxHeap.add(auxDistance);
                List<Integer> auxEdgeIds = auxHeapKeyPairs.remove(auxDistance);
                if(auxEdgeIds == null)
                {
                    auxEdgeIds = new ArrayList<Integer>();
                }
                auxEdgeIds.add(edgeId);
                auxHeapKeyPairs.put(auxDistance, auxEdgeIds);
                auxPairHeapKey.put(edgeId, auxDistance);
            }
        }
        // Updates data structures' references
        Clustering.pairs = auxPairs;
        Clustering.heap = auxHeap;
        Clustering.heapKeyPairs = auxHeapKeyPairs;
        Clustering.pairHeapKey = auxPairHeapKey;
    }


    /**
     * Determines whether nodes at positions i and j of the given array differ
     * by at most the given spacing bits or not. Returns hamming distance if
     * conditions met.
     * @param nodes Matrix with the associated bits for each node.
     * @param i Position of the first node in the nodes array to compare.
     * @param j Position of the second node in the nodes array to compare.
     * @param bits Number of bits per node.
     * @param spacing Maximum number of differing bits accepted.
     * @return Hamming distance if <= given distance, any bigger distance
     * otherwise.
     */
    private static int closestPairHammingDistance(int[][] nodes, int i, int j,
                                                  int bits, int spacing)
    {
        // Walks through the bits of each node comparing them
        int distance = 0;
        for(int bit = 0; bit < bits; bit++)
        {
            if(nodes[i][bit] != nodes[j][bit])
            {
                if (++distance > spacing)
                {   // Don't need to calculate the rest
                    return distance;
                }
            }
        }
        // Returns the real distance
        return distance;
    }

    /**
     * Finds and updates the distance between the two vertices that belong to
     * different clusters and are the closest to each other.
     * @param graph Graph with the distance function to examine.
     * @param distances Array of int with the ids of the edges or pairs.
     * @return Array of size 2 with p and q in positions 0 and 1 respectively.
     */
    private static int [] updateClosestPairSpacing(Graph graph, int[] distances)
    {
        // Gets the closest pair of points guaranteeing that they belong to
        // different clusters
        int closestPair = distances[Clustering.currentDistanceIndex++];
        Edge edge = graph.getEdge(Integer.valueOf(closestPair));
        Integer p = Integer.valueOf(edge.getTail());
        Integer q = Integer.valueOf(edge.getHead());
        while(Clustering.vertexCluster.get(p)
                == Clustering.vertexCluster.get(q))
        {
            closestPair = distances[Clustering.currentDistanceIndex++];
            edge = graph.getEdge(Integer.valueOf(closestPair));
            p = edge.getTail();
            q = edge.getHead();
        }
        Clustering.closestPairSpacing = edge.getCost();
        // Stores p and q in an array of integers and returns it
        int [] pAndQ = new int[2];
        pAndQ[0] = p;
        pAndQ[1] = q;
        return pAndQ;
    }

    /**
     * Merges the clusters with the given ids into a single one. <br/>
     * <b>Pre:</b> clusterOfP != clusterOfQ.
     * @param clusterOfP Id of the first cluster to merge.
     * @param clusterOfQ Id of the second cluster to merge.
     * @return Id of the new (big) cluster (could be clusterOfP or clusterOfQ).
     */
    private static int mergeClusters(int clusterOfP, int clusterOfQ)
    {
        // Moves vertices from the smaller cluster to the bigger one
        int sizeOfClusterOfP
                = Clustering.clusterVertices.get(Integer.valueOf(clusterOfP))
                .size();
        int sizeOfClusterOfQ
                = Clustering.clusterVertices.get(Integer.valueOf(clusterOfQ))
                .size();
        List<Integer> verticesIds = null;
        int originCluster = 0;
        int destinationCluster = 0;
        if(sizeOfClusterOfP >= sizeOfClusterOfQ)
        {
            originCluster = clusterOfQ;
            destinationCluster = clusterOfP;
        }
        else
        {
            originCluster = clusterOfP;
            destinationCluster = clusterOfQ;
        }

        // Removes each vertex from originCluster and adds it to
        // destinationCluster
        verticesIds =
                Clustering.clusterVertices.get(Integer.valueOf(originCluster));
        List<Integer> verticesAux = new Vector<Integer>(verticesIds);
        for(Integer vertexId : verticesAux)
        {
            // Removes vertex from one cluster and adds it to the other
            Clustering.removeFromCluster(originCluster, vertexId);
            Clustering.addToCluster(destinationCluster, vertexId);
        }

        // Updates clustersNumber
        Clustering.clustersNumber = Clustering.clusterVertices.size();

        // Returns the id of the new (big) cluster
        return destinationCluster;
    }

    /**
     * Adds the edge with the given id to the heap, putting its respective
     * distance in the heap and mapping its edgeId as appropriate.
     * @param distance Key to add to the heap.
     * @param edgeId Id of the edge to map with the given distance.
     */
    private static void addToHeap(int distance, int edgeId)
    {
        Clustering.heap.add(distance);
        List<Integer> edgeIds = Clustering.heapKeyPairs.remove(distance);
        if(edgeIds == null)
        {
            edgeIds = new ArrayList<Integer>();
        }
        edgeIds.add(edgeId);
        Clustering.heapKeyPairs.put(distance, edgeIds);
        Clustering.pairHeapKey.put(edgeId, distance);
    }

    /**
     * Extracts a value from the heap updating collections as appropriate. If
     * there is more than one edge with the same score (distance), gets the
     * first one from the chained list.
     * @return The edge that has been removed from the heap.
     */
    private static Edge extractFromHeap()
    {
        // Removes the smallest distance from the heap
        int distance = Clustering.heap.poll();

        // Updates hashmaps accordingly
        List<Integer> edgeIds = Clustering.heapKeyPairs.remove(distance);
        int edgeId = edgeIds.remove(0);
        Clustering.heapKeyPairs.put(distance, edgeIds);
        Clustering.pairHeapKey.remove(edgeId);

        // Removes the edge from its set and returns it
        Edge edge = Clustering.pairs.remove(Integer.valueOf(edgeId));
        return edge;
    }

    /**
     * Adds the given vertex to the given cluster. Updates maps as appropriate.
     * @param clusterId Id of the cluster to which the vertex will be assigned.
     * @param vertexId Id of the vertex to assign to the given cluster.
     */
    private static void addToCluster(int clusterId, Integer vertexId)
    {
        List<Integer> verticesIds =
                Clustering.clusterVertices.remove(Integer.valueOf(clusterId));
        if(verticesIds == null)
        {
            verticesIds = new Vector<Integer>();
        }
        verticesIds.add(vertexId);
        Clustering.clusterVertices.put(clusterId, verticesIds);
        Clustering.vertexCluster.put(vertexId, clusterId);
    }

    /**
     * Removes the given vertex from the given cluster. Updates maps as
     * appropriate. <br/>
     * <b>Pre:</b> The given vertex is found in the given cluster.
     * @param clusterId Id of the cluster from which the vertex will be removed.
     * @param vertexId Id of the vertex to be removed.
     */
    private static void removeFromCluster(int clusterId, Integer vertexId)
    {
        List<Integer> verticesIds =
                Clustering.clusterVertices.remove(Integer.valueOf(clusterId));
        verticesIds.remove(vertexId);
        if(verticesIds.size() > 0)
        {
            Clustering.clusterVertices.put(clusterId, verticesIds);
        }
        Clustering.vertexCluster.remove(vertexId);
    }
}
