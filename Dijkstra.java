/**
 * $Id: Dijkstra.java, v 1.0 24/06/14 21:50 oscarfabra Exp $
 * {@code Dijkstra} Is an implementation of Dijkstra's shortest path
 * algorithm for directed graphs with no negative edges. <br/>
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 24/06/14
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Class that implements Dijkstra's shortest path algorithm using a heap for
 * faster performance.
 */
public class Dijkstra
{
    //-------------------------------------------------------------------------
    // CONSTANT
    //-------------------------------------------------------------------------

    // Value to assign to an theoretical infinite value
    private static final int INFINITY = 1000000;

    //-------------------------------------------------------------------------
    // CLASS ATTRIBUTES
    //-------------------------------------------------------------------------

    // List of vertices processed so far
    private static List<Integer> x;

    // List of computed shortest path distances from s to any other vertex of
    // the given graph
    private static int [] a;

    // Heap on which to store vertices not yet processed during execution
    private static PriorityQueue<Integer> heap;

    // Maps vertices with their corresponding heap keys, uses chaining in case
    // there are multiple vertices with the same heap key (greedy score)
    private static Map<Integer,List<Integer>> vertexHeapKey;

    // Maps heap keys with their corresponding vertex ids. In this case there's
    // no need for chaining since all vertices have different ids
    private static Map<Integer,Integer> heapKeyVertex;

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    private Dijkstra(){}    // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Solves Dijkstra's shortest path algorithm starting on vertex with id s
     * in the given graph object, s in [1...n], where n is the number of
     * vertices in graph.
     <b>Pre:</b> Given directed graph has no negative edges.
     * @param s Id of the starting vertex in graph.
     * @param graph Graph to search the shortest path for.
     * @return List of shortest distances from s to each of the other vertices.
     */
    public static int [] solve(int s, Graph graph)
    {
        // Initializes the x list on which to store the ids of the vertices
        // processed so far, the path list a on which to store shortest path
        // distances from s to each other vertex, the heap on which to store
        // vertices not yet explored, and the vertexHeapKey and heapKeyVertex
        // arrays on which to store the key of each vertex in the heap
        int n = graph.getN();
        Dijkstra.x = new ArrayList<Integer>(n);
        Dijkstra.a = new int [n];
        Dijkstra.heap = new PriorityQueue<Integer>(n);
        Dijkstra.vertexHeapKey = new HashMap<Integer, List<Integer>>(n);
        Dijkstra.heapKeyVertex = new HashMap<Integer, Integer>(n);

        // Assumes infinite distances between s and all other vertices
        for(int i = 0; i < n; i++)
        {
            Dijkstra.a[i] = Dijkstra.INFINITY;
        }

        // Mark vertex s as processed and the distance to itself as 0
        Dijkstra.x.add(s);
        Dijkstra.a[s - 1] = 0;

        // Walks through each vertex, calculates its key and adds it to the
        // vertexHeapKey and heapKeyVertex HashMaps
        for(int i = 1; i <= n; i++)
        {
            if(i != s)
            {
                int vertexScore = Dijkstra.minGreedyScore(i, graph);
                Dijkstra.a[i - 1] = vertexScore;
                Dijkstra.addToHeap(i, vertexScore);
            }
        }

        // Walks through each vertex in the graph assigning the shortest path
        // from s to such vertex
        while(Dijkstra.x.size() < n)
        {
            // Extracts the key for the minimum path vertex not yet explored,
            // removing it from the corresponding HashMaps
            int [] w = Dijkstra.extractFromHeap();
            int wId = w[0];
            int wScore = w[1];
            Dijkstra.a[wId - 1] = wScore;
            Dijkstra.x.add(wId);

            // Updates keys of the implicated edges (those with tails in X, and
            // heads in V - X)
            List<Edge> edgesLeaving = graph.getEdgesLeaving(wId);
            if(edgesLeaving != null)
            {
                for(Edge edge : edgesLeaving)
                {
                    int vId = edge.getHead();
                    if(!Dijkstra.x.contains(vId))
                    {
                        // If vId is not in X, then it is in the heap
                        int vKey = Dijkstra.heapKeyVertex.get(vId);

                        // Removes head vertex with id vId from the heap, updates
                        // HashMaps accordingly
                        Dijkstra.deleteFromHeap(vKey, vId);

                        // Recomputes the smallest greedy score for this vertex
                        int vScore = Math.min(vKey, a[wId - 1] + edge.getCost());

                        // Re-inserts vertex vId into the heap, updates HashMaps
                        // accordingly
                        Dijkstra.addToHeap(vId, vScore);
                    }
                }
            }
        }

        // Returns the computed array with shortest paths
        return a;
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Computes the smallest greedy score of vertex with id vertexId in the
     * given graph. <br/>
     * @param vertexId Id of the not-yet-explored vertex.
     * @param graph Graph to examine.
     * @return Smallest greedy score of the given vertex.
     */
    private static int minGreedyScore(int vertexId, Graph graph)
    {
        // Obtains the edges that point to the vertex with the given id
        List<Edge> edgesArriving = graph.getEdgesArriving(vertexId);
        // If the vertex doesn't have incoming edges, then score is infinite
        if(edgesArriving == null)
        {
            return Dijkstra.INFINITY;
        }
        // Finds the smallest greedy score of the given vertex
        int min = Dijkstra.INFINITY;
        for(Edge edge : edgesArriving)
        {
            min = Math.min(min,
                    Dijkstra.a[edge.getTail() - 1] + edge.getCost());
        }
        // Returns the smallest greedy score of the given vertex
        return min;
    }

    /**
     * Adds a new value to the heap, updating the hashmaps accordingly.
     * @param vertexId Id of vertex whose reference will be added to the heap.
     * @param vertexScore Score to add to heap representing the given vertex.
     */
    private static void addToHeap(int vertexId, int vertexScore)
    {
        Dijkstra.heap.add(vertexScore);
        List<Integer> vertexIds = Dijkstra.vertexHeapKey.get(vertexScore);
        if(vertexIds == null)
        {
            vertexIds = new ArrayList<Integer>();
        }
        vertexIds.add(vertexId);
        Dijkstra.vertexHeapKey.put(vertexScore, vertexIds);
        Dijkstra.heapKeyVertex.put(vertexId, vertexScore);
    }

    /**
     * Extracts the minimum score from heap, gets the corresponding vertex id
     * and returns it. If there is more than one vertex with the same score,
     * returns the first one in the chained list. Updates hashmaps accordingly.
     * @return An array with vertex id and vertex score that corresponds with
     * the min-score in the heap.
     */
    private static int [] extractFromHeap()
    {
        int [] w = new int[2];
        int wKey = Dijkstra.heap.poll();
        List<Integer> vertexIds = Dijkstra.vertexHeapKey.get(wKey);
        w[0] = vertexIds.remove(0);
        if(vertexIds.size() > 0)
        {
            Dijkstra.vertexHeapKey.put(wKey, vertexIds);
        }
        w[1] = Dijkstra.heapKeyVertex.remove(w[0]);
        return w;
    }

    /**
     * Removes the vertex with the specified vKey and vId from the heap,
     * updating hashmaps accordingly.
     * @param vKey Score in the heap for a vertex to remove.
     * @param vId Id of the vertex to remove from the heap.
     */
    private static void deleteFromHeap(int vKey, int vId)
    {
        Dijkstra.heap.remove(vKey);
        List<Integer> vertexIds = Dijkstra.vertexHeapKey.remove(vKey);
        vertexIds.remove(Integer.valueOf(vId));
        if(vertexIds.size() > 0)
        {
            Dijkstra.vertexHeapKey.put(vKey, vertexIds);
        }
        Dijkstra.heapKeyVertex.remove(vId);
    }
}
