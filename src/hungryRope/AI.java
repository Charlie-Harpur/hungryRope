/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hungryRope;

import static hungryRope.HungryRope.*;
import java.awt.Point;
import java.io.IOException;
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
    
    public void move()
    {
        head = bodyCoords.get(0);
        aiMove();
        moveSnake();
    }
    
    /**
     * Determines the {@link Direction} of an AI controlled {@link Snake}
     */
    private void aiMove()
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
    
    /**
     * Returns the size of available space in a direction from the head
     * @param checkingDirection Direction from head to check
     * @return Available space in {@code checkingDirection} maxed out at score + 1
     */
    private int getBoxSize(Direction checkingDirection)
    {
        for (int x = 0; x < boxGrid.length; x++)
        {
            for (int y = 0; y < boxGrid[0].length; y++)
            {
                boxGrid[x][y] = 0;
            }
        }
        boxSize = 0;
        
        boxChecker(makePoint(checkingDirection.axis, getCoord(checkingDirection.axis, head) + checkingDirection.posOrNeg, getCoord(checkingDirection.notAxis(), head)));
        return boxSize;
    }
    
    private void boxChecker(Point checkingPoint)
    {
      if (!checkBody(checkingPoint) && boxGrid[checkingPoint.x][checkingPoint.y] == 0 && !(boxSize > this.score))
      {
          boxGrid[checkingPoint.x][checkingPoint.y] = 1;
          boxSize++;
          boxChecker(new Point(checkingPoint.x - 1, checkingPoint.y));
          boxChecker(new Point(checkingPoint.x, checkingPoint.y - 1));
          boxChecker(new Point(checkingPoint.x, checkingPoint.y + 1));
          boxChecker(new Point(checkingPoint.x + 1, checkingPoint.y));
      }
    }
    
    /**
     * Gets the positive or negative movement along an axis for the head to get to the food
     * @param axis The axis to check
     * @return Direction along axis the head must move to get to the food
     */
    private int getFoodDirection(char axis)
    {
        return getCoord(axis, head) > getCoord(axis, food) ? -1 : 1;
    }
    
    /**
     * Checks if the next place the Snake will be is a body part of any Snake
     * @return True if the next coordinate is a body
     */
    private Point nextCoord()
    {
        return makePoint(direction.axis, getCoord(direction.axis, head) + direction.posOrNeg, getCoord(direction.notAxis(), head));
    }
    
    /**
     * Finds the furthest orthogonal direction the snake can safely travel
     * @return Furthest {@link Direction} the snake can travel
     */
    private Direction longestDirection()
    {
        Direction longestDirection = new Direction (' ', 0);
        int longestDirectionNum = 0;
        char axis = this.direction.axis;
        for (int axisNum = 0; axisNum < 2; axisNum++)
        {
            for (int posOrNeg = 1; posOrNeg >= -1; posOrNeg -= 2)
            {
                if (checkDirection(head, new Direction(axis, posOrNeg)) > longestDirectionNum)
                {
                    longestDirectionNum = checkDirection(head, new Direction(axis, posOrNeg));
                    longestDirection = new Direction(axis, posOrNeg);
                }
            }
            axis = new Direction (axis, 0).notAxis();
        }
        return longestDirection;
    }
    
    /**
     * Finds how far the Snake can safely travel in that {@link Direction}
     * @param point Point to checkingDirection from
     * @param direction {@link Direction} to check length
     * @return length the Snake can safely travel in that {@link Direction}
     */
    private int checkDirection(Point point, Direction direction)
    {
        int dimension = direction.posOrNeg == 1 ? direction.axis == 'x' ? WIDTH - 1 : HEIGHT - 1 : 0, distance = 0;
        for (int i = getCoord(direction.axis, point) + direction.posOrNeg; (i >= dimension && direction.posOrNeg == -1) || (i < dimension && direction.posOrNeg == 1); i += direction.posOrNeg)
        {
            distance = direction.posOrNeg > 0 ? i - getCoord(direction.axis, point) : getCoord(direction.axis, point) - i;
            if (checkBody(makePoint(direction.axis, i, getCoord(direction.notAxis(), point))))
                break;
        }
        return distance;
    }
}
