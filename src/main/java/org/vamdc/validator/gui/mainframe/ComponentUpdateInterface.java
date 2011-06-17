package org.vamdc.validator.gui.mainframe;

import org.vamdc.validator.interfaces.XSAMSIOModel;

/**
 * Interface components must implement to be able to be informed about model changes
 * @author Misha Doronin
 */
public interface ComponentUpdateInterface {
	/**
	 * Bind component to model
	 * @param data XSAMSIOModel to get data from
	 */
	public void setModel(XSAMSIOModel data);
	
	/**
	 * Reset component to it's default state
	 */
	public void resetComponent();
	
	/**
	 * Update data from model
	 * @param isFinal true if it's the final update, document is fully ready
	 */
	public void updateFromModel(boolean isFinal);
	
}
