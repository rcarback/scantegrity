package software.engine.gui;

import software.authoring.invisibleink.PrintableBallotMakerWithBarcodes;
import software.common.BallotGeometry;
import software.common.InputConstants;
import software.common.Util;
import software.engine.MeetingFour;
import software.engine.MeetingOne;
import software.engine.invisibleink.Test;
import software.engine.ioExample.MeetingFourIn;


import java.util.*;
import javax.swing.*;

import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;



import java.io.*;

/*
 * PunchscanLive.java
 *
 * This codebase suffers from constantly changing design (at least 19 major times 
 * now).... needs to be rewritten when a final set of specs are drawn up.
 *
 * Created on September 23, 2006, 10:24 AM
 */

/**
 *
 * @author  rick
 */
public class PunchscanLive extends javax.swing.JFrame implements Hasher.HashingProgressMonitor, ElectionConsoleInterface {
    
	private static final long serialVersionUID = 1L;

	private Test testScantegrityII=null;
	
	/** Creates new form PunchscanLive */
    public PunchscanLive() {
        edh = null;
        testScantegrityII=new Test();
        
        initComponents();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        VerifySoftwareButton = new javax.swing.JButton();
        jMeetingOneButton = new javax.swing.JButton();
        jMeetingTwoButton = new javax.swing.JButton();
        jMeetingThreeButton = new javax.swing.JButton();
        jMeetingFourButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaConsole = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Punchscan Live");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        VerifySoftwareButton.setText("Verify Software");
        VerifySoftwareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GetDrives(evt);
            }
        });

        jMeetingOneButton.setText("Initialize Election (Meeting One)");
        jMeetingOneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartMeetings(evt);
            }
        });

        jMeetingTwoButton.setText("Pre-Election Audit (Meeting Two)");
        jMeetingTwoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartMeetings(evt);
            }
        });

        jMeetingThreeButton.setText("Calculate Election Results (Meeting Three)");
        jMeetingThreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartMeetings(evt);
            }
        });

        jMeetingFourButton.setText("Post Election Audit (Meeting Four)");
        jMeetingFourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartMeetings(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18));
        jLabel1.setText("Please Select an Option:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(VerifySoftwareButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jMeetingOneButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jMeetingTwoButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jMeetingThreeButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jMeetingFourButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(VerifySoftwareButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMeetingOneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMeetingTwoButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMeetingThreeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMeetingFourButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jTextAreaConsole.setColumns(20);
        jTextAreaConsole.setEditable(false);
        jTextAreaConsole.setRows(5);
        jScrollPane1.setViewportView(jTextAreaConsole);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        writeFinalAuditLog();
    }//GEN-LAST:event_formWindowClosing
    
    private void StartMeetings(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartMeetings
        int meeting = 0;
        if (evt.getSource() == jMeetingOneButton) meeting = 1;
        else if (evt.getSource() == jMeetingTwoButton) meeting = 2;
        else if (evt.getSource() == jMeetingThreeButton) meeting = 3;
        else if (evt.getSource() == jMeetingFourButton) meeting = 4;

        JFileChooser jFileChooser;
        jFileChooser = new JFileChooser(new File(InputConstants.privateFolder));
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        WriteLog("Getting Private/Public Storage Locations...\n");
        try
        {
            //Ask where to store Private ED
            if (meeting == 1)
                jFileChooser.setDialogTitle("Choose Location to Store Private Election Data");            
            else //Ask where ED is stored
                jFileChooser.setDialogTitle("Election Data Private Storage Location");            

            jFileChooser.setVisible(true);
            int retVal = jFileChooser.showOpenDialog(this);
            
            if (retVal == JFileChooser.APPROVE_OPTION)
            {
                InputConstants.setPrivateFolder(jFileChooser.getSelectedFile().getCanonicalPath()+Util.fileSeparator);
            }
            else
            {
                return;
            }

            //Ask where to store Public ED
            if (meeting == 1)
                jFileChooser.setDialogTitle("Choose Location to store Public Election Data");
            else //Ask where ED is stored
                jFileChooser.setDialogTitle("Election Data Public Storage Location");            

            jFileChooser.setCurrentDirectory(new File(InputConstants.publicFolder));
            jFileChooser.setSelectedFile(new File(InputConstants.publicFolder));
            jFileChooser.setVisible(true);
            retVal = jFileChooser.showOpenDialog(this);
            
            if (retVal == JFileChooser.APPROVE_OPTION)
            {
                InputConstants.setPublicFolder(jFileChooser.getSelectedFile().getCanonicalPath()+Util.fileSeparator);
            }
            else
            {
                return;
            }
        }
        catch (Throwable t)
        {
            WriteLog( t.getMessage() + "\n" );
            t.printStackTrace();
            return;
        }
      
        //Init Election Data Handler/Generator..
        try
        {
            edh = new ElectionDataHandler(InputConstants.publicFolder, InputConstants.privateFolder, meeting);
            WriteLog("Getting Passwords...\n");
            EnterPasswords ph = new EnterPasswords(meeting, edh, this);
            ph.setVisible(true);        
        }
        catch ( Exception ex )
        {
        	ex.printStackTrace();
            WriteLog( ex.getMessage() + "\n" );
        }
    }//GEN-LAST:event_StartMeetings

    private void GetDrives(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GetDrives
        new DriveSelector(this).setVisible(true);
    }//GEN-LAST:event_GetDrives
    
    public void TestDrives(ListModel drives)
    {
        WriteLog( "Checking the following drives:\n" );

        for (int i = 0; i < drives.getSize(); i++)
        {
            WriteLog( "\t" + drives.getElementAt(i) + "\n" );
        }

        WriteLog( "Please wait...\n" );        
        
        if (drives.getSize() <= 1)
        {
            WriteLog( "Error: Verification failed! Not enough drives to do the test!\n" );
        }
        else
        {
            Vector<File> driveVec = new Vector<File>();
            for (int i = 0; i < drives.getSize(); i++)
                driveVec.add( new File( drives.getElementAt(i).toString() ) );
            
            ProgressBar pb = new ProgressBar();
            
            Hasher h = new Hasher( driveVec );
            
            h.addMonitor( this );
            h.addMonitor( pb );
            pb.setHasher( h );
            
            pb.setVisible(true);
            setVisible(false);
            
            h.start();
        }
    }
    
    // Hasher.HashingProgressMonitor members
    public void updateHashingProgress( int current, int maximum )
    {
        // no nothing
    }
    
    public void finishedHashing( Vector<byte[]> hashes, Vector<File> drives, long durationMS )
    {
        boolean failure = false;
        double secs = ( durationMS ) / 1000.0;
        
        setVisible(true);
        
        WriteLog( "(Verification process took " + secs + " seconds)\n");
     
        if ( hashes.size() < drives.size() )
        {
            failure = true;
        
            WriteLog( "WARNING: User prematurely canceled the verification process.\n" );
            
            if ( hashes.size() > 0 )
            {
                WriteLog( "Partial Verification Results:\n" );
            }
        }
        else
        {
            WriteLog( "Verification Results:\n" );
        }
        
        for (int i = 0; i < hashes.size(); i++)
        {
            WriteLog( drives.get(i).getAbsolutePath() + " = " +
                      Hash2String( hashes.get(i) ) + "\n" );
            
            if ( i > 0 && !failure )
            {
                for (int j = 0; j < hashes.get(0).length; j++)
                {
                    if ( hashes.get(0)[j] != hashes.get(i)[j] )
                    {
                        failure = true;
                        break;
                    }
                }
            }
        }
        
        if ( failure )
            WriteLog( "Software does NOT match. Verification FAILED!\n" );
        else
            WriteLog( "Software matches. Verification SUCCESS!\n" );
    }
    
    public static String Hash2String( byte[] hash )
    {
        String s = "0x";
        
        for (int i=0;i<hash.length;i++)
            s += String.format( "%1$02X", hash[i] );
            
        return s;
    } 
    
    public void processKeys(int meeting)
    {
        if (meeting < 1 || meeting > 4) return;
        
        WriteLog("Processing Keys, Please Wait...\n");        
        try
        {
            edh.ProcessKeys();
        }
        catch ( Exception ex )
        {
            WriteLog( ex.getMessage() + "\n" );
            ex.printStackTrace();
            return;
        }
        
        WriteLog("Running Meeting " + meeting + "...");        
        try {
            byte [] superKey = edh.getSuper();
            
            {
	            byte [] s1 = new byte[superKey.length/2];
	            byte [] s2 = new byte[superKey.length/2];
	            
	            System.arraycopy(superKey, 0, s1, 0, superKey.length/2);
	            System.arraycopy(superKey, superKey.length/2, s2, 0, superKey.length/2);
	        	
	            InputConstants.MK1=s1;
	            InputConstants.MK2=s2;
            }
    		
            switch(meeting)
            {
                case 1: {
                	if (InputConstants.FrontEnd.equals(InputConstants.BallotType.SCANTEGRITY_II)) {
                		testScantegrityII.testMeetingOne();
                	} else {                	
                		System.out.println("Not implemented Yet");                	
                	}
                    break;
                }
                case 2:
                	File f=new File(InputConstants.MeetingTwoIn);
                	if (!f.exists()) {
                		int n=JOptionPane.showConfirmDialog(this,
                			    "No input found for MeetingTwo\n"
                			    + "Do you want to generate random input?",
                			    "Input not found",
                			    JOptionPane.YES_NO_OPTION);
                		if (n==JOptionPane.NO_OPTION) {
                			return;
                		}
                		MeetingOne m1 = new MeetingOne(InputConstants.MeetingOneIn);
                		InputConstants.NoBallots=m1.getNumberOfBallots();
                		testScantegrityII.testCreatetMeetingTwoInput();
                	}

                	if (InputConstants.FrontEnd.equals(InputConstants.BallotType.SCANTEGRITY_II)) {
                		testScantegrityII.testMeetingTwo();
                		
                    	//ask if the it should produce the ballots to be printed
                		int n=JOptionPane.showConfirmDialog(this,
                			    "Do you want to generate PDF ballots for printing?",
                			    "Print ballots?",
                			    JOptionPane.YES_NO_OPTION);
                		if (n==JOptionPane.NO_OPTION) {
                			return;
                		}
                		
                		
                		ElectionSpecification es=new ElectionSpecification(InputConstants.ElectionSpec);
                		BallotGeometry geom=new BallotGeometry(InputConstants.Geometry);

                    	PrintableBallotMakerWithBarcodes pbm=new PrintableBallotMakerWithBarcodes(es,geom);
                    	pbm.init(InputConstants.PdfTemplate,InputConstants.MeetingTwoPrints);

                		MeetingOne m1 = new MeetingOne(InputConstants.MeetingOneIn);
                		InputConstants.NoBallots=m1.getNumberOfBallots();
                    	//make ballots in batches
                    	for (int i=0;i<InputConstants.NoBallots*InputConstants.PercentVoted;i+=InputConstants.PrintBatchSize) {
                        	pbm.makePrintableBallots(InputConstants.privateFolder, i,Math.min(i+99, (int)(InputConstants.NoBallots*InputConstants.PercentVoted)));	
                    	}
                		
                	} else {
                		System.out.println("Not implemented Yet");
                	}
                	
                	//ask if the it should produce the ballots to be printed
                    break;
                case 3:
                	f=new File(InputConstants.MeetingThreeIn);                	
                	if (!f.exists()) {
                		Object[] options = {"Read Ballots From Folder",
                                "Generate Random Ballots",
                                "Cancel"};
			            int n = JOptionPane.showOptionDialog(this,
			                "No input found for MeetingThree",
			                "What do you want tot do?",
			                JOptionPane.YES_NO_CANCEL_OPTION,
			                JOptionPane.QUESTION_MESSAGE,
			                null,
			                options,
			                options[0]);
			            if (n==JOptionPane.CANCEL_OPTION)
			            	return;
			            if (n==JOptionPane.NO_OPTION) {//generate random ballots
			            	if (InputConstants.FrontEnd.equals(InputConstants.BallotType.SCANTEGRITY_II)) {
		                		MeetingOne m1 = new MeetingOne(InputConstants.MeetingOneIn);
		                		InputConstants.NoBallots=m1.getNumberOfBallots();
			            		testScantegrityII.testCreateRandomVotedBallots();
			            	}
			            	/*
			            	MeetingOne m1=new MeetingOne(InputConstants.MeetingOneIn);
			            	MeetingThreeIn m3in=new MeetingThreeIn();
			            	m3in.write(m1.getEs(),InputConstants.MeetingThreeIn,InputConstants.MeetingTwoPrints,InputConstants.SerialMap);
			            	*/			            	
			            }
			            else {//read ballots from a folder
			            	JFileChooser jfc=new JFileChooser(InputConstants.publicFolder);
			            	jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			            	jfc.setDialogTitle("Choose the folder with the ballots from the scanner");
			            	jfc.setVisible(true);
			                int retVal = jfc.showOpenDialog(this);
			                if (retVal == JFileChooser.APPROVE_OPTION)
			                {			                	
			                	InputConstants.MeetingThreeIn=jfc.getSelectedFile().getCanonicalPath()+Util.fileSeparator;
			                }
			                else
			                {
			                    return;
			                }
			            }
			            	
                	}
            		if (InputConstants.FrontEnd.equals(InputConstants.BallotType.SCANTEGRITY_II)) {
            			//Ask if create m3in Only or create it and run it.
            			testScantegrityII.revealMarkedSymbols();
            			testScantegrityII.computeResults();
            		} else {
            			System.out.println("Not yet implemented");
            		}			                	                	
                    break;
                case 4:
                    MeetingFour m4 = new MeetingFour(InputConstants.MeetingOneIn);
                    
                	f=new File(InputConstants.MeetingFourIn);                	
                	if (!f.exists()) {
                		int n=JOptionPane.showConfirmDialog(this,
                			    "No input found for MeetingFour\n"
                			    + "Do you want to generate random input?",
                			    "Input not found",
                			    JOptionPane.YES_NO_OPTION);
                		if (n==JOptionPane.YES_OPTION) {
                			MeetingFourIn.write(InputConstants.MeetingThreeOut,InputConstants.MeetingFourIn);
                		} else
                			break;
                	}                	
                    
                	m4.init(InputConstants.MK1, InputConstants.MK2);
                    m4.go(InputConstants.MeetingFourIn, InputConstants.MeetingFourOut);
                    /*
            		int n=JOptionPane.showConfirmDialog(this,
            			    "Do you want to open all the unused ballots?",
            			    "Extra check",
            			    JOptionPane.YES_NO_OPTION);
            		if (n==JOptionPane.YES_OPTION) {
                		String[] m4RevealUnvoted={m3inFile,m2inFile};
            			m4.revealUnvoted(m4RevealUnvoted, m1inFile,m4outAllUnused);
            		}
            		*/
                    break;
                default:
                    WriteLog("Not a proper meeting!\n");
                    break;
            }
            
            edh.CleanUp();
            WriteLog("Done\n");            
        } catch (Exception ex) {
            WriteLog("Error"+ex.getMessage() + "\n");
            ex.printStackTrace();
        } 
        writeFinalAuditLog();
    }
    
    public void writeFinalAuditLog()
    {
        String signature = "ERROR GENERATING SIGNATURE";
        
        try
        {
            if ( edh != null && jTextAreaConsole != null )
                signature = edh.sign( jTextAreaConsole.getText() );
        
            BufferedWriter auditLog = new BufferedWriter(new FileWriter(InputConstants.publicFolder + "/logfile", true));
            
            auditLog.write( "\n\n>>>>> Audit Log written " + Calendar.getInstance().getTime().toString() + " <<<<<\n" );
            if ( jTextAreaConsole != null )
                auditLog.write( jTextAreaConsole.getText() );
            
            auditLog.write( "\n\n---BEGIN RSA SIGNATURE---\n" );
            auditLog.write( signature );
            auditLog.write( "\n---END RSA SIGNATURE---\n" );

            auditLog.close();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            System.exit(-1);
        }
    }
    
    public void WriteLog(String output)
    {
        if ( jTextAreaConsole != null )
            jTextAreaConsole.insert( output, jTextAreaConsole.getText().length() );
        System.out.println( output );
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PunchscanLive().setVisible(true);
            }
        });
    }
    
    private ElectionDataHandler edh;
    //private String publicED="";
    //private String privateED="";
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton VerifySoftwareButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jMeetingFourButton;
    private javax.swing.JButton jMeetingOneButton;
    private javax.swing.JButton jMeetingThreeButton;
    private javax.swing.JButton jMeetingTwoButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaConsole;
    // End of variables declaration//GEN-END:variables
    
}
