/**
 * $Id: Graph.java, v 1.1 14/08/14 20:42 oscarfabra Exp $
 * {@code Graph} Represents a directed graph with n vertices and m edges.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.3
 * @since 14/08/14
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a directed graph with n vertices and m edges.
 */
public class Graph
{
    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // Number of vertices, n = V.size()
    private int n;

    // Number of edges, m = E.size()
    private int m;

    // Map of vertices with (vertexId, Vertex) pairs
    private Map<Integer,Vertex> V;

    // Map of edges with (edgeId, Edge) pairs
    private Map<Integer,Edge> E;

    // Each vertex points to the ids of its leaving edges
    private Map<Integer,List<Integer>> vertexEdgesLeaving;

    // Each vertex points to the ids of its arriving edges
    private Map<Integer,List<Integer>> vertexEdgesArriving;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    /**
     * Creates a new graph from the given map of adjacent edges of each vertex.
     * @param n Number of vertices of the graph.
     * @param vertexEdges Map of Lists with the adjacent edges of each vertex.
     */
    public Graph(int n, Map<Integer, List<Edge>> vertexEdges)
    {
        // Initializes the map of vertices, O(n) algorithm
        System.out.println("-- Creating new graph...");
        this.V = new HashMap<Integer, Vertex>(n);
        this.n = 0;
        System.out.print("-- Initializing list of vertices V...");
        for(int i = 0; i < n; i++)
        {
            this.putVertex(new Vertex(i + 1));
        }
        System.out.println("done.");

        // Initializes the list of edges, vertexEdgesArriving, and
        // vertexEdgesLeaving. O(m) algorithm.
        this.E = new HashMap<Integer, Edge>(this.n * 2);
        this.m = 0;
        this.vertexEdgesArriving = new HashMap<Integer, List<Integer>>(this.n);
        this.vertexEdgesLeaving = new HashMap<Integer, List<Integer>>(this.n);
        System.out.println("-- Initializing list of edges E...");
        int i = 1;
        for(Integer key : vertexEdges.keySet())
        {
            List<Edge> adjacentEdges = vertexEdges.get(key);
            for(Edge edge : adjacentEdges)
            {
                this.putEdge(edge);
            }
            // Prints a message in standard output for logging purposes
            if(i % 2000 == 0)
            {
                System.out.println("-- [" + i + " edges initialized so far.]");
                i++;
            }
        }
        System.out.println("...list of edges E initialized.");
        System.out.println("-- ...finished creating new graph.");
    }

    /**
     * Copy constructor.
     * @param that Graph to copy attributes from.
     */
    public Graph(Graph that)
    {
        copy(that);
    }

    //-------------------------------------------------------------------------
    // CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Extracts the corresponding adjacency lists to initialize a new Graph
     * from the given list of edges.
     * <b>Pre: </b>The input format must be a list of String, each in the form
     * "a b c" where a is the tail, b the head, and c the cost of each edge,
     * a, b in [1...n], c in Z (could be negative).
     * <br/>
     * @param m Number of edges to build.
     * @param edges List of String, each with head, tail and cost of an edge.
     * @return Map with the adjacent edges of each vertex.
     */
    public static Map<Integer, List<Edge>> buildVertexEdges(int m,
            List<String> edges)
    {
        // Walks through the given list of strings constructing the hashmap of
        // list of edges
        Map<Integer, List<Edge>> vertexEdges = new HashMap<Integer,
                List<Edge>>();
        int newEdgeId = 1;
        for(int i = 0; i < m; i++)
        {
            // Extracts and adds the corresponding pair (key, value of list) to
            // the hashmap. Each pair (key, list) in the hashmap is the id of a
            // vertex, and the list of adjacent edges adjacent to it.
            String line = edges.get(i);
            String[] values = line.split(" ");
            int key = Integer.parseInt(values[0]);
            int value = Integer.parseInt(values[1]);
            int cost = Integer.parseInt(values[2]);
            Edge edge = new Edge(newEdgeId++, key, value, cost);
            Graph.addVertexEdge(vertexEdges, key, edge);
        }
        // Returns the vertexEdges hashmap
        return vertexEdges;
    }

    /**
     * Adds a key and value to the given map. Values are edges. The edge is
     * added to the chained list of the given key.
     * @param vertexEdges HashMAp to add a new Edge value to.
     * @param key Key to assign the new edge to.
     * @param newEdge Edge object to add to the given
     */
    private static void addVertexEdge(Map<Integer, List<Edge>> vertexEdges,
                                      int key, Edge newEdge)
    {
        // Removes the list of the given key, adds the value, and puts it
        // again
        List<Edge> adjacentEdges = vertexEdges.remove(key);
        if(adjacentEdges == null)
        {
            adjacentEdges = new ArrayList<Edge>();
        }
        adjacentEdges.add(newEdge);
        vertexEdges.put(key, adjacentEdges);
    }

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Copies the attributes from the given graph to this graph.
     * @param that Graph to copy attributes from.
     */
    public void copy(Graph that)
    {
        // Copies the list of vertices
        System.out.println("-- Copying graph...");
        System.out.print("-- Initializing list of vertices V...");
        this.n = that.n;
        this.V = new HashMap<Integer, Vertex>(this.n);
        for(Integer key : that.V.keySet())
        {
            this.V.put(key, that.V.get(key));
        }
        System.out.println("done.");

        // Copies the list of edges
        System.out.print("-- Initializing list of edges E...");
        this.m = that.m;
        this.E = new HashMap<Integer, Edge>(this.m);
        for(Integer key : that.E.keySet())
        {
            this.E.put(key, that.E.get(key));
        }
        System.out.println("done.");

        // Copies the vertexEdgesLeaving HashMap
        System.out.print("-- Initializing adjacency lists...");
        this.vertexEdgesLeaving = new HashMap<Integer, List<Integer>>(this.n);
        for(Integer key : that.vertexEdgesLeaving.keySet())
        {
            this.vertexEdgesLeaving.put(key, that.vertexEdgesLeaving.get(key));
        }
        // Copies the vertexEdgesArriving HashMap
        this.vertexEdgesArriving = new HashMap<Integer, List<Integer>>(this.n);
        for(Integer key : that.vertexEdgesArriving.keySet())
        {
            this.vertexEdgesArriving.put(key, that.vertexEdgesArriving.get(key));
        }
        System.out.println("done.");
        System.out.println("-- ...finished copying graph.");
    }

    /**
     * Gets the number of vertices n of this graph.
     * @return The number of vertices n.
     */
    public int getN()
    {
        return this.n;
    }

    /**
     * Gets the number of edges m of this graph.
     * @return The number of edges m.
     */
    public int getM()
    {
        return this.m;
    }

    /**
     * Gets the ids of the vertices of the graph.
     * @return Set of integers that represent the ids of V.
     */
    public Set<Integer> getVertexKeys()
    {
        return this.V.keySet();
    }

    /**
     * Gets the vertex with the given id in V.
     * @param vertexId Id of the vertex to look for.
     * @return Vertex with the given id.
     */
    public Vertex getVertex(int vertexId)
    {
        if(!this.V.containsKey(vertexId))
        {
            return null;
        }
        return this.V.get(vertexId);
    }

    /**
     * Sets the vertex with the given id as explored or unexplored
     * depending on the boolean variable given.
     * @param vertexId Id of the vertex to look for.
     * @param explored If vertex needs to be set to explored or unexplored.
     */
    public void setVertexExploredValue(int vertexId, boolean explored)
    {
        Vertex vertex = this.V.remove(vertexId);
        vertex.setExplored(explored);
        this.V.put(vertexId, vertex);
    }

    /**
     * Returns a list of the edges that come out from the given vertex.
     * @param vertexId Id of the vertex to look for.
     * @return List of edges coming out from the given vertex.
     */
    public List<Edge> getEdgesLeaving(int vertexId)
    {
        // Checks whether the given vertex has edges coming out from it
        if(!this.vertexEdgesLeaving.containsKey(Integer.valueOf(vertexId)))
        {
            return null;
        }
        // Walks through the list of edges that come out from the given vertex
        List<Edge> edgesLeaving = new ArrayList<Edge>();
        for(Integer edgeId : this.vertexEdgesLeaving.get(vertexId))
        {
            edgesLeaving.add(this.E.get(edgeId));
        }
        return edgesLeaving;
    }

    /**
     * Returns a list of the edges that arrive at the given vertex.
     * @param vertexId Id of the vertex to look for.
     * @return List of edges coming out from the given vertex.
     */
    public List<Edge> getEdgesArriving(int vertexId)
    {
        // Checks whether the given vertex has edges arriving
        if(!this.vertexEdgesArriving.containsKey(Integer.valueOf(vertexId)))
        {
            return null;
        }
        // Walks through the list of edges that arrive at the given vertex
        List<Edge> edgesArriving = new ArrayList<Edge>();
        for(Integer edgeId : this.vertexEdgesArriving.get(vertexId))
        {
            edgesArriving.add(this.E.get(edgeId));
        }
        return edgesArriving;
    }

    /**
     * Gets the keys of the edges hashmap.
     * @return Set of keys of the edges of this graph.
     */
    public Set<Integer> getEdgeKeys()
    {
        return this.E.keySet();
    }

    /**
     * Gets the edge in E with the given key edgeId.
     * @param edgeId Key of the edge in the hashmap E.
     * @return Edge in E with the given key.
     */
    public Edge getEdge(Integer edgeId)
    {
        return this.E.get(edgeId);
    }

    /**
     * Adds a the given vertex to set V of this graph. Replaces any vertex
     * with the same id.
     * <b>Pre:</b> Set V has been initialized.
     * @param vertex Vertex to add to this graph.
     */
    public void putVertex(Vertex vertex)
    {
        this.V.put(vertex.getId(), vertex);
        this.n = this.V.size();
    }

    /**
     * Adds a new edge to the edges set E and to the vertexEdges hashmap.
     * Replaces any edge with the same id.
     * <b>Pre:</b> Collections E, vertexEdgesLeaving, and vertexEdgesArriving
     * have been initialized.
     * @param edge Adjacent edge to assign to both of its vertices.
     */
    public void putEdge(Edge edge)
    {
        // Adds the edge to the E set
        this.E.put(edge.getId(), edge);
        this.m = this.E.size();

        // Removes the list of the tail vertex, adds the value, and puts it
        // again in the corresponding hashmap
        int vertexId = edge.getTail();
        List<Integer> adjEdgesIds = this.vertexEdgesLeaving.remove(vertexId);
        if(adjEdgesIds == null)
        {
            adjEdgesIds = new ArrayList<Integer>();
        }
        adjEdgesIds.add(edge.getId());
        this.vertexEdgesLeaving.put(vertexId, adjEdgesIds);

        // Does it again for the head vertex, storing it in the corresponding
        // hashmap
        vertexId = edge.getHead();
        adjEdgesIds = vertexEdgesArriving.remove(vertexId);
        if(adjEdgesIds == null)
        {
            adjEdgesIds = new ArrayList<Integer>();
        }
        adjEdgesIds.add(edge.getId());
        vertexEdgesArriving.put(vertexId, adjEdgesIds);
    }
}
