/**
 * $Id: Combinations.java, v1.0 30/08/14 01:24 PM oscarfabra Exp $
 * {@code Combinations} Is a class that finds and returns all possible
 * combinations of a Set of numbers given subsets size k.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 30/08/14
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Combinations finds and returns all possible subset combinations of a given
 * Set of numbers and subsets size k.
 */
public class Combinations
{
    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private Combinations() { }      // This class shouldn't be instantiated.

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Finds and returns all possible subset combinations of a given Set of
     * numbers and subsets size k.
     * @param subsetList List of numbers to find combinations for, subsets in
     *               {1,2,...,n}, n = subset.size()
     * @param k Size of permutations to find, 2 <= k <= subset.size()
     * @return List of subsets of the given list of integers.
     */
    public static List<Set<Integer>> solve(List<Integer> subsetList, int k)
    {
        // Returns result depending on base cases.
        List<Set<Integer>> allSubsets = new ArrayList<Set<Integer>> ();
        if (k == 0)
        {
            // Adds subset {1} assuming subset param received as {1,2,...,n}
            allSubsets.add(new TreeSet<Integer>());
            return allSubsets;
        }
        if(k > subsetList.size())
        {
            // Nothing to add. Base case needed in order to keep items ordered.
            return allSubsets;
        }

        // Creates a copy of the subset with last item removed.
        List<Integer> subsetWithoutX = new ArrayList<Integer>(subsetList);
        int size = subsetWithoutX.size();
        Integer x = subsetWithoutX.remove(size - 1);

        // Recursively adds subsets with last item and subsets without last item
        // included. Then adds x to each subset of subsetsWithX.
        List<Set<Integer>> subsetsWithoutX = solve(subsetWithoutX, k);
        List<Set<Integer>> subsetsWithX = solve(subsetWithoutX, k - 1);
        for(Set<Integer> auxSubset : subsetsWithX)
        {
            auxSubset.add(x);
        }

        // Adds subsets to the allSubsets list of sets and returns it
        allSubsets.addAll(subsetsWithoutX);
        allSubsets.addAll(subsetsWithX);
        return allSubsets;
    }
}
