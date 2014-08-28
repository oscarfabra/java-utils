/**
 * $Id: Vertex.java, v 1.0 8/06/14 10:52 oscarfabra Exp $
 * {@code Vertex} Represents a Vertex for the Graph class.
 *
 * @author <a href="mailto:oscarfabra@gmail.com">Oscar Fabra</a>
 * @version 1.0
 * @since 8/06/14
 */

/**
 * Class that represents a Vertex for the Graph class.
 * @see Graph
 * @see Edge
 */
public class Vertex
{
    //-------------------------------------------------------------------------
    // ATTRIBUTES
    //-------------------------------------------------------------------------

    // Identifies a vertex unequivocally
    private int id;

    // Indicates if current vertex has been explored or not
    private boolean explored;

    //-------------------------------------------------------------------------
    // CONSTRUCTORS
    //-------------------------------------------------------------------------

    /**
     * Creates a new Vertex assigning a unique identifier.
     */
    public Vertex(int newId)
    {
        this.id = newId;
        this.explored = false;
    }

    /**
     * Copy constructor.
     * @param that Vertex to copy attributes from.
     */
    public Vertex(Vertex that)
    {
        this.id = that.id;
        this.explored = that.explored;
    }

    //-------------------------------------------------------------------------
    // PUBLIC METHODS
    //-------------------------------------------------------------------------

    /**
     * Gets the id of this vertex.
     * @return Id of this vertex.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Says if the current vertex has been explored or not.
     * @return Whether current vertex has been explored.
     */
    public boolean isExplored()
    {
        return this.explored;
    }

    /**
     * Decides whether current value is explored or not.
     * @param value Whether to set the vertex as explored or unexplored.
     */
    public void setExplored(boolean value)
    {
        this.explored = value;
    }
}
