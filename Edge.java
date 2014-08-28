/**
 * $Id: Edge.java, v 1.2 03/07/14 10:52 oscarfabra Exp $
 * {@code Edge} Represents an Edge in the Graph class containing the id of its
 * tail and head vertices and its cost. <br/>
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.2
 * @since 03/07/14
 */

/**
 * Class that represents an Edge in the Graph class containing the id of its
 * tail and head vertices and its cost. <br/>
 * @see Graph
 * @see Vertex
 */
public class Edge
{
    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // Identifies the edge unequivocally
    private int id;

    // Id of tail vertex
    private int tail;

    // Id of head vertex
    private int head;

    // Cost of the edge
    private int cost;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    /**
     * Creates a new Edge given its id, the id of its head vertex, and the id
     * of its tail vertex.
     * @param id Id to assign to this edge.
     * @param tail Id of the tail vertex.
     * @param head Id of the head vertex.
     * @param cost Cost of the edge
     */
    public Edge(int id, int tail, int head, int cost)
    {
        this.id = id;
        this.tail = tail;
        this.head = head;
        this.cost = cost;
    }

    /**
     * Copy constructor.
     * @param that Edge to copy attributes from.
     */
    public Edge(Edge that)
    {
        this.id = that.id;
        this.tail = that.tail;
        this.head = that.head;
        this.cost = that.cost;
    }

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Gets the id of the tail vertex.
     * @return The id of the tail vertex.
     */
    public int getTail()
    {
        return this.tail;
    }

    /**
     * Gets the id of the head vertex.
     * @return The id of the head vertex.
     */
    public int getHead()
    {
        return this.head;
    }

    /**
     * Gets the id of this edge.
     * @return The id of this edge.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Gets the cost of this edge.
     * @return The cost of this edge.
     */
    public int getCost() {
        return this.cost;
    }
}
