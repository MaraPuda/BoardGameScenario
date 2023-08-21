package emotional_UNO;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mara Pudane
 */
public class emotional_UNO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //runs JADE

        RunJade r = new RunJade(false, "30000");
        ContainerController home = r.getHome();
        TimeSync.sTime();

        //runs interface       

        try {


            AgentController b = RunJade.home.createNewAgent("GUI", "emotional_UNO.GUIAgent", args);
            b.start();
        } catch (Exception e) {
            e.printStackTrace();

        }

        startAgent(0, "Ana", "Alex", "Gita");
        startAgent(1, "Gita", "Ana", "Robert");
        startAgent(2, "Robert", "Gita", "Greg");
        startAgent(3, "Greg", "Robert", "Maria");
        startAgent(4, "Maria", "Greg", "Alex");
        startAgent(5, "Alex", "Maria", "Ana");


    }

    public static void startAgent(int number, String name, String left, String right) {

        try {

            Object[] args = new Object[10];
            Control c = new Control(false, true, true);
            EmotionVector[] ev = {new EmotionVector('a', 0.0, false, 0.0), new EmotionVector('d', 0.0, false, 0.0), new EmotionVector('f', 0.0, false, 0.0), new EmotionVector('j', 0.0, false, 0.0), new EmotionVector('s', 0.0, false, 0.0)};
            Personality p = new Personality(0, 0, 0.58, 0, 0.45);
            args[2] = (Object) p;
            ArrayList inc = new ArrayList<Incoming>();
            //inc.add(new Incoming (true, 0, ' ', false));
            args[3] = inc;
            PAD m = new PAD(p.pers.P, p.pers.A, p.pers.D);
            PAD cu = new PAD(p.pers.P, p.pers.A, p.pers.D);
            UNO.UNOargs UA = new UNO.UNOargs(left, right, new ArrayList<UNO.Card>(), new UNO.Card(), " ");
            args[5] = startBelDesInt(name);
            args[7] = new SocBelDesInt(importSocialBels(name), 0, false);
            args[8] = UA;

            double b = 0;

            CurrentStateDescription csd = new CurrentStateDescription(m, cu, cu, b, ev);

            args[0] = c;


            args[4] = csd;
            args[9] = (Integer) 0;
            AgentController a = RunJade.home.createNewAgent(name, "emotional_UNO.EmotionalAgent", args);
            a.start();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static BelDesInt startBelDesInt(String Name) {

        String[] PlayerArray = {"Ana", "Alex", "Gita", "Greg", "Robert", "Maria"};
        Belief b = new Belief("a", 0);
        List<Belief> bels = new ArrayList();
        List<Intention> ints = new ArrayList();
       
        BelDesInt BDI = new BelDesInt(bels, 0, ints);

        return BDI;
    }

    public static List<SocialBelief> importSocialBels(String towho) {
        List<SocialBelief> bels = new ArrayList<SocialBelief>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("..."));///add file name
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                SocialBelief sb = new SocialBelief("", 0, 0);
                if (towho.equals(data[3])) {
                    sb.Subject = data[0];
                    sb.Number = Double.parseDouble(data[1]);
                    sb.Type = Integer.parseInt(data[2]);
                    bels.add(sb);
                }

            }
            reader.close();
            return bels;
        } catch (Exception e) {
            System.err.format("File not read.");
            e.printStackTrace();
            return null;
        }


    }
}
