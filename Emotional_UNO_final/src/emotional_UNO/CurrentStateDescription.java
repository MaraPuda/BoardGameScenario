package emotional_UNO;

/**
 *
 * @author Mara Pudane
 */
public class CurrentStateDescription {
    public PAD currentState;
    public PAD mood;
    public double moodTime;
    public PAD prevMood;
    public EmotionVector [] emoVect;
    public double DecSecs;
    
    
    public CurrentStateDescription (PAD cs, PAD m, PAD pm, double mt, EmotionVector [] ev)
    {
    currentState = cs;
    mood = m;
    moodTime = mt;
    emoVect = ev;
    prevMood = pm;
    DecSecs = 0;
    }
}    
  


