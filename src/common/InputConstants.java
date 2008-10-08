/*
 * Scantegrity - Successor to Punchscan, a High Integrity Voting System
 * Copyright (C) 2006  Richard Carback, David Chaum, Jeremy Clark, 
 * Aleks Essex, Stefan Popoveniuc, and Jeremy Robin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package software.common;

public class InputConstants {
	public static byte[] MK1 = "G7S-)bj^l;q1800]".getBytes();
	public static byte[] MK2 = "K*dst>p9H6c38?[!".getBytes();
	public static byte[] C = "LinuxUsersGroup ".getBytes();		
	
	public static int NoBallots = 200;
	public static int NoDs = 1;
	public static double PercentCheck = 0.5;
	public static double PercentVoted = 0.5;

	public static int Dpi = 150;
	public static int NoCols = 1;
	
	//public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/InvisibleInk/PolkCounty/";
	//public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/POLK COUNTY, FLORIDA NOVEMBER 7, 2000/version6letter/";
	//public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/Crypto08/Captcha/";
	public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/InvisibleInk/WOTE08/";
	//public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/InvisibleInk/lettersONLY/";
	
	//public static String tempDir = "D:/PunchScan2.0/PunchScan2.0/Elections/InvisibleInk/TakomaParkDemo/rick/";	
	public static String ElectionSpec = tempDir+"ElectionSpec.xml";
	public static String Partitions = tempDir+"partitions.xml";
	
	public static String MeetingOneIn = tempDir+"MeetingOneIn.xml";
	public static String MeetingTwoIn = tempDir+"MeetingTwoIn.xml";
	public static String MeetingThreeIn = tempDir+"MeetingThreeIn.xml";
	public static String MeetingFourIn = tempDir+"MeetingFourIn.xml";
	
	public static String MeetingOneOut = tempDir+"MeetingOneOut.xml";
	public static String MeetingTwoOut = tempDir+"MeetingTwoOut.xml";
	public static String MeetingThreeOut = tempDir+"MeetingThreeOut.xml";
	public static String MeetingThreeOutCodes = tempDir+"MeetingThreeOutCodes.xml";
	public static String MeetingFourOut = tempDir+"MeetingFourOut.xml";
	public static String MeetingThreeAndAHalfOut = tempDir+"MeetingThreeAndAHalfOut.xml";
	
	public static String MeetingTwoPrints = tempDir+"MeetingTwoPrints.xml";
	public static String Codes = tempDir+"PrintCodes.xml";
	public static String SeedForCodes = tempDir+"SeedForCodes.xml";
	public static String MeetingTwoCodesCommitments = tempDir+"MeetingTwoOutCommitments.xml";	
	public static String SerialMap = tempDir+"SerialMap.xml";
	public static String ClearTextBallots=tempDir+"ballots/";//"clearBallots.txt";
	
	public static String ContestedCodes = tempDir+"ContestedCodes.xml";
	public static String ReplyToContestedCodes = tempDir+"ReplyToContestedCodes.xml";
	public static String BallotsToBeRetrievedFromTheWarehouse = tempDir+"BallotsToBeRetrievedFromTheWarehouse.xml";
	
	public static String Geometry = tempDir+"geometry.xml";
	public static String PdfTemplate = tempDir+"template.pdf";
	public static String ImageTemplate = tempDir+"background.bmp";
	
	public static String Background = tempDir+"background.pdf";
	public static String JavaCreatedForm = tempDir+"javaCreatedForm.pdf";
	
	public static String PdfFormsDir = tempDir+"pdfBallots/";	
	
	public static String BallotsDir = tempDir+"ballots/";
	public static String BallotsBackupDir = tempDir+"backup/";
	public static String ScannesDir = tempDir+"scannes/";
	
	public static void setTempDir(String tempDir) {
		InputConstants.tempDir=tempDir;
	
		InputConstants.ElectionSpec = tempDir+"ElectionSpec.xml";
		InputConstants.Partitions = tempDir+"partitions.xml";
		
		InputConstants.MeetingOneIn = tempDir+"MeetingOneIn.xml";
		InputConstants.MeetingTwoIn = tempDir+"MeetingTwoIn.xml";
		InputConstants.MeetingThreeIn = tempDir+"MeetingThreeIn.xml";
		InputConstants.MeetingFourIn = tempDir+"MeetingFourIn.xml";
		
		InputConstants.MeetingOneOut = tempDir+"MeetingOneOut.xml";
		InputConstants.MeetingTwoOut = tempDir+"MeetingTwoOut.xml";
		InputConstants.MeetingThreeOut = tempDir+"MeetingThreeOut.xml";
		InputConstants.MeetingThreeOutCodes = tempDir+"MeetingThreeOutCodes.xml";
		InputConstants.MeetingFourOut = tempDir+"MeetingFourOut.xml";
		InputConstants.MeetingThreeAndAHalfOut = tempDir+"MeetingThreeAndAHalfOut.xml";
		
		InputConstants.MeetingTwoPrints = tempDir+"MeetingTwoPrints.xml";
		InputConstants.Codes = tempDir+"PrintCodes.xml";
		InputConstants.MeetingTwoCodesCommitments = tempDir+"MeetingOneOutCodes.xml";		
		InputConstants.SeedForCodes = tempDir+"SeedForCodes.xml";
		InputConstants.SerialMap = tempDir+"SerialMap.xml";
		InputConstants.ClearTextBallots=tempDir+"clearBallots.txt";
		
		InputConstants.ContestedCodes = tempDir+"ContestedCodes.xml";
		InputConstants.ReplyToContestedCodes = tempDir+"ReplyToContestedCodes.xml";		
		InputConstants.BallotsToBeRetrievedFromTheWarehouse = tempDir+"BallotsToBeRetrievedFromTheWarehouse.xml";
		
		InputConstants.Geometry = tempDir+"geometry.xml";
		InputConstants.PdfTemplate = tempDir+"template.pdf";
		
		InputConstants.Background = tempDir+"background.pdf";
		InputConstants.JavaCreatedForm = tempDir+"javaCreatedForm.pdf";
		
		InputConstants.PdfFormsDir = tempDir+"pdfBallots/";
	}	
}
