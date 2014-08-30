/**
 * $Id: MST.java, v 1.0 03/07/14 20:38 oscarfabra Exp $
 * {@code MST} Class that implements a greedy algorithm for computing the
 * minimum spanning tree of a given undirected graph.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 03/07/14
 */

import java.util.*;

/**
 * Class that implements a greedy algorithm for computing the Minimum Spanning
 * Tree (MST) of a given undirected graph.
 */
public class MST
{
    //-------------------------------------------------------------------------
    // CLASS VARIABLES
    //-------------------------------------------------------------------------

    // List of vertices already added to the MST
    private static List<Integer> x = null;

    // Heap on which to store edge costs of the vertices not yet included in
    // the MST
    private static PriorityQueue<Integer> heap = null;

    // Maps scores in the heap with their corresponding vertex ids. Uses a
    // chained-list representation since 2+ vertices could have the same score.
    private static Map<Integer,List<Integer>> verticesHeapKeys = null;

    // Maps vertex ids with their respective scores in heap. In this case
    // there's no need for a chained list since there are no duplicate vertex
    // ids.
    private static Map<Integer, Integer> heapKeysVertices = null;

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    private MST(){ }        // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Finds the Minimum Spanning Tree (MST) of the given graph using Prim's
     * MST algorithm and returns its cost; O(n * m) algorithm. <br/>
     * <b>Pre: </b>The given graph is connected and has no cycles.
     * @param graph Graph to examine.
     * @param mst List of Integers in which to store the MST.
     * @return The overall cost of the MST found.
     */
    public static long solveByPrims(Graph graph, List<Integer> mst)
    {
        // Initializes the first vertex s, the number of vertices, and the
        // overall cost for finding the MST
        int s = 1;
        int n = graph.getN();
        long cost = 0;
        MST.x = new ArrayList<Integer>(n);
        MST.x.add(s);

        // Main loop for finding and storing the MST, O(n*m) algorithm
        System.out.println("Building Minimum Spanning Tree using Prim's " +
                "Algorithm...");
        while(MST.x.size() < n)
        {
            // Finds the cheapest edge and adds its cost to the overall cost
            Edge minEdge = MST.findCheapestEdge(graph);
            cost += minEdge.getCost();

            // Adds the appropriate vertex id
            if(!x.contains(Integer.valueOf(minEdge.getHead())))
            {
                x.add(minEdge.getHead());
            }
            else
            {
                x.add(minEdge.getTail());
            }
            // Message in standard output for debugging purposes
            if(x.size()%10 == 0)
            {
                System.out.println("-- [" + x.size() + " vertices covered " +
                        "so far.]");
            }
        }
        System.out.println("...MST built.");

        // Copies the list of vertices V into the given list mst
        for(Integer vertexId : x)
        {
            mst.add(vertexId);
        }

        return cost;
    }

    /**
     * Finds the Minimum Spanning Tree (MST) of the given graph using Prim's
     * MST algorithm using heaps; O(mlogn) algorithm. <br/>
     * <b>Pre: </b>The given graph is connected and has no cycles.
     * @param graph Graph to examine.
     * @param mst List of Integers in which to store the MST.
     * @return The overall cost of the MST found.
     */
    public static long solveByHeaps(Graph graph, List<Integer> mst)
    {
        // Initializes the first vertex s, the number of vertices, and the
        // overall cost for finding the MST
        int s = 1;
        int n = graph.getN();
        long cost = 0;
        MST.x = new ArrayList<Integer>(n);
        MST.x.add(s);

        // Initializes the heap with the appropriate scores for each vertex
        MST.heap = new PriorityQueue<Integer>(n - 1);
        MST.verticesHeapKeys = new HashMap<Integer, List<Integer>>(n - 1);
        MST.heapKeysVertices = new HashMap<Integer, Integer>(n - 1);
        for(int i = s + 1; i<= n; i++)
        {
            int score = MST.findVertexScore(i, graph);
            MST.addToHeap(score, i);
        }

        // Walks through the graph getting the MST as appropriate
        while(MST.x.size() < n)
        {
            // Extracts the minimum cost edge from the heap
            int [] v = MST.extractFromHeap();
            int vId = v[0];
            int vScore = v[1];
            cost += vScore;
            MST.x.add(vId);

            // Recomputes keys of the altered vertices (those adjacent to vId)
            for(Edge edge : graph.getAdjacentEdges(vId))
            {
                // Finds the associated vertex wId
                int wId;
                if(edge.getTail() == vId)
                {
                    wId = edge.getHead();
                }
                else
                {
                    wId = edge.getTail();
                }

                // Updates the corresponding values in the heap
                if(!MST.x.contains(Integer.valueOf(wId)))
                {
                    int wScore = MST.removeFromHeap(wId);
                    int newScore = Math.min(wScore,edge.getCost());
                    MST.addToHeap(newScore, wId);
                }
            }
        }
        // Copies the list of vertices V into the given list mst
        for(Integer vertexId : x)
        {
            mst.add(vertexId);
        }
        // Returns the cost of the MST
        return cost;
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Finds the cheapest edge of the given graph with a vertex in x, and the
     * other vertex outside of x.
     * @param graph Graph to examine.
     * @return Edge with the smallest cost that posses the required conditions.
     */
    private static Edge findCheapestEdge(Graph graph)
    {
        // Assumes smallest cost to be a very large number
        int minCost = 1000000;
        Edge minEdge = null;

        for(Edge crossingEdge : findCrossingEdges(graph))
        {
            if(crossingEdge.getCost() < minCost)
            {
                minEdge = crossingEdge;
                minCost = crossingEdge.getCost();
            }
        }
        // Returns the edge with the minimum cost
        return minEdge;
    }

    /**
     * Finds a returns the edges that have one vertex in x and the other
     * outside x.
     * @param graph Graph to examine.
     * @return List of edges that have one vertex in x and the other outside x.
     */
    private static List<Edge> findCrossingEdges(Graph graph)
    {
        // Determines which are the crossing edges
        List<Edge> crossingEdges = new ArrayList<Edge>();
        for(Integer uId : MST.x)
        {
            // Explores the adjacent edges of the vertices that are in x
            for(Edge edge : graph.getAdjacentEdges(uId))
            {
                Integer vertexA = edge.getTail();
                Integer vertexB = edge.getHead();
                Integer vertexToAdd = 0;
                if(MST.x.contains(vertexA) && !MST.x.contains(vertexB))
                {
                    vertexToAdd = vertexB;
                }
                else if(!MST.x.contains(vertexA) && MST.x.contains(vertexB))
                {
                    vertexToAdd = vertexA;
                }
                // If only one of the vertices is in x, then we know it's a
                // crossing edge
                if(vertexToAdd != 0)
                {
                    crossingEdges.add(edge);
                }
            }
        }
        return crossingEdges;
    }

    /**
     * Gets the heap key for the vertex with the given id as the cost of the
     * cheapest edge that contains the given vertex and other vertex in X.
     * <b>Pre: </b>The given vertex is outside X.
     * @param vertexId Id of the vertex to find the heap key for.
     * @param graph Graph to examine.
     * @return Cost of the cheapest edge that contains the given vertex and the
     * other vertex in X.
     */
    private static Integer findVertexScore(int vertexId, Graph graph)
    {
        // Assumes smallest cost to be a very large number
        int minCost = 1000000;

        // Walks through the list of vertices added to the MST looking for its
        // corresponding cheapest edge
        for(Integer uId : MST.x)
        {
            // Explores the edges that contain the given vertex and another
            // vertex in x
            for(Edge edge : graph.getAdjacentEdges(uId))
            {
                if((edge.getTail() == vertexId || edge.getHead() == vertexId)
                        && (edge.getCost() < minCost))
                {
                    minCost = edge.getCost();
                }
            }
        }
        // Returns the minimum cost of the adjacent edge with the given edge
        // and another vertex in X. If no such edge exists, minCost is a large
        // number, which represents infinite.
        return minCost;
    }

    /**
     * Adds the vertex with the given id to the heap, putting its respective
     * score in the heap and mapping its vertexId as appropriate.
     * @param score Key to add to the heap.
     * @param vertexId Id of the vertex to map with the given score.
     */
    private static void addToHeap(int score, int vertexId)
    {
        MST.heap.add(score);
        List<Integer> vertexIds = MST.verticesHeapKeys.remove(score);
        if(vertexIds == null)
        {
            vertexIds = new ArrayList<Integer>();
        }
        vertexIds.add(vertexId);
        MST.verticesHeapKeys.put(score,vertexIds);
        MST.heapKeysVertices.put(vertexId, score);
    }

    /**
     * Removes a value from the heap updating hashmaps as appropriate. If there
     * is more than one vertex with the same score, gets the first one from the
     * chained list.
     * @return The id and score of the removed vertex in positions 0 and 1 of
     * the returning array, respectively.
     */
    private static int [] extractFromHeap()
    {
        // Removes the lowest edge cost from the heap
        int vScore = MST.heap.poll();

        // Updates hashmaps accordingly
        List<Integer> vertexIds = MST.verticesHeapKeys.remove(vScore);
        int vId = vertexIds.remove(0);
        MST.verticesHeapKeys.put(vScore, vertexIds);
        MST.heapKeysVertices.remove(vId);

        // Creates and assigns the array to return
        int [] v = new int[2];
        v[0] = vId;
        v[1] = vScore;

        // Returns the array
        return v;
    }

    /**
     * Removes a value from the heap given its vertex id. Updates hashmaps as
     * appropriate.
     * @param vId Id of the vertex to remove from the heap.
     * @return Score related to the given vertex id.
     */
    private static int removeFromHeap(int vId)
    {
        // Gets the corresponding score of the given vertex and removes the
        // references to such vertex
        int vScore = MST.heapKeysVertices.remove(vId);
        List<Integer> vertexIds = MST.verticesHeapKeys.remove(vScore);
        vertexIds.remove(Integer.valueOf(vId));
        MST.verticesHeapKeys.put(vScore, vertexIds);

        // Finally the score of the given vertex from the heap
        MST.heap.remove(vScore);

        // Return the score of the given vertex
        return vScore;
    }
}
