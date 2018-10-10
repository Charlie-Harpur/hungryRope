package hungryRope;

/**
 *
 * @author chhar9972
 */
public class Direction
{
    char axis;
    int posOrNeg;

    Direction (char axis, int posOrNeg)
    {
        this.axis = axis;
        this.posOrNeg = posOrNeg;
    }

    public boolean equals(Direction direction2)
    {
        return (this.axis == direction2.axis && this.posOrNeg == direction2.posOrNeg);
    }
}
