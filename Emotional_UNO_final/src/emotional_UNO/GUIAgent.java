
package emotional_UNO;

import UNO.GamePlay;
import UNO.UNOboard;
import UNO.ontology.UNOOntology;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mara Pudane
 */
public class GUIAgent extends GuiAgent {

    transient protected UNO.UNOboard myGui;
    private Codec codec = new SLCodec();
    private Ontology ontology = UNOOntology.getInstance();

    @Override
    protected void setup() //inicializaacija
    {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);
        this.addBehaviour(new processMessages());
        UNO.UNOboard gui = new UNO.UNOboard(this);
        gui.setVisible(true);


    }

    @Override
    protected void onGuiEvent(GuiEvent ge) {
        switch (ge.getType()) {
            case 0: //deal cards
                UNO.Card[][] aoH = (UNO.Card[][]) ge.getParameter(0);
                UNO.Card cc = (UNO.Card) ge.getParameter(1);
                String tempName = "";
                ArrayList<UNO.Card> temp = new ArrayList<UNO.Card>();
                for (int m = 0; m < aoH.length; m++) {
                    for (int n = 0; n < aoH[m].length; n++) {
                        // System.out.println("SKAITITAJS: " + m);
                        ACLMessage cards = new ACLMessage(ACLMessage.INFORM);
                        switch (m) {

                            case 0:
                                cards.addReceiver(new AID("Ana", AID.ISLOCALNAME));
                                temp.add(aoH[m][n]);
                                tempName = "Ana";
                                

                                break;
                            case 1:
                                cards.addReceiver(new AID("Gita", AID.ISLOCALNAME));
                                temp.add(aoH[m][n]);
                                tempName = "Gita";


                                

                                break;
                            case 2:
                                cards.addReceiver(new AID("Robert", AID.ISLOCALNAME));

                                temp.add(aoH[m][n]);
                                tempName = "Robert";
                               
                                break;
                            case 3:
                                cards.addReceiver(new AID("Greg", AID.ISLOCALNAME));
                                temp.add(aoH[m][n]);
                                tempName = "Greg";

                               
                                break;
                            case 4:
                                cards.addReceiver(new AID("Maria", AID.ISLOCALNAME));
                                temp.add(aoH[m][n]);
                                tempName = "Maria";

                                
                                break;
                            case 5:
                                cards.addReceiver(new AID("Alex", AID.ISLOCALNAME));
                                temp.add(aoH[m][n]);
                                tempName = "Alex";

                               
                                break;

                        }

                        UNO.UNOboard.outputCards(tempName, temp);
                        temp.clear();
                        cards.setLanguage(codec.getName());
                        cards.setOntology(ontology.getName());
                        System.out.println(getContentManager().getOntology(cards));
                        System.out.println(getContentManager().getLanguageNames());
                        ContentElementList cel = new ContentElementList();
                        UNO.Card c = aoH[m][n];

                        UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
                        gm.setCard(c);
                        gm.setMesType(2);
                        gm.setMove("Ana"); //Ana starts game
                        gm.setEmoStrength(0.0);
                        gm.setEmoType(" ");
                        cel.add(gm);


                        try {
                            getContentManager().fillContent(cards, cel);

                        } catch (Codec.CodecException | OntologyException ex) {
                            Logger.getLogger(GUIAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }



                        send(cards);

                    }

                  



                }


                break;
            case 1://game starts
              
                ACLMessage c = new ACLMessage(ACLMessage.INFORM);
                c.setLanguage(codec.getName());
                c.setOntology(ontology.getName());
                ContentElementList cel = new ContentElementList();
                
                UNO.Card Current = UNO.GamePlay.CurrentCard;
                UNO.UNOboard.displayCurrentCard(Current, "Base");
                GamePlay.PlayedCards.add(Current);
                UNO.ontology.GeneralMessage gm = new UNO.ontology.GeneralMessage();
                gm.setCard(Current);
                gm.setMesType(3);
                gm.setEmoStrength(0.0);
                gm.setEmoType(" ");
                gm.setMove("Ana"); 
                cel.add(gm);
            
                try {
                    getContentManager().fillContent(c, cel);

                } catch (Codec.CodecException | OntologyException ex) {
                    Logger.getLogger(GUIAgent.class.getName()).log(Level.SEVERE, null, ex);
                }

                c.addReceiver(new AID("Ana", AID.ISLOCALNAME));
                c.addReceiver(new AID("Gita", AID.ISLOCALNAME));
                c.addReceiver(new AID("Robert", AID.ISLOCALNAME));
                c.addReceiver(new AID("Greg", AID.ISLOCALNAME));
                c.addReceiver(new AID("Maria", AID.ISLOCALNAME));
                c.addReceiver(new AID("Alex", AID.ISLOCALNAME));

                send(c);
                break;

            case 2:
                ACLMessage primaff = new ACLMessage(ACLMessage.INFORM);
                primaff.addReceiver(new AID("Marons", AID.ISLOCALNAME));
                Double strength = (Double) ge.getParameter(0);
                boolean neg = (boolean) ge.getParameter(1);
                if (neg == true) {
                    strength = strength * -1;
                }
                String ss = strength.toString();
                primaff.setContent(ss);
                send(primaff);


        }

    }

    private class processMessages extends CyclicBehaviour {

        int cardCount = 0;

        @Override
        public void action() {


            ACLMessage msg = myAgent.receive();
            if (msg != null) {

                ContentManager cm = myAgent.getContentManager();
                ContentElement ce;
                try {
                    ce = (ContentElement) cm.extractContent(msg);
                    //UNO.UNOargs ua = (UNO.UNOargs) args[8];
                    UNO.ontology.GeneralMessage gm = (UNO.ontology.GeneralMessage) ce;
                    int t = gm.getMesType();

                    switch (t) {
                        case 0:
                        case 1:
                        case 2:
                            cardCount++;

                            if (cardCount >= 18) {
                                UNOboard.PlayButton.setEnabled(true);
                            }
                            break;
                        case 3: //Delay the game


                            ACLMessage fwd = new ACLMessage(ACLMessage.INFORM);
                            //fwd = msg;

                            fwd.setOntology(msg.getOntology());
                            fwd.setLanguage(msg.getLanguage());

                            ContentElementList cel = new ContentElementList();
                            UNO.ontology.GeneralMessage fwdgm = new UNO.ontology.GeneralMessage();

                            fwdgm.setCard(gm.getCard());
                            fwdgm.setMesType(gm.getMesType());
                            fwdgm.setMove(gm.getMove());
                            fwdgm.setEmoStrength(gm.getEmoStrength());
                            fwdgm.setEmoType(gm.getEmoType());

                            cel.add(fwdgm);
                            doWait(2000);

                            AMSAgentDescription[] agents = null;


                            String[] agentNames = {"Ana", "Gita", "Greg", "Robert", "Alex", "Maria"};
                            for (int i = 0; i < agentNames.length; i++) {
                         
                                fwd.addReceiver(new AID(agentNames[i], AID.ISLOCALNAME));
                              
                            }
                          
                            fwd.setSender(msg.getSender());
                            try {
                                myAgent.getContentManager().fillContent(fwd, cel);

                            } catch (Codec.CodecException | OntologyException ex) {
                                Logger.getLogger(GUIAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            myAgent.send(fwd);
                            break;

                    }







                } catch (Codec.CodecException | OntologyException ex) {
                    Logger.getLogger(GUIAgent.class.getName()).log(Level.SEVERE, null, ex);
                }


            } else {
                block();
            }
        }
    }
}
