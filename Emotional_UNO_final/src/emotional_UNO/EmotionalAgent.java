package emotional_UNO;

import UNO.Card;
import UNO.GamePlay;
import UNO.ontology.UNOOntology;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mara Pudane
 * Contains the agent architecture.
 */
public class EmotionalAgent extends Agent {

    public ArrayList EmotionPAD;
    public ArrayList MoodPAD;
    public ArrayList ActionsDone;
    public ArrayList SBDIList;
    private Codec codec = new SLCodec();
    private Ontology ontology = UNOOntology.getInstance();


    protected void setup() //agent initialization
    {
        Object args[] = getArguments();
        System.out.println(getLocalName() + " is in da house.");

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        Personality p = (Personality) args[2];
        this.addBehaviour(new InterpretInput());
        this.addBehaviour(new LaunchBehaviours());
        this.addBehaviour(new EmotionCycle(this, 1000));
        //needed for data output, l
        this.EmotionPAD = new ArrayList(); 
        this.MoodPAD = new ArrayList();
        this.ActionsDone = new ArrayList();
        this.SBDIList = new ArrayList();
       
        addPADIntensity(p.pers, 0);
        addPADMoodIntensity(p.pers);
    }

    protected void takeDown() {
        //mood, emotion, action done change output, ... can be replaced with path
        //in the thesis, these outputs were used to analyze data
        try {
            PrintWriter writer = new PrintWriter("..." + getLocalName() + "_moodPAD.csv", "UTF-8");
            PAD pad;

            for (int i = 0; i < MoodPAD.toArray().length; i++) {
                double writeP = 0.0;
                double writeA = 0.0;
                double writeD = 0.0;
                pad = (PAD) MoodPAD.get(i);

                if (pad != null) {
                    writeP = pad.P;
                    writeA = pad.A;
                    writeD = pad.D;
                    writer.println(this.getLocalName() + "," + writeP + "," + writeA + "," + writeD);
                }
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PrintWriter writer = new PrintWriter("..." + getLocalName() + "_corePAD.csv", "UTF-8");
            Object args[] = this.getArguments();
            Personality p = (Personality) args[2];
            writer.println(this.getLocalName() + "," + p.pers.P + "," + p.pers.A + "," + p.pers.D);
           writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PrintWriter writer = new PrintWriter("..." + getLocalName() + "_emotionPAD.csv", "UTF-8");
            outputListElement ole;

            for (int i = 0; i < EmotionPAD.toArray().length; i++) {
                double writeP = 0.0;
                double writeA = 0.0;
                double writeD = 0.0;
                int writesource = 0;
                int t;

                ole = (outputListElement) EmotionPAD.get(i);

                if (ole != null) {
                    writeP = ole.P;
                    writeA = ole.A;
                    writeD = ole.D;
                    writesource = ole.source;
                    t = TimeSync.getDifference(ole.t);
                   writer.println(this.getLocalName() + "," + writeP + "," + writeA + "," + writeD + "," + writesource + "," + t);
                }
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);

        }

       
        try {
            PrintWriter writer = new PrintWriter("..." + getLocalName() + "_socbeliefs.csv", "UTF-8");
            SocialBelief sb;
            for (int i = 0; i < SBDIList.toArray().length; i++) {
                sb = (SocialBelief) SBDIList.get(i);
                String writesub = (String) sb.Subject;
                double writepnum = sb.Number;
                int writet = sb.Type;
                //Object writeObject; //kas būs objekts???
                //actdone = (Intention) ;

                if (sb != null) {

                    
                    writer.println(this.getLocalName() + "," + writesub + "," + writepnum + "," + writet);
                }
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    private class LaunchBehaviours extends CyclicBehaviour {

        @Override
        public void action() {

            Object args[] = myAgent.getArguments();
            List<Incoming> inc = (List<Incoming>) args[3];
            Control cnt = (Control) args[0];

            if (inc.isEmpty() == false) {
                if ((inc.get(0).IsNew == true && cnt.BehCycleDone == true) || (inc.get(0).IsNew == false && cnt.BehCycleDone == true && inc.get(0).Rational == false)) {
                    cnt.BehCycleDone = false;
                    myAgent.setArguments(args);
                    myAgent.addBehaviour(new PrimitiveReasoning()); //adds primitive reasoning as beghaviour
                } else {
                    block(500);
                }

            }

        }
    }

    private class InterpretInput extends CyclicBehaviour {

        @Override
        public void action() {

            ACLMessage msg = myAgent.receive(); //agent listens for messages
            if (msg != null) { //if message is received, tries to read it
                try {
                  ContentManager cm = myAgent.getContentManager();
                    ContentElement ce = (ContentElement) cm.extractContent(msg);
                    Object args[] = myAgent.getArguments();
                    UNO.UNOargs ua = (UNO.UNOargs) args[8];
                    Personality p = (Personality) args[2];
                    Control cnt = (Control) args[0];
                    SocBelDesInt sbdi = (SocBelDesInt) args[7];
                    UNO.ontology.GeneralMessage gm = (UNO.ontology.GeneralMessage) ce;
                    List<Incoming> inc = (List<Incoming>) args[3];

                    CurrentStateDescription csd = (CurrentStateDescription) args[4];

                    int t = gm.getMesType();
                    cycleOff();
                  

                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    ContentElementList cel = new ContentElementList();
                    UNO.ontology.GeneralMessage gm1 = new UNO.ontology.GeneralMessage();

                    switch (t) { //agent distinguishes among various emotion types here
                        case 0: //emotional message received
                            String s = gm.getEmoType();
                            double susc = susceptibility(gm.getEmoStrength(), p.N, p.E, s.charAt(0));

                            if (gm.getEmoStrength() > susc) {
                                
                               //uncomment if primary layer enabled
                                /*
                                 Incoming n = new Incoming(false, false, 0, '', false, false);
                                 n.IsNew = true; n.Strength =
                                  gm.getEmoStrength();
                                 n.HoldEmotion = s.charAt(0);
                                  if (n.HoldEmotion != 'j') { n.Tag = false; n.Strength = n.Strength * -1; } else { n.Tag = true; }
                            inc.add(n);
                                 */ 
                                  

                                Incoming n2 = new Incoming(false, false, 0, ' ', false, false);
                                n2.IsNew = true;
                                n2.Strength = gm.getEmoStrength();
                                n2.EmotionType = s.charAt(0);

                                inc.add(n2);
                                
                                //uncomment if only tertiary layer must be checked
                                /*
                                 Incoming n3 = new Incoming(false, false, 0, '', false, false); n3.IsNew = true;
                                 n3.Strength = gm.getEmoStrength();
                                 n3.EmotionType = s.charAt(0); n3.Tertiary = true; 
                                 n3.Who = msg.getSender().getLocalName();
                                 inc.add(n3);
                                 */

                                args[4] = csd;
                                args[3] = inc;
                                myAgent.setArguments(args);


                            }
                            break;
                        case 1: //direct emotion communication and manipulation - after the answer is received
                            System.out.println(gm.getMove());

                            if ("None".equals(gm.getMove())) {
                                
                               sbdi.BeliefSet.add(new SocialBelief(msg.getSender().getLocalName(),13, 1)); 
                               Incoming n4 = new Incoming(false, true,gm.getEmoStrength(), (char) gm.getEmoType().charAt(0), false, false);
                               inc.add(n4); 
                                 
                            } else {


                                if ("Yes".equals(gm.getMove())) {
                                    sbdi.BeliefSet.add(new SocialBelief(msg.getSender().getLocalName(), 12, 1));
                                } else { 
                                    sbdi.BeliefSet.add(new SocialBelief(msg.getSender().getLocalName(), 12, 0));  
                                 
                                }

                                Incoming n4 = new Incoming(true, true, 9999, ' ', true, false);
                                inc.add(n4);

                            }
                            args[7] = sbdi;
                            args[3] = inc;
                            myAgent.setArguments(args);
                            break;

                        case 2: //dealing the cards
                            BelDesInt bdi = (BelDesInt) args[5];
                            UNO.Card c = gm.getCard();
                            Belief b = new Belief("Have", c);
                            bdi.BeliefSet.add(b);
                            ua.Hand.add((UNO.Card) c);
                            args[8] = ua;
                            myAgent.setArguments(args);

                            reply.addReceiver(msg.getSender());
                            reply.setLanguage(codec.getName());
                            reply.setOntology(ontology.getName());

                            gm1.setCard(c);
                            gm1.setMove("a");
                            gm1.setMesType(2);
                            gm1.setEmoType("a");
                            gm1.setEmoStrength(0.0);
                            cel.add(gm1);
                            try {
                                getContentManager().fillContent(reply, cel);

                            } catch (Codec.CodecException | OntologyException ex) {
                                Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            send(reply);

                            break;



                        case 3: //the move has been made

                            UNO.Card cc = gm.getCard();

                            if (gm.getMove().equals(myAgent.getLocalName())) {
                                String rec = ua.PlayOnRight;
                                if (msg.getSender().getLocalName().equals(ua.PlayOnRight)) {
                                    rec = ua.PlayOnLeft;
                                } else {
                                    rec = ua.PlayOnRight;
                                }
                                
                                if (gm.getCard().getType() != 20) {
                                    ua.Current = gm.getCard();
                                }
                                sendMessage(rec, 6, ua.Current, rec + "next", " ", 0.0); //youre next for fear


                            }
                            
                            // BelDesInt bdi = (BelDesInt) args[5];
                            //bdi.BeliefUpdate(msg.getSender().getLocalName(), cc);



                            ua.Turn = gm.getMove();
                            args[8] = ua;
                            // args[5] = bdi;
                            myAgent.setArguments(args);
                            if (gm.getMove().equals(myAgent.getLocalName())) {
                                Incoming n4 = new Incoming(true, true, 9999, ' ', false, false);
                                inc.add(n4);
                           }



                            cycleOn();
                            break;
                        case 4:
                            BelDesInt bdi1 = (BelDesInt) args[5];
                            if (!msg.getSender().getLocalName().equals(myAgent.getLocalName())) {
                                Belief b1 = new Belief("Uno", msg.getSender().getLocalName());
                                bdi1.BeliefSet.add(b1);
                                myAgent.setArguments(args);
                            }
                            printBeliefs(bdi1.BeliefSet, "Gita");


                            break;
                        case 5:
                            
                            //Incoming n6 = new Incoming(true, true, 0, ' ', false, false);
                            //inc.add(n6);
                            BelDesInt bdi2 = (BelDesInt) args[5];

                            Belief b2 = new Belief("None", msg.getSender().getLocalName());
                            bdi2.BeliefSet.add(b2);
                            myAgent.setArguments(args);
                            myAgent.doDelete();
                            break;

                        case 6: //starting direct expression
                            cycleOff();
                            String rec;
                            int have = 0;

                            if (gm.getMove().equals(myAgent.getLocalName() + "next")) {

                                  if (!"Ana".equals(myAgent.getLocalName())) {
                                    Evaluation e = new Evaluation();
                                    Incoming i = e.evaluateCards(myAgent, 0, ua.Hand, ua.Hand);
                                    inc.add(i);

                                }
                           if (ua.Hand.size() <= 3) {//if the hand size corresponds      //and no wild cards
                                    for (ListIterator<UNO.Card> iter = ua.Hand.listIterator(); iter.hasNext();) {

                                        UNO.Card wc = new UNO.Card();
                                        wc = iter.next();
                                        if ("w".equals(wc.getColor())) {

                                            have = 1;

                                        }

                                    }
                                }
                                double emovalue;
                                if (have == 0) {
                                    if (GamePlay.getDirection() == true) {
                                        rec = ua.PlayOnRight;
                                    } else {
                                        rec = ua.PlayOnLeft;
                                    }
                              
                                            
                                    emotionalMessage(rec, csd.currentState.D);
                                }


                            }
                            args[3] = inc;
                            myAgent.setArguments(args);

                            break;


                    }


                    cycleOn();


                } catch (Codec.CodecException | OntologyException ex) {
                    System.out.println("Something not working!");
                    Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);

                }


            } else {


                block();
            }
        }
    }

    private class PrimitiveReasoning extends Behaviour {

        private boolean finished = false;
        int state = 1;

        public void action() {


            if (getCritical() == false) {
                Object args[] = myAgent.getArguments();
                switch (state) {
                    case 1:
                        PrimitiveEmotions b = new PrimitiveEmotions();
                        myAgent.addBehaviour(b);
                        finished = false;
                        state = state + 1;

                        break;
                    case 2:

                        CurrentStateDescription csd = (CurrentStateDescription) args[4];
                        showClosestEmotion(csd.currentState);
                        SecondaryReasoning sr = new SecondaryReasoning();
                        myAgent.addBehaviour(sr);
                        state = state + 1;
                        finished = true;
                        break;
                }
            } else {
                block();
            }

        }

        @Override
        public boolean done() {

            return finished;
        }
    }

    private class PrimitiveEmotions extends Behaviour {

        private boolean finished = false;

        public void action() {
            if (getCritical() == false) {
                cycleOff();
              
                Object args[] = myAgent.getArguments();
                CurrentStateDescription csd = (CurrentStateDescription) args[4];
                double affect = primaryObjectiveEvaluation();
                if (affect != 0) {

                    nullAllTheEmotions(csd.emoVect);
                    args[4] = csd;
                    myAgent.setArguments(args);
                    calculateSubjectiveEmotion(affect); //ietver arī integrāciju PAD telpā un parametru iestatīšanu
                }
                if ("Gita".equals(myAgent.getLocalName())) {
                    cycleOn();
                }
                finished = true;

            } else {
                block();
            }
        }

        @Override
        public boolean done() {

            return finished;
        }

        private EmotionVector[] nullAllTheEmotions(EmotionVector[] ev) {
            for (int i = 0; i < ev.length; i++) {
                ev[i].Last = false;
            }
            return ev;
        }
    }

    private class SecondaryReasoning extends Behaviour {

        private boolean finished = false;
        int state = 1;

        public void action() {

            if (getCritical() == false) {

                switch (state) {
                    case 1:

                        SecondaryEmotions b = new SecondaryEmotions();
                        myAgent.addBehaviour(b);
                        finished = false;
                        state = state + 1;

                        break;
                    case 2:

                        Object args[] = myAgent.getArguments();
                        UNO.UNOargs ua = (UNO.UNOargs) args[8];

                        if (ua.Turn.equals(myAgent.getLocalName())) {



                            BelDesInt bdi = (BelDesInt) args[5];
                            CurrentStateDescription csd = (CurrentStateDescription) args[4];
                            int p = getProfile(csd.currentState);
                          
                            bdi.BeliefSet = updateBeliefs(bdi.BeliefSet, ua);

                            bdi.Desire = generateOptions(bdi.IntentionSet, bdi.BeliefSet, p, ua);

                            List<Intention> i = filter(bdi.IntentionSet, bdi.BeliefSet, bdi.Desire, p);

                            for (ListIterator<Intention> iter = i.listIterator(); iter.hasNext();) {
                                Intention iwr = iter.next();
                                addIntention(iwr, p); //output 
                            }

                            if (!i.isEmpty()) {
                                emotionalAnswer(i.get(0).Type);
                            }
                            
                           
                            UNO.UNOMove um = new UNO.UNOMove(myAgent, codec, ontology, i);
                            myAgent.addBehaviour(um);
                            do {
                            } while (um.done() == false);

                        }

                        TertiaryReasoning b1 = new TertiaryReasoning();
                        myAgent.addBehaviour(b1);
                        state = state + 1;
                        finished = true;
                        break;
                }

            } else {
                block();
            }
        }

        @Override
        public boolean done() {

            return finished;
        }

        private List<Belief> updateBeliefs(List<Belief> bels, UNO.UNOargs ua) {

            for (ListIterator<Belief> iter = bels.listIterator(); iter.hasNext();) {
                Belief b = iter.next();
                if ("Have".equals(b.Type) || "Have2".equals(b.Type)) {
                    iter.remove();
                }
                if ("Current".equals(b.Type)) {
                    iter.remove();
                }
            }

            for (ListIterator<Card> iter = ua.Hand.listIterator(); iter.hasNext();) {
                Card c = iter.next();
                Belief b1 = new Belief("Have", c);
                bels.add(b1);
            }
            Belief b2 = new Belief("Current", ua.Current);
            bels.add(b2);

            return bels;
        }

        private int generateOptions(List<Intention> ints, List<Belief> bels, double pm, UNO.UNOargs ua) {
            int des = 0; //NC
            boolean f = false;
            for (ListIterator<Belief> iter = bels.listIterator(); iter.hasNext();) {
                Belief b = iter.next();

                if ("None".equals(b.Type)) {
                    des = 2;
                    break;
                }
                if ("Uno".equals(b.Type) & f == false) {
                    if ((UNO.GamePlay.Clockwise == true & ua.PlayOnRight.equals(b.Object.toString())) || (UNO.GamePlay.Clockwise == false & ua.PlayOnLeft.equals(b.Object.toString()))) {
                        des = 1;
                        f = true; //NAW
                    } else {
                        des = 2; //NP
                    }
                    

                }
                if ("Uno".equals(b.Type)) {
                    iter.remove();
                }


            }
  
            return des;
        }

        private List<Intention> filter(List<Intention> ints, List<Belief> bels, int Desire, int Profile) {

            ints = updateIntentions(ints, bels);
            boolean skip = false;
            List<Intention> i = new ArrayList<>();
            for (ListIterator<Intention> iter = ints.listIterator(); iter.hasNext();) {
                Intention i1 = iter.next();
                if ("Pick".equals(i1.Type) || "Pass".equals(i1.Type)) {
                    skip = true;
                    i.add(i1);

                }
            }




            Intention inu = new Intention("SayUno", null);

            if (skip == false) {
                switch (Desire) {
                    case 0: //here the behaviours based on octants are chosen

                        switch (Profile) {
                            case 1:
                                //pick normal first
                                i.add(selectCardMinPts(ints));
                                //add no uno action

                                break;
                            case 2:
                                //pick normal first

                                i.add(selectCardMinPts(ints));

                                //normal cards
                                break;
                            case 3:
                                //pick normal first 
                                i.add(selectCardMaxPts(ints));

                                //i.add(selectCardMinPts(ints));
                                //add ability to sayUNO
                                i.add(inu);
                                
                                break;
                            case 4:
                                //pick normal first
                                i.add(selectCardMaxPts(ints));
                               //add ability to sayUNO
                                i.add(inu);
                                break;
                            case 5:

                                //difficult cards 
                                i.add(selectCardMinPts(ints));

                                break;
                            case 6:
                                //difficult cards
                                i.add(selectCardMinPts(ints));
                                break;
                            case 7:
                                //difficult cards
                                i.add(selectCardMinPts(ints));
                                //add ability to sayUNO
                                i.add(inu);
                               
                                break;
                            case 8:
                                //difficult cards
                                i.add(selectCardMinPts(ints));
                                //add ability to sayUNO
                                i.add(inu);
                                break;

                        }


                        break;
                     //can be uncommented to unlock more desires if necessary
                    /*
                     * case 1://here also comes in catching if UNO said 
                     * int maxp= 0; 
                     * //i = ints.get(0); 
                     * for (ListIterator<Intention> iter = ints.listIterator(); iter.hasNext(); ) 
                     * { Intention i1 = iter.next(); 
                     * try {
                     * UNO.Card c = (UNO.Card) i1.Object;
                     * //System.out.println(c.getPoints()); 
                     * if(c.getPoints()>=maxp) 
                     * { maxp = c.getPoints(); 
                     * i.add(i1);
                     * //System.out.println("try"); //System.out.println(maxp);
                     * } } catch (Exception e) { //System.out.println("catch");
                     * i.add(ints.get(0)); 
                     * }
                     *
                     * }
                     * break; 
                     * 
                     * case 2: 
                     * int maxp1 = 0; //i = ints.get(0); 
                     * for
                     * (ListIterator<Intention> iter = ints.listIterator();
                     * iter.hasNext(); ) 
                     * { Intention i1 = iter.next();
                     * //System.out.println("times"); 
                     * try {
                     * //System.out.println(i1.Type); 
                     * UNO.Card c = (UNO.Card)
                     * i1.Object; //System.out.println(c.getPoints()); if
                     * (c.getPoints()>=maxp1) 
                     * { maxp1 = c.getPoints();
                     * i.add(i1); //System.out.println("try");
                     * //System.out.println(maxp1); } } 
                     * catch (Exception e) {
                     * i.add(ints.get(0));
                     *
                     * }
                     *
                     * }
                     * break;
                     */

                }
            }

            
            return i;
        }

        private Intention selectCardMaxPts(List<Intention> ints) {
            int maxp1 = 0;
            int pts = 0;
            Intention i = new Intention("Nothing", 0);
            for (ListIterator<Intention> iter = ints.listIterator(); iter.hasNext();) {
                Intention i1 = iter.next();
                try {
                    UNO.Card c = (UNO.Card) i1.Object;
                    if ("w".equals(c.getColor())) {
                        pts = 50;
                    } else if (c.getType() == 10 || c.getType() == 11 || c.getType() == 12) {
                        pts = 20;
                    } else {
                        pts = c.getType();
                    }



                    if (pts >= maxp1) {
                        maxp1 = pts;
                        i = i1;
                    }


                } catch (Exception e) {
                    i = ints.get(0);

                }
            }
            return i;
        }

        private Intention selectCardMinPts(List<Intention> ints) {
            int minp1 = 100;
            int pts = 0;
            Intention i = new Intention("Nothing", 0);
            for (ListIterator<Intention> iter = ints.listIterator(); iter.hasNext();) {
                Intention i1 = iter.next();
                try {
                    UNO.Card c = (UNO.Card) i1.Object;
                    if ("w".equals(c.getColor())) {
                        pts = 50;
                    } else if (c.getType() == 10 || c.getType() == 11 || c.getType() == 12) {
                        pts = 20;
                    } else {
                        pts = c.getType();
                    }

                    if (pts <= minp1) {
                        minp1 = pts;
                        i = i1;
                    }
                } catch (Exception e) {
                    i = ints.get(0);

                }
            }
            ints.add(i);

            return i;
        }

        private List<Intention> updateIntentions(List<Intention> ints, List<Belief> bels) {
            UNO.Card c = new UNO.Card();
            boolean noHandDeliberation = false; //true if agent has no choice
            boolean picked = false; //true if agent has picked the card once
            for (ListIterator<Belief> iter = bels.listIterator(); iter.hasNext();) {
                Belief b = iter.next();
                if ("Current".equals(b.Type)) {
                    UNO.Card belc = (UNO.Card) b.Object;
                    c.setNumber(belc.getNumber());
                    c.setColor(belc.getColor());
                    c.setType(belc.getType());

                }

            }
            for (ListIterator<Intention> iter = ints.listIterator(); iter.hasNext();) {
                Intention i = iter.next();
                if ("Pick".equals(i.Type) || "Pass".equals(i.Type) || "Play".equals(i.Type) || "PlayPass".equals(i.Type) || "PlayChDirection".equals(i.Type) || "PlayPlus2".equals(i.Type) || "PlayPlus4".equals(i.Type) || "PlayChColor".equals(i.Type) || "NoMove".equals(i.Type)) {
                    iter.remove();
                }

            }

            if (c.getType() == 11) {
                Intention i = new Intention("Pass", c);
                ints.add(i);
                noHandDeliberation = true;
            }
            if (c.getType() == 12) {
                Intention i = new Intention("Pick", 2);
                ints.add(i);
                noHandDeliberation = true;
            }
            if (c.getType() == 14) {
                Intention i = new Intention("Pick", 4);
                ints.add(i);
                noHandDeliberation = true;
            }
            if (noHandDeliberation == false) {
                for (ListIterator<Belief> iter = bels.listIterator(); iter.hasNext();) {
                    Belief b = iter.next();
                    if ("class UNO.Card".equals(b.Object.getClass().toString())) {
                        UNO.Card belc = (UNO.Card) b.Object;
                        String s = getIntType(belc);
                        if ("Have".equals(b.Type) || "Have2".equals(b.Type)) {
                            if (c.getColor().equals(belc.getColor())) {
                                Intention i = new Intention(s, (Card) b.Object);
                                ints.add(i);

                            }
                            if (c.getType() == belc.getType()) {
                                Intention i = new Intention(s, (Card) b.Object);
                                ints.add(i);
                            }

                            if ("w".equals(belc.getColor())) {
                                Intention i = new Intention(s, (Card) b.Object);
                                ints.add(i);
                            }
                            if (belc.getType() == 11 && c.getType() == 15) {
                                Intention i = new Intention(s, (Card) b.Object);
                                ints.add(i);
                            }
                            if (belc.getType() == 12 && c.getType() == 16) {
                                Intention i = new Intention(s, (Card) b.Object);
                                ints.add(i);
                            }
                        }
                    }
                }

            }
            for (ListIterator<Belief> iter = bels.listIterator(); iter.hasNext();) {
                Belief b = iter.next();
                if ("Have2".equals(b.Type)) {
                    picked = true;
                }
            }



            if (ints.isEmpty() == true && picked == false) {

                UNO.DrawCard dc = new UNO.DrawCard(1);

                myAgent.addBehaviour(dc);

                while (dc.done() == false) {
                }


            }


            if (ints.isEmpty() == true) {
                ints.add(new Intention("NoMove", 0));

            }




            return ints;
        }

        private String getIntType(Card c) {
            String s = "";
            switch (c.getType()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    s = "Play";
                    break;
                case 13:
                    s = "PlayChColor";
                    break;
                case 10:
                    s = "PlayChDirection";
                    break;
                case 11:
                    s = "PlayPass";
                    break;
                case 12:
                    s = "PlayPlus2";
                    break;
                case 14:
                    s = "PlayPlus4";
                    break;

            }

            return s;
        }

       
        
    }

    private class SecondaryEmotions extends Behaviour {

        private boolean finished = false;

        ;
        

        public void action() {

            if (getCritical() == false) {
                cycleOff();

                Object[] args = myAgent.getArguments();
                Incoming i = secondaryObjectiveEvaluation();
                double affect = i.Strength;
                if (affect < 0) {
                    affect = affect * -1;
                }
                if (affect != 0) {

                    CurrentStateDescription csd = (CurrentStateDescription) args[4];
                    Personality p = (Personality) args[2];

                    double leftover;
                    char type = i.EmotionType;
                    boolean isSame = isTheSame(csd.emoVect, type);
                    if (isSame == true) {
                        leftover = getCurrentSecondaryIntensityofEmotion(csd.emoVect, type);
                    } else {
                        leftover = 0;
                    }

                    double subjSecondaryEmotion = calculatesubjectiveSecondaryEmotion(affect, type, leftover, isSame);


                    csd.emoVect = nullOtherEmotions(csd.emoVect, type);
                    csd.emoVect = setNewValue(csd.emoVect, type, subjSecondaryEmotion + leftover);
                    csd.emoVect = setNewLast(csd.emoVect, type);

                    //i.DemoStrength = 0.0;

                    csd.currentState = secondaryToPAD(subjSecondaryEmotion, type, csd.currentState, p.pers);

                    args[4] = csd;

                    myAgent.setArguments(args);

                }
                cycleOn();
                finished = true;



            } else {
                block();
            }
            done();
        }

        @Override
        public boolean done() {

            return finished;
        }

        private double calculatesubjectiveSecondaryEmotion(double iObj, char type, double leftover, boolean same) {
            Object[] args = myAgent.getArguments();
            Personality coreState = (Personality) args[2];
            CurrentStateDescription csd = (CurrentStateDescription) args[4];

            double N = coreState.N;
            double E = coreState.E;
            double maxY;
            double s;
            double x0;
            double DecN = 1.89444 * N - 0.06292;
            double DecSec = 15 + 20 * DecN;
            double lambda = 4.6 / DecSec;
            double iExpr = 0;
            double iSubj = 0;

            switch (type) {

                case 'a':
                    double angON = 1.11485 * N + 0.07318;
                    double angTN = 0.95926 * N + 0.11563;
                    angTN = normalize(angTN);
                    angON = normalize(angON);
                    maxY = -0.7 * angTN + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((angTN / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, angTN, s, x0);
                        csd.emoVect[0].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;
                    } else {
                        iSubj = appraisal(iObj, angTN, s, x0);
                        csd.emoVect[0].SecLeft = Math.log(iSubj) / -lambda;
                    }

                    iExpr = expression(iSubj, angON, s, x0);
                    break;

                case 'd':
                    double disEX = 1.251748 * E - 0.1279;
                    double disAPR = 1.177632 * N + 0.104483;
                    disEX = normalize(disEX);
                    disAPR = normalize(disAPR);
                    maxY = -0.7 * disAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((disAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, disAPR, s, x0);
                        csd.emoVect[1].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;


                    } else {
                        iSubj = appraisal(iObj, disAPR, s, x0);
                        csd.emoVect[1].SecLeft = Math.log(iSubj) / -lambda;

                    }


                    iExpr = expression(iSubj, disEX, s, x0);

                    break;

                case 'f':
                    double feaAPR = 0.947368 * N + 0.161053;
                    double feaEX = 1.006993 * E - 0.02589;
                    feaAPR = normalize(feaAPR);
                    feaEX = normalize(feaEX);
                    maxY = -0.7 * feaAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((feaAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, feaAPR, s, x0);
                        csd.emoVect[2].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;

                    } else {
                        iSubj = appraisal(iObj, feaAPR, s, x0);
                        csd.emoVect[2].SecLeft = Math.log(iSubj) / -lambda;

                    }

                    iExpr = expression(iSubj, feaEX, s, x0);

                    break;
                case 'j':
                    double joyAPREX = 0.986 * E + 0.07036;
                    joyAPREX = normalize(joyAPREX);
                    maxY = -0.7 * joyAPREX + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((joyAPREX / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, joyAPREX, s, x0);
                        csd.emoVect[3].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;

                    } else {
                        iSubj = appraisal(iObj, joyAPREX, s, x0);
                        csd.emoVect[3].SecLeft = Math.log(iSubj) / -lambda;
                    }

                    iExpr = expression(iSubj, joyAPREX, s, x0);
                    break;

                case 's':
                    double sadEX = 1.006993 * E - 0.02589;
                    double sadAPR = 0.921053 * N + 0.24515;
                    sadAPR = normalize(sadAPR);
                    sadEX = normalize(sadEX);
                    maxY = -0.7 * sadAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((sadAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, sadAPR, s, x0);
                        csd.emoVect[4].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;


                    } else {
                        iSubj = appraisal(iObj, sadAPR, s, x0);
                        csd.emoVect[4].SecLeft = Math.log(iSubj) / -lambda;

                    }


                    iExpr = expression(iSubj, sadEX, s, x0);
                    break;
            }


            args[4] = csd;

            myAgent.setArguments(args);
            sendMessagetoAll(myAgent.getLocalName(), 0, Character.toString(type), iExpr);

            return iSubj;
        }

        private double getCurrentSecondaryIntensityofEmotion(EmotionVector[] ev, char type) {
            double current = 0;
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type == type) {
                    current = ev[i].Value;
                }
            }

            return current;


        }

        private boolean isTheSame(EmotionVector[] ev, char type) {
            char temp = ' ';
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Last == true) {
                    temp = ev[i].Type;
                }
            }

            if (temp == type) {
                return true;
            } else {
                return false;
            }
        }

        private EmotionVector[] nullOtherEmotions(EmotionVector[] ev, char type) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type != type) {
                    ev[i].Value = 0;
                    ev[i].SecLeft = 0;
                }
            }
            return ev;
        }

        private PAD secondaryToPAD(double iSubj, char type, PAD current, PAD core) {

            PAD maxEmotion = new PAD(0, 0, 0);
            double coreToMax;

            double newP, newA, newD;
            switch (type) {
                case 'a':
                    maxEmotion = new PAD(0.28, 0.86, 0.66);
                    break;
                case 'd':
                    maxEmotion = new PAD(0.2, 0.675, 0.555);
                    break;
                case 'f':
                    maxEmotion = new PAD(0.19, 0.91, 0.285);
                    break;
                case 'j':
                    maxEmotion = new PAD(0.905, 0.755, 0.73);
                    break;
                case 's':
                    maxEmotion = new PAD(0.14, 0.355, 0.295);
                    break;
            }
            int p = getProfile(maxEmotion);
            PAD ext = getExtreme(p);
            coreToMax = distance(maxEmotion, core);
            double scaled = coreToMax * iSubj;



            if (ext.P == 0) {
                newP = current.P - Math.abs(maxEmotion.P - current.P) * scaled;
            } else {
                newP = current.P + Math.abs(maxEmotion.P - current.P) * scaled;
            }
            if (ext.A == 0) {
                newA = current.A - Math.abs(maxEmotion.A - current.A) * scaled;
            } else {
                newA = current.A + Math.abs(maxEmotion.A - current.A) * scaled;
            }
            if (ext.D == 0) {
                newD = current.D - Math.abs(maxEmotion.D - current.D) * scaled;
            } else {
                newD = current.D + Math.abs(maxEmotion.D - current.D) * scaled;
            }


            newP = normalize(newP);
            newA = normalize(newA);
            newD = normalize(newD);

            PAD newcurrent = new PAD(newP, newA, newD);
            addPADIntensity(newcurrent, 2);
            return newcurrent;
        }

        private EmotionVector[] setNewValue(EmotionVector[] ev, char type, double value) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type == type) {
                    ev[i].Value = value;
                }
            }
            return ev;
        }

        private EmotionVector[] setNewLast(EmotionVector[] ev, char type) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type != type) {
                    ev[i].Last = false;
                } else {
                    ev[i].Last = true;
                }

            }

            return ev;
        }
    }

    private class TertiaryReasoning extends Behaviour {

        private boolean finished = false;
        int state = 1;

        public void action() {

            if (getCritical() == false) {
                Object args[] = myAgent.getArguments();
                List<Incoming> n = (List<Incoming>) args[3];

                switch (state) {
                    case 1:
                        TertiaryEmotions b = new TertiaryEmotions();
                        myAgent.addBehaviour(b);
                        finished = false;
                        state = state + 1;

                        break;
                    case 2:
                        finished = true;


                        Control cnt = (Control) args[0];

                        SocBelDesInt sbdi = (SocBelDesInt) args[7];
                        if (n.isEmpty() == false) {
                            if (n.get(0).Rational == true) {
                                n.remove(0);
                            }

                        }

                        SocialBelief sb;

                        for (int i = 0; i < sbdi.BeliefSet.size(); i++) {
                            sb = (SocialBelief) sbdi.BeliefSet.get(i);
                            if (sb.Type == 12) {
                                sbdi.BeliefSet = updateRelationshipBeliefs(sbdi.BeliefSet, sb.Subject, sb.Number);
                                sbdi.BeliefSet.remove(sb);

                            }
                            if (sb.Type == 13) {
                                sbdi.BeliefSet = updateStatusBeliefs(sbdi.BeliefSet, sb.Subject);
                                sbdi.BeliefSet.remove(sb);

                            }
                        }



                        cnt.BehCycleDone = true;
                        args[0] = cnt;
                        args[3] = n;
                        myAgent.setArguments(args);


                        finished = true;
                   
                }
            } else {
                block();
            }
        }

        @Override
        public boolean done() {

            return finished;
        }

        private List<SocialBelief> updateRelationshipBeliefs(List<SocialBelief> BeliefSet, String name, double helped) {
            SocialBelief sb;
               for (int i = 0; i < BeliefSet.size(); i++) {
                sb = (SocialBelief) BeliefSet.get(i);
                 
                if (sb.Subject.equals(name)) {
                    
                    if (sb.Type == 10) {
                        System.out.println(sb.Subject+ sb.Type+ sb.Number+ " "+helped);
                        if (helped == 1) {
                            if (sb.Number < 1) {
                                sb.Number = sb.Number + 0.1;
                                
                            }
                        } else {
                            if (sb.Number > -1) {
                                sb.Number = sb.Number - 0.1;
                            }
                            
                        }
                        System.out.println(sb.Subject+ sb.Type+ sb.Number);
                      
                        addSBDI(sb);
                    }
                }
            }

            return BeliefSet;
        }

        private List<SocialBelief> updateStatusBeliefs(List<SocialBelief> BeliefSet, String name) {
            SocialBelief sb;
            for (int i = 0; i < BeliefSet.size(); i++) {
                sb = (SocialBelief) BeliefSet.get(i);
                if (sb.Subject.equals(name)) {
                    if (sb.Type == 11) {
                        
                        sb.Number = sb.Number - 0.1;
                        if (sb.Number<0)
                        {sb.Number = 0;}
                        addSBDI(sb);

                    }
                }
            }

            return BeliefSet;
        }
    }

    private class TertiaryEmotions extends Behaviour {

        private boolean finished = false;

        public void action() {

            if (getCritical() == false) {
                cycleOff();


                Object[] args = myAgent.getArguments();
                Incoming i = tertiaryObjectiveEvaluation();
                SocBelDesInt sbdi = (SocBelDesInt) args[7];
                
                if (i.Strength != 0) {


                    CurrentStateDescription csd = (CurrentStateDescription) args[4];
                    Personality p = (Personality) args[2];


                    double leftover;
                    char type = i.EmotionType;
                  


                    double rimpact = calculateRelationshipImpact(sbdi.BeliefSet, "Ana");

                    double simpact = calculateStatusImpact(sbdi.BeliefSet, "Ana");

                    double finalaffect = calculateObjectiveAffect(rimpact, simpact, i.Strength);

                    boolean isSame = isTheSame(csd.emoVect, type);

                    if (isSame == true) {
                        leftover = getCurrentIntensityofEmotion(csd.emoVect, type); //out of PAD space
                    } else {
                        leftover = 0;
                    }

                    double subjTertiaryEmotion = calculatesubjectiveTertiaryEmotion(Math.abs(finalaffect), type, leftover, isSame);
                    //System.out.println(i.Strength + " " + rimpact + " " + rimpact + " "+ finalaffect);
                    csd.emoVect = nullOtherEmotions(csd.emoVect, type);
                    csd.emoVect = setNewValue(csd.emoVect, type, subjTertiaryEmotion + leftover);
                    csd.emoVect = setNewLast(csd.emoVect, type);
                    i.Strength = 0.0;

                    csd.currentState = tertiaryToPAD(finalaffect, subjTertiaryEmotion, type, csd.currentState, p.pers);


                    addPADIntensity(csd.currentState, 3);


                    args[4] = csd;
                    myAgent.setArguments(args);



                }
                cycleOn();
                finished = true;

            } else {
                block();
            }
        }

        @Override
        public boolean done() {

            return finished;
        }

        private boolean isTheSame(EmotionVector[] ev, char type) {
            char temp = ' ';
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Last == true) {
                    temp = ev[i].Type;
                }
            }

            if (temp == type) {
                return true;
            } else {
                return false;
            }
        }

        private double getCurrentIntensityofEmotion(EmotionVector[] ev, char type) {
            double current = 0;
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type == type) {
                    current = ev[i].Value;
                }
            }

            return current;


        }

        private EmotionVector[] nullOtherEmotions(EmotionVector[] ev, char type) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type != type) {
                    ev[i].Value = 0;
                    ev[i].SecLeft = 0;
                }
            }
            return ev;
        }

        private EmotionVector[] setNewValue(EmotionVector[] ev, char type, double value) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type == type) {
                    ev[i].Value = value;
                }
            }
            return ev;
        }

        private EmotionVector[] setNewLast(EmotionVector[] ev, char type) {
            for (int i = 0; i < ev.length; i++) {
                if (ev[i].Type != type) {
                    ev[i].Last = false;
                } else {
                    ev[i].Last = true;
                }
            }
            return ev;
        }

        private double calculatesubjectiveTertiaryEmotion(double iObj, char type, double leftover, boolean same) {
            Object[] args = myAgent.getArguments();
            Personality coreState = (Personality) args[2];
            CurrentStateDescription csd = (CurrentStateDescription) args[4];

            double N = coreState.N;
            double E = coreState.E;
            double maxY;
            double s;
            double x0;
            double DecN = 1.89444 * N - 0.06292;
            double DecSec = 15 + 20 * DecN;
            double lambda = 4.6 / DecSec;
            double iExpr = 0;
            double iSubj = 0;

            switch (type) {

                case 'a':
                    double angON = 1.11485 * N + 0.07318;
                    double angTN = 0.95926 * N + 0.11563;
                    angTN = normalize(angTN);
                    angON = normalize(angON);
                    maxY = -0.7 * angTN + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((angTN / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, angTN, s, x0);
                        csd.emoVect[0].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;
                    } else {
                        iSubj = appraisal(iObj, angTN, s, x0);
                        csd.emoVect[0].SecLeft = Math.log(iSubj) / -lambda;
                    }

                    iExpr = expression(iSubj, angON, s, x0);
                    break;

                case 'd':
                    double disEX = 1.251748 * E - 0.1279;
                    double disAPR = 1.177632 * N + 0.104483;
                    disEX = normalize(disEX);
                    disAPR = normalize(disAPR);
                    maxY = -0.7 * disAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((disAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, disAPR, s, x0);
                        csd.emoVect[1].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;


                    } else {
                        iSubj = appraisal(iObj, disAPR, s, x0);
                        csd.emoVect[1].SecLeft = Math.log(iSubj) / -lambda;

                    }


                    iExpr = expression(iSubj, disEX, s, x0);

                    break;

                case 'f':
                    double feaAPR = 0.947368 * N + 0.161053;
                    double feaEX = 1.006993 * E - 0.02589;
                    feaAPR = normalize(feaAPR);
                    feaEX = normalize(feaEX);
                    maxY = -0.7 * feaAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((feaAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, feaAPR, s, x0);
                        csd.emoVect[2].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;

                    } else {
                        iSubj = appraisal(iObj, feaAPR, s, x0);
                        csd.emoVect[2].SecLeft = Math.log(iSubj) / -lambda;

                    }

                    iExpr = expression(iSubj, feaEX, s, x0);

                    break;
                case 'j':
                    double joyAPREX = 0.986 * E + 0.07036;
                    joyAPREX = normalize(joyAPREX);
                    maxY = -0.7 * joyAPREX + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((joyAPREX / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, joyAPREX, s, x0);
                        csd.emoVect[3].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;

                    } else {
                        iSubj = appraisal(iObj, joyAPREX, s, x0);
                        csd.emoVect[3].SecLeft = Math.log(iSubj) / -lambda;
                    }

                    iExpr = expression(iSubj, joyAPREX, s, x0);
                    break;

                case 's':
                    double sadEX = 1.006993 * E - 0.02589;
                    double sadAPR = 0.921053 * N + 0.24515;
                    sadAPR = normalize(sadAPR);
                    sadEX = normalize(sadEX);
                    maxY = -0.7 * sadAPR + 1.2;
                    s = -maxY / -13.8;
                    x0 = 6.9 * s;

                    if (same == true) {

                        double iObj2 = -(s * Math.log((sadAPR / leftover) - 1) - x0);

                        iObj = iObj + iObj2;
                        if (iObj >= 1) {
                            iObj = 1;
                        }

                        iSubj = appraisal(iObj, sadAPR, s, x0);
                        csd.emoVect[4].SecLeft = Math.log(iSubj) / -lambda;
                        iSubj = iSubj - leftover;


                    } else {
                        iSubj = appraisal(iObj, sadAPR, s, x0);
                        csd.emoVect[4].SecLeft = Math.log(iSubj) / -lambda;

                    }


                    iExpr = expression(iSubj, sadEX, s, x0);
                    break;
            }
            args[4] = csd;
            myAgent.setArguments(args);
            sendMessagetoAll(myAgent.getLocalName(), 0, Character.toString(type), iExpr);
            return iSubj;


        }

        private double calculateRelationshipImpact(List beliefset, String agent) {
            SocialBelief sb;
            double status = 0;
            double relationship = 0;
            double S;
            for (int i = 0; i < beliefset.size(); i++) {
                sb = (SocialBelief) beliefset.get(i);
                if (sb.Subject.equals(agent)) {
                    if (sb.Type == 10) {
                        relationship = sb.Number;
                    }
                }
            }

            double relationcoeff;

            if (relationship < 0) {
                relationcoeff = relationship;
            } else {
                relationcoeff = (relationship / 2) + 1;
            }

            if (relationship == 0) {
                relationcoeff = 1;
            }

            return relationcoeff;
        }

        private double calculateStatusImpact(List beliefset, String agent) {
            SocialBelief sb;
            double status = 0;
            double relationship = 0;
            double S;
            for (int i = 0; i < beliefset.size(); i++) {
                sb = (SocialBelief) beliefset.get(i);
                if (sb.Subject.equals(agent)) {
                    if (sb.Type == 11) {
                        status = sb.Number;
                    }
                }
            }

          
            double statuscoeff = (status / 2) + 1;
            if (status == 0) {
                statuscoeff = 1;
            }

            return statuscoeff;
        }

        private PAD tertiaryToPAD(double impact, double iSubj, char type, PAD current, PAD core) {
            PAD maxEmotion = new PAD(0, 0, 0);
            double coreToMax;
            double newP, newA, newD;
            switch (type) {
                case 'a':
                    maxEmotion = new PAD(0.28, 0.86, 0.66);
                    break;
                case 'd':
                    maxEmotion = new PAD(0.2, 0.675, 0.555);
                    break;
                case 'f':
                    maxEmotion = new PAD(0.19, 0.91, 0.285);
                    break;
                case 'j':
                    maxEmotion = new PAD(0.905, 0.755, 0.73);
                    break;
                case 's':
                    maxEmotion = new PAD(0.14, 0.355, 0.295);
                    break;
            }
            int p = getProfile(maxEmotion);
            PAD ext = getExtreme(p);
            coreToMax = distance(maxEmotion, core);
            double scaled = coreToMax * iSubj;

            if (impact >= 0) {
                if (ext.P == 0) {
                    newP = current.P - Math.abs(maxEmotion.P - current.P) * scaled;
                } else {
                    newP = current.P + Math.abs(maxEmotion.P - current.P) * scaled;
                }
                if (ext.A == 0) {
                    newA = current.A - Math.abs(maxEmotion.A - current.A) * scaled;
                } else {
                    newA = current.A + Math.abs(maxEmotion.A - current.A) * scaled;
                }
                if (ext.D == 0) {
                    newD = current.D - Math.abs(maxEmotion.D - current.D) * scaled;
                } else {
                    newD = current.D + Math.abs(maxEmotion.D - current.D) * scaled;
                }
            } else {
                if (ext.P == 0) {
                    newP = current.P + Math.abs(maxEmotion.P - current.P) * scaled;
                } else {
                    newP = current.P - Math.abs(maxEmotion.P - current.P) * scaled;
                }
                if (ext.A == 0) {
                    newA = current.A + Math.abs(maxEmotion.A - current.A) * scaled;
                } else {
                    newA = current.A - Math.abs(maxEmotion.A - current.A) * scaled;
                }
                if (ext.D == 0) {
                    newD = current.D + Math.abs(maxEmotion.D - current.D) * scaled;
                } else {
                    newD = current.D - Math.abs(maxEmotion.D - current.D) * scaled;
                }

            }

            newP = normalize(newP);
            newA = normalize(newA);
            newD = normalize(newD);
            PAD newcurrent = new PAD(newP, newA, newD);

            return newcurrent;
        }

        private double calculateObjectiveAffect(double rimpact, double simpact, double DemoStrength) {
            double affect = DemoStrength;
            double coeff = 0;
            if (rimpact < 0) {
                coeff = rimpact * (2 - simpact);
            } else {
                coeff = (simpact + rimpact) / 2;
            }
            affect = coeff * affect;
         

            if (affect > 1) {
                affect = 1;
            }

            if (affect < -1) {
                affect = -1;
            }



            return affect;
        }
    }

    private class EmotionCycle extends TickerBehaviour {

        public EmotionCycle(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {


            Object[] args = myAgent.getArguments();
          
            Control c = (Control) args[0];
            List<Incoming> inc = (List<Incoming>) args[3];
            CurrentStateDescription csd = (CurrentStateDescription) args[4];
            Personality core = (Personality) args[2];
            EmotionVector ev;
            boolean tag;
            char Last = ' ';
            double state = 0;
            boolean isPrimitive = false;
            // printList(inc,"Ana");
            Incoming n = new Incoming(false, false, 0, ' ', false, false);
            //printList(inc, "Ana");
            if (inc.isEmpty() == false && inc.get(0).Rational == false) {
                n = inc.get(0);
            }



            if (c.Cycle == false) {
      
                block();
            } else {


                if (n.IsNew == false) {
                    Last = getTheLastEmotion();
              
                    tag = true;
                    if (Last == ' ') {
                        isPrimitive = true;
                        if (csd.currentState.P > core.pers.P) 
                        {
                            Last = 'p';
                            tag = true;
                        } else {
                            Last = 'n';
                            tag = false;
                        }

                    }
                    if (isPrimitive == true) {
                        state = getCurrentNegPosIntensityFromPAD(tag, core.pers, csd.currentState);




                    } else {
                        switch (Last) {

                            case 'a':
                                state = csd.emoVect[0].Value;
                                break;
                            case 'd':
                                state = csd.emoVect[1].Value;
                                break;
                            case 'f':
                                state = csd.emoVect[2].Value;
                                break;
                            case 'j':
                                state = csd.emoVect[3].Value;
                                break;
                            case 's':
                                state = csd.emoVect[4].Value;
                                break;
                        }

                    }
                    
                    if (state > 0.011) {
                        

                        decayLastEmotion(Last, state, isPrimitive);

                    } else {
                        addPADIntensity(csd.currentState, 0);
                        if (state > 0.001) {
                            moodDecay(n);
                         
                        }

                    }
                } else {


                    //PRIMARY LEVEL
                    tag = true;
                    if (n.EmotionType == ' ') {
                        if (n.Tag == true)
                        {
                            tag = true;
                        } else {
                            tag = false;
                        }
                        state = getCurrentNegPosIntensityFromPAD(tag, core.pers, csd.currentState);
                        isPrimitive = true;
                        double DecN = 1.89444 * core.N - 0.06292;
                        double t = 0;
                        if (DecN < 0.034) {
                            DecN = 0.034;
                        }
                        double DecSec = 15 + 20 * DecN;
                        double lambda = 4.6 / DecSec;

                        csd.DecSecs = Math.log(state) / -lambda;
                    } else {
                        isPrimitive = false;
                    }

                    csd.prevMood = csd.mood;

                    csd.mood = moodUpdate(isPrimitive, tag, csd.mood, csd.currentState, core.pers, csd.emoVect);

                    csd.moodTime = moodDecay(n);

                    if (n.Processed == true && n.Rational == false) {
                        


                        if (inc.size() >= 1) 
                        {
                          
                            inc.remove(0);


                        } else {
                            inc.get(0).IsNew = false;

                        }
                    }

                    args[3] = inc;
                    myAgent.setArguments(args);
                }
            }


        }

        private char getTheLastEmotion() {
            char lastEmotion = ' ';
            Object[] args = myAgent.getArguments();
            EmotionVector ev;
            CurrentStateDescription csd = (CurrentStateDescription) args[4];


            //gets actual emotion
            for (int i = 0; i < csd.emoVect.length; i++) {
                ev = csd.emoVect[i];
                if (ev.Last == true) {
                    lastEmotion = ev.Type;
                }
           

            }






            return lastEmotion;
        }

        private double decayLastEmotion(char Last, double state, boolean isPrimitive) {

            Object[] args = myAgent.getArguments();
            Personality p = (Personality) args[2];
            CurrentStateDescription csd = (CurrentStateDescription) args[4];
            double subjInt = 0;
            double DecN = 1.89444 * p.N - 0.06292;
            double t = 0;
            if (DecN < 0.034) {
                DecN = 0.034;
            }
            double DecSec = 15 + 20 * DecN; 
            double lambda = 4.6 / DecSec;

            if (isPrimitive == true) {
               
                t = csd.DecSecs;
                csd.DecSecs = csd.DecSecs + 1;
              
                subjInt = Math.exp(-lambda * t);
                decayIntegration(csd, p, subjInt, Last, true, state);

            }


            double prevvalue;

            switch (Last) {

                case 'a':
                    if (csd.emoVect[0].Value > 0.0001) {
                        prevvalue = csd.emoVect[0].Value;
                        csd.emoVect[0].SecLeft = csd.emoVect[0].SecLeft + 1;
                        csd.emoVect[0].Value = decay(csd.emoVect[0].SecLeft, lambda);

                        decayIntegration(csd, p, csd.emoVect[0].Value, Last, false, prevvalue);
                    }
                    break;
                case 'd':
                    if (csd.emoVect[1].Value > 0.0001) {
                        prevvalue = csd.emoVect[1].Value;
                        csd.emoVect[1].SecLeft = csd.emoVect[1].SecLeft + 1;
                        csd.emoVect[1].Value = decay(csd.emoVect[1].SecLeft, lambda);

                        decayIntegration(csd, p, csd.emoVect[1].Value, Last, false, prevvalue);
                    }
                    break;
                case 'f':

                    if (csd.emoVect[2].Value > 0.0001) {
                        prevvalue = csd.emoVect[2].Value;
                        csd.emoVect[2].SecLeft = csd.emoVect[2].SecLeft + 1;
                        csd.emoVect[2].Value = decay(csd.emoVect[2].SecLeft, lambda);

                        decayIntegration(csd, p, csd.emoVect[2].Value, Last, false, prevvalue);

                    }
                    break;
                case 'j':
                    if (csd.emoVect[3].Value > 0.0001) {
                        prevvalue = csd.emoVect[3].Value;
                        csd.emoVect[3].SecLeft = csd.emoVect[3].SecLeft + 1;
                        csd.emoVect[3].Value = decay(csd.emoVect[3].SecLeft, lambda);

                        decayIntegration(csd, p, csd.emoVect[3].Value, Last, false, prevvalue);
                    }
                    break;
                case 's':
                    if (csd.emoVect[4].Value > 0.0001) {
                        prevvalue = csd.emoVect[4].Value;
                        csd.emoVect[4].SecLeft = csd.emoVect[4].SecLeft + 1;
                        csd.emoVect[4].Value = decay(csd.emoVect[4].SecLeft, lambda);

                        decayIntegration(csd, p, csd.emoVect[4].Value, Last, false, prevvalue);
                    }
                    break;


            }


            return csd.DecSecs;
        }

        public void decayIntegration(CurrentStateDescription Csd, Personality CoreState, double Value, char Last, boolean Primitive, double prevValue) {
            Object[] args = myAgent.getArguments();
            double maxP = 0.93 - 0.5;
            double minP = 0.5 - 0.15;
            double maxA = 0.83 - 0.5;
            double newP, newA, newD;
            PAD maxEmotion = new PAD(0, 0, 0);
            double coreToMax;


            if (Primitive == true) {
                double coreToCurrent = distance(Csd.currentState, CoreState.pers);
                double scaled = ((coreToCurrent * Value) / prevValue) / coreToCurrent;
                if (Last == 'n') {
                    Csd.currentState.P = CoreState.pers.P - scaled * Math.abs(Csd.currentState.P - CoreState.pers.P);
                    Csd.currentState.A = CoreState.pers.A + scaled * Math.abs(Csd.currentState.A - CoreState.pers.A);
                } else if (Last == 'p') {
                    Csd.currentState.P = CoreState.pers.P + scaled * Math.abs(Csd.currentState.P - CoreState.pers.P);
                    Csd.currentState.A = CoreState.pers.A + scaled * Math.abs(Csd.currentState.A - CoreState.pers.A);
                }
                addPADIntensity(Csd.currentState, 1); //aizkomentēts, lai var novērtēt, kā iet kopā

            } else {

                switch (Last) {
                    case 'a':
                        maxEmotion = new PAD(0.28, 0.86, 0.66);
                        break;
                    case 'd':
                        maxEmotion = new PAD(0.2, 0.675, 0.555);
                        break;
                    case 'f':
                        maxEmotion = new PAD(0.19, 0.91, 0.285);
                        break;
                    case 'j':
                        maxEmotion = new PAD(0.905, 0.755, 0.73);
                        break;
                    case 's':
                        maxEmotion = new PAD(0.14, 0.355, 0.295);
                        break;
                }
                int p = getProfile(maxEmotion);
                PAD ext = getExtreme(p);
                coreToMax = distance(maxEmotion, CoreState.pers);
                double coreToCurrent = distance(Csd.currentState, CoreState.pers);
                //double scaled;


                double Px;
                double Ax;
                double Dx;
                Px = ((Csd.currentState.P - CoreState.pers.P) + (CoreState.pers.P * prevValue)) / prevValue;
                Ax = ((Csd.currentState.A - CoreState.pers.A) + (CoreState.pers.A * prevValue)) / prevValue;
                Dx = ((Csd.currentState.D - CoreState.pers.D) + (CoreState.pers.D * prevValue)) / prevValue;
                PAD x = new PAD(Px, Ax, Dx);


                if (Csd.currentState.P < CoreState.pers.P) {
                    newP = CoreState.pers.P - Math.abs(Px - CoreState.pers.P) * Value;
                } else {
                    newP = CoreState.pers.P + Math.abs(Px - CoreState.pers.P) * Value;
                }
                if (Csd.currentState.A < CoreState.pers.A) {
                    newA = CoreState.pers.A - Math.abs(Ax - CoreState.pers.A) * Value;
                } else {
                    newA = CoreState.pers.A + Math.abs(Ax - CoreState.pers.A) * Value;
                }
                if (Csd.currentState.D < CoreState.pers.D) {
                    newD = CoreState.pers.D - Math.abs(Dx - CoreState.pers.D) * Value;
                } else {
                    newD = CoreState.pers.D + Math.abs(Dx - CoreState.pers.D) * Value;
                }

                PAD newcurrent = new PAD(newP, newA, newD);
                Csd.currentState = newcurrent;

                addPADIntensity(Csd.currentState, 0);
                //sendMessagetoAll(myAgent.getLocalName(), 0, Character.toString(Last), Value);


            }



            args[4] = Csd;
            myAgent.setArguments(args);

        }

        public PAD moodUpdate(boolean isPrimitive, boolean Tag, PAD currentmood, PAD currentstate, PAD corestate, EmotionVector[] emovec) {
            PAD mood;
            // get the octant in which currentstate is and centre of that octant
            int p = getProfile(currentstate);

            PAD ext = getExtreme(p);
            // d4 = get distance from currentmood to extreme in which the currentstate is
            double d4 = Math.sqrt(Math.pow((currentmood.P - ext.P), 2) + Math.pow((currentmood.A - ext.A), 2) + Math.pow((currentmood.D - ext.D), 2));
            double d1 = Math.sqrt(Math.pow((currentstate.P - corestate.P), 2) + Math.pow((currentstate.A - corestate.A), 2) + Math.pow((currentstate.D - corestate.D), 2));
            double d2 = 0;

            // d2 = get distance from the core to maximum possible value of emotion

            //in case of positive/negative value

            if (isPrimitive == true) {
                if (Tag = true) {
                    d2 = Math.sqrt(Math.pow((1 - corestate.P), 2) + Math.pow((1 - corestate.A), 2) + Math.pow((corestate.D - corestate.D), 2));
                } else {
                    d2 = Math.sqrt(Math.pow((0 - corestate.P), 2) + Math.pow((0 - corestate.A), 2) + Math.pow((corestate.D - corestate.D), 2));
                }


            } else {
                //in case of emotion the emotion is calculated based on last emotion
                double[] coretomax = Personality.getDeltas(corestate);
                char e = ' ';
                for (int i = 0; i < emovec.length; i++) {
                    if (emovec[i].Last == true) {
                        e = emovec[i].Type;
                    }
                }

                switch (e) {
                    case 'a':
                        d2 = coretomax[0];
                        break;
                    case 'd':
                        d2 = coretomax[1];
                        break;
                    case 'f':
                        d2 = coretomax[2];
                        break;
                    case 'j':
                        d2 = coretomax[3];
                        break;
                    case 's':
                        d2 = coretomax[4];
                        break;
                }
            }
            // get the proportion of the distance (d1/d2) and fraction (d3)

            double prop = d1 * d4 / d2;
            double Afract = Math.abs(ext.A - currentmood.A) * prop;
            double Pfract = Math.abs(ext.P - currentmood.P) * prop;
            double Dfract = Math.abs(ext.D - currentmood.D) * prop;
            double newA;
            double newP;
            double newD;

            // get the current mood state based on fraction


            if (ext.P == 0) {
                newP = currentmood.P - Pfract;
            } else {
                newP = currentmood.P + Pfract;
            }


            if (ext.A == 0) {
                newA = currentmood.A - Afract;
            } else {
                newA = currentmood.A + Afract;
            }


            if (ext.D == 0) {
                newD = currentmood.D - Dfract;
            } else {
                newD = currentmood.D + Dfract;
            }
         
            mood = new PAD(newP, newA, newD);
            //addPADMoodIntensity(mood);
            return mood;
        }

        public double moodDecay(Incoming i) {
            double newP = 0;
            double newA = 0;
            double newD = 0;

            Object[] args = myAgent.getArguments();
            CurrentStateDescription csd = (CurrentStateDescription) args[4];
            Personality p = (Personality) args[2];
            int prof = getProfile(csd.mood);
            PAD ext = getExtreme(prof);

            PAD mextreme = p.maxExtreme;
            //how far is the core from the extreme, the value of "y", at which the emotions have just started, "x" = 0
            double maxpossible = Math.sqrt(Math.pow((mextreme.P - p.pers.P), 2) + Math.pow((mextreme.A - p.pers.A), 2) + Math.pow((mextreme.D - p.pers.D), 2)); //?
            double time = csd.moodTime;
            //if new irritation has appeared, the length of the decay must be calculated, namely, the current "y" value, and new time must be set - corresponding "x"

            if (i.IsNew == true) {
                double currentdistancetocore = Math.sqrt(Math.pow((csd.mood.P - p.pers.P), 2) + Math.pow((csd.mood.A - p.pers.A), 2) + Math.pow((csd.mood.D - p.pers.D), 2)); //d1
                time = (1200 * currentdistancetocore) / maxpossible;
                csd.moodTime = time;

            } else {
                double current = Math.sqrt(Math.pow((csd.mood.P - p.pers.P), 2) + Math.pow((csd.mood.A - p.pers.A), 2) + Math.pow((csd.mood.D - p.pers.D), 2));
                double fractionsize = maxpossible / 1200;
                double fractionNo = current / fractionsize;
                double distance = fractionsize * csd.moodTime; //distance lielajā nogrieznī, ideāli distance sākumā = current



                double max = Math.sqrt(Math.pow((ext.P - p.pers.P), 2) + Math.pow((ext.A - p.pers.A), 2) + Math.pow((ext.D - p.pers.D), 2));


                double Afract = Math.abs(p.pers.A - csd.mood.A) / fractionNo;
                double Pfract = Math.abs(p.pers.P - csd.mood.P) / fractionNo;
                double Dfract = Math.abs(p.pers.D - csd.mood.D) / fractionNo;

      
                if (csd.mood.P > p.pers.P) {//vai nosac pareizi
                    newP = csd.mood.P - Pfract;
                } else {
                    newP = csd.mood.P + Pfract;
                }


                if (csd.mood.A > p.pers.A) {
                    newA = csd.mood.A - Afract;
                } else {
                    newA = csd.mood.A + Afract;
                }


                if (csd.mood.D > p.pers.D) {
                    newD = csd.mood.D - Dfract;
                } else {
                    newD = csd.mood.D + Dfract;
                }


                if (newP > 1) {
                    newP = 1;
                }
                if (newA > 1) {
                    newA = 1;
                }
                if (newD > 1) {
                    newD = 1;
                }

                csd.mood = new PAD(newP, newA, newD);
          
            }
            if (csd.moodTime > 0) {
                csd.moodTime = csd.moodTime - 1;
            } else {
                csd.moodTime = 0;

            }
            args[4] = csd;
            myAgent.setArguments(args);

            return time;
        }
    }

    //DAZADAS METODES
    public void cycleOff() {
        Object[] args = this.getArguments();
        Control c = (Control) args[0];
        c.Cycle = false;
        args[0] = c;
        this.setArguments(args);
        //System.out.println("Turning cycle off!");
    }

    public void cycleOn() {
        Object[] args = this.getArguments();
        Control c = (Control) args[0];
        c.Cycle = true;
        args[0] = c;
        this.setArguments(args);
        //System.out.println("Turning cycle on!");
    }

    public void criticalOn() {
        Object[] args = this.getArguments();
        Control c = (Control) args[0];
        c.Critical = true;
        args[0] = c;
        this.setArguments(args);
        // System.out.println("Critical on!");
    }

    public boolean getCritical() {
        Object[] args = this.getArguments();
        Control c = (Control) args[0];
        return c.Critical;
    }

    public double primaryObjectiveEvaluation() {
        Object[] args = this.getArguments();
        List<Incoming> n = (List<Incoming>) args[3];
        Incoming i = new Incoming(false, false, 0, ' ', false, false);
        for (ListIterator<Incoming> iter = n.listIterator(); iter.hasNext();) {
            Incoming inc = iter.next();
            if (inc.Rational == false && inc.Processed == false && inc.EmotionType == ' ' && iter.nextIndex() == 1) {
                i = inc;
                inc.Processed = true;
                break;
            }
        }
  

        double a = i.Strength;
        return a;
    }

    public Incoming secondaryObjectiveEvaluation() {
        Object[] args = this.getArguments();
        List<Incoming> n = (List<Incoming>) args[3];
        Incoming i = new Incoming(false, false, 0, ' ', false, false);
        for (ListIterator<Incoming> iter = n.listIterator(); iter.hasNext();) {
            Incoming inc = iter.next();

            if (inc.Rational == false && inc.Processed == false && inc.EmotionType != ' ' && iter.nextIndex() == 1 && inc.Tertiary == false) {
                i = inc;
   
                inc.Processed = true;
                break;
            }
        }



        return i;
    }

    public Incoming tertiaryObjectiveEvaluation() {
        Object[] args = this.getArguments();
        List<Incoming> n = (List<Incoming>) args[3];
        Incoming i = new Incoming(false, false, 0, ' ', false, false);
        for (ListIterator<Incoming> iter = n.listIterator(); iter.hasNext();) {
            Incoming inc = iter.next();

            if (inc.Rational == false && inc.Processed == false && inc.Tertiary == true && iter.nextIndex() == 1) {
                i = inc;
            
                inc.Processed = true;
                break;
            }
        }



        return i;
    }

    public void calculateSubjectiveEmotion(double iObj) //calculates intensity of primitive affect
    {

        Object[] args = this.getArguments();
        Personality coreState = (Personality) args[2];
        CurrentStateDescription csd = (CurrentStateDescription) args[4];
        double pp; //personality impact
        boolean pos = true; //is emotion positive?

        //chooses parameter depending on emotion positivity or negativity
        if (iObj < 0) {
            pos = false;
            iObj = iObj * (-1);
        }
        // dependence on the personality
        if (pos == true) {
            pp = 1.00699 * coreState.E + 0.01839;
    
        } else {
            pp = 1.04605 * coreState.N + 0.15211;
           
        }

        pp = normalize(pp);
        //function parameter calculation 
        double maxY = pp * 0.3 + 0.7;
        double s = -maxY / -13.8;
        double x0 = 1 - 6.9 * s;
        getObjectiveIrritation(pos, iObj, coreState, csd, maxY, s, x0);
    }

    public void getObjectiveIrritation(boolean Tag, double Incoming, Personality CoreState, CurrentStateDescription Csd, double maxY, double s, double x0) //adds up the irritations 
    {
        double currentI = 0.001; 
        double leftover = 0;
        double subjective = 0; 
        boolean adding = false; 
        double maxDistance = 0; 
        double currentDistance = 0; 
        double subjectiveA = 0; 
        currentI = getCurrentNegPosIntensityFromPAD(Tag, CoreState.pers, Csd.currentState);


        if (Tag == true) {

            double test = maxY / currentI - 1;
            if (test < 0) {
                currentI = maxY / 1.001;
            }
            if (currentI > maxY) {
                currentI = maxY - 0.001;
            }
            if (currentI == 0) {
                leftover = 0;
            } else {
                leftover = -(s * Math.log(maxY / currentI - 1) - x0); //calculates the leftover irritation
            }
            if (Csd.currentState.P > CoreState.pers.P) {
                //if the tag is positive and person is closer to positive then get add the values to get bell efect
                Incoming = Incoming + leftover;
                if (Incoming >= 1) {
                    Incoming = 1;
                }
                subjective = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                subjectiveA = subjective;
                adding = true;

            } else {    //if the tag is positive and person is negative, no bell effect; incoming remains the same
                subjective = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                Incoming = Incoming + leftover;
                if (Incoming >= 1) {
                    Incoming = 1;
                }
                subjectiveA = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                adding = false;
            }
        } else {

            double test = maxY / currentI - 1;
            if (test < 0) {
                currentI = maxY / 1.001;
            }
            if (currentI == 0) {
                leftover = 0;
            } else {
                leftover = -(s * Math.log(maxY / currentI - 1) - x0); //calculates the leftover irritation
            }

            if (Csd.currentState.P < CoreState.pers.P) {
                //if the tag is negative and person is closer to negative then get add the values to get bell efect

                Incoming = Incoming + leftover;
                if (Incoming >= 1) {
                    Incoming = 1;
                }
                subjective = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                subjectiveA = subjective;
                adding = true;


            } else {//if the tag is negative and person is positive, no bell effect; incoming remains the same
                subjective = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                Incoming = Incoming + leftover;
                if (Incoming >= 1) {
                    Incoming = 1;
                }
                subjectiveA = maxY / (1 + Math.exp((-(Incoming - x0)) / s));
                adding = false;

            }
        }


        
        transformPrimitiveIntensityToPAD(adding, Tag, subjective, subjectiveA, Csd, CoreState, maxY);

    

    }

    public void transformPrimitiveIntensityToPAD(boolean Bell, boolean Tag, double Value, double ValueA, CurrentStateDescription Csd, Personality CoreState, double maxY) {

        Object[] args = this.getArguments();
        double maxP = 0.93 - 0.5;
        double maxA = 0.83 - 0.5;
        double minP = 0.5 - 0.15;
        double maxposEmo = maxY;

        if (Tag == true) {

            if (Bell == true) {

                Csd.currentState.P = CoreState.pers.P + Value * maxP;
                Csd.currentState.A = CoreState.pers.A + ValueA * maxA;

                if (Csd.currentState.A > maxposEmo) {
                    Csd.currentState.A = maxposEmo;
                }

            } else {
                Csd.currentState.P = CoreState.pers.P + Value * maxP;
                Csd.currentState.A = CoreState.pers.A + ValueA * maxA; 

                if (Csd.currentState.A > maxposEmo) {
                    Csd.currentState.A = maxposEmo;
                }
            }

        } else //pie negatīvas vērtības
        {
           
            if (Bell == true) {

                Csd.currentState.P = CoreState.pers.P - Value * minP; 
                Csd.currentState.A = CoreState.pers.A + ValueA * maxA;

                if (Csd.currentState.A > maxposEmo) {
                    Csd.currentState.A = maxposEmo;
                }

            } else {
                Csd.currentState.P = CoreState.pers.P - Value * maxP;
                Csd.currentState.A = CoreState.pers.A + ValueA * maxA;

                if (Csd.currentState.A > maxposEmo) {
                    Csd.currentState.A = maxposEmo;
                }

            }
        }
       
        args[4] = Csd;
     
        Personality p = (Personality) args[2];
        addPADIntensity(Csd.currentState, 1);

        //mood to arguments

        this.setArguments(args);




    }

    public double getCurrentNegPosIntensityFromPAD(boolean Tag, PAD core, PAD c) {
        double currentI = 0;
        double maxDistance = 0;
        double currentDistance = 0;

        if (Tag == true) {
            maxDistance = Math.sqrt(Math.pow((0.93 - core.P), 2) + Math.pow((0.83 - core.A), 2)); //0.93 iegūts šādi = 0.5+0.86/2 = 0.93; tāpat arī A
            currentDistance = Math.sqrt(Math.pow((c.P - core.P), 2) + Math.pow((c.A - core.A), 2));

            currentI = currentDistance / maxDistance;
            if (currentI == 0) {
                currentI = 0;
            }
        } else {
            maxDistance = Math.sqrt(Math.pow((core.P - 0.15), 2) + Math.pow((core.A - 0.17), 2)); //0.15 iegūts šādi = 1-(0.5+0.70/2) = 0.15; tāpat arī A
            currentDistance = Math.sqrt(Math.pow((c.P - core.P), 2) + Math.pow((c.A - core.A), 2));
            currentI = currentDistance / maxDistance;
            if (currentI == 0) {
                currentI = 0;
            }
        }

        return currentI;
    }

    public double integrateInPAD(boolean Tag, PAD core, PAD c) {
        double currentI = 0;
        double maxDistance = 0;
        double currentDistance = 0;

        if (Tag == true) {
            maxDistance = Math.sqrt(Math.pow((0.93 - core.P), 2) + Math.pow((0.83 - core.A), 2)); //0.93 iegūts šādi = 0.5+0.86/2 = 0.93; tāpat arī A
            currentDistance = Math.sqrt(Math.pow((c.P - core.P), 2) + Math.pow((c.A - core.A), 2));

            currentI = currentDistance / maxDistance;
            if (currentI == 0) {
                currentI = 6.82121E-06;
            }
        } else {
            maxDistance = Math.sqrt(Math.pow((core.P - 0.15), 2) + Math.pow((core.A - 0.17), 2)); //0.15 iegūts šādi = 1-(0.5+0.70/2) = 0.15; tāpat arī A
            currentDistance = Math.sqrt(Math.pow((c.P - core.P), 2) + Math.pow((c.A - core.A), 2));
            currentI = currentDistance / maxDistance;
            if (currentI == 0) {
                currentI = 6.82121E-06;
            }
        }


        return currentI;
    }

    public void showClosestEmotion(PAD csd) {

        double[] deltas = Personality.getDeltas(csd);
        double distance = 0.0;
        int emo = 0;
        for (int i = 0; i < deltas.length; i++) {
            if (deltas[i] > distance) {
                distance = deltas[i];
                emo = i;
            }

        }

        switch (emo) {
            case 0:
                //System.out.println("I am angry!");
                break;
            case 1:
                //System.out.println("I am disgusted!");
                break;
            case 2:
                //System.out.println("I am afraid!");
                break;
            case 3:
                //  System.out.println("I am happy!");
                break;
            case 4:
                // System.out.println("I am sad!");
                break;

        }


    }

    public static double[] getDeltas(PAD core) //gets distance from a point in PAD space to emotions
    {
        double[] distances = new double[5];

        double anger = Math.sqrt(Math.pow((0.28 - core.P), 2) + Math.pow((0.86 - core.A), 2) + Math.pow((0.66 - core.D), 2));
        distances[0] = anger;
        double disgust = Math.sqrt(Math.pow((0.2 - core.P), 2) + Math.pow((0.675 - core.A), 2) + Math.pow((0.555 - core.D), 2));;
        distances[1] = disgust;
        double fear = Math.sqrt(Math.pow((0.19 - core.P), 2) + Math.pow((0.91 - core.A), 2) + Math.pow((0.285 - core.D), 2));
        distances[2] = fear;
        double joy = Math.sqrt(Math.pow((0.905 - core.P), 2) + Math.pow((0.755 - core.A), 2) + Math.pow((0.73 - core.D), 2));
        distances[3] = joy;
        double sadness = Math.sqrt(Math.pow((0.14 - core.P), 2) + Math.pow((0.355 - core.A), 2) + Math.pow((0.295 - core.D), 2));
        distances[4] = sadness;

        return distances;
    }

    public int getProfile(PAD csd) //http://howie.gse.buffalo.edu/effilno/interests/math/octants/
    {
        int profile = 0;


        if (csd.P >= 0.5) {
            if (csd.A >= 0.5) {
                if (csd.D >= 0.5) {
                    //P+ A+ D+
                    profile = 1;
                } else //P+ A+ D-
                {
                    profile = 2;
                }
            } else if (csd.D >= 0.5) {
                //P+ A- D+
                profile = 3;
            } else //P+ A- D-
            {
                profile = 2;
            }
        } else {
            if (csd.A >= 0.5) {
                if (csd.D >= 0.5) {
                    //P- A+ D+
                    profile = 5;
                } else //P- A+ D-
                {
                    profile = 6;
                }
            } else if (csd.D >= 0.5) {
                //P- A- D+
                profile = 7;
            } else //P- A- D-
            {
                profile = 8;
            }
        }
        return profile;
    }

    public double appraisal(double iObj, double traitDependence, double s, double x0) {

        double subjInt = traitDependence / (1 + Math.exp((-(iObj - x0)) / s));

        return subjInt;
    }

    public double expression(double iSubj, double traitDependence, double s, double x0) {


        double expPower = traitDependence / (1 + Math.exp(-(iSubj - (x0)) / s));
        return expPower;
    }

    public double decay(double t, double lambda) {
        double subjInt = Math.exp(-lambda * t);
        return subjInt;
    }

    public double susceptibility(double iObj, double N, double E, char Type) {
        double traitDep = 0.0;
        switch (Type) {
            case 'a':
                traitDep = 0.95926 * N + 0.11563;
                break;

            case 'd':
                traitDep = 1.177632 * N + 0.104483;
                break;

            case 'f':
                traitDep = 0.947368 * N + 0.161053;
                break;

            case 'j':
                traitDep = 0.986 * E + 0.07036;
                break;

            case 's':
                traitDep = 0.921053 * N + 0.24515;
                break;

        }
        traitDep = normalize(traitDep);

        double treshold = -0.09382 * traitDep + 0.110849;


        return treshold;
    }

    public double distance(PAD p1, PAD p2) {
        double d;

        d = Math.sqrt(Math.pow((p1.P - p2.P), 2) + Math.pow((p1.A - p2.A), 2) + Math.pow((p1.D - p2.D), 2));

        return d;

    }

    public PAD getExtreme(int octant) //negative = 0.25, positive = 0.75, octant no: http://howie.gse.buffalo.edu/effilno/interests/math/octants/
    {
        PAD c = new PAD(0, 0, 0);
        switch (octant) //centroīdi
        {
            case 1:
                c.P = 1;
                c.A = 1;
                c.D = 1;
                break;
            case 2:
                c.P = 1;
                c.A = 1;
                c.D = 0;
                break;
            case 3:
                c.P = 1;
                c.A = 0;
                c.D = 1;
                break;
            case 4:
                c.P = 1;
                c.A = 0;
                c.D = 0;
                break;
            case 5:
                c.P = 0;
                c.A = 1;
                c.D = 1;
                break;
            case 6:
                c.P = 0;
                c.A = 1;
                c.D = 0;
                break;
            case 7:
                c.P = 0;
                c.A = 0;
                c.D = 1;
                break;
            case 8:
                c.P = 0;
                c.A = 0;
                c.D = 0;
                break;

        }
    

        return c;
    }

    public double normalize(double value) {
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        return value;
    }

    public void addPADIntensity(PAD p, int s) {

        outputListElement a = new outputListElement(p.P, p.A, p.D, s);
        EmotionPAD.add(a);

    }

    public void addPADMoodIntensity(PAD p) {

        PAD a = new PAD(p.P, p.A, p.D);
        MoodPAD.add(a);

    }

    public void addIntention(Intention i, int p) {

        outputListElementBehaviours oleb = new outputListElementBehaviours(p, i);
        ActionsDone.add(oleb);

    }

    public void addSBDI(SocialBelief i) {

        SocialBelief sb = new SocialBelief(i.Subject, i.Type, i.Number);
        SBDIList.add(sb);

    }

    public void sendMessage(String reciever, int type, UNO.Card c, String move, String emotion, double strength) {

        ContentElementList cel = new ContentElementList();
        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(reciever, AID.ISLOCALNAME));
        msg.setLanguage(codec.getName());
        msg.setOntology(ontology.getName());
        gm.setCard(c);
        gm.setMove(move);
        gm.setEmoType(emotion);
        gm.setEmoStrength(strength);
        gm.setMesType(type);
        cel.add(gm);
        try {
            getContentManager().fillContent(msg, cel);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        send(msg);

    }

    private void printList(List<Incoming> n, String agentname) {

        if (this.getLocalName().equals(agentname)) {
            Incoming i = new Incoming(false, false, 0, ' ', false, false);
            System.out.println("-------------------------INCOMINGS--------------------------");
            for (ListIterator<Incoming> iter = n.listIterator(); iter.hasNext();) {
                Incoming inc = iter.next();
                String rat;
                if (inc.Rational == true) {
                    rat = "R";
                } else {
                    rat = "E";
                }


                System.out.println(rat + " Is processed: " + inc.Processed + "|| Is new: " + inc.IsNew + " || " + inc.EmotionType + "   ||   " + inc.Strength + " || Tert: " + inc.Tertiary);
            }
            System.out.println("______________________________________________________________");
        }

    }

    private void printCards(List<Card> n, String agentname) {

        if (this.getLocalName().equals(agentname)) {
            Incoming i = new Incoming(false, false, 0, ' ', false, false);
            System.out.println("-------------------------CARDS--------------------------");
            for (ListIterator<Card> iter = n.listIterator(); iter.hasNext();) {
                Card c = iter.next();
                String rat;



                System.out.println(c.getNumber() + " Color: " + c.getColor() + " Type " + c.getType());
            }
            System.out.println("______________________________________________________________");
        }

    }

    private void printBeliefs(List<Belief> n, String agentname) {


        if (this.getLocalName().equals(agentname)) {
            //Object o = null;
            //Belief b = new Belief("",o);
            UNO.Card c = new UNO.Card();
            System.out.println("-------------------------BELIEFS--------------------------");
            for (ListIterator<Belief> iter = n.listIterator(); iter.hasNext();) {
                Belief b1 = iter.next();
                if (b1.Object.getClass() == c.getClass()) {
                    c = (UNO.Card) b1.Object;
                    System.out.println("Type: " + b1.Type + "|| Card: " + c.getColor() + c.getType());
                } else {
                    System.out.println("Type: " + b1.Type + "|| Card: " + b1.Object);
                }
            }
            System.out.println("______________________________________________________________");
        }

    }

    private void printIntentions(List<Intention> n, String agentname) {

        if (this.getLocalName().equals(agentname)) {
            UNO.Card c = new UNO.Card();
            //Object o = null;
            //Belief b = new Belief("",o);
            System.out.println("-------------------------INTENTIONS--------------------------");
            for (ListIterator<Intention> iter = n.listIterator(); iter.hasNext();) {
                Intention b1 = iter.next();
          
                System.out.println("Type: " + b1.Type + "|| Card: " + b1.Object);
            }
            System.out.println("______________________________________________________________");
        }

    }

    private void printSocialBeliefs(List<SocialBelief> n, String agentname) {


        if (this.getLocalName().equals(agentname)) {
            //Object o = null;
            //Belief b = new Belief("",o);
            UNO.Card c = new UNO.Card();
            System.out.println("-------------------------SOCIAL BELIEFS--------------------------");
            for (ListIterator<SocialBelief> iter = n.listIterator(); iter.hasNext();) {
                SocialBelief b1 = iter.next();

                int type = b1.Type;
                String subject = b1.Subject;
                double number = b1.Number;
                System.out.println("Type: " + b1.Type + "|| Who: " + b1.Subject + " || " + b1.Number);




            }
            System.out.println("______________________________________________________________");
        }
    }

    public void sendMessagetoAll(String sender, int type, String emotion, double strength) {

        ContentElementList cel2 = new ContentElementList();
        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        String[] agentNames = {"Ana", "Gita", "Greg", "Robert", "Alex", "Maria"};
        for (int i = 0; i < agentNames.length; i++) {
            if (!agentNames[i].equals(sender)) {
                msg.addReceiver(new AID(agentNames[i], AID.ISLOCALNAME));
            }
        }

        msg.setLanguage(codec.getName());
        msg.setOntology(ontology.getName());
        UNO.Card dummy = new UNO.Card();
        dummy.setColor("Unknown");
        dummy.setType(1000);
        String move = "Unknown";
        gm.setCard(dummy);
        gm.setMove(move);
        gm.setEmoType(emotion);
        gm.setEmoStrength(strength);
        gm.setMesType(type);
        cel2.add(gm);
        try {
            getContentManager().fillContent(msg, cel2);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        send(msg);

    }

    private void emotionalMessage(String name, double D) { //agent will ask for a communication

        //   System.out.println (name + " " + D);
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        request.addReceiver(new AID(name, AID.ISLOCALNAME));
        request.setLanguage("fipa-sl");
        request.setOntology("UNO-ontology");
        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
        ContentElementList cel = new ContentElementList();
        Card c = new Card();
        c.setColor(" ");
        c.setType(20);
        int Type = 1;
        gm.setCard(c);
        gm.setMesType(Type);
        gm.setMove("None");
        gm.setEmoStrength(0.5);
        if (D < 0) { //choosing emotion type
            gm.setEmoType("s");
        } else {
            gm.setEmoType("a");
        }

        cel.add(gm);

        try {
            this.getContentManager().fillContent(request, cel);

        } catch (Codec.CodecException | OntologyException ex) {
            Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.addBehaviour(new AchieveREInitiator(this, request) {

            @Override
            protected void handleInform(ACLMessage inform) {
                //  System.out.println("Protocol finished. Rational Effect achieved. Received the following message.");
            }
        });


    }

    private void emotionalAnswer(String t) {
        final String didhelp = t;
        MessageTemplate mt = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);
        this.addBehaviour(new AchieveREResponder(this, mt) {

            @Override
            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
                ACLMessage resp = request.createReply();
                resp.setPerformative(ACLMessage.INFORM);
                resp.setLanguage("fipa-sl");
                resp.setOntology("UNO-ontology");
                try {
                    // System.out.println(request.getSender());
                    ContentManager cm = myAgent.getContentManager();
                    ContentElement ce = (ContentElement) cm.extractContent(request);
                    Object args[] = myAgent.getArguments();
                    SocBelDesInt sbdi = (SocBelDesInt) args[7];
                    UNO.ontology.GeneralMessage gm = (UNO.ontology.GeneralMessage) ce;
                    List<Incoming> inc = (List<Incoming>) args[3];

                    CurrentStateDescription csd = (CurrentStateDescription) args[4];
                    sbdi.BeliefSet.add(new SocialBelief(request.getSender().getLocalName(), 13, 1));
                    Incoming n4 = new Incoming(false, true, gm.getEmoStrength(), (char) gm.getEmoType().charAt(0), false, false);
                    inc.add(n4);
                    args[7] = sbdi;
                    args[3] = inc;
                    myAgent.setArguments(args);



                    ContentElementList cel = new ContentElementList();
                    Card c = new Card();
                    c.setColor(" ");
                    c.setType(20);
                    int Type = 1;
                    gm.setCard(c);
                    gm.setMesType(Type);

                    if ("PlayChDirection".equals(didhelp) || "PlayPlus4".equals(didhelp) || "PlayPlus2".equals(didhelp)) {
                        gm.setMove("No");
                    } else {
                   
                        gm.setMove("Yes");//}
                    }

                    gm.setEmoStrength(0);
                    gm.setEmoType(" ");


                    cel.add(gm);

                    try {
                        myAgent.getContentManager().fillContent(resp, cel);

                    } catch (Codec.CodecException | OntologyException ex) {
                        Logger.getLogger(EmotionalAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return resp;

                } catch (CodecException ex) {
                    return resp;
                } catch (UngroundedException ex) {
                    return resp;
                } catch (OntologyException ex) {
                    return resp;
                }



            }
        });
    }
}