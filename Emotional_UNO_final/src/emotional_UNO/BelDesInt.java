package emotional_UNO;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Mara Pudane
 */
public class BelDesInt {

    public List<Belief> BeliefSet;
    public int Desire;
    public List<Intention> IntentionSet;
    public static int TESTTICK = 0;

    public BelDesInt(List beliefset, int desire, List intset) {
        BeliefSet = beliefset;
        Desire = desire;
        IntentionSet = intset;
    }
}
