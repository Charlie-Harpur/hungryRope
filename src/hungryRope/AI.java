/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hungryRope;

import static hungryRope.HungryRope.*;
import java.util.ArrayList;

/**
 *
 * @author chhar9972
 */
public class AI extends Snake{
    int[][] boxGrid = new int[WIDTH][HEIGHT];
    int boxSize;
    
    public AI (int r, int g, int b)
    {
        super(r, g, b);
        this.direction = new Direction('x', 1);
    }
    
    /**
     * Determines the {@link Direction} of an AI controlled {@link Snake}
     */
    public void aiMove()
    {
        Direction checkingDirection = direction;
        int longestDirectionLength = 0, largestBoxSize = 0;
        ArrayList<Direction> orthDirections = new ArrayList();
        
        for (int axisChanger = 0; axisChanger < 2; axisChanger++)
        {
            for (int posOrNegChanger = 0; posOrNegChanger < 2; posOrNegChanger++)
            {
                orthDirections.add(new Direction (checkingDirection.axis, checkingDirection.posOrNeg));
                checkingDirection.posOrNeg *= -1;
            }
         checkingDirection.axis = checkingDirection.notAxis();
        }
        
        //Primary Objective (Move towards food)
        direction.axis = getCoord(direction.axis, head) != getCoord(direction.axis, food) ? direction.axis : 
                getCoord(direction.notAxis(), head) != getCoord(direction.notAxis(), food) ? direction.notAxis() : ' ';
        direction.posOrNeg = getFoodDirection(direction.axis);

        if (getBoxSize(direction) < this.score)
        {
            //Checks other direction to food
            direction = new Direction(direction.notAxis(), getFoodDirection(direction.notAxis()));

            //Catch all
            if(getBoxSize(direction) < this.score)
            {
                ArrayList<Direction> equalBoxSize = new ArrayList();
                for(Direction catchDirection : orthDirections)
                {
                    if(getBoxSize(catchDirection) > largestBoxSize)
                    {
                        equalBoxSize.clear();
                        equalBoxSize.add(new Direction(catchDirection.axis, catchDirection.posOrNeg));
                        largestBoxSize = getBoxSize(catchDirection);
                        direction = catchDirection;
                    }else if(largestBoxSize == getBoxSize(catchDirection))
                    {
                        equalBoxSize.add(new Direction(catchDirection.axis, catchDirection.posOrNeg));
                    }
                }

                //If multiple directions have an equal box size that's also the largest found
                if (equalBoxSize.size() > 1)
                {
                    for (Direction catchDirection : equalBoxSize)
                    {
                        if (checkDirection(head, catchDirection) > longestDirectionLength)
                        {
                            longestDirectionLength = checkDirection(head, catchDirection);
                            direction = catchDirection;
                        }
                    }
                }
            }
        }
    }
}
