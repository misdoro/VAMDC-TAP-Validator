package org.vamdc.validator.interfaces;

/**
 * Progress monitor interface, that can be attached to XSAMSIOModel to track query progress.
 * @author doronin
 *
 */
public interface ProgressMonitor {

	/**
	 * Method called when Model starts fetching XSAMS document
	 */
	public void started();
	
	/**
	 * Method called every 1000 lines/every 1MB of processed input XSAMS document.
	 */
	public void tick();
	
	/**
	 * Method called when XSAMS document becomes ready
	 * @param documentLines lines of document if OK, -1 in case of an error 
	 */
	public void done(long documentLines, String query);
}
