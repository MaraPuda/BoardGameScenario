package UNO;

import emotional_UNO.GUIAgent;
import jade.gui.GuiEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Mara Pudane
 */
public class UNOboard extends javax.swing.JFrame implements ActionListener{

    /**
     * Creates new form UNOboard
     */   private static GUIAgent myAgent;

    public static void displayCurrentCard(UNO.Card c, String Name) {
     
      
        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(current, Name + " " + o + "\n" ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(current,Name + " " + o + "\n" ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(current, Name + " " + o + "\n" ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(current, Name + " " + o + "\n" ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(current, Name + " " + o + "\n" ,Color.BLACK);
                                        }
                                           if ("Unknown".equals(c.getColor()))
                                        {
                                            String o = "Pick up";
                                        appendToPane(current, Name + " " + o + "\n" ,Color.BLACK);
                                        }
    }

   
        
    public UNOboard(GUIAgent a) {
        myAgent = a;
        initComponents();
    }
    
     @Override
 public void actionPerformed(ActionEvent e) {
      
         
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Deal = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        AnaHand = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        GitaHand = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        RobertHand = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        AlexHand = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        MariaHand = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        GregHand = new javax.swing.JTextPane();
        PlayButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        current = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UNO - affective agents");

        Deal.setText("Deal");
        Deal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DealActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Gita");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Robert");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Greg");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Maria");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Alex");

        jScrollPane7.setViewportView(AnaHand);

        jScrollPane1.setViewportView(GitaHand);

        jScrollPane2.setViewportView(RobertHand);

        jScrollPane3.setViewportView(AlexHand);

        jScrollPane4.setViewportView(MariaHand);

        jScrollPane5.setViewportView(GregHand);

        PlayButton.setText("Play");
        PlayButton.setEnabled(false);
        PlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PlayButtonActionPerformed(evt);
            }
        });

        current.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jScrollPane6.setViewportView(current);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Ana");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(269, 269, 269)
                        .addComponent(Deal)
                        .addGap(18, 18, 18)
                        .addComponent(PlayButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jScrollPane3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(81, 81, 81)
                                        .addComponent(jLabel8))
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(86, 86, 86)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(89, 89, 89)
                                .addComponent(jLabel6)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(105, 105, 105)
                                .addComponent(jLabel1)
                                .addGap(78, 78, 78))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane7)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(105, 105, 105)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addComponent(jLabel5)))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Deal)
                            .addComponent(PlayButton))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DealActionPerformed
        // TODO add your handling code here:
        GamePlay gp = new GamePlay(1);//picks, how cards are shuffled
        Card[][] arrayofHands;
        Card c;
        c = GamePlay.CurrentCard;
         arrayofHands = gp.deal(6,7); //6 spēlētājiem
        GuiEvent g = new GuiEvent(this, 0);
        g.addParameter(arrayofHands);
        g.addParameter(c);
          myAgent.postGuiEvent(g);   
          Deal.setEnabled(false);
      
    }//GEN-LAST:event_DealActionPerformed

    private void PlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PlayButtonActionPerformed
        GuiEvent g = new GuiEvent(this, 1);
        myAgent.postGuiEvent(g);   
          PlayButton.setEnabled(false);
    }//GEN-LAST:event_PlayButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UNOboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UNOboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UNOboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UNOboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UNOboard(myAgent).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextPane AlexHand;
    public static javax.swing.JTextPane AnaHand;
    private javax.swing.JButton Deal;
    private static javax.swing.JTextPane GitaHand;
    private static javax.swing.JTextPane GregHand;
    private static javax.swing.JTextPane MariaHand;
    public static javax.swing.JButton PlayButton;
    private static javax.swing.JTextPane RobertHand;
    private static javax.swing.JTextPane current;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    // End of variables declaration//GEN-END:variables
public static void clearHand(String Name, ArrayList<Card> Hand)
    {
      
        switch (Name){
        case "Ana":
                                  AnaHand.setText("");
                                 
                                 
                                
                              break;
                              case "Gita":
                                  GitaHand.setText("");
                                  
                              break;
                              case "Robert":
                                  RobertHand.setText("");
                                 
                              break;
                              case "Greg":
                                  GregHand.setText("");
                                    
                              break;
                              case "Maria":
                                  MariaHand.setText("");
                              
                              break;
                              case "Alex":
                                  AlexHand.setText("");
                                  
                              break;
        }                   
                          
    }

    public static void outputCards(String Name, ArrayList<Card> Hand)
    {
        Card c;
    switch (Name)
                           {   
                              
                              case "Ana":
                                  Iterator i = Hand.iterator();
                                  while (i.hasNext()) {
                                  
                                        c = (Card) i.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(AnaHand, o + " " ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(AnaHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AnaHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AnaHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AnaHand, o + " " ,Color.BLACK);
                                        }
                                       
                                     }
                                
                              break;
                              case "Gita":
                                   Iterator iGita = Hand.iterator();
                                  while (iGita.hasNext()) {
                                 
                                        c = (Card) iGita.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(GitaHand, o + " " ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(GitaHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GitaHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GitaHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GitaHand, o + " " ,Color.BLACK);
                                        }
                                       
                                     }
                              break;
                              case "Robert":
                                 Iterator iRobert = Hand.iterator();
                                  while (iRobert.hasNext()) {
                                    
                                        c = (Card) iRobert.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(RobertHand, o + " " ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(RobertHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(RobertHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(RobertHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(RobertHand, o + " " ,Color.BLACK);
                                        }
                                        
                                     }
                              break;
                              case "Greg":
                                    Iterator iGreg = Hand.iterator();
                                  while (iGreg.hasNext()) {
                                   
                                        c = (Card) iGreg.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(GregHand, o + " " ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(GregHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GregHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GregHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(GregHand, o + " " ,Color.BLACK);
                                        }
                                     
                                     }
                              break;
                              case "Maria":
                                  Iterator iMaria = Hand.iterator();
                                  while (iMaria.hasNext()) {
                                  
                                        c = (Card) iMaria.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(MariaHand, o + " " ,Color.RED);
                                        }
                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(MariaHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(MariaHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(MariaHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(MariaHand, o + " " ,Color.BLACK);
                                        }
                                        
                                        
                                        
                                     }
                              break;
                              case "Alex":
                                   Iterator iAlex = Hand.iterator();
                                  while (iAlex.hasNext()) {
                                    //  AnaHand.
                                      
                                        c = (Card) iAlex.next();
                                        if ("r".equals(c.getColor()))
                                        {
                                        String o = outputType(c.getType());
                                        appendToPane(AlexHand, o + " " ,Color.RED);
                                        }

                                        if ("b".equals(c.getColor()))
                                        {String o = outputType(c.getType());
                                            
                                       appendToPane(AlexHand, o + " " ,Color.BLUE);
                                        }
                                          if ("g".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AlexHand, o + " " ,Color.GREEN);
                                        }
                                          if ("y".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AlexHand, o + " " ,Color.ORANGE);
                                        }
                                          if ("w".equals(c.getColor()))
                                        {
                                            String o = outputType(c.getType());
                                        appendToPane(AlexHand, o + " " ,Color.BLACK);
                                        }
                                        
                                        
                                        
                                     }
                              break;
                          
                          }
    
    
    }
    

    private static void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
         aset = sc.addAttribute(aset, StyleConstants.FontSize,20);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
       
        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
    
     private static String outputType(int type) {
        String out = "";
        
        if (type<10)
          
            
         out = String.valueOf(type);
        
        else{
         switch (type)
         {
            case 10:
                out = "⇄";
            break;
            case 11:
                out = "↺"; 
            break;
            case 12:
                out = "+2";
            break;
            case 13:
                out = "w"; 
            break;
            case 14:
                out = "+4"; 
            break;
         }  
        }    
        return out;    
            
    }
   
}
