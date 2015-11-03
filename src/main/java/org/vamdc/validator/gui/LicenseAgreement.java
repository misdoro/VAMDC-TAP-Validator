package org.vamdc.validator.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.vamdc.validator.Setting;

public class LicenseAgreement {

	public static final int LicenseVersion=120701;
	
	public static boolean verify(Component parent){
		
		if (Setting.LicenseVersion.getInt()==LicenseVersion)
			return true;
		
		if (Setting.LicenseVersion.getInt()<LicenseVersion && acceptLicenseAgreement(parent)){
			Setting.LicenseVersion.saveValue(LicenseVersion);
			return true;
		}
		return false;
	}
	
	/**
	 * Show validation panel
	 * @param parent
	 * @return
	 */
	private static Boolean acceptLicenseAgreement(Component parent){
		JEditorPane editorPane = new JEditorPane();
	    editorPane.setEditable(false);
	    URL helpURL = LicenseAgreement.class.getResource("/disclaimer.html");
	      
	    if (helpURL != null) {
	    	try {
	    		editorPane.setPage(helpURL);
	    	} catch (IOException e) {
	    		System.err.println("Attempted to read a bad URL: " + helpURL);
	    	}
	    } else {
	          System.err.println("Couldn't find file: Terms.html");
	    }
	
	    //Put the editor pane in a scroll pane.
	    JScrollPane scrollPane = new JScrollPane(editorPane);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    scrollPane.setPreferredSize(new Dimension(800, 600));
	      
	    Object[] options = { "I accept", "I do not accept" };
	    return JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(parent, scrollPane, "Terms and Conditions",
	    		  			JOptionPane.OK_CANCEL_OPTION , JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
	}
	
}
