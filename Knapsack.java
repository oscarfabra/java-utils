/**
 * $Id: Knapsack.java, v1.0 9/08/14 05:38 PM oscarfabra Exp $
 * {@code Knapsack} Solves an instance of the popular knapsack problem.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 9/08/14
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Knapsack is a class that solves the knapsack problem defined as follows:
 * Input: n items. Each has attributes: value v_i (non-negative), size w_i
 * (non-negative and integral), capacity W (a non-negative integer).
 * Output: A subset S of {1, 2, ..., n} that:
 * Maximizes: Sum of v_i's, for all i in S
 * Subject to: Sum of w_i's are upper bounded by W, for all i in S.
 */
public class Knapsack
{
    //-------------------------------------------------------------------------
    // CONSTANT
    //-------------------------------------------------------------------------

    // Constant to determine whether to solve the problem by a straightforward
    // manner or optimized for big numbers
    public static final int THRESHOLD = 100000;

    // Sets the biggest weight possible for the knapsack
    public static final int MAX_WEIGHT = 1000000;

    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // Stores the selected items for later retrieval
    private static List<Item> selectedItems = null;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private Knapsack() {}       // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Builds and returns a list of strings with the value and weight of each
     * item and number of items n.
     * <b>Pre:</b> Each line of the given list of string has the form:
     * [value_i] [weight_i] for each i in {1...n}
     * @param lines List of string with the attributes for each item.
     * @param n Number of items.
     * @return List of items for a particular knapsack problem.
     */
    public static List<Item> buildItems(List<String> lines, int n)
    {
        List<Item> items = new ArrayList<Item>(n);
        int newItemId = 1;
        for(String line : lines)
        {
            String [] valueAndWeight = line.split(" ");
            int value = Integer.parseInt(valueAndWeight[0]);
            int weight = Integer.parseInt(valueAndWeight[1]);
            Item item = new Item(newItemId++, value, weight);
            items.add(item);
        }
        return items;
    }

    /**
     * Solves the given instance of the knapsack problem.
     * @param items List of items.
     * @param W Knapsack size.
     * @param n Number of items.
     * @param e Error margin acceptable for solution, e in [0...1.0)
     * @return Value of the optimal solution.
     */
    public static int solve(List<Item> items, int W, int n, float e)
    {
        // If no error margin is allowed, use the (trivial) dynamic programming
        // approach
        int value = 0;
        if(e == 0.0)
        {
            // Determines whether to solve the problem using a straightforward
            // implementation or one that's optimized for big numbers
            long compare = Long.valueOf(W + 1) * Long.valueOf(n + 1);
            if(Knapsack.THRESHOLD >= compare)
            {
                value = Knapsack.solveStraightforward(items, W, n);
            }
            else
            {
                value = Knapsack.solveForBigData(items, W, n);
            }
        }
        else
        {
            // Determines whether to use a greedy heuristic (fastest of all) or
            // a dynamic programming heuristic based on the given e.

            // Finds w_max, i.e., the weight of the heaviest item
            int i = 0;
            int maxWeight = 0;
            for(Item item : items)
            {
                if(item.getWeight() > maxWeight)
                {
                    maxWeight = item.getWeight();
                }
            }
            // If heaviest item is at most e * W, use a greedy heuristic
            if(maxWeight <= e * W)
            {
                value = Knapsack.solveGreedyHeuristic(items, W, n);
            }
            else    // Otherwise, use the dynamic programming heuristic
            {
                value = Knapsack.solveDPHeuristic(items, W, n, e);
            }
        }

        // Returns the value
        return value;
    }

    /**
     * Returns the list of the selected items; null if empty.
     * @return List of selected items.
     */
    public static List<Item> getSelectedItems()
    {
        return Knapsack.selectedItems;
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Solves the knapsack problem using a straightforward (exact) dynamic
     * programming implementation and updates the selectedItems class list for
     * later retrieval.
     * @param items Array of items.
     * @param W Knapsack size.
     * @param n Number of items.
     * @return Value of the optimal solution.
     */
    private static int solveStraightforward(List<Item> items, int W, int n)
    {
        // Initializes matrix and its first column
        int [][] a = new int[W + 1][n + 1];
        for(int x = 0; x <= W; x++)
        {
            a[x][0] = 0;
        }

        // Walks through the table filling up the corresponding values
        for(int i = 1; i <= n; i++)
        {
            for(int x = 0; x <= W; x++)
            {
                int value = items.get(i - 1).getValue();
                int weight = items.get(i - 1).getWeight();
                int firstCase = a[x][i - 1];
                int secondCase = (x >= weight)? a[x - weight][i - 1] + value :
                        firstCase;
                a[x][i] = Math.max(firstCase, secondCase);
            }
        }

        // Trace backwards to get the solution from the completed table
        int x = W;
        Knapsack.selectedItems = new ArrayList<Item>(n / 2);
        for(int i = n; i > 0; i--)
        {
            // If conditions met, then item was selected
            int value = items.get(i - 1).getValue();
            int weight = items.get(i - 1).getWeight();
            if(x >= weight && ((a[x][i] - value) == a[x - weight][i - 1]))
            {
                Knapsack.selectedItems.add(items.get(i - 1));
                x -= weight;
            }
        }

        // Returns the value of the optimal solution
        return a[W][n];
    }

    /**
     * Solves the knapsack problem using an implementation optimized for big n.
     * @param items List of items.
     * @param W Knapsack size.
     * @param n Number of items.
     * @return Value of the optimal solution.
     */
    private static int solveForBigData(List<Item> items, int W, int n)
    {
        // Initializes appropriate data structure and its first column
        System.out.print("Initializing 2-D array...");
        List<Integer>[] a = (ArrayList<Integer>[])new ArrayList[W + 1];
        for(int x = 0; x <= W; x++)
        {
            a[x] = new ArrayList<Integer>(2);
            a[x].add(0);
            a[x].add(0);
        }
        System.out.println("done.");

        // Walks through the table filling out the corresponding values, uses
        // memoization; it only keeps the past column (at index 0) and the one
        // that is computing (at index 1), to avoid memory issues
        System.out.println("Filling out table...");
        for(int i = 1; i <= n; i++)
        {
            for(int x = 0; x <= W; x++)
            {
                int value = items.get(i - 1).getValue();
                int weight = items.get(i - 1).getWeight();
                int firstCase = a[x].get(0);
                int secondCase = (x >= weight)? a[x - weight].get(0) + value :
                        firstCase;
                a[x].set(1, Math.max(firstCase, secondCase));
            }
            // Updates 2-D array a for better memory usage
            for(int x = 0; x <= W; x++)
            {
                int aux = a[x].get(1);
                a[x].set(0, aux);
                a[x].set(1, 0);
            }
            // Message in standard output for logging purposes
            if(i % 10 == 0)
            {
                System.out.println("-- [" + i + " items filled out so far.]");
            }
        }
        System.out.println("... table filled out.");

        // Returns the value of the optimal solution
        return a[W].get(0);
    }

    /**
     * Solves the knapsack problem using a greedy heuristic approach.
     * @param items Array of items.
     * @param W Knapsack size.
     * @param n Number of items.
     * @return Value of an acceptable solution (can be the optimal).
     */
    private static int solveGreedyHeuristic(List<Item> items, int W, int n)
    {
        // Sorts items in decreasing order of their "bang per buck" ratios
        QuickBuck.sort(items, n);

        // Packs items in this order until one doesn't fit, then halt
        Knapsack.selectedItems = new ArrayList<Item>(n / 2);
        int weightSoFar = 0;
        int value = 0;
        for(Item item : items)
        {
            if(item.getWeight() <= (W - weightSoFar))
            {
                Knapsack.selectedItems.add(item);
                value += item.getValue();
                weightSoFar += item.getWeight();
            }
            else    // Element doesn't fit, so halt
            {
                break;
            }
        }

        // Returns the total value of the packed items
        return value;
    }

    /**
     * Solves the knapsack problem using a dynamic programming heuristic.
     * @param items List of items.
     * @param W Knapsack size.
     * @param n Number of items.
     * @param e Error margin acceptable for solution, e in [0...1.0)
     * @return Value of an acceptable solution (can be the optimal).
     */
    private static int solveDPHeuristic(List<Item> items, int W, int n, float e)
    {
        // Finds the biggest value
        int vMax = 0;
        for(Item item : items)
        {
            if(item.getValue() > vMax)
            {
                vMax = item.getValue();
            }
        }

        // Initializes 2-D array
        int [][] a = new int[n][n * vMax];
        for(int x = 0; x < n * vMax; x++)
        {
            a[0][x] = Knapsack.MAX_WEIGHT;
        }
        a[0][0] = 0;

        // Walks through the 2-D array solving the corresponding subproblems.
        // a[i][x] contains the minimum total size needed to achieve value >= x
        // while using only the first i items
        for(int i = 1; i < n; i++)
        {
            Item item = items.get(i);
            int value = item.getValue();
            int weight = item.getWeight();
            boolean halt = true;
            for(int x = 0; x < n * vMax; x++)
            {
                int secondCase = weight;
                secondCase += (value >= x)? a[i - 1][0]: a[i - 1][x - value];
                if (secondCase > Knapsack.MAX_WEIGHT)
                {
                    secondCase = Knapsack.MAX_WEIGHT;
                }
                a[i][x] = Math.min(a[i - 1][x], secondCase);

                // Checks whether to halt after this x-loop
                if((a[i - 1][x] <= W) || (secondCase <= W))
                {
                    halt = false;
                }
            }
            // halts if no more loops required (all values are bigger than W)
            if(halt)
            {
                break;
            }
        }

        // Finds and returns the largest x such that a[n - 1][x] <= W
        int largestX = 0;
        for(int x = 0; (x < n * vMax) && (a[n - 1][x] <= W) ; x++)
        {
            if(a[n - 1][x] <= W)
            {
                largestX = x;
            }
        }
        return largestX;
    }
}
