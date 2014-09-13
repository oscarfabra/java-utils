/**
 * $Id: OptimalBST.java, v1.0 6/09/14 10:00 PM oscarfabra Exp $
 * {@code OptimalBST} Is a class that computes the value of an Optimal Search
 * Tree given a set of frequencies for each item.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 6/09/14
 */

/**
 * OptimalBST is a class that solves the Optimal Binary Search Tree Problem
 * defined as follows:
 * Input: Frequencies p_1,p_2,...,p_n for items 1,2,...,n.
 * Output: A valid search tree that minimizes the weighted (average) search
 * time.
 */
public class OptimalBST
{
    //-------------------------------------------------------------------------
    // CONSTANT
    //-------------------------------------------------------------------------

    // Represents the largest value possible
    public static final double INFINITY = 1000000;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private OptimalBST(){ }     // This class shouldn't be instantiated.

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Computes and returns the value of the optimal binary search tree that
     * minimizes the weighted (average) search time. Assumes items in sorted
     * order, 1 < 2 < 3 ... < n.
     * @param p Array of frequencies for each item in {1,2,...,n}.
     * @param n Number of items to add to the tree.
     * @return Total value of a search tree that minimizes average search time.
     */
    public static double solve(double [] p, int n)
    {
        // Initializes main table
        double [][] a = new double[n][n];

        // Walks through each possible optimal BST for items in {i,...,j}
        for(int s = 0; s < n; s++)
        {
            for(int i = 0; (i + s) < n; i++)
            {
                double min = OptimalBST.INFINITY;
                double firstTerm = 0;
                double secondTerm = 0;
                double thirdTerm = 0;

                // Finds the first term of the recursion
                for(int k = i; k <= (i + s) && (i + s) < n; k++)
                {
                    firstTerm += p[k];
                }

                // Walks through the previous diagonal of a to compute the
                // value for a[i][i + s]
                for(int r = i; r <= (i + s) && (i + s) < n; r++)
                {
                    // Finds the second and third terms of the recursion
                    secondTerm = (i > (r - 1)) ? 0 : a[i][r - 1];
                    thirdTerm = ((r + 1) > (i + s)) ? 0 : a[r + 1][i + s];

                    if((firstTerm + secondTerm + thirdTerm) < min)
                    {
                        min = (firstTerm + secondTerm + thirdTerm);
                    }
                }

                // Sets the appropriate value
                a[i][i + s] = min;
            }
        }

        // Returns the weight of an optimal binary search tree
        return a[0][n - 1];
    }
}
