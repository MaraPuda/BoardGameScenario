
package UNO;

import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 *
 * @author Mara Pudane
 */
public class NoPoints extends OneShotBehaviour {
   private Codec Codec;
    private Ontology Ontology;

    public NoPoints(Agent a, Codec codec, Ontology ontology) {
        Codec = codec;
        Ontology = ontology;
    }

    @Override
    public void action() {
        
        System.out.println(myAgent.getLocalName() + "'s last move!");
    }    
}
