package emotional_UNO;

import UNO.Card;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Mara Pudane
 */
public class Evaluation {
     public Incoming evaluateCards(Agent my, int type, List<UNO.Card> hand, List<UNO.Card> prevHand) {
           double affect = 0;
           int des = getDesire(my);
           Incoming inc = new Incoming(false, false, 0, ' ', false, false);

           switch(des)
           {
               case 0:
                  
                 inc = evaluateNC(my, type, hand, prevHand);
              
                break;
                   
               case 1:
                   inc  = evaluateNC(my, type, hand, prevHand);
                   break;
               case 2:
                   //evaluate
                   break;
           }
           return inc;
       }
        private Incoming evaluateNC(Agent my, int type, List<Card> hand, List<Card> prevHand) {
            Incoming inc = new Incoming(false, false, 0, ' ', false, false);
        switch (type)
        {
            case 0: //FEAR - reaction to possible moves, if the probability is over 50, the fear is directly proportional 0..1
                double fearvalue = 0.0;
                double badProb = deckComparison(my, hand);
                if (badProb >0.5)
                {
                    fearvalue = (badProb-0.5)*2;
                }          
                
                inc.EmotionType = 'f';
                inc.Rational = false;
                inc.Strength = fearvalue;
                inc.IsNew = true;
            
              break;
            case 1: 
                //ANGER/JOY
                double emovalue = 0.0;
                int difference = 0;
                difference = hand.size()-prevHand.size();
                if (hand.isEmpty())
                {emovalue = -1;}
                else
                {
                if (difference > 0) {
                    emovalue = (double) difference / hand.size();
                } else if (difference < 0) {
                  
                    emovalue = (double) difference / prevHand.size();
                }
                }
                if  (emovalue>0)
                        inc.EmotionType = 'a';
                        else
                {
                        inc.EmotionType = 'j';
                        emovalue = emovalue*(-1);
                }
                inc.Rational = false;
                inc.Strength = emovalue;
                inc.IsNew = true;
                break;
                
            case 2: //DISGUST- 
              int[] PlayedNo = new int[UNO.GamePlay.PlayedCards.size()];
                     
             if (hand.size()>prevHand.size())
             {
             
             }
             
         }
        return inc;
    }
       private int getDesire(Agent my)
       {
       Object args[] = my.getArguments();
       BelDesInt bdi = (BelDesInt) args[5];
       int des = bdi.Desire;       
       return des;
       }
       
       private double deckComparison(Agent my, List<UNO.Card> hand) {
        List<UNO.Card> temp = new ArrayList();
        List<UNO.Card> tempPlayed = new ArrayList();
        List<UNO.Card> tempDeck = new ArrayList();
        tempPlayed.addAll(UNO.GamePlay.PlayedCards);
        tempDeck.addAll(UNO.GamePlay.JustADeck);
       // System.out.println (UNO.GamePlay.PlayedCards.size());
          //printCards(hand, "Ana");
        int[] PlayedNo = new int[UNO.GamePlay.PlayedCards.size()];
        int[] AllNo = new int[UNO.GamePlay.JustADeck.size()];
        int i = 0;
        int bad = 0;
       
           for (int j = 0; j < AllNo.length; j++) {
               AllNo[j] = j;
           }

           for (ListIterator<UNO.Card> iterx2 = tempPlayed.listIterator(); iterx2.hasNext();) {
               Card c2 = iterx2.next();

               PlayedNo[i] = c2.getNumber();
               i++;
           }
           int count = 0;

           for (int k = 0; k < AllNo.length; k++) {
               for (int l = 0; l < PlayedNo.length; l++) {
                   if (AllNo[k] == PlayedNo[l]) {
                       AllNo[k] = 300;
                       count = count + 1;
                   }

               }

           }
           
           for (int k = 0; k < AllNo.length; k++) {
               for (ListIterator<UNO.Card> iterx2 = tempDeck.listIterator(); iterx2.hasNext();) {
                   Card c2 = iterx2.next();
                   if (AllNo[k] == c2.getNumber()) {
                       temp.add(c2);
                   }

               }
           }
  
        int good = 0;
    
      
        for (ListIterator<UNO.Card> iter = temp.listIterator(); iter.hasNext();) {
                   Card c = iter.next();
                   if (c.getType() == 10 || c.getType() == 11 || c.getType() == 12 || c.getType() == 14) {
                       bad = bad + 1;
                       iter.remove();
                   }
        }
        
           for (ListIterator<UNO.Card> iter2 = hand.listIterator(); iter2.hasNext();) {
               Card c2 = iter2.next();

               for (ListIterator<UNO.Card> iter3 = temp.listIterator(); iter3.hasNext();) {
                   Card c3 = iter3.next();
                  

                       if (c3.getColor().equals(c2.getColor()) || c3.getType() == c2.getType()) {
                           good = good + 1;
                            iter3.remove();
                       }
                   }

               }
         
        bad = bad+temp.size();
      
        int left = UNO.GamePlay.JustADeck.size() - UNO.GamePlay.PlayedCards.size();
        double badProp = (double) bad/left;  
        
        
        return badProp;
        }
}
