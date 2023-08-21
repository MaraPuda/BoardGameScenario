
package emotional_UNO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Mara Pudane
 * Necessary for output.
 */
public class TimeSync {
    public static String STIME;

    public static void sTime()
    {
    
   STIME = new SimpleDateFormat("HH.mm.ss").format(new Date());
  
}
    public static int getDifference(String t1)
    {
        
        
    int d = 0; 
    String[] strArray = t1.split("\\.");
    int[] intArray = new int[strArray.length];
   
     for(int i = 0; i < strArray.length; i++) {
        intArray[i] = Integer.parseInt(strArray[i]);
       // System.out.println(strArray[i]);
        
}
    
     int i1 = (intArray[0]*60)+(intArray[1]*60)+intArray[2];
     
     String[] strArray2 = STIME.split("\\.");
    int[] intArray2 = new int[strArray2.length];
     for(int i = 0; i < strArray2.length; i++) {
        intArray2[i] = Integer.parseInt(strArray2[i]);
}
    int i2 = (intArray2[0]*60)+(intArray2[1]*60)+intArray2[2];
    
   d = i1-i2;

   return d;
}
    
    
}
