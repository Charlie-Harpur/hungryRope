/*
 * Snake class
 */

package hungryRope;

import static hungryRope.HungryRope.*;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Snake class stores snake data such as {@link bodyCoords}, {@link aiStatus}, {@link headColour},
 * {@link bodyColour}, {@link direction}, and {@link alive}
 */
public class Snake {
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
        bodyColour = new Color(r, g, b);
        r = r > 0 ? 255 : 0;
        g = g > 0 ? 255 : 0;
        b = b > 0 ? 255 : 0;
        headColour = new Color(r, g, b);
        score = 1;
        alive = true;
        if (aiStatus) this.direction = new Direction('x', 1);
        else this.direction = new Direction(' ', 0);
        bodyCoords = new ArrayList();
        bodyCoords.add(new Point (random(4, WIDTH - 4), random(4, HEIGHT - 4)));
    }
    
    /**
     * Moves snake and changes direction if {@link aiStatus} = true
     */
    public void move()
    {
        head = bodyCoords.get(0);
        if(aiStatus) aiMove();
        moveSnake();
    }
    
    /**
     * Determines the {@link Direction}
     * @see Direction
     */
    public void aiMove()
    {
        //Primary objective
        direction.axis = head.getY() != food.getY() ? 'y' : head.getX() != food.getX() ? 'x' : ' ';
        direction.posOrNeg = getCoord(direction.axis, head) > getCoord(direction.axis, food) ? -1 : 1;
        //Detours that navigate body parts
        swapDirection();
    }
    
    public void swapDirection()
    {
        if (checkBody(makePoint(direction.axis, getCoord(direction.axis, head) + direction.posOrNeg, getCoord(notAxis(direction.axis), head)), grid))
        {
            direction.axis = notAxis(direction.axis);
        }
        if (checkBody(makePoint(direction.axis, getCoord(direction.axis, head) + direction.posOrNeg, getCoord(notAxis(direction.axis), head)), grid))
        {
            direction.posOrNeg *= -1;
        }
    }

    /**
     * Moves the snake and its and its body Point {@link Direction} along the grid
     * also checks if the snake has hit the edges/body parts, and if it's collected food
     * @see Direction
     */
    public void moveSnake()
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

        if (direction.axis == 'y')
        {//Moves the bodyCoords head forwards if the direction is +2 or -2 it moves along y axis otherwise it moves along x
            bodyCoords.set(0, new Point ((int) prevHead.getX(), (int) prevHead.getY() + direction.posOrNeg));
        }else if (direction.axis == 'x')
        {
            bodyCoords.set(0, new Point ((int) prevHead.getX() + direction.posOrNeg, (int) prevHead.getY()));
        }
        
        head = bodyCoords.get(0);

        checkFood();

        checkHit();
    }

    /**
     * Checks if the head's coordinates equal the food's coordinates
     */
    public void checkFood()
    {
        //Adds to score and changes food if the bodyCoords head is on the food
        if (bodyCoords.get(0).equals(food))
        {
            score += 3;
            food = new Point (random(0, WIDTH), random(0, HEIGHT));
        }
    }

    /**
     * Checks if the snake has hit a body part
     */
    public void checkHit()
    {
        //Make checkBody work (for ai and hit detection)
        for (Snake snake : snakes)
        {
            for (Point body : snake.bodyCoords.subList(1, snake.bodyCoords.size()))
            {
                if (body.equals(head))
                {
                    this.alive = false;
                    System.out.println("Ded");
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
     * @param map Map to look through
     * @return True if at {@code point} on {@code map} contains "body"
     */
    public boolean checkBody(Point point, String[][] map)
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
