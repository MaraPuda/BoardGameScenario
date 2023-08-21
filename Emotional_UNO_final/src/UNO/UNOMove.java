package UNO;

import emotional_UNO.Intention;
import jade.content.ContentElementList;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mara Pudane
 * Class implements the UnoMove once the agent has made decision on what it will be.
 */
public class UNOMove extends OneShotBehaviour {

    private Codec Codec;
    private Ontology Ontology;
    private List<emotional_UNO.Intention> IntentionList;
    private emotional_UNO.Intention Intention;

    public UNOMove(Agent a, Codec codec, Ontology ontolog, List<emotional_UNO.Intention> i) {
        Codec = codec;
        Ontology = ontolog;
        IntentionList = i;
        if (IntentionList.isEmpty())
        { Intention = new Intention("", (Object) "");
            Intention.Type = "NoMove";}
                    else
        {Intention = IntentionList.get(0);}
        
    }

    @Override
    public void action() {
        boolean uno = false;
       
        for (ListIterator<Intention> iter = IntentionList.listIterator(); iter.hasNext();) {
            Intention i = iter.next();
            if ("SayUno".equals(i.Type)) {
                uno = true;
            }
        }
        Object args[] = myAgent.getArguments();
        UNO.UNOargs ua = (UNO.UNOargs) args[8];
        ArrayList inc = (ArrayList) args[3];
        ArrayList prevHand = ua.Hand;
        String rec;


        if (GamePlay.getDirection() == true) {
            rec = ua.PlayOnRight;

        } else {
            rec = ua.PlayOnLeft;
        }
        Card myMove = new Card();


        switch (Intention.Type) {
            case "Pick": //object is int



                for (int i = 0; i < (int) Intention.Object; i++) {
                    ua.Hand.add(UNO.GamePlay.drawCard());
                }

                if ((int) Intention.Object == 2) {
                    ua.Current.setType(16);
                } else {
                    ua.Current.setType(17);
                }

                myMove = ua.Current;

                break;
            case "NoMove":
               
                myMove = ua.Current;

                break;
            case "Pass":
                ua.Current.setType(15);
                myMove = ua.Current;

                break;
            case "Play":
                Card cplay = (Card) Intention.Object;
          
                ua.Current = cplay;
                ua.Hand.remove(cplay);
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);
                //setcurrent
                break;
            case "PlayChColor":
                Card cchc = (Card) Intention.Object;
                ua.Hand.remove(cchc);
                ua.Current.setColor(decideColour(ua.Hand));
                ua.Current.setType(cchc.getType());
                ua.Current.setPoints(50);
                ua.Current.setNumber(cchc.getNumber());
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);
                break;
            case "PlayChDirection":
                Card chdr = (Card) Intention.Object;

                ua.Hand.remove(chdr);
                GamePlay.changeDirection();

                if (GamePlay.getDirection() == true) {
                    rec = ua.PlayOnRight;
                } else {
                    rec = ua.PlayOnLeft;
                }
         
                ua.Current = chdr;
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);
                break;
            case "PlayPass":
                Card cpass = (Card) Intention.Object;
                ua.Hand.remove(cpass);
              
                ua.Current = cpass;
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);
                break;

            case "PlayPlus2":
                Card cpp2 = (Card) Intention.Object;
                ua.Hand.remove(cpp2);
                
                ua.Current = cpp2;
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);



                break;
            case "PlayPlus4":
                Card cpp4 = (Card) Intention.Object;
                ua.Hand.remove(cpp4);
                ua.Current.setColor(decideColour(ua.Hand));
                ua.Current.setType(cpp4.getType());
                ua.Current.setNumber(cpp4.getNumber());
                ua.Current.setPoints(50);
                myMove = ua.Current;
                GamePlay.PlayedCards.add(myMove);

                break;


        }

  
        ua.Turn = rec;
        sendMoveDoneMessage(myMove, 3, rec);

        UNO.UNOboard.clearHand(myAgent.getLocalName(), ua.Hand);
        UNO.UNOboard.outputCards(myAgent.getLocalName(), ua.Hand);


        UNO.UNOboard.displayCurrentCard(myMove, myAgent.getLocalName());

        if (ua.Hand.size() == 1 && uno == true) {
            sendYellUNO();

        }

        if (ua.Hand.isEmpty()) {
            sendWin();
        }

        args[8] = ua;
        myAgent.setArguments(args);



    }

    private void sendMoveDoneMessage(Card c, int Type, String next) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        msg.setLanguage(Codec.getName());
        msg.setOntology(Ontology.getName());

        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ContentElementList cel = new ContentElementList();

        gm.setCard(c);
        gm.setMesType(Type);
        gm.setMove(next);
        gm.setEmoStrength(0.0);
        gm.setEmoType(" ");

        cel.add(gm);




     
        try {
            myAgent.getContentManager().fillContent(msg, cel);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(UNOMove.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addReceiver(new AID("GUI", AID.ISLOCALNAME));
        //System.out.println("My name is " + myAgent.getLocalName()+ " and I am sending UNO move message!");
        myAgent.send(msg);


    }

    private String decideColour(ArrayList hand) {
        String colour = "b";
        Card c;
        int b = 0;
        int r = 0;
        int g = 0;
        int y = 0;
        int[] counts = {0, 0, 0, 0};
        int max = 0;
        int index = 0;
        //cikls, kas saskaita, cik dažādu kāršu ir un izvēlas, kādu wild likt  
        for (int i = 0; i < hand.size(); i++) {
            c = (Card) hand.get(i);
            switch (c.getColor()) {
                case "b":
                    b++;
                    break;
                case "r":
                    r++;
                    break;
                case "g":
                    g++;
                    break;
                case "Y":
                    y++;
                    break;
            }

        }
        counts[0] = b;
        counts[1] = r;
        counts[2] = g;
        counts[3] = y;

        for (int j = 0; j < counts.length; j++) {
            if (counts[j] > max) {
                max = counts[j];
                index = j;
            }
        }
        switch (index) {
            case 0:
                colour = "b";
                break;
            case 1:
                colour = "r";
                break;
            case 2:
                colour = "g";
                break;
            case 3:
                colour = "y";
                break;
        }

        return colour;

    }

    private void sendYellUNO() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        msg.setLanguage(Codec.getName());
        msg.setOntology(Ontology.getName());

        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ContentElementList cel = new ContentElementList();
        Card c = new Card();
        c.setColor("Unknown");
        c.setType(20);
        int Type = 4;
        gm.setCard(c);
        gm.setMesType(Type);
        gm.setMove("None");
        gm.setEmoStrength(0.0);
        gm.setEmoType(" ");

        cel.add(gm);




        String[] agentNames = {"Ana", "Gita", "Greg", "Robert", "Alex", "Maria"};
        for (int i = 0; i < agentNames.length; i++) {
                 msg.addReceiver(new AID(agentNames[i], AID.ISLOCALNAME));
         
        }

        try {
            myAgent.getContentManager().fillContent(msg, cel);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(UNOMove.class.getName()).log(Level.SEVERE, null, ex);
        }

        myAgent.send(msg);


    }

    private void sendWin() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        msg.setLanguage(Codec.getName());
        msg.setOntology(Ontology.getName());

        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ContentElementList cel = new ContentElementList();
        Card c = new Card();
        c.setColor("Unknown");
        c.setType(20);
        int Type = 5;
        gm.setCard(c);
        gm.setMesType(Type);
        gm.setMove("None");
        gm.setEmoStrength(0.0);
        gm.setEmoType(" ");
        cel.add(gm);




        AMSAgentDescription[] agents = null;

        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(new Long(-1));
        try {
            agents = AMSService.search(myAgent, new AMSAgentDescription(), sc);
        } catch (FIPAException ex) {
            Logger.getLogger(UNOMove.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < agents.length; i++) {
            msg.addReceiver(agents[i].getName());
            //System.out.println(agents[i].getName());
        }

        try {
            myAgent.getContentManager().fillContent(msg, cel);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(UNOMove.class.getName()).log(Level.SEVERE, null, ex);
        }

        myAgent.send(msg);
    }

   
   
}
