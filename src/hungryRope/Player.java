/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hungryRope;

/**
 *
 * @author chhar9972
 */
public class Player extends Snake{
    Direction prevDirection;
    
    public Player(int r, int g, int b)
    {
        super(r, g, b);
        this.direction = new Direction();
    }
}
