/**
 * $Id: Item.java, v1.0 10/08/14 07:35 PM oscarfabra Exp $
 * {@code Item} Represents an item for the knapsack problem.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 10/08/14
 */

/**
 * Item represents an item for the knapsack problem.
 * @see Knapsack
 */
public class Item
{
    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    private int id;         // The id of the item
    private int value;      // The value of the item
    private int weight;     // The weight of the item

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    /**
     * Creates a new Item object.
     * @param id Id to assign to the new item.
     * @param value Value of the item.
     * @param weight Weight of the item.
     */
    public Item(int id, int value, int weight)
    {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Gets the id of the item.
     * @return Id of the item.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Gets the value of the item.
     * @return Value of the item.
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the weight of the item.
     * @return Weight of the item.
     */
    public int getWeight() {
        return weight;
    }
}
