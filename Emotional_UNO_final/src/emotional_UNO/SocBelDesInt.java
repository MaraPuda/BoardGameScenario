package emotional_UNO;

import java.util.List;

/**
 *
 * @author Mara Pudane
 */
public class SocBelDesInt {

    public List<SocialBelief> BeliefSet;
    public int Desire;
    public boolean Intention = true;
    public static int TESTTICK = 0;

    public SocBelDesInt(List beliefset, int desire, boolean intention) {
        BeliefSet = beliefset;
        Desire = desire;
        Intention = intention;
    }

    public static List<SocialBelief> updateBeliefSet(List beliefset, SocialBelief newBelief) {
        SocialBelief sb;
        boolean isUpdated = false;
        for (int i = 0; i < beliefset.size(); i++) {
            sb = (SocialBelief) beliefset.get(i);
            if (sb.Subject.equals(newBelief.Subject) & sb.Type == newBelief.Type) {
                sb.Number = newBelief.Number;
                beliefset.set(i, sb);
                isUpdated = true;
            }

        }

        if (isUpdated == false) {
            beliefset.add(newBelief);
        }



        return beliefset;
    }
}
