/*
 * Snake class
 */

package hungryRope;

import static hungryRope.HungryRope.*;
import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Snake class stores snake data such as {@link bodyCoords}, {@link aiStatus}, {@link headColour},
 * {@link bodyColour}, {@link direction}, and {@link alive}
 */
public class Snake{
    boolean aiStatus, alive;
    int score;
    Color bodyColour, headColour;
    Point head;
    Direction direction, prevDirection;
    ArrayList<Point> bodyCoords;
    
    /**
     * Creates a new Snake with a random start position and no {@link Direction}
     * @param r Red value
     * @param g Green value
     * @param b Blue value
     * @param aiStatus whether the Snake is AI controlled
     * @see Direction
     */
    public Snake(int r, int g, int b, boolean aiStatus)
    {
        this.aiStatus = aiStatus;
        this.bodyColour = new Color(r, g, b);
        r = r > 0 ? 255 : 0;
        g = g > 0 ? 255 : 0;
        b = b > 0 ? 255 : 0;
        this.headColour = new Color(r, g, b);
        this.score = 1;
        this.alive = true;
        if (aiStatus) this.direction = new Direction('x', 1);
        else this.direction = new Direction();
        this.bodyCoords = new ArrayList();
        if (!replaying)
        {
            this.bodyCoords.add(new Point (random(4, WIDTH - 4), random(4, HEIGHT - 4)));
        }
    }
    
    /**
     * Moves snake and changes direction if {@link aiStatus} = true
     */
    public void move()
    {
        head = bodyCoords.get(0);
        if(aiStatus) aiMove();
        try
        {
            moveSnake();
        }
        catch(IndexOutOfBoundsException IOOBE)
        {
            replaying = false;
        } catch (IOException ex) {
            System.out.println("IOEXCEPTION");
        }
    }
    
    /**
     * Determines the {@link Direction}
     * @see Direction
     */
    public void aiMove()
    {
        //Primary Objective
        if (direction.axis == 'y')
        {
            direction.axis = head.y != food.y ? 'y' : head.x != food.x ? 'x' : ' ';
        }else
        {
           direction.axis = head.x != food.x ? 'x' : head.y != food.y ? 'y' : ' ';
        }
        direction.posOrNeg = getCoord(direction.axis, head) > getCoord(direction.axis, food) ? -1 : 1;
        if(nextCoordBody() || checkBox(head, direction, 0))
        {
            direction.axis = notAxis(direction.axis) != getCoord(notAxis(direction.axis), food) ? notAxis(direction.axis) : direction.axis;
            direction.posOrNeg = getCoord(direction.axis, head) > getCoord(direction.axis, food) ? -1 : 1;
        }
        //Detours that navigate body parts
        if ((nextCoordBody() || checkBox(head, direction, 0)) && (getFoodDirection(notAxis(direction.axis)) != 0))
        {
            direction.axis = notAxis(direction.axis);
            direction.posOrNeg = getFoodDirection(direction.axis) != 0 ? getFoodDirection(direction.axis) : direction.posOrNeg;
        }
        
        if (nextCoordBody() || checkBox(head, direction, 0)) direction = longestDirection();
    }
    
    
    /*
    Check a direction 
    */
    
    public int getBoxSize(Point startingPoint, Point currentPoint, Direction checkingDirection, int boxWidth, int boxLength, int totalFreeSpace)
    {
        int numAvailableDirections = 0;
        boolean properSizeBox = false;
        Point lookingAt;
        Direction prevSpot = new Direction(checkingDirection.axis, checkingDirection.posOrNeg * -1);
        ArrayList<Direction> availableDirection = new ArrayList();
        
        for (int axisChanger = 0; axisChanger < 2; axisChanger++)
        {
            for (int posOrNegChanger = 0; posOrNegChanger < 2; posOrNegChanger++)
            {
                if (checkingDirection != prevSpot)
                {
                    lookingAt = new Point (makePoint(checkingDirection.axis, getCoord(checkingDirection.axis, currentPoint) + checkingDirection.posOrNeg, getCoord(notAxis(checkingDirection.axis), currentPoint)));
                    if (!checkBody(lookingAt))
                    {
                        numAvailableDirections++;
                        totalFreeSpace++;
                        availableDirection.add(checkingDirection);
                    }
                }
                checkingDirection.posOrNeg *= -1;
            }
         checkingDirection.axis = notAxis(checkingDirection.axis);
        }
            
            
                properSizeBox = !checkBox(makePoint(availableDirection.axis, getCoord(availableDirection.axis, currentPoint) + availableDirection.posOrNeg,
                        getCoord(notAxis(availableDirection.axis), currentPoint)), 
                        availableDirection, totalFreeSpace);
    }
    
    /**
     * Gets the positive or negative movement along an axis for the head to get to the food
     * @param axis The axis to check
     * @return Direction along axis the head must move to get to the food
     */
    public int getFoodDirection(char axis)
    {
        return getCoord(direction.axis, head) > getCoord(direction.axis, food) ? -1 : 1;
    }
    
    /**
     * Checks if the next place the Snake will be is a body part of any Snake
     * @return True if the next coordinate is a body
     */
    public boolean nextCoordBody()
    {
        return checkBody(makePoint(direction.axis, getCoord(direction.axis, head) + direction.posOrNeg, getCoord(notAxis(direction.axis), head)));
    }
    
    /**
     * Finds the furthest orthogonal direction the snake can safely travel
     * @return Furthest {@link Direction} the snake can travel
     */
    public Direction longestDirection()
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
            axis = notAxis(axis);
        }
        return longestDirection;
    }
    
    /**
     * Finds how far the Snake can safely travel in that {@link Direction}
     * @param point Point to checkingDirection from
     * @param direction {@link Direction} to check length
     * @return length the Snake can safely travel in that {@link Direction}
     */
    public int checkDirection(Point point, Direction direction)
    {
        int dimension = direction.posOrNeg == 1 ? direction.axis == 'x' ? WIDTH - 1 : HEIGHT - 1 : 0, distance = 0;
        for (int i = getCoord(direction.axis, point) + direction.posOrNeg; (i >= dimension && direction.posOrNeg == -1) || (i < dimension && direction.posOrNeg == 1); i += direction.posOrNeg)
        {
            distance = direction.posOrNeg > 0 ? i - getCoord(direction.axis, point) : getCoord(direction.axis, point) - i;
            if (checkBody(makePoint(direction.axis, i, getCoord(notAxis(direction.axis), point))))
                break;
        }
        return distance;
    }

    /**
     * Moves the snake and its and its body Point {@link Direction} along the grid
     * also checks if the snake has hit the edges/body parts, and if it's collected food
     * @see Direction
     */
    public void moveSnake() throws IndexOutOfBoundsException, IOException
    {
        //Moves bodyCoords, checks for food collection, and checks if the bodyCoords rammed itself
        Point prevHead = bodyCoords.get(0);
        //Variable used to prevent bodyCoords from going back in on itself
        prevDirection = direction;

        if (bodyCoords.size() != score)
        {//Grows the bodyCoords
            bodyCoords.add(new Point(0,0));
        }
        for (int i = bodyCoords.size() - 1; i > 0; i--)
        {//Moves the bodyCoords body forwards
            bodyCoords.set(i, bodyCoords.get(i - 1));
        }
        
        if (replaying)
        {
            bodyCoords.set(0, new Point (readFileLine(frame * 4), readFileLine(frame * 4 + 1)));
        }else{
            if (direction.axis == 'y')
            {//Moves the bodyCoords head forwards if the direction is +2 or -2 it moves along y axis otherwise it moves along x
                bodyCoords.set(0, new Point ((int) prevHead.x, (int) prevHead.getY() + direction.posOrNeg));
            }else if (direction.axis == 'x')
            {
                bodyCoords.set(0, new Point ((int) prevHead.x + direction.posOrNeg, (int) prevHead.getY()));
            }
        }
        
        head = bodyCoords.get(0);

        checkFood();

        checkHit();
        if (recordingReplay)
        {
            replay.add("" + head.x);
            replay.add("" + head.y);
            replay.add("" + food.x);
            replay.add("" + food.y);
        }
    }

    /**
     * Checks if the head's coordinates equal the food's coordinates
     */
    public void checkFood() throws NumberFormatException, IOException
    {
        //Adds to score and changes food if the bodyCoords head is on the food
        if (bodyCoords.get(0).equals(food))
        {
            score += 3;
            if (replaying)
            {
                food = new Point (readFileLine(frame * 4 + 2), readFileLine(frame * 4 + 3));
            }else
            {
                do
                {
                    food = new Point (random(0, WIDTH), random(0, HEIGHT));
                }while (foodOnBody());
            }
        }
    }
    
    /**
     * Checks if the food coordinates are on a body part to prevent the food from spawning on a snake
     * @return True if food coordinates are on a body part
     */
    public boolean foodOnBody()
    {
        for (Snake snake : snakes)
        {
            for (Point bodyCoord : snake.bodyCoords)
            {
                if (bodyCoord.equals(food))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the snake has hit a body part
     */
    public void checkHit()
    {
        for (Snake snakeCheckHit : snakes)
        {
            for (int bodyCoordIndex = !snakeCheckHit.equals(this) ? 0 : 1; bodyCoordIndex < snakeCheckHit.bodyCoords.size(); bodyCoordIndex++)
            {
                if (snakeCheckHit.bodyCoords.get(bodyCoordIndex).equals(head))
                {
                    this.alive = false;
                }
            }
        }
    }
    
    /**
     * Makes a Point with coordinates where {@code onAxis} is on {@code axis}
     * @param axis Axis to put {@code onAxis} on
     * @param onAxis Coordinate on {@code axis}
     * @param offAxis Coordinate off {@code axis}
     * @return New Point with coordinates based on the axis
     */
    public Point makePoint(char axis, int onAxis, int offAxis)
    {
        Point newPoint = axis == 'x' ? new Point (onAxis, offAxis) : new Point (offAxis, onAxis);
        return newPoint;
    }
    
    /**
     * Checks for "body" at {@code point}
     * @param point Point on {@code map}
     * @return True if at {@code point} on {@code map} contains "body"
     */
    public boolean checkBody(Point point)
    {
        try
        {
                switch (grid[point.x][point.y].substring(1, 6).trim())
            {
                case "body":
                    return true;
                case "head":
                    return true;
                default:
                    return false;
            }
        }catch(ArrayIndexOutOfBoundsException AIOOBE)
        {
            return true;
        }
    }
}
