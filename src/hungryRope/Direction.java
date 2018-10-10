package hungryRope;

/**
 * Stores axis, and the positive or negative movement along the axis
 */
public class Direction
{
    char axis;
    int posOrNeg;

    /**
     * Creates a new direction
     * @param axis
     * @param posOrNeg
     */
    Direction (char axis, int posOrNeg)
    {
        this.axis = axis;
        this.posOrNeg = posOrNeg;
    }

    /**
     * Checks if two directions are equal to each other
     * @param direction2
     * @return Whether the direction is equal to the other direction
     */
    public boolean equals(Direction direction2)
    {
        return (this.axis == direction2.axis && this.posOrNeg == direction2.posOrNeg);
    }
}
