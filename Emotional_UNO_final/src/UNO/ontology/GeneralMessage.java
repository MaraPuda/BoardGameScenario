package UNO.ontology;

import jade.content.Predicate;

/**
 *
 * @author Mara Pudane
 */
public class GeneralMessage implements Predicate{
 
    private int MesType;
    private UNO.Card Card;
    private String Move;
    private String EmoType;
    private double EmoStrength;
  
    
    public int getMesType()
    {
    return MesType;
    }
      public void setMesType(int mestype)
    {
    this.MesType = mestype;
    }
    
    
    public UNO.Card getCard()
    {
    return Card;
    }
       
     public void setCard(UNO.Card card)
    {
    this.Card = card;
    
    } 
        
    
   public String getMove()
    {
    return Move;
    }
       
     public void setMove(String move)
    {
    this.Move = move;
   }
     
     
     public String getEmoType()
    {
    return EmoType;
    }
      public void setEmoType(String emotype)
    {
    this.EmoType = emotype;
    }
    
    
    public double getEmoStrength()
    {
    return EmoStrength;
    }
       
     public void setEmoStrength(double emostr)
    {
    this.EmoStrength = emostr;
    
    } 
}
