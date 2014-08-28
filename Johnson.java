/**
 * $Id: Johnson.java, v1.0 16/08/14 05:40 PM oscarfabra Exp $
 * {@code Johnson} Is a class that computes the length of all shortest paths
 * between any two pair of vertices of a given graph.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 16/08/14
 */

/**
 * Johnson is a class that solves the All-Pairs Shortest Paths (APSP) Problem
 * implementing Johnson's algorithm. The APSP Problem is defined as follows:
 * Input: Directed graph G=(V,E) with edge costs c_e for each e in E.
 * Output: Either,
 * (A) Compute the length of a shortest u->v path for all pairs of
 * vertices u, v in V,
 * OR,
 * (B) Correctly report that G contains a negative cycle.
 */
public class Johnson
{
    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private Johnson() { }   // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Returns a 2-D array of integers with the length of the shortest paths
     * between all pairs of vertices using Johnson's algorithm.
     * @param graph Graph to examine.
     * @return 2-D array with the length of the shortest paths between all
     * pairs of vertices.
     */
    public static int[][] solve(Graph graph)
    {
        // Forms gPrime graph by adding a new vertex s and a new edge (s, v)
        // with length 0 for each v in set V of graph.
        Graph gPrime = new Graph(graph);
        System.out.print("-- Forming gPrime by adding new vertex s and edges...");
        int sId = gPrime.getN() + 1;
        Vertex s = new Vertex(sId);
        int newEdgeId = gPrime.getM() + 1;
        gPrime.putVertex(s);
        for(Integer headId : graph.getVertexKeys())
        {
            Edge edge = new Edge(newEdgeId++, sId, headId, 0);
            gPrime.putEdge(edge);
        }
        System.out.println("done.");

        // Runs B-F alg on GPrime with source vertex s, returns null if a
        // negative-cost cycle found
        System.out.println("-- Running Bellman-Ford Algorithm...");
        int [] paths = BellmanFord.solve(sId, gPrime);
        System.out.println("-- ...finished running Bellman-Ford Algorithm.");
        if(paths == null)
        {
            return null;
        }

        // Defines weights for each vertex v in graph as the shortest s->v path
        int n = graph.getN();
        int [] weights = new int[n];
        for(int i = 0; i < n; i++)
        {
            weights[i] = paths[i];  // No need to verify s since sId = n + 1
        }

        // Sets the costs for each edge in gPrime
        System.out.print("-- Setting edge costs for gPrime...");
        for(Integer edgeId : graph.getEdgeKeys())
        {
            Edge e = graph.getEdge(edgeId);
            int cPrime = e.getCost() + weights[e.getTail() - 1]
                    - weights[e.getHead() - 1];
            Edge ePrime = new Edge(edgeId, e.getTail(), e.getHead(), cPrime);
            gPrime.putEdge(ePrime); // Replaces any previous edge with same id
        }
        System.out.println("done.");

        // Walks through each vertex in gPrime computing shortest-paths and
        // storing them in the allPairs 2-D array
        System.out.println("-- Computing shortest paths from each vertex in " +
                "gPrime...");
        int [][] allPairs = new int[n][n];
        for(Integer vId : graph.getVertexKeys())
        {
            paths = Dijkstra.solve(vId, gPrime);
            for(int i = 0; i < n; i++)
            {
                allPairs[vId - 1][i] = paths[i];
            }
            // Prints a message in standard output for logging purposes
            if(vId % 50 == 0)
            {
                System.out.println("-- [" + vId + " vertices solved so far.]");
            }
        }
        System.out.println("-- ...finished computing shortest paths.");

        // Updates allPairs array to its appropriate values and returns it
        System.out.print("-- Getting actual shortest paths' lengths...");
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(allPairs[i][j] != BellmanFord.INFINITY)
                {
                    allPairs[i][j] = allPairs[i][j] - weights[i] + weights[j];
                }
            }
        }
        System.out.println("done.");
        return allPairs;
    }
}
