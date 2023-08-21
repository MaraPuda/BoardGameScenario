package emotional_UNO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Mara Pudane
 */
public class Personality {
    public double O = 0;
    public double C = 0;
    public double E = 0;
    public double A = 0;
    public double N = 0;
    public PAD pers;
    public PAD maxExtreme;
    public static boolean DEFAULT_PERSONALITY;
    public static ArrayList<Personality> PERSONALITIES = new ArrayList<>();
    
    
    
    public Personality (double o, double c, double e, double a, double n)
    {
    O = o;
    C = c;
    E = e;
    A = a;
    N = n;
    pers = getCoreAffect(this);
    maxExtreme = getMaximumExtreme(this);
    }
    
    public static void readPersonalities()
    { //nolasa no faila 
        File file = new File("personalities.txt");
        int size = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("personalities.txt")))
        {
            //Personality p;
            String currentline = "";

        
          
          while ((currentline = br.readLine()) != null) {
              String[]data = currentline.split(",");
              
              
              Personality p = new Personality(0,0,0,0,0);
              p.O = Double.valueOf(data[0]);
              //System.out.println(p.O);
               p.C = Double.valueOf(data[1]);
                p.E = Double.valueOf(data[2]);
                 p.A = Double.valueOf(data[3]);
                  p.N = Double.valueOf(data[4]);
                 // System.out.println(p.N);
               PERSONALITIES.add(p);
          }
               
            

        
        } catch (IOException ex) {
          System.out.println("problem accessing file"+file.getAbsolutePath());
        
    
    }
    }   
    public static PAD getCoreAffect(Personality p)
    {
    
        
    double[][] constant = new double[][]{
                  {0.,0.,0.21,0.59,0.19},
                  {0.15,0.,0.,0.3,-0.57},
                  {0.25,0.17,0.6,-0.32,0.}};    
    PAD coreState = new PAD(0,0,0);
    if (p.O >1)
    {
      coreState.P = 0;
      coreState.A = 0;
      coreState.D = 0;
    }
    else
    {
    coreState.P = ((0.21*p.E+0.59*p.A+0.19*p.N)+1)/2;
    coreState.A = ((0.15*p.O+0.3*p.A-0.57*p.N)+1)/2;
    coreState.D = ((0.25*p.O+0.17*p.C+0.6*p.E-0.32*p.A)+1)/2;
       
    }
    
    return coreState;
    }
    
    //gets deltas for PAD calculation
    public static double [] getDeltas(PAD core) //gets distance from a point in PAD space to emotions
            
    {
    double[] distances = new double[5];
    
    double anger = Math.sqrt(Math.pow((0.28 - core.P),2)+ Math.pow((0.86-core.A),2)+Math.pow((0.66-core.D),2));
    distances[0] = anger;
    double disgust = Math.sqrt(Math.pow((0.2 - core.P),2)+ Math.pow((0.675-core.A),2)+Math.pow((0.555-core.D),2));;
    distances[1] = disgust;
    double fear = Math.sqrt(Math.pow((0.19 - core.P),2)+ Math.pow((0.91-core.A),2)+Math.pow((0.285-core.D),2));
    distances[2] = fear;
    double joy = Math.sqrt(Math.pow((0.905 - core.P),2)+ Math.pow((0.755-core.A),2)+Math.pow((0.73-core.D),2));
    distances[3] = joy;
    double sadness = Math.sqrt(Math.pow((0.14 - core.P),2)+ Math.pow((0.355-core.A),2)+Math.pow((0.295-core.D),2));
    distances[4] = sadness;
    
    return distances;
    }
        public static double [] getCoeff(Personality p)
            
    {
    double[] coefficients = new double[15];
    
    double AngTN = 0.95926*p.N + 0.11563;
    double AXON = 1.11485*p.N + 0.07318;
    double SadE = 0.979021*p.E + 0.063397;
    double SadN = 0.921053*p.N + 0.24515;
    double JoyE = 0.986*p.E + 0.07036;
    double FearE = 1.006993*p.E - 0.02589;
    double FearN = 0.947368*p.N + 0.161053;
    double DisgE = 1.251748*p.E - 0.1279;
    double DisgN = 1.177632*p.N + 0.104483;
    double AngDecSec = 1.89444*p.N - 0.06929;
    double FearDecSec = 1.89444*p.N - 0.06929;
    double JoyDecSec = 1.89444*p.N - 0.06929;
    double SadnessDecSec = 1.89444*p.N - 0.06929;
    
    
    coefficients[0] = AngTN;
    coefficients[1] = AXON;
    coefficients[2] = DisgN;
    coefficients[3] = DisgE;
    coefficients[4] = FearN;
    coefficients[5] = FearE;
    coefficients[6] = JoyE;
    coefficients[7] = SadN;
    coefficients[8] = SadE;
    coefficients[9] = AngDecSec;
    coefficients[10] = FearDecSec;
    coefficients [11] = JoyDecSec;
    coefficients [12] = SadnessDecSec;
    //paarbaude uz lielaaku par 1 vai mazaaku par 0
    
   for (int i = 0; i<coefficients.length; i++)
    {
    if (coefficients[i]>1)
        coefficients[i] = 1;
    if (coefficients[i]<0)
        coefficients[i] = 0;
    
    }    
    
    
    return coefficients;
    
    
    }
        public static double [] getPrimitiveAffect(Personality p)
            
    {
    double[] affect = new double[2];
    double Pos = -5.2999*p.E+1.07838;
    double Neg = -5.055*p.N+3.82197;
    
    affect[0] = Pos;
    affect[1] = Neg;
    
     for (int i = 0; i<affect.length; i++)
    {
    if (affect[i]>1)
        affect[i] = 0.999;
    if (affect[i]<0)
        affect[i] = 0.001;
    
    }
    
    return affect;
    }
        
         public PAD getExtreme(int octant) //negative = 0.25, positive = 0.75, octant no: http://howie.gse.buffalo.edu/effilno/interests/math/octants/
    {
    PAD c = new PAD(0,0,0);
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
    
  public PAD getMaximumExtreme(Personality po) //gets maximum extreme for personality
    {
       PAD ext = new PAD(0,0,0); 
       double dist;
       double max = 0;
       PAD maxpoint = new PAD(0,0,0);
       PAD p = getCoreAffect(this);
       for (int i = 1; i<9; i++)
       {
           ext = getExtreme(i);
           dist = Math.sqrt(Math.pow((ext.P - p.P), 2) + Math.pow((ext.A - p.A), 2) + Math.pow((ext.D - p.D), 2));
           if (dist> max)
           {
           max = dist;
           maxpoint = ext;
           }
           
        
       }
        return maxpoint;
      
        
    }
        
}
