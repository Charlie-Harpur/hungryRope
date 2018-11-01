package hungryRope;

/**
 * Stores axis, and the positive or negative movement along the axis
 */
public class Direction
{
    char axis;
    int posOrNeg;

    Direction ()
    {
        this.axis = ' ';
        this.posOrNeg = '0';
    }
    
    /**
     * Creates a new Direction
     * @param axis axis of movement
     * @param posOrNeg direction along axis
     */
    Direction (char axis, int posOrNeg)
    {
        this.axis = axis;
        this.posOrNeg = posOrNeg;
    }
    
    /**
     * Returns the other axis than the {@link Direction}
     * @return (When {@code axis} = 'x') y (When {@code axis} = 'y') x
     */
    public char notAxis()
    {
        return this.axis == 'x' ? 'y' : 'x';
    }

    /**
     * Checks if two Directions are equal to each other
     * @param direction2 direction to compare to
     * @return Whether the Direction is equal to the other Direction
     */
    public boolean equals(Direction direction2)
    {
        return (this.axis == direction2.axis && this.posOrNeg == direction2.posOrNeg);
    }
}
