package UNO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Mara Pudane
 * Class is used for deck-related functions.
 */
public class GamePlay {

    public static List Deck;
    public static Card CurrentCard;
    public static int CardTicker;
    public static boolean Clockwise = true;
    public static List<Card> PlayedCards;
    public static boolean LastRound = false;
    public int GameType;
    public static List<Card> JustADeck = new ArrayList();

    public GamePlay
            (int gt) //implements various hands how cards can be dealt
    {
        this.GameType = gt;
        List<Card> d = new ArrayList();
        PlayedCards = new ArrayList();


        int[] values = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 10, 10, 10, 10, 10, 10, 10,
            11, 11, 11, 11, 11, 11, 11, 11,
            12, 12, 12, 12, 12, 12, 12, 12,
            13, 13, 13, 13,
            14, 14, 14, 14};
        String[] colours = {"y", "y", "y", "y", "y", "y", "y", "y", "y", "y",
            "r", "r", "r", "r", "r", "r", "r", "r", "r", "r",
            "g", "g", "g", "g", "g", "g", "g", "g", "g", "g",
            "b", "b", "b", "b", "b", "b", "b", "b", "b", "b",
            "y", "y", "y", "y", "y", "y", "y", "y", "y",
            "r", "r", "r", "r", "r", "r", "r", "r", "r",
            "g", "g", "g", "g", "g", "g", "g", "g", "g",
            "b", "b", "b", "b", "b", "b", "b", "b", "b",
            "y", "y", "r", "r", "g", "g", "b", "b",
            "y", "y", "r", "r", "g", "g", "b", "b",
            "y", "y", "r", "r", "g", "g", "b", "b",
            "w", "w", "w", "w",
            "w", "w", "w", "w"};
        int[] points = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            1, 2, 3, 4, 5, 6, 7, 8, 9,
            20, 20, 20, 20, 20, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 20, 20,
            50, 50, 50, 50,
            50, 50, 50, 50};

        for (int i = 0; i < values.length; i++) {
            Card a = new Card();
            a.setNumber(i);
            a.setColor(colours[i]);
            a.setType(values[i]);
            a.setPoints(points[i]);
            d.add(a);
            JustADeck.add(a);
            System.out.println(a.getNumber() + a.getColor());
        }

        CardTicker = 0;
        Deck = d;

        switch (gt) {
            case 0:
                Collections.shuffle(d);
                CurrentCard = (Card) d.get(0);
                break;

            case 1:

                CurrentCard = (Card) d.get(0);
                System.out.println(CurrentCard.getNumber() + CurrentCard.getColor());
                //gives wild card to each agent when cards = 3
                Collections.swap(d, 1, (d.size() - 1));
                Collections.swap(d, 1 + 3, (d.size() - 2));
                Collections.swap(d, 1 + 6, (d.size() - 3));
                Collections.swap(d, 1 + 9, (d.size() - 4));
                Collections.swap(d, 1 + 12, (d.size() - 5));
                Collections.swap(d, 1 + 15, (d.size() - 6));
                for (int i = 0; i < 6; i++) {
                    Collections.swap(d, 2 + (i) * 3, 77 + i);
                }
                break;


            case 2:
                CurrentCard = (Card) d.get(0);
                System.out.println(CurrentCard.getNumber() + CurrentCard.getColor());
                //gives  cards to each agent, and wild cards to everyone, except the first agent
           
                Collections.swap(d, 5, (d.size() - 2));
                Collections.swap(d, 8, (d.size() - 3));
                Collections.swap(d, 11, (d.size() - 4));
                Collections.swap(d, 14, (d.size() - 5));
                Collections.swap(d, 17, (d.size() - 6));


        }

    }

    public Card[][] deal(int howmanyagents, int howmanycardstoagent) {
        Card[][] arrayofHands = new Card[howmanyagents][howmanycardstoagent];

        for (int m = 0; m < howmanyagents; m++) {
            for (int n = 0; n < howmanycardstoagent; n++) {

                arrayofHands[m][n] = (Card) Deck.get(CardTicker);
                CardTicker++;


            }



        }
        return arrayofHands;
    }

    public static ArrayList getAllowed(ArrayList hand, Card current) {
        ArrayList allowed = new ArrayList();
        Card c;


        for (int i = 0; i < hand.size(); i++) {
            c = (Card) hand.get(i);
            if (c.getColor().equals(current.getColor())) {
                allowed.add(c);
            } else if (c.getType() == current.getType()) {
                allowed.add(c);
            }

        }

        if (allowed.isEmpty() == true) {
            for (int i = 0; i < hand.size(); i++) {
                c = (Card) hand.get(i);
                if (c.getColor().equals("w")) {
                    allowed.add(c);
                }

            }
        }


        return allowed;
    }

    public static Card drawCard() {
        Card c;

        if (CardTicker < Deck.size()) {
            c = (Card) Deck.get(CardTicker);
            CardTicker++;
        } else {
            reshuffle();
            c = (Card) Deck.get(CardTicker);
            CardTicker++;
        }
 
        return c;

    }

    public static void reshuffle() {

        Collections.shuffle(PlayedCards);


        Deck.clear();
        Deck.addAll(PlayedCards);

        PlayedCards.clear();
        CardTicker = 1;
        System.out.println("RESHUFFLE DONE!");
    }

    public static void changeDirection() {
        if (Clockwise == true) {
            Clockwise = false;
        } else {
            Clockwise = true;
        }

    }

    public static boolean getDirection() {
        return Clockwise;
    }
}
