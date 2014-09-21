/**
 * $Id: QuickBuck.java, v1.0 16/09/14 04:23 PM oscarfabra Exp $
 * {@code QuickBuck} Is a class that uses quicksort to sort an array of items
 * based on their value/weight ratios.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 16/09/14
 */

import java.util.ArrayList;
import java.util.List;

/**
 * QuickBuck is a class that uses quicksort to sort a list of items based on
 * their value/weight rates.
 */
public class QuickBuck
{
    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // List of items to sort
    private static List<Item> items = null;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    private QuickBuck() { }     // This class shouldn't be instantiated

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------


    /**
     * Sorts list of items in decreasing order of their value/weight ratios.
     * @param items List of items.
     * @param n Number of items.
     */
    public static void sort(List<Item> items, int n)
    {
        float [] bangPerBuck = new float[n];
        int i = 0;
        for(Item item : items)
        {
            bangPerBuck[i++] = (float)((item.getValue() * 1.0) / item.getWeight());
        }

        // Copies items list to a temporary list
        QuickBuck.items = new ArrayList<Item>(items);

        // Sorts bangPerBuck AND items according to values of bangPerBuck
        sort(bangPerBuck);

        // Updates the given items list using the same memory space
        i = 0;
        for(Item item : QuickBuck.items)
        {
            items.set(i++, item);
        }
    }

    //-------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    //-------------------------------------------------------------------------

    /**
     * Sorts the given array in decreasing order using quicksort pivoting over
     * the first element.
     * @param a Array of numbers.
     */
    private static void sort(float [] a)
    {
        sort(a, 0, a.length - 1);
    }

    /**
     * Sorts array a in [lb...ub] in decreasing order using the first element
     * as pivot.
     * @param a Array of numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     */
    private static void sort(float[] a, int lb, int ub)
    {
        if(ub <= lb){ return; }
        int p = partitionFirst(a, lb, ub);
        sort(a, lb, p - 1);
        sort(a, p + 1, ub);
    }

    /**
     * Partitions the array a in [index...ub] using the first element as pivot.
     * @param a Array of numbers.
     * @param lb Lower-bound index.
     * @param ub Upper-bound index.
     * @return Index of the pivot element.
     */
    private static int partitionFirst(float[] a, int lb, int ub)
    {
        float p = a[lb];
        int i = lb + 1;
        for(int j = lb + 1; j <= ub; j++)
        {
            if(a[j] > p)
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
     * @param a Array of numbers.
     * @param j Index of element to swap.
     * @param i Index of element to swap.
     */
    private static void swap(float[] a, int j, int i)
    {
        float aux = a[j];
        a[j] = a[i];
        a[i] = aux;

        // Swap items accordingly
        Item auxItem = QuickBuck.items.get(j);
        QuickBuck.items.set(j, QuickBuck.items.get(i));
        QuickBuck.items.set(i, auxItem);
    }
}
