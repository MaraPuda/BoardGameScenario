package UNO.ontology;

import jade.content.Concept;

/**
 *
 * @author Mara Pudane
 */
public class Move implements Concept{
    
    private int Type;
    private char Colour;
    private String Name;
       public String getName()
    {
    return Name;
    }
     public void setName(String name)
    {
    this.Name = name;
    } 
    
    public int getType()
    {
    return Type;
    }
     public char getColour()
    {
    return Colour;
    } 
     
     public void setType(int type)
    {
    this.Type = type;
    }
     public void setColour(char colour)
    {
    this.Colour = colour;
    }
     
     
}
