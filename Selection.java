/**
 * $Id: Selection.java, v 1.0 29/05/14 21:57 oscarfabra Exp $
 * {@code Selection} Is a class that solves the problem of selecting the order 
 * i statistic from a given array a (that is, the ith smallest element in the 
 * array).
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @since 29/05/2014
 */
public class Selection
{
    //-------------------------------------------------------------------------
    // CONSTANT
    //-------------------------------------------------------------------------

    // Defines the length of each subgroup for the DSelect algorithm
    public static final int DSELECT_SUBGROUP_LENGTH = 5;

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    // This class should not be instantiated.
    private Selection(){}

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Solves the selection problem using a randomized approach.
     * @param a Array of integers.
     * @param i Order statistic to search for.
     * @return The ith smallest element in the array.
     */
    public static int RSelect(int [] a, int i)
    {
        return RSelect(a, a.length, i - 1);
    }

    /**
     * Solves the selection problem using a deterministic approach.
     * @param a Array of integers.
     * @param i Order statistic to search for.
     * @return The ith smallest element in the array.
     */
    public static int DSelect(int [] a, int i)
    {
        // Use an auxiliary array to store the index of the pivot element
        int [] aux = new int[1];

        // Creates a matrix of pairs (value, index) from array a
        int [][] b = new int[a.length][2];
        for(int k = 0; k < a.length; k++)
        {
            b[k][0] = a[k];
            b[k][1] = k;
        }

        // Calls the main recursive DSelect algorithm
        return DSelect(b, a.length, i - 1, aux);
    }

    /**
     * Prints the elements of array a in standard output.
     * @param a Array of integers.
     */
    public static void show(int [] a)
    {
        for(int i = 0; i < a.length; i++)
        {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    /**
     * Copies array aux into array a.
     * <b>Pre: </b> a.length = aux.length
     * @param a Array of integers.
     * @param aux Array of integers.
     */
    public static void copy(int [] a, int [] aux)
    {
        for(int i = 0; i < a.length; i++)
        {
            a[i] = aux[i];
        }
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Solves the selection problem using a randomized approach.
     * @param a Array of integers.
     * @param n Length of the array.
     * @param i Order statistic to search for.
     * @return The ith smallest element in the array.
     */
    private static int RSelect(int [] a, int n, int i)
    {
        if(n == 1)
        {
            return a[i];
        }
        // chooses pivot p from a uniformly at random
        int p = chooseRandomPivot(a, n);
        // partitions a around p and returns the new index of p
        int j = partitionFirst(a, n);

        if(j == i)      // if j == i, then there's just one element
        {
            return p;
        }
        else if(j > i)  // if j > i, then the element is on the left side of a
        {
            int [] b = subArray(a, 0, j - 1);
            return RSelect(b, j, i);
        }
        else            // if j < i, then the element is on the right side of a
        {
            int [] b = subArray(a, j + 1, n - 1);
            return RSelect(b, n - j - 1, i - j - 1);
        }
    }

    /**
     * Chooses a pivot p from array a in [0...n-1] uniformly at random and
     * swaps it with the element at position 0. <br/>
     * @param a Array of integers.
     * @param n Length of the array.
     * @return Element to pivot the array at.
     */
    private static int chooseRandomPivot(int[] a, int n)
    {
        int index = (int)(Math.random()*n);
        swap(a, 0, index);
        return a[0];
    }

    /**
     * Partitions the array a in [0...n-1] using the first element as pivot.
     * @param a Array of integers.
     * @param n Length of the array.
     * @return Index of the pivot element.
     */
    private static int partitionFirst(int[] a, int n)
    {
        int p = a[0];
        int i = 1;
        for(int j = 1; j <= n-1; j++)
        {
            if(a[j] < p)
            {
                swap(a, j, i);
                i++;
            }
        }
        swap(a, 0, i - 1);
        return i - 1;
    }

    /**
     * Swaps the elements in array a at indices j and i. <br/>
     * @param a Array of integers.
     * @param j Index of element to swap.
     * @param i Index of element to swap.
     */
    private static void swap(int[] a, int j, int i)
    {
        int aux = a[j];
        a[j] = a[i];
        a[i] = aux;
    }

    /**
     * Gets a sub-array of integers from array a in [lb...ub]
     * @param a Array of integers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @return The sub-array of integers.
     */
    private static int[] subArray(int[] a, int lb, int ub)
    {
        int [] b = new int[ub - lb + 1];
        for(int i = lb; i <= ub; i++)
        {
            b[i - lb] = a[i];
        }
        return b;
    }

    /**
     * Solves the selection problem using a deterministic approach.
     * @param a Array of integers.
     * @param n Length of the array.
     * @param i Order statistic to search for.
     * @return The ith smallest element in the array.
     */
    private static int DSelect(int [][] a, int n, int i, int [] aux)
    {
        if(n == 1)
        {
            aux[0] = a[0][1];
            return a[0][0];
        }

        // breaks a into groups of DSELECT_SUBGROUP_LENGTH and sorts each group
        int subgroups = (n % DSELECT_SUBGROUP_LENGTH == 0)?
                n/DSELECT_SUBGROUP_LENGTH : n/DSELECT_SUBGROUP_LENGTH + 1;
        int [] values = getMatrixValues(a, n);
        sortGroups(values, subgroups);

        // gets the median of each subgroup with their respective indices
        int [][] c = getMiddleElements(values, subgroups);

        // recursively computes the median of c
        int divisions = (n % DSELECT_SUBGROUP_LENGTH == 0)?
                n/(DSELECT_SUBGROUP_LENGTH*2) :
                n/(DSELECT_SUBGROUP_LENGTH*2) + 1;
        int p = DSelect(c, subgroups, divisions, aux);

        // partitions a around p
        // if(c.length == 1) { aux[0] = a[0][1]; }
        int j = DPartition(values, n, aux[0]);

        if(j == i)      // if j == i, then there's just one element
        {
            aux[0] = i;
            return p;
        }
        else if(j > i)  // if j > i, then the element is on the left side of a
        {
            int [][] b = subMatrix(a, 0, j - 1);
            return DSelect(b, j, i, aux);
        }
        else            // if j < i, then the element is on the right side of a
        {
            int [][] b = subMatrix(a, j + 1, n - 1);
            return DSelect(b, n - j - 1, i - j - 1, aux);
        }
    }

    /**
     * Copies the values from matrix a in [0...n]
     * @param a Array of integers.
     * @param n Length of the array.
     * @return Array of values from matrix a.
     */
    private static int[] getMatrixValues(int[][] a, int n)
    {
        int [] b = new int[n];
        for(int i = 0; i < n; i++)
        {
            b[i] = a[i][0];
        }
        return b;
    }

    /**
     * Breaks array a into groups of DSELECT_SUBGROUP_LENGTH and sorts each
     * group using QuickSort.
     * @param a Array of integers.
     * @param subgroups Number of subgroups.
     */
    private static void sortGroups(int[] a, int subgroups)
    {
        // Sorts each group of a using quicksort
        for(int i = 0; i < subgroups; i++)
        {
            int lb = i * DSELECT_SUBGROUP_LENGTH;
            int ub = lb + DSELECT_SUBGROUP_LENGTH;
            if(ub > a.length){ ub = a.length; }
            QuickSort.sortBounded(a, lb, ub - 1);
        }
    }

    /**
     * Gets the median of each subgroup of a.
     * @param a Array of integers.
     * @param subgroups Number of subgroups.
     * @return Matrix of integers containing the median and its index within
     * each subgroup of a.
     */
    private static int[][] getMiddleElements(int[] a, int subgroups)
    {
        int [][] c = new int[subgroups][2];
        for(int i = 0; i < subgroups; i++)
        {
            int lb = i * DSELECT_SUBGROUP_LENGTH;
            int ub = lb + DSELECT_SUBGROUP_LENGTH;
            int med = (DSELECT_SUBGROUP_LENGTH % 2 == 0) ?
                    (DSELECT_SUBGROUP_LENGTH / 2) - 1 :
                    (DSELECT_SUBGROUP_LENGTH / 2);
            if(ub > a.length)
            {
                int gap = a.length - lb;
                med = (gap % 2 == 0) ? (gap / 2) - 1 : (gap / 2);
            }
            int index = lb + med;
            c[i][0] = a[index];
            c[i][1] = index;
        }
        return c;
    }

    /**
     * Gets a sub-matrix of pairs (value, index) from matrix a in [lb...ub].
     * @param a Matrix of integers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @return The sub-matrix of pairs (value, index).
     */
    private static int[][] subMatrix(int[][] a, int lb, int ub)
    {
        int [][] b = new int[ub - lb + 1][2];
        for(int i = lb; i <= ub; i++)
        {
            b[i - lb][0] = a[i][0];
            b[i - lb][1] = a[i][1];
        }
        return b;
    }

    /**
     * Partitions a around p for the DSelect algorithm.
     * @param a Array of integers.
     * @param n Length of the array.
     * @param index Index of pivot element.
     * @return Index of the pivot element.
     */
    private static int DPartition(int[] a, int n, int index)
    {
        swap(a, 0, index);
        return partitionFirst(a, n);
    }

    //-------------------------------------------------------------------------
    // MAIN
    //-------------------------------------------------------------------------

    /**
     * Main test method.
     * @param args The ith order statistic to look for in the first place,
     *             followed by a list of numbers separated by spaces that
     *             comprise the array to search the number at.
     */
    public static void main(String [] args)
    {
        if(args.length == 0)
        {
            System.out.println("Must enter the ith order statistic to look " +
                    "for in the first place, followed by a list of integers " +
                    "separated by spaces.");
            System.exit(-1);
        }

        int i = Integer.parseInt(args[0]);
        int [] a = new int[args.length - 1];
        int [] aux = new int[args.length - 1];
        for(int k = 1; k < args.length; k++)
        {
            a[k - 1] = Integer.parseInt(args[k]);
            aux[k - 1] = a[k - 1];
        }
        System.out.println("Array received: ");
        Selection.show(a);

        // Solves the selection problem using the RSelect algorithm
        int element = Selection.RSelect(a, i);
        System.out.println("The " + i + "th order statistic using RSelect " +
                "is: " + element);

        // Solves the selection problem using the DSelect algorithm
        Selection.copy(a, aux);
        element = Selection.DSelect(a, i);
        System.out.println("The " + i + "th order statistic using DSelect " +
                "is: " + element);
    }
}
