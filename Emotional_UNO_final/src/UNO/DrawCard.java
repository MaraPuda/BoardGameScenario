package UNO;

import emotional_UNO.Belief;
import jade.core.behaviours.OneShotBehaviour;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Mara Pudane
 */
public class DrawCard extends OneShotBehaviour {
    public int I;
    
    public DrawCard (int i) {
       
       I = i;
    } 
    @Override
    public void action() {
        switch (I)
        {
        case 1:
        Object args[] = myAgent.getArguments();
        UNO.UNOargs ua = (UNO.UNOargs) args[8];
        emotional_UNO.BelDesInt bdi = (emotional_UNO.BelDesInt) args[5];
        Card newc = UNO.GamePlay.drawCard();
        ua.Hand.add(newc);
        emotional_UNO.Belief b = new emotional_UNO.Belief("Have2", newc);
        bdi.BeliefSet.add(b);
        args[5] = bdi;
        args[8] = ua;
        
        myAgent.setArguments(args);
        
        break;
     }
}
   
}
