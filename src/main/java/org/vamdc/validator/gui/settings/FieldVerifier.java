package org.vamdc.validator.gui.settings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * Verify input of text component according to chosen type
 *
 */
public class FieldVerifier extends InputVerifier{
	public static enum Type{
		URL,
		HTTPURL,
		FILE,
		DIR,
		INT
	}

	private Type type;
	private String error;

	/**
	 * It's necessary to set the field verification type!
	 * @param inputType
	 */
	public FieldVerifier(Type inputType){
		this.type = inputType;
		error="";
	}

	/**
	 * Verify field
	 */
	@Override
	public boolean verify(JComponent input) {
		String text = ((JTextComponent)input).getText();
		error = "";
		if (text.length()>0)
			switch(this.type){
			case FILE:
				File file = new File(text);
				error="Expecting readable file.";
				return (file.exists() && file.canRead() && file.isFile());
			case DIR:
				File dir = new File(text);
				error="Expecting writeable directory.";
				return (dir.exists() && dir.canWrite() && dir.isDirectory());
			case URL:
				try{
					new URL(text);
					return true;
				}catch(MalformedURLException e){
					error=e.getMessage();
					return false;
				}
			case HTTPURL:
				try{
					URL http = new URL(text);
					error="Protocol should be HTTP";
					return http.getProtocol().equalsIgnoreCase("http");
				}catch(MalformedURLException e){
					error=e.getMessage();
					return false;
				}
			case INT:
				error="Expecting integer value.";
				try{
					return (text.equals(String.valueOf(Integer.valueOf(text))));
				}catch (NumberFormatException e){
					error=e.getMessage();
					return false;
				}
			}

		return true;
	}

	/**
	 * Display warning if field has an error
	 */
	public boolean shouldYieldFocus(JComponent input){
		if (verify(input))
			return true;
		else{
			return JOptionPane.showConfirmDialog(input, "Input at "+input.getName()+" of type "+type.name()+" is wrong, "+error+". Ignore?", "Input", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
		}
	}

}
