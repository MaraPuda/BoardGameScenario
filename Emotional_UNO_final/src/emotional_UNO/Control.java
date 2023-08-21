package emotional_UNO;

/**
 *
 * @author Mara Pudane
 */
public class Control {

    public boolean Critical;
    public boolean Cycle;
    public boolean BehCycleDone;

    public Control(boolean critical, boolean cycle, boolean bcd) {
        Critical = critical;
        Cycle = cycle;
        BehCycleDone = bcd;
    }
}
