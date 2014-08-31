/**
 * $Id: BigMatrix.java, v1.0 11/08/14 01:25 PM oscarfabra Exp $
 * {@code BigMatrix} is a class that represents an 2-D array to use for
 * working with 2-D arrays that have very large number of rows and columns.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 11/08/14
 */

import java.util.ArrayList;
import java.util.List;

/**
 * BigMatrix represents a 2-D array to use for working with 2-D arrays that
 * have VERY large number of rows and columns.
 */
public class BigMatrix
{
    //-------------------------------------------------------------------------
    // CONSTANT
    //-------------------------------------------------------------------------

    // Length of a side of a square partition in which to divide a BigMatrix
    public static final int PARTITION_SIDE = 100000;

    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // List of smaller partitioned tables
    private List<double[][]> bigMatrix = null;

    // Number of columns' partitions of the bigMatrix
    private int colsPartitions;

    // Number of rows' partitions of the bigMatrix
    private int rowsPartitions;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    /**
     * Creates a new BigMatrix with the given parameters.
     * @param rows Number of rows of the BigMatrix.
     * @param columns Number of columns of the BigMatrix.
     */
    public BigMatrix(int rows, int columns)
    {
        // Initializes the list of smaller partitioned tables
        this.rowsPartitions =  (rows % PARTITION_SIDE == 0) ?
                rows / PARTITION_SIDE : (rows / PARTITION_SIDE) + 1;
        this.colsPartitions = (columns % PARTITION_SIDE == 0) ?
                columns / PARTITION_SIDE : (columns / PARTITION_SIDE) + 1;
        int size = this.rowsPartitions * this.colsPartitions;
        this.bigMatrix = new ArrayList<double[][]>(size);
        for(int i = 0; i < size; i++)
        {
            int newCols = PARTITION_SIDE;
            int newRows = PARTITION_SIDE;
            if((i + 1) % colsPartitions == 0)
            {
                newCols = columns % PARTITION_SIDE;
            }
            if(i >= (size - colsPartitions))
            {
                newRows = rows % PARTITION_SIDE;
            }
            this.bigMatrix.add(new double[newRows][newCols]);
        }
    }

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Sets the specified element at the given indices.
     * @param row Row position, row in [0, 1, ..., rows - 1]
     * @param column Column position, column in [0, 1, ..., columns - 1]
     * @param item Number to add to the BigMatrix.
     */
    public void set(int row, int column, double item)
    {
        // Finds the small table in which to store the item
        int rowPartition = row / PARTITION_SIDE;
        int colPartition = column / PARTITION_SIDE;
        int itemRow = row % PARTITION_SIDE;
        int itemCol = column % PARTITION_SIDE;
        int bigIndex = rowPartition * this.colsPartitions + colPartition;

        // Gets the table in which to store the item, stores the item and
        // updates the list
        double [][] smallMatrix = this.bigMatrix.get(bigIndex);
        smallMatrix[itemRow][itemCol] = item;
        this.bigMatrix.set(bigIndex, smallMatrix);
    }

    /**
     * Returns the element of the BigMatrix at the specified position.
     * @param row Row position, row in [0, 1, ..., rows - 1]
     * @param column Column position, column in [0, 1, ..., columns - 1]
     * @return Number at the given position.
     */
    public double get(int row, int column)
    {
        // Finds small table in which to store the item
        int rowPartition = row / PARTITION_SIDE;
        int colPartition = column / PARTITION_SIDE;
        int itemRow = row % PARTITION_SIDE;
        int itemCol = column % PARTITION_SIDE;
        int bigIndex = rowPartition * this.colsPartitions + colPartition;

        // Gets the table from which to retrieve the item and retrieves it
        double [][] smallMatrix = this.bigMatrix.get(bigIndex);
        return smallMatrix[itemRow][itemCol];
    }
}
