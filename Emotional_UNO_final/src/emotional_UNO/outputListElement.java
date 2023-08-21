
package emotional_UNO;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author Mara Pudane
 * Only for output purposes
 */
public class outputListElement {
    public double P = 0;
    public double A = 0;
    public double D = 0;
    public int source;
    String t;
  
  
    
    
    public outputListElement (double p, double a, double d, int s)
    {
    P = p;
    A = a;
    D = d;
    source = s;
    t = new SimpleDateFormat("HH.mm.ss").format(new Date());
    }
    
}
