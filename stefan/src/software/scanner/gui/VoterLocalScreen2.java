package software.scanner.gui;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.gwu.voting.standardFormat.electionSpecification.ElectionSpecification;

import software.common.BallotGeometry;
import software.common.Cluster;
import software.common.ImagePanel;
import software.common.Prow;
import software.common.SecurityUtil;
import software.common.Util;
import software.scanner.ScannedBallotInterface;
import software.scanner.ScannedBallotWithPdfOverlaysAndXmlSignatures;

import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.PdfWriter;

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
public class VoterLocalScreen2 extends javax.swing.JFrame implements MouseListener,MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private JPanel jPanel1;
	private JPanel imageStatus;
	private JLabel imageOnLabel;
	private Font fontOnLabel = new Font("Ariel",Font.BOLD,24);
	private JScrollPane jScrollPane;
	private JButton jButtonZoomIn;
	private JButton jButtonZoomOut;
	private JPanel jPanelZoom;
	private JButton browseButton;
	private JPanel browsePanel;	
	
	final JFileChooser fc = new JFileChooser(".");
	BufferedImage image = null;
	//BufferedImage displayedImage = null;
	double zoom = 1.0;
	double rotationAngle = 0;
	private JTextArea jTextAreaXMLBallot;
	private JButton jButtonSpoil;
	private JButton jButtonRotate;
	private JScrollPane jScrollPaneXMLBallot;
	private JButton jButtonAdvanced;
	private JLabel jLabelStatus;
	private JButton jButtonRegister;
	private Font jButtonRegisterFont;

	ScannedBallotInterface sb = null;
	ScannedBallotWithPdfOverlaysAndXmlSignatures sbOverlays=null;
	
	boolean advanced = false;
	
	FileWatcher fileWatcher = null;
	
	private String password = null;
	private SecretKey key = null;
	
	private String folderWhereToWriteVotedBallots = null;
	private String folderWhereToWriteVotedBallotsBACKUP = null;
	private String folderToWatchForNewVotedBallots = null; 
	
	private String errorsLog=null;
	PrintStream errorsLogBos=null;
	
	Hashtable<String,Vector<String>> serialNoToEncBallots = null;
	
	private LinkedList<String> ballotQueue= null;
	
	int noPollWorkers=3;
	String electionID="PunchScan Election";
	String pollingPlaceName = "Polling place 1";
	
	int noSpoiledBallots = 0;
	int noCastBallots = 0;
	
	Vector<Cluster> markedClusters=null;
	
	double perfectWidth=0;
	double perfectHeight=0;
	
	boolean withReports=true;
	boolean withOverlays=true;
	boolean interactive=true;
	
	Cipher cipher=null;
	KeyPair keyPair=null;
	X509Certificate cert=null;	
	
	static {
		 Security.addProvider(new BouncyCastleProvider());
		 Prow.setOperation(Prow.Operation.NONE);
	}	
	
	public static void main(String[] args) throws Exception {
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) { }		

		String dir="C:/Documents and Settings/stefan/Desktop/claimDemocracy/";
		String geometry=dir+"geometry.xml";
		String electionSpec=dir+"ElectionSpec.xml";
		String pass= null;
		String img = null;
			
		BallotGeometry geom=new BallotGeometry(geometry);
		ElectionSpecification es=new ElectionSpecification(electionSpec);
		
		ScannedBallotInterface sb=null;
		
		if (geom.getBottomNode("0", "0", "0")==null)
			if (geom.getSerialTop("1")==null)
				sb = new software.scanner.scantegrity.ScannedBallotWithBarcodes(geom,es);
			else
				sb = new software.scanner.scantegrity.ScannedBallot(geom,es);
		else
			sb = new software.scanner.ScannedBallot(geom,es);

		VoterLocalScreen2 vls = new VoterLocalScreen2(sb,pass,dir+"tempBallots",dir+"scannesColor150spi",false);
		vls.setFolderWhereToWriteVotedBallotsBACKUP(dir+"tempBallotsBACKUP");
		vls.setElectionID(es.getId());
		vls.setPollingPlaceName("testing...");
		vls.setNoPollWorkers(3);
		vls.setPerfectWidth(geom.getWidth());
		vls.setPerfectHeight(geom.getHeight());
		vls.setWithOverlays(false);
		vls.setWithReports(false);
		vls.go(img);
	}
	
	public VoterLocalScreen2(ScannedBallotInterface sb, String pass,String votedBallotsDestFolder, String votedBallotsSourceFolder, boolean newFilesOnly) throws Exception {
		super();
		this.sb=sb;
		setKeys();
		sbOverlays=new ScannedBallotWithPdfOverlaysAndXmlSignatures(sb,keyPair);
				
		setAlwaysOnTop(true);		
		
		password = pass;
		if (password!=null && password.length()>0) {
			if (password.length() > 16)
				password = password.substring(0,16);
			while (password.length() !=16)
				password = password +" ";					
			key = new SecretKeySpec(password.getBytes(),"AES");
			
			try {
				cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE,key);				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}			
		}
				
		File f = new File(votedBallotsDestFolder);
		if (!f.exists())
			f.mkdirs();
		else
			if (!f.isDirectory())
				throw new Exception(f.getAbsolutePath()+" is not a folder");

		setFolderWhereToWriteVotedBallots(f.getAbsolutePath());
		
		f=new File(votedBallotsSourceFolder);
		if (!f.exists())
			f.mkdirs();
		else
			if (!f.isDirectory())
				throw new Exception(f.getAbsolutePath()+" is not a folder");
		
		this.folderToWatchForNewVotedBallots = f.getAbsolutePath();
				
		serialNoToEncBallots = new Hashtable<String,Vector<String>>();
		
		ballotQueue = new LinkedList<String>();
		
		imageOnLabel = new JLabel();
		imageOnLabel.setFont(fontOnLabel);
				
		fileWatcher = new FileWatcher(folderToWatchForNewVotedBallots);
		fileWatcher.setScannNewFilesOnly(newFilesOnly);
		
		//Write the certificate to the folder with the ballots
		String date = (new Date()).toString().replaceAll(":","-");;
		String votedBallotsFileName = folderWhereToWriteVotedBallots
		+"/cert_"+date+".cer";
		OutputStream fos;
		try {			
			fos = new BufferedOutputStream(new FileOutputStream(votedBallotsFileName));
			byte[] certBytes=SecurityUtil.toPEM(cert).getBytes();
			fos.write(certBytes);
			fos.close();
			
			if (folderWhereToWriteVotedBallotsBACKUP!=null) {
				votedBallotsFileName = folderWhereToWriteVotedBallotsBACKUP
				+"/cert_"+date+".cer";
				fos = new BufferedOutputStream(new FileOutputStream(votedBallotsFileName));
				fos.write(certBytes);
				fos.close();				
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void go() {
		if (withReports)
		try {
			produceStartReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			errorsLogBos=new PrintStream(new FileOutputStream(errorsLog));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		fileWatcher.start();
		initGUI();
		this.setVisible(true);
	}
	
	public void go(String img) {
		go();
		if (img!=null) {
			ballotQueue.addLast(img);
			processImage();
		}
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener( new WindowAdapter() {
				   public void windowClosing( WindowEvent e ){
					   close();
				   }
			} ); 			
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				BorderLayout jPanel1Layout = new BorderLayout();
				jPanel1.setPreferredSize(new Dimension(400, 300));
				jPanel1.setLayout(jPanel1Layout);
				{
					browsePanel = new JPanel();
					BorderLayout browsePanelLayout = new BorderLayout();
					browsePanelLayout.setHgap(5);
					jPanel1.add(browsePanel, BorderLayout.NORTH);
					browsePanel.setLayout(browsePanelLayout);
					{
						browseButton = new JButton();
//						browsePanel.add(browseButton, BorderLayout.WEST);
						browseButton.setText("Browse");
						browseButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								int rv = fc.showOpenDialog(new JFrame(
									"Choose a file"));
								if (rv == JFileChooser.APPROVE_OPTION) {
									if (ballotQueue.isEmpty()) {
										ballotQueue.addLast(fc.getSelectedFile().getPath());
										processImage();
									} else {
										ballotQueue.addLast(fc.getSelectedFile().getPath());										
									}
								}
							}
						});
					}
					{
						jButtonRegister = new JButton();
						browsePanel.add(jButtonRegister, BorderLayout.CENTER);
						jButtonRegister.setText("Waiting for a ballot...");
						jButtonRegister.setEnabled(false);
						jButtonRegisterFont = new Font("Ariel",Font.BOLD,22);
						jButtonRegister.setFont(jButtonRegisterFont);
						jButtonRegister.setForeground(new java.awt.Color(82,41,0));
						jButtonRegister.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								registerVote();
							}
						});
					}					
					{
						jPanelZoom = new JPanel();
						browsePanel.add(jPanelZoom, BorderLayout.EAST);
						{
							jButtonZoomOut = new JButton();
							jPanelZoom.add(jButtonZoomOut);
							jButtonZoomOut.setText("-");
							jButtonZoomOut
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									if (zoom > 0.1) {
										zoom -= 0.1;
										zoom();
									}
								}
								});
						}
						{
							jButtonZoomIn = new JButton();
							jPanelZoom.add(jButtonZoomIn);
							jButtonZoomIn.setText("+");
							jButtonZoomIn.setAlignmentY(0.0f);
							jButtonZoomIn
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									if (zoom < 3) {
										zoom += 0.1;
										zoom();
									}
								}
								});
						}
						{
							jButtonRotate = new JButton();
							//jPanelZoom.add(jButtonRotate);
							jButtonRotate.setText("Rotate");
							jButtonRotate.setEnabled(false);
							jButtonRotate
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									rotationAngle += Math.PI/2;
									rotate();
								}
								});
						}
					}
				}
				{
					jScrollPane = new JScrollPane();
					jPanel1.add(jScrollPane, BorderLayout.CENTER);
					{
						jScrollPane.setViewportView(imageOnLabel);
						if (image!=null)
							imageOnLabel.setIcon(new ImageIcon(image));
						imageOnLabel.setVerticalAlignment(SwingConstants.TOP);
						imageOnLabel.setLayout(null);
						imageOnLabel.setName("Mark Detector");
						imageOnLabel.addMouseListener(this);
						imageOnLabel.addMouseMotionListener(this);
					}
				}
				{
					imageStatus = new JPanel();
					BorderLayout imageStatusLayout = new BorderLayout();
					jPanel1.add(imageStatus, BorderLayout.SOUTH);
					imageStatus.setLayout(imageStatusLayout);
					{
						jButtonAdvanced = new JButton();
						imageStatus.add(jButtonAdvanced, BorderLayout.EAST);
						jButtonAdvanced.setText(">>");
						jButtonAdvanced.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {							
								if (advanced == false) {									
									if (!isAuthorized())
										return;
									jButtonAdvanced.setText("<<");
									jPanel1.add(
											jScrollPaneXMLBallot,
											BorderLayout.EAST);
									advanced = true;
								} else {
									jButtonAdvanced.setText(">>");
									jPanel1.remove(jScrollPaneXMLBallot);
									advanced = false;
								}
							}
						});
					}
					{
						jLabelStatus=new JLabel(makeStatusText());
						imageStatus.add(jLabelStatus, BorderLayout.CENTER);
					}
					{
						jButtonSpoil = new JButton();
						imageStatus.add(jButtonSpoil, BorderLayout.WEST);
						jButtonSpoil.setText("Spoil Ballot");
						jButtonSpoil.setEnabled(false);
						jButtonSpoil.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								if (!isAuthorized())
									return;
								spoil(null);
							}
						});
					}
					{
						jScrollPaneXMLBallot = new JScrollPane();
						{
							jTextAreaXMLBallot = new JTextArea();
							jScrollPaneXMLBallot
								.setViewportView(jTextAreaXMLBallot);
						}
					}
				}
			}
			
			fileWatcher.addActionListner(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					if (ballotQueue.isEmpty()) {
						ballotQueue.addLast(evt.getActionCommand());
						processImage();
					} else {
						ballotQueue.addLast(evt.getActionCommand());						
					}
					
				}						
			});

			
			pack();
			setSize(670, 700);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void registerVote() {
		Vector<String> v = new Vector<String>();
		v.add(ballotQueue.removeFirst());
		serialNoToEncBallots.put(sb.getSerial(),v);
		
		try {
			writeOnDiskEncrypted();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		
		if (!sb.isMailIn() && withOverlays) {
			String overlayFile="overlay_"+sb.getSerial()+".pdf";
			sbOverlays.makePdfOverlay(overlayFile);
			Util.printPdfSilently(overlayFile);
		}
		jButtonRegister.setText("Waiting for a ballot...");
		jButtonRegister.setEnabled(false);
		jButtonSpoil.setEnabled(false);
		jTextAreaXMLBallot.setText("");
		if (!sb.isMailIn()) {
			imageOnLabel.setIcon(null);
			imageOnLabel.setText("Thank you for voting");
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!ballotQueue.isEmpty())
			processImage();
	}

	private String makeStatusText() {
		return "Cast:"+noCastBallots+" Spoiled:"+noSpoiledBallots;
	}

	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	public void 	mouseExited(MouseEvent e) {
		
	}
	
	public void 	mousePressed(MouseEvent e) {
		
	}
	public void 	mouseReleased(MouseEvent e) {
		
	}
	 public void 	mouseDragged(MouseEvent e) {
		 
	 }

	public void mouseMoved(MouseEvent evt) {
		try {
		int x = evt.getX();
		int y = evt.getY();
		for (int i=0;i<markedClusters.size();i++) {
			Cluster c = markedClusters.get(i);
			if (x>=c.getXmin()*zoom && x<=c.getXmax()*zoom && y>=c.getYmin()*zoom && y<=c.getYmax()*zoom) {
				imageOnLabel.setToolTipText(c.toString());				
				return;
			}
		}
		} catch (NullPointerException e) {
			
		}
		imageOnLabel.setToolTipText("");		
		//imageOnLabel.setToolTipText(toolTip);
	}
	public void emphasizeClusters(Vector<Cluster> v,String s, Font f,Color color) {		
		//BufferedImage duplicate = image;//ImagePanel.duplicate(image);
		Graphics g = image.getGraphics();
		for (int i=0;i<v.size();i++) {
			Cluster c = new Cluster(v.get(i));
			c.scale(sb.getDpi());
			ImagePanel.drawChar(g,c.getCenterOfMass(),s,f,color);
		}
		//AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(zoom,zoom),AffineTransformOp.TYPE_BILINEAR);
		//displayedImage = op.filter(duplicate,null);		
		//imageOnLabel.setIcon(new ImageIcon(image));		
		this.repaint();
	}
	
	public void detect() throws Exception {		
		try {
			sb.detect(image);
		} catch (Exception e) {
			//maybe the image is upsidedown
			image =(new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI,image.getWidth()/2,image.getHeight()/2),AffineTransformOp.TYPE_BILINEAR)).filter(image,null);
			try {
				sb.detect(image);
			} catch(Exception e1) {
				throw new Exception(e.getMessage()+"\nRotating the image\n"+e1.getMessage());
			}
		}		
	}
	
	public void zoom() {
		AffineTransformOp op = new AffineTransformOp(
			AffineTransform.getScaleInstance(
				zoom,
				zoom),
			AffineTransformOp.TYPE_BILINEAR);

		image = op.filter(image, null);
		//imageOnLabel.setIcon(new ImageIcon(image));		
	}

	public void rotate() {
		AffineTransformOp op = new AffineTransformOp(
			AffineTransform.getRotateInstance(rotationAngle,image.getWidth()/2,image.getHeight()/2),AffineTransformOp.TYPE_BILINEAR);

		image = op.filter(image, null);
		//imageOnLabel.setIcon(new ImageIcon(image));		
	}
/*	
	public void writeOnDisk() {
		//Write the balot in a file
		Date date = new Date();
		String votedBallotsFileName = folderWhereToWriteVotedBallots+"BallotNO_"+sb.getSerial()+"_"+date.toString()+".xml";
		votedBallotsFileName = votedBallotsFileName.replaceAll(":","-");
		OutputStream fos;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(votedBallotsFileName));
			fos.write(sb.toXMLString());
			fos.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/	
	public void writeOnDiskEncrypted() throws Exception {
		byte[] encryptedBallot = sbOverlays.getSignedXml();
		if (key!=null) {
			try {
				encryptedBallot = cipher.doFinal(encryptedBallot);		
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
		}
		//Write the balot in a file
		String date = (new Date()).toString().replaceAll(":","-");
		String nameOfFileScanned=new File(serialNoToEncBallots.get(sb.getSerial()).lastElement()).getName();
		String votedBallotsFileName = folderWhereToWriteVotedBallots +
		"/"+nameOfFileScanned+"_"+sb.getSerial()+".enc";
		//"/BallotNO_"+sb.getSerial()+"_"+date+".enc";
		OutputStream fos;
		try {			
			fos = new BufferedOutputStream(new FileOutputStream(votedBallotsFileName));
			fos.write(encryptedBallot);
			fos.close();
			
			if (folderWhereToWriteVotedBallotsBACKUP!=null) {
				votedBallotsFileName = folderWhereToWriteVotedBallotsBACKUP
				+"/BallotNO_"+sb.getSerial()+"_"+date+".enc";
				fos = new BufferedOutputStream(new FileOutputStream(votedBallotsFileName));
				fos.write(encryptedBallot);
				fos.close();				
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		noCastBallots++;
		jLabelStatus.setText(makeStatusText());		
	}

	public String getFolderToWatchForNewVotedBallots() {
		return folderToWatchForNewVotedBallots;
	}

	public void setFolderToWatchForNewVotedBallots(
			String folderToWatchForNewVotedBallots) {
		this.folderToWatchForNewVotedBallots = folderToWatchForNewVotedBallots;
	}

	public String getFolderWhereToWriteVotedBallots() {
		return folderWhereToWriteVotedBallots;
	}

	public void setFolderWhereToWriteVotedBallots(
			String folderWhereToWriteVotedBallots) {
		this.folderWhereToWriteVotedBallots = folderWhereToWriteVotedBallots;
		this.errorsLog=this.folderWhereToWriteVotedBallots+"/errors.log";
	}

	public String getFolderWhereToWriteVotedBallotsBACKUP() {
		return folderWhereToWriteVotedBallotsBACKUP;
	}

	public void setFolderWhereToWriteVotedBallotsBACKUP(
			String folderWhereToWriteVotedBallotsBACKUP) throws Exception {
		this.folderWhereToWriteVotedBallotsBACKUP = folderWhereToWriteVotedBallotsBACKUP;
		File f=new File(folderWhereToWriteVotedBallotsBACKUP);
		if (!f.exists())
			f.mkdirs();
		else
			if (!f.isDirectory())
				throw new Exception(f.getAbsolutePath()+" is not a folder");
		
	}

	public boolean isAuthorized() {
		if (password==null || password.length()==0)
			return true;
		setAlwaysOnTop(false);
		 String inputValue = "";
		 while (inputValue.trim().compareTo(password.trim()) !=0 ) {
			 inputValue = JOptionPane.showInputDialog("Password");
			 if (inputValue==null) {
				 setAlwaysOnTop(true);
				 return false;
			 }
		 }
		 setAlwaysOnTop(true);
		 return true;
	}

	public void processImage() {
		String filePath = ballotQueue.getFirst();
		try {
			int notries=0;
			boolean exception=false;
			while (notries<10) {
				exception=false;
				try {
					image = ImageIO.read(new File(filePath));
				} catch (IIOException iioex) {
					try {
						Thread.sleep(500);
						exception=true;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (exception)
					notries++;
				else
					notries=11;
			}
		} catch (IOException e) {
			e.printStackTrace();
			setAlwaysOnTop(false);
			if (isInteractive())
				JOptionPane
				.showMessageDialog(
					null,
					"Error in reading the image \n"+e.getMessage(),
					"alert",
					JOptionPane.ERROR_MESSAGE);
			setAlwaysOnTop(true);
			return;
		}
		// convert the image to a color image
/*		
		ColorSpace gray_space =
		     ColorSpace.getInstance (ColorSpace.CS_LINEAR_RGB);
		  ColorConvertOp convertToGrayOp = new ColorConvertOp ( gray_space, null);
		  image = convertToGrayOp.filter (image, null);		
*/		
		
		imageOnLabel.setText("");
		try {			
			detect();					
		} catch (Exception e1) {
			spoil(e1);
			setAlwaysOnTop(true);
			return;							
		}	

		image=sb.getPerfectImage();
				
		//emphasizeClusters(sb.getAlignment(), ""+'\uf055', f, Color.BLUE);
		Font f = new Font("Wingdings 2", Font.BOLD,82);		
		markedClusters=new Vector<Cluster>();
		markedClusters.addAll(sb.getFullVotes());
		markedClusters.addAll(sb.getUnderVotes());
		
		emphasizeClusters(markedClusters,""+'\uf050',f,Color.GREEN);
		//emphasizeClusters(markedClusters,""+'\uf058',f,Color.GREEN);
		emphasizeClusters(sb.getOverVotes(),""+'\uf058',f,Color.BLUE);
		jTextAreaXMLBallot.setText(sb.toProw().toString());		
		
		zoom=(double)this.getHeight()/(double)image.getHeight();
		zoom();
		//zoom = 1;
		try {
		imageOnLabel.setIcon(new ImageIcon(
				image));
		} catch (Exception e) {
			spoil(e);
			return;
		}		
		
		String s = sb.getSerial();
		Vector<String> v = serialNoToEncBallots.get(s);
		if (v==null)  {
			jButtonRegister.setText("Cast ballot number "+sb.getSerial());
			jButtonRegister.setEnabled(true);
			jButtonSpoil.setEnabled(true);
		} else {
			spoil(new Exception("You've scanned ballot number |"+s+"| "+v.size()+" more times. Thank you for checking it again."));
			v.add(filePath);
			return;
		}
		
		if (!isInteractive())
			registerVote();
	}
	
	public void spoil(Exception e) {
		setAlwaysOnTop(false);
		if (isInteractive() && e!=null) {			
			JOptionPane
			.showMessageDialog(
				null,
				e.getMessage()+"\nThis ballot will be automatically spoiled",
				"alert",
				JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
		}
		
		errorsLogBos.print("Error on file "+ballotQueue.getFirst()+"\n");
		e.printStackTrace(errorsLogBos);
		errorsLogBos.print("\n");
		errorsLogBos.flush();
		
		noSpoiledBallots++;
		jTextAreaXMLBallot.setText("");
		imageOnLabel.setIcon(null);
		ballotQueue.removeFirst();
		jButtonRegister.setEnabled(false);								
		jButtonSpoil.setEnabled(false);
		jLabelStatus.setText(makeStatusText());
		if (!ballotQueue.isEmpty()) {
			processImage();
		} else {
			jButtonRegister.setText("Waiting for a ballot...");																	
		}
		setAlwaysOnTop(true);
	}
	
	public void produceStartReport() throws Exception {		
		String fileName= "BeginingReport.pdf";
		
		Calendar now=Calendar.getInstance();
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		PdfWriter.getInstance(document,new FileOutputStream(fileName));
		document.open();
		Paragraph p=new Paragraph("POLLING PLACE OPENING REPORT");
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);
		document.add(new Paragraph("Election ID: "+electionID));
		document.add(new Paragraph("Polling place name: "+pollingPlaceName));
		document.add(new Paragraph("Time of printing:"+now.getTime()));
		document.add(new Paragraph("Public key:"));
		String certPEM=SecurityUtil.toPEM(cert);
		document.add(new Paragraph(certPEM));
		
		BarcodePDF417 codeEAN = new BarcodePDF417();
		codeEAN.setText(certPEM);
		Image imageEAN = codeEAN.getImage();
		document.add(imageEAN);		
		
		for (int i=0;i<noPollWorkers;i++)
			document.add(new Paragraph("Poll worker (print name) "+(i+1)+": ________________________	Signature____________________"));
		document.add(new Paragraph(""));
		document.close();
		
		Util.printPdfPrintDialog(fileName);
	}
	
	public void produceStopReport() throws Exception {
		String fileName= "ClosingReport.pdf";
		
		Calendar now=Calendar.getInstance();
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		PdfWriter.getInstance(document,new FileOutputStream(fileName));
		document.open();
		Paragraph p=new Paragraph("POLLING PLACE CLOSING REPORT");
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);
		document.add(new Paragraph("Election ID: "+electionID));
		document.add(new Paragraph("Polling place name: "+pollingPlaceName));
		document.add(new Paragraph("Time of printing:"+now.getTime()));
		document.add(new Paragraph("Total number of cast ballots: "+noCastBallots));
		document.add(new Paragraph("Total number of spoiled ballots: "+noSpoiledBallots));
		
		document.add(new Paragraph("PKCS#7 signature of :"+("Cast: "+noCastBallots+" Spoiled:"+noSpoiledBallots)));
		
		//instead of certificate put the signature of cast ballots and spoiled ballots Base64 and 2D barcode
        CMSProcessableByteArray cmsFile=new CMSProcessableByteArray(("Cast: "+noCastBallots+" Spoiled:"+noSpoiledBallots).getBytes());
        CMSSignedDataGenerator gen=new CMSSignedDataGenerator();
        gen.addSigner(keyPair.getPrivate(), cert, CMSSignedGenerator.DIGEST_SHA256);
        CMSSignedData signed = gen.generate(cmsFile, "BC");		
        
		document.add(new Paragraph(new String(Base64.encode(signed.getEncoded()))));
		
		BarcodePDF417 codeEAN = new BarcodePDF417();
		codeEAN.setText(signed.getEncoded());
		Image imageEAN = codeEAN.getImage();
		document.add(imageEAN);		
		
		for (int i=0;i<noPollWorkers;i++)
			document.add(new Paragraph("Poll worker "+(i+1)+": ________________ 		Signature____________________"));
			document.add(new Paragraph(""));
		document.close();
		
		Util.printPdfPrintDialog(fileName);
	}
	
	
	void setKeys() throws Exception {
		//generate the private key
		KeyPairGenerator  kpGen = KeyPairGenerator.getInstance("RSA", "BC");
        kpGen.initialize(1024, new SecureRandom());
        keyPair = kpGen.generateKeyPair();
		cert=makeCert();
	}
	
	
	protected X509Certificate makeCert() throws Exception {
        X509V3CertificateGenerator  certGen = new X509V3CertificateGenerator();
        
		String cnName=electionID+" - "+pollingPlaceName; 
		
        X500Principal cn=new X500Principal("CN="+cnName);
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(cn);
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 50000));
        long validity=60*60*24;//30 days
        certGen.setNotAfter(new Date(System.currentTimeMillis() + validity*1000));
        certGen.setSubjectDN(cn);
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        
        certGen.addExtension(X509Extensions.SubjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, "NoPollworkers: "+noPollWorkers)));

        X509Certificate cert = certGen.generate(keyPair.getPrivate(), "BC");
        
        return cert;        
	}	
	
	public String getElectionID() {
		return electionID;
	}

	public void setElectionID(String electionID) {
		this.electionID = electionID;
	}

	public int getNoPollWorkers() {
		return noPollWorkers;
	}

	public void setNoPollWorkers(int noPollWorkers) {
		this.noPollWorkers = noPollWorkers;
	}

	public String getPollingPlaceName() {
		return pollingPlaceName;
	}

	public void setPollingPlaceName(String pollingPlaceName) {
		this.pollingPlaceName = pollingPlaceName;
	}

	public double getPerfectWidth() {
		return perfectWidth;
	}

	public double getPerfectHeight() {
		return perfectHeight;
	}
	
	public void setPerfectWidth(double perfectWidth) {
		this.perfectWidth = perfectWidth;
	}

	public void setPerfectHeight(double perfectHeight) {
		this.perfectHeight = perfectHeight;
	}

	public boolean isWithOverlays() {
		return withOverlays;
	}

	public void setWithOverlays(boolean withOverlays) {
		this.withOverlays = withOverlays;
	}

	public boolean isWithReports() {
		return withReports;
	}

	public void setWithReports(boolean withReports) {
		this.withReports = withReports;
	}

	public int getNoCastBallots() {
		return noCastBallots;
	}

	public int getNoSpoiledBallots() {
		return noSpoiledBallots;
	}
	
	public void close() {
		   if (isAuthorized()) {
			   fileWatcher.setRunning(false);
			   if(withReports)
			   try {
				   produceStopReport();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				errorsLogBos.close();
	           setVisible(false);
	           dispose();
	           //System.exit(-1);
		   }		
	}

	public boolean isInteractive() {
		return interactive;
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

}
