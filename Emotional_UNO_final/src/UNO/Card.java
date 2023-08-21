package UNO;

import jade.content.Concept;

/**
 *
 * @author Mara Pudane
 * Class objects are UNO cards.
 */
public class Card implements Concept {

    private int Number;
    private int Type;
    private String Colour;
    private int Points;

    public void setNumber(int number) {
        this.Number = number;
    }

    public int getNumber() {
        return Number;
    }

    public int getPoints() {
        return Points;
    }

    public int getType() {
        return Type;
    }

    public String getColor() {
        return Colour;
    }

    public void setType(int type) {
        this.Type = type;
    }

    public void setColor(String colour) {
        this.Colour = colour;
    }

    public void setPoints(int p) {
        this.Points = p;
    }
}
