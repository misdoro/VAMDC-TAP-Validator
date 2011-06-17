package org.vamdc.validator.interfaces;

/**
 * Interface for class reporting document errors
 * @author doronin
 */
public interface DocumentError {
	/**
	 * @return error message
	 */
	public String getMessage();
	
	/**
	 * @return document element caused the error
	 */
	public DocumentElement getElement();
	
}
