package UNO;

import java.util.ArrayList;


/**
 *
 * @author Mara Pudane
 */
public class UNOargs {
    public String PlayOnLeft;
    public String PlayOnRight;
    public ArrayList <Card> Hand; 
    public Card Current;
    public String Turn;
    
    public UNOargs(String pol, String por, ArrayList <Card> h, Card current, String turn)
    {
    PlayOnLeft = pol;
    PlayOnRight = por;
    Hand = h;
    Current = current;
    Turn = turn;
    }
}
