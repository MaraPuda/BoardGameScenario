
package emotional_UNO;

/**
 *
 * @author Mara Pudane
 */
public class Incoming {
    public boolean Rational;
    public boolean IsNew;
    public String Who;
    public double Strength;// Says how strong the objective emotion would be outside of an agent
    public char EmotionType;
    public boolean Tertiary;
    public char HoldEmotion;
    public boolean Tag;
    public boolean Processed;
  
        
    public Incoming (boolean r, boolean in,  double ds, char det, boolean tert, boolean p)
    {
    Rational = r;
    IsNew = in;
    Strength = ds;
    EmotionType = det;
    Tertiary = tert;
    Tag = getTag(det);
    Processed = false;  
    Who = "";
    }
    public boolean getTag(char det)
    {
        boolean tag;
    if (det != 'j')
          tag = false;
    else
        tag = true;
    return tag;
    }
    
}
