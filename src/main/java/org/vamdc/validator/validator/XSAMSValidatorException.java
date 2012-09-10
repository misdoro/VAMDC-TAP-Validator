package org.vamdc.validator.validator;

import java.io.IOException;

/**
 * This exception is thrown by XSAMSSource class during initialization or query if something went wrong.
 * @author Misha Doronin
 *
 */
public class XSAMSValidatorException extends IOException{
	public XSAMSValidatorException(String string) {
		super(string);
	}

	/**
	 *  Generated serial version
	 */
	private static final long serialVersionUID = 4187442303556147545L;

	
	
}
