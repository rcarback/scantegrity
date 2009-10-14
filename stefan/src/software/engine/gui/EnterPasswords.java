/*
 * EnterPasswords.java
 *
 * Created on September 26, 2006, 3:59 PM
 */

package software.engine.gui;

import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.*;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 *
 * @author  rick
 */
public class EnterPasswords extends javax.swing.JDialog {
    
	private static final long serialVersionUID = 1L;
	
	private boolean done=false;
	
	
	/** Creates new form EnterPasswords */
    public EnterPasswords() {
        passwordList = new Vector<String>();
        userList = new Vector<String>();
        meeting = 0;
        parent = null;
        edh = null;
        initComponents();
    }
    
    public EnterPasswords(int meeting, ElectionDataHandler handler, ElectionConsoleInterface parent) throws SAXException, IOException {
        passwordList = new Vector<String>();
        userList = new Vector<String>();
        this.meeting = meeting;
        this.parent = parent;
        this.edh = handler;
        initComponents();
        if (meeting != 1)
        {
        	//jLabel4.setText("Threshold:");
        	jLabel4.setText("Missing:");
            jTextThresh.setText((new Integer(edh.GetKeyFactor())).toString());
            jTextThresh.setEnabled(false);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jUserName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPassword = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextThresh = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18));
        jLabel1.setText("Enter Trustee Information");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel2.setText("Trustee Name:");

        jLabel3.setText("Password:");

        jPassword.setColumns(15);
        //jPassword.addActionListener(new java.awt.event.ActionListener() {
            //public void actionPerformed(java.awt.event.ActionEvent evt) {
              //  ReturnPasswordList(evt);
            //}
        //});

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(42, 42, 42)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jUserName)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel(evt);
            }
        });

        jButton2.setText("Next User");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddPassword(evt);
            }
        });

        jButton3.setText("Done");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReturnPasswordList(evt);
            }
        });

        jTextThresh.setText("0");

        //jLabel4.setText("Missing:");
        jLabel4.setText("Threshold:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jButton1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextThresh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton3))))
                    .add(layout.createSequentialGroup()
                        .add(96, 96, 96)
                        .add(jLabel1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton3)
                    .add(jButton2)
                    .add(jTextThresh, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Cancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel
        passwordList.clear();
        userList.clear();
        this.dispose();
    }//GEN-LAST:event_Cancel

    private void ReturnPasswordList(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReturnPasswordList
        try {
            edh.SetKeyFactor(Integer.parseInt(jTextThresh.getText()));
            this.dispose();

            AddPassword(evt);

            if (meeting >= 1 && meeting <= 4 && passwordList.size() >= 1)
            {            
                //Have the parent process the passwords.
                parent.processKeys(meeting);
                
                done=true;
            }            
        } catch (Exception e) {
            System.out.println("Unable to return password list!");
            e.printStackTrace();
        }
        
    }//GEN-LAST:event_ReturnPasswordList

    private void AddPassword(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddPassword
        String user = jUserName.getText();
        String pass = new String(jPassword.getPassword());
        if (user != "" && pass != "" && edh != null)
        {
            try
            {
                if (edh.AddUser(user, pass))
                {
                    userList.add(user);
                    passwordList.add(pass);            
                }
                else
                {
                    parent.WriteLog("User " + user + " has entered wrong user/pass.");
                }
            }
            catch ( Exception ex )
            {
                parent.WriteLog("User " + user + " has entered wrong user/pass.");
                ex.printStackTrace();
            }
        }
        
        jUserName.setText("");
        jUserName.requestFocusInWindow();
        jPassword.setText("");
    }//GEN-LAST:event_AddPassword
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EnterPasswords().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPassword;
    private javax.swing.JTextField jTextThresh;
    private javax.swing.JTextField jUserName;
    // End of variables declaration//GEN-END:variables
    public Vector<String> passwordList;
    public Vector<String> userList;
    
    public ElectionConsoleInterface parent;
    public ElectionDataHandler edh;
    public int meeting;
    
    public boolean isDone() {
    	return done;
    }
}
