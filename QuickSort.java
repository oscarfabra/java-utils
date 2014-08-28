/**
 * $Id: QuickSort.java, v 1.0 20/05/14 21:57 oscarfabra Exp $
 * {@code QuickSort} Class that sorts an array of numbers using quicksort.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 20/05/14
 */


/**
 * Class that sorts an array of numbers using quicksort.
 */
public class QuickSort
{
    //-------------------------------------------------------------------------
    // CLASS ENUMERATOR
    //-------------------------------------------------------------------------    

    /**
     * Enumeration that holds the possible pivots.
     */
    public static enum Pivot
    {
        FIRST,              // Pivots over the first element
        LAST,               // Pivots over the last element
        MEDIAN_OF_THREE     // Pivots using the median-of-three rule
    }

    //-------------------------------------------------------------------------
    // CONSTRUCTOR
    //-------------------------------------------------------------------------

    // This class should not be instantiated
    private QuickSort(){}

    //-------------------------------------------------------------------------
    // PUBLIC CLASS METHODS
    //-------------------------------------------------------------------------

    /**
     * Sorts the given array using quicksort, pivoting over the element at
     * index pivot. <br/>
     * @param a Array of int numbers.
     * @param pivot Pivoting method (FIRST, LAST or MEDIAN_OF_THREE).
     * @return The number of comparisons used to sort the array.
     */
    public static long sortAndCountComparisons(int[] a, Pivot pivot)
    {
        return sortAndCount(a, 0, a.length - 1, pivot);
    }

    /**
     * Sorts the given array using quicksort pivoting over the first element.
     * @param a Array of int numbers.
     */
    public static void sort(int [] a)
    {
        sort(a, 0, a.length - 1);
    }

    /**
     * Sorts the given array only within indices [lb...ub]
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     */
    public static void sortBounded(int [] a, int lb, int ub)
    {
        sort(a, lb, ub);
    }

    /**
     * Prints the elements of the given array in standard output.
     * @param a Array of int numbers.
     */
    public static void show(int [] a)
    {
        for(int i = 0; i < a.length; i++)
        {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Sorts the given array from indices lb to ub using quicksort, and
     * pivoting over the element at index pivot. <br/>
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @param pivot Pivoting method (FIRST, LAST or MEDIAN_OF_THREE).
     * @return The number of comparisons used to sort the array.
     */
    private static long sortAndCount(int[] a, int lb, int ub, Pivot pivot)
    {
        if( lb >= ub)
        {
            return 0;
        }
        int p = partition(a, lb, ub, pivot);
        long l = (p - lb > 0) ? p - lb : 0;
        l = sortAndCount(a, lb, p - 1, pivot) + l;
        long r = (ub - p > 0) ? ub - p : 0;
        r = sortAndCount(a, p + 1, ub, pivot) + r;
        return l + r;
    }

    /**
     * Sorts array a in [lb...ub] using the first element as pivot.
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     */
    private static void sort(int[] a, int lb, int ub)
    {
        if(ub >= lb){ return; }
        int p = partitionFirst(a, lb, ub);
        sort(a, lb, p - 1);
        sort(a, p + 1, ub);
    }

    /**
     * Partitions the array a in [lb...ub] calling to the corresponding helper
     * method depending on the specified pivot. <br/>
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @param pivot Pivoting method (FIRST, LAST or MEDIAN_OF_THREE).
     * @return Index of the pivot element.
     */
    private static int partition(int[] a, int lb, int ub, Pivot pivot)
    {
        // Chooses a pivot and swaps it with the first element of the array
        choosePivot(a, lb, ub, pivot);
        // Partitions the array using the first element as pivot
        return partitionFirst(a, lb, ub);
    }


    /**
     * Chooses a pivot element based on the given arguments and swaps it with
     * the lower-bound element. <br/>
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @param pivot Pivoting method (FIRST, LAST or MEDIAN_OF_THREE).
     */
    private static void choosePivot(int[] a, int lb, int ub, Pivot pivot)
    {
        int index = lb;
        if(pivot != Pivot.FIRST && pivot != Pivot.MEDIAN_OF_THREE)
        {
            // if pivot is the last element, swap
            index = ub;
        }
        else if(pivot == Pivot.MEDIAN_OF_THREE)
        {
            // if pivot is the median-of-three
            index = medianOfThree(a, lb, ub);
        }
        // swaps if necessary
        if(index != lb)
        {
            swap(a, lb, index);
        }
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
     * Finds and returns the median-of-three pivot for a in [lb...ub].
     * @param a Array of int numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @return The median-of-three for a in [lb...ub].
     */
    private static int medianOfThree(int[] a, int lb, int ub)
    {
        int x = a[lb];
        int y = a[lb + (ub - lb)/2];
        int z = a[ub];
        if((x <= y && y <= z) || (z <= y && y <= x))
        {
            return lb + (ub - lb)/2;
        }
        else if((y <= x && x <= z) || (z <= x && x <= y))
        {
            return lb;
        }
        return ub;
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
    }

    /**
     * Converts the given array of String into an array of int.
     * @param a Array of int numbers.
     * @param args List of numbers separated by spaces.
     */
    private static void convertStrings(int[] a, String[] args)
    {
        // Stores again the arguments received by the main
        for(int i = 0; i < args.length; i++)
        {
            a[i] = Integer.parseInt(args[i]);
        }
    }

    //-------------------------------------------------------------------------
    // MAIN
    //-------------------------------------------------------------------------

    /**
     * Main test method for the QuickSort class. <br/>
     * @param args List of numbers separated by spaces.
     */
    public static void main(String [] args)
    {
        if(args.length == 0)
        {
            System.exit(-1);
        }

        int [] a = new int[args.length];
        QuickSort.convertStrings(a, args);
        System.out.println("Array received: ");
        QuickSort.show(a);

        // Sorts the array and counts comparisons using first element as pivot
        long comparisons = QuickSort.sortAndCountComparisons(a, Pivot.FIRST);
        System.out.println("Sorted array using first element as pivot: ");
        QuickSort.show(a);
        System.out.println("Number of comparisons: " + comparisons);
        System.out.println();

        // Stores again the arguments received by the main
        QuickSort.convertStrings(a, args);

        // Sorts and counts comparisons using last element as pivot
        comparisons = QuickSort.sortAndCountComparisons(a, Pivot.LAST);
        System.out.println("Sorted array using last element as pivot: ");
        QuickSort.show(a);
        System.out.println("Number of comparisons: " + comparisons);
        System.out.println();

        // Stores again the arguments received by the main
        QuickSort.convertStrings(a, args);

        // Sorts and counts comparisons using the median-of-three
        comparisons = QuickSort.sortAndCountComparisons(a, Pivot.MEDIAN_OF_THREE);
        System.out.println("Sorted array using median-of-three elements as pivots: ");
        QuickSort.show(a);
        System.out.println("Number of comparisons: " + comparisons);
        System.out.println();
    }
}
