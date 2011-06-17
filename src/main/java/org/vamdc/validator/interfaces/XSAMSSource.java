package org.vamdc.validator.interfaces;

import java.io.InputStream;
import java.util.Collection;

/**
 * Interface between xsams source and saving/validating controller
 * @author Misha Doronin
 */
public abstract class XSAMSSource{
	/**
	 * Create XSAMSSource of specified type
	 * @throws XSAMSSourceException
	 */
	public XSAMSSource() throws XSAMSSourceException{
	}
	/**
	 * Get XSAMS XML data stream corresponding to query
	 * @param query VSS1 query to put on plugin
	 * @return stream of XML data corresponding to query
	 * @throws XSAMSSourceException error occured, if any
	 */
	public abstract InputStream getXsamsStream(String query) throws XSAMSSourceException;
	
	/**
	 * Get supported restrictables
	 * @return collection of restrictables, empty collection if none defined.
	 */
	public abstract Collection<String> getRestrictables();
	
}
