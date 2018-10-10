/*
 * Snake class
 */

package hungryRope;

import static hungryRope.HungryRope.*;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 * @author chhar9972
 */
public class Snake {
    boolean Ai, alive;
    int score;
    Color bodyColour, headColour;
    Point head;
    Direction direction, prevDirection;
    ArrayList<Point> bodyCoords;
    
    //Constructor
    Snake(int r, int g, int b, boolean Ai)
    {
        
        this.Ai = Ai;
        bodyColour = new Color(r, g, b);
        r = r > 0 ? 255 : 0;
        g = g > 0 ? 255 : 0;
        b = b > 0 ? 255 : 0;
        headColour = new Color(r, g, b);
        score = 1;
        alive = true;
        if (Ai) this.direction = new Direction('x', 1);
        else this.direction = new Direction(' ', 0);
        bodyCoords = new ArrayList();
        bodyCoords.add(new Point (random(4, width - 4), random(4, height - 4)));
    }
    
    public void move()
    {
        head = bodyCoords.get(0);
        if(Ai) aiMove();
        moveSnake();
        head = bodyCoords.get(0);
    }
    
    public void aiMove()
    {
        direction.axis = head.getY() != food.getY() ? 'y' : head.getX() != food.getX() ? 'x' : ' ';
        direction.posOrNeg = getCoord(head, direction.axis) > getCoord(food, direction.axis) ? -1 : 1;

        if (checkBody(makePoint(direction.axis, getCoord(head, direction.axis) + direction.posOrNeg, getCoord(head, notAxis(direction.axis))), grid))
        {
            System.out.println("po");
        }
    }

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

        checkFood();

        checkHit();
    }

    public void checkFood()
    {
        //Adds to score and changes food if the bodyCoords head is on the food
        if (bodyCoords.get(0).equals(food))
        {
            score += 3;
            food = new Point (random(0, width), random(0, height));
        }
    }

    private void checkHit()
    {
        //Make better later
        //Checks if the bodyCoords head has hit any part of the body
        for (int i = 1; i < bodyCoords.size(); i++)
        {//Checks to see if the head ran into a part of the body
            if (bodyCoords.get(i).equals(bodyCoords.get(0))) alive = false;
        }
    }
    
    private Point makePoint(char axis, int coord1, int coord2)
    {
        Point newPoint = axis == 'x' ? new Point (coord1, coord2) : new Point (coord2, coord1);
        return newPoint;
    }
    
    private boolean checkBody(Point coords, String[][] map)
    {
            return "body".equals(map[(int) coords.getX()][(int) coords.getY()].substring(1, 6).trim());
    }
}
