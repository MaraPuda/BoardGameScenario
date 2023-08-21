package UNO.ontology;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;
/**
 *
 * @author Mara Pudane
 */
public class UNOOntology extends Ontology{
    public static final String ONTOLOGY_NAME = "UNO-ontology";
    public static final String CARD = "Card";
   
    public static final String EMOTYPE = "EmoType";
    public static final String EMOSTRENGTH = "EmoStrength";
    public static final String CARDCOLOR = "Color";
    public static final String CARDTYPE = "Type";
    public static final String CARDNUMBER = "Number";
    public static final String TOWHO = "AgentName"; 
    public static final String MESSAGE = "Messages";
    public static final String MESTYPE = "MesType";
    public static final String MESCARD = "Card";
    public static final String MESMOVE = "Move";
    private static Ontology theInstance = new UNOOntology();
    
    public static Ontology getInstance()
    {
        return theInstance;
    }
    
    private UNOOntology()
    {
    super(ONTOLOGY_NAME, BasicOntology.getInstance());
    
    
        try {
            add(new ConceptSchema(CARD), UNO.Card.class);
            add(new PredicateSchema (MESSAGE), GeneralMessage.class);
            
            ConceptSchema cs = (ConceptSchema) getSchema(CARD);
            PredicateSchema ms = (PredicateSchema) getSchema(MESSAGE);
          
            
            
            
            cs.add(CARDCOLOR, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(CARDTYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            cs.add(CARDNUMBER, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

            ms.add(MESTYPE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
            ms.add(MESMOVE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            ms.add(MESCARD, (ConceptSchema) getSchema(CARD));
            ms.add(EMOTYPE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            ms.add(EMOSTRENGTH, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
        } 
        catch (OntologyException ex) {
           ex.printStackTrace();
        }
    
    }
    
    
    
}
