package org.vamdc.validator.source;

import net.ivoa.xml.votable.v1.Info;
import net.ivoa.xml.votable.v1.VOTABLE;

/**
 * This exception is thrown by XSAMSSource class during initialization or query if something went wrong.
 * @author Misha Doronin
 *
 */
public class XSAMSSourceException extends Exception{
	public XSAMSSourceException(String string) {
		super(string);
	}

	public XSAMSSourceException(VOTABLE errorInfo){
		super(convertVOTABLE(errorInfo));
	}
	
	private static String convertVOTABLE(VOTABLE errorInfo){
		if (errorInfo==null)
			return "";
		StringBuilder result = new StringBuilder();
		for (Info info:errorInfo.getRESOURCE().get(0).getINFO()){
			result.append(info.getValue()).append("\n");
		}
		return result.toString();
	}
	
	/**
	 *  Generated serial version
	 */
	private static final long serialVersionUID = 4187442303556147545L;

	
	
}
