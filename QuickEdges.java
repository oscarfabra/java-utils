/**
 * $Id: QuickSort.java, v 1.0 20/05/14 21:57 oscarfabra Exp $
 * {@code QuickSort} Class that sorts the edges of a given graph in increasing
 * order of their respective costs.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.1
 * @since 20/05/14
 */

/**
 * Class that sorts the edges of a given graph in increasing order of their
 * respective costs.
 */
public class QuickEdges
{
    //-------------------------------------------------------------------------
    // CLASS VARIABLES
    //-------------------------------------------------------------------------

    // Array of integers in which to store the edges of the graph
    private static int [] edges;

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    // This class should not be instantiated
    private QuickEdges(){}

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Sorts the edges of the given graph according to their respective costs
     * and stores their ids in the given edges array.
     * @param graph Graph to examine.
     * @param edges Array of integers on which to store the ids of edges in
     *              increasing order of their costs.
     */
    public static void sortEdges(Graph graph, int[] edges)
    {
        // Stores the ids of each edge in the edges class array, and their
        // costs in the costs array
        int m = graph.getM();
        int [] costs = new int[m];
        QuickEdges.edges = new int[m];
        int i = 0;
        for(Integer edgeId : graph.getEdgeKeys())
        {
            QuickEdges.edges[i] = edgeId;
            costs[i++] = graph.getEdge(edgeId).getCost();
        }

        // Sorts the costs array AND the edges class array
        QuickEdges.sort(costs);

        // Updates the given edges array
        for(i = 0; i < m; i++)
        {
            edges[i] = QuickEdges.edges[i];
        }
    }

    /**
     * Sorts the given array using quicksort pivoting over the first element.
     * @param a Array of int numbers.
     */
    public static void sort(int [] a)
    {
        sort(a, 0, a.length - 1);
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Sorts array a in [lb...ub] using the first element as pivot.
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     */
    private static void sort(int[] a, int lb, int ub)
    {
        if(lb >= ub){ return; }
        int p = partitionFirst(a, lb, ub);
        sort(a, lb, p - 1);
        sort(a, p + 1, ub);
    }

    /**
     * Partitions the array a in [index...ub] using the first element as pivot.
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @return Index of the pivot element.
     */
    private static int partitionFirst(int[] a, int lb, int ub)
    {
        int p = a[lb];
        int i = lb + 1;
        for(int j = lb + 1; j <= ub; j++)
        {
            if(a[j] < p)
            {
                swap(a, j, i);
                i++;
            }
        }
        swap(a, lb, i -1);
        return i - 1;
    }

    /**
     * Swaps the elements in array a at indices j and i. <br/>
     * @param a Array of int numbers.
     * @param j Index of element to swap.
     * @param i Index of element to swap.
     */
    private static void swap(int[] a, int j, int i)
    {
        int aux = a[j];
        a[j] = a[i];
        a[i] = aux;

        // Swaps the respective edge ids
        aux = QuickEdges.edges[j];
        QuickEdges.edges[j] = QuickEdges.edges[i];
        QuickEdges.edges[i] = aux;
    }
}
