/**
 * $Id: Median.java, v 1.0 22/06/14 17:43 oscarfabra Exp $
 * {@code Median} Class that implements the "Median Maintenance" algorithm
 * for any given array of integers (i.e. the running median from a stream of
 * integers).
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 22/06/14
 */

import java.util.*;

/**
 * Class that implements the "Median Maintenance" algorithm for any given
 * array of integers. That is, the kth median of an expanding stream of 
 * integers. The problem is defined as follows:
 * Letting x_i denote the ith number of the file, the kth median m_k is
 * defined as the median of the numbers x_1,...,x_k. If k is odd, then m_k
 * is ((k + 1)/2)th smallest number among x_1,...,x_k; if k is even, then
 * m_k is the (k/2)th smallest number among x_1,...,x_k. <br/>
 */
public class Median
{

    //-------------------------------------------------------------------------
    // CLASS VARIABLES
    //-------------------------------------------------------------------------

    // Heap in which to store the first 50% order statistics
    private static PriorityQueue<Integer> heapLow;

    // Heap in which to store the last 50% order statistics
    private static PriorityQueue<Integer> heapHigh;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private Median(){}      // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Solves the "Median Maintenance" problem for the given array of integers.
     * Letting x_i denote the ith number of the file, the kth median m_k is
     * defined as the median of the numbers x_1,...,x_k. If k is odd, then m_k
     * is ((k + 1)/2)th smallest number among x_1,...,x_k; if k is even, then
     * m_k is the (k/2)th smallest number among x_1,...,x_k. <br/>
     *
     * @param a Array to examine.
     * @param n Length of the array.
     * @return List with all the mentioned medians.
     */
    public static List<Integer> getAllMedians(int[] a, int n)
    {
        // Initializes the heapLow for extracting the maximum number
        Median.heapLow = new PriorityQueue<Integer>(n / 2,
                Collections.reverseOrder());

        // Initializes the heapHigh for extracting the minimum number
        Median.heapHigh = new PriorityQueue<Integer>(n / 2);

        // Walks through the given array of integers finding each of the
        // medians and adding them to the list to return
        List<Integer> medians = new ArrayList<Integer>(n);
        for(int i = 1; i <= n; i++)
        {
            // Gets next element to examine
            int element = a[i - 1];
            if(i == 1)
            {
                Median.heapLow.add(element);
            }
            else
            {
                // If next element is smaller than the biggest of heapLow, adds it
                // to heapLow, otherwise, adds it to heapHigh
                int maxLow = Median.heapLow.peek();
                if(element <= maxLow)
                {
                    Median.heapLow.add(element);
                }
                else
                {
                    Median.heapHigh.add(element);
                }
            }

            // Guarantees that there will always be at most 1 more element in
            // heapLow than in heapHigh
            if(Median.heapLow.size() > Median.heapHigh.size() + 1)
            {
                int aux = Median.heapLow.poll();
                Median.heapHigh.add(aux);
            }
            else if(Median.heapHigh.size() > Median.heapLow.size())
            {
                int aux = Median.heapHigh.poll();
                Median.heapLow.add(aux);
            }

            // Gets the ith median and adds it to the medians list
            int median = Median.heapLow.peek();
            medians.add(median);
        }

        // Returns the list of medians
        return medians;
    }
}
