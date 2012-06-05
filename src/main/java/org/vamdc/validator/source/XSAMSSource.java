package org.vamdc.validator.source;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.vamdc.dictionary.HeaderMetrics;

/**
 * Interface between xsams source and saving/validating controller
 * @author Misha Doronin
 */
public interface XSAMSSource{
	
	/**
	 * Get XSAMS XML data stream corresponding to query
	 * @param query VSS query to give to plugin
	 * @return stream of XML data corresponding to query
	 * @throws XSAMSSourceException error occured, if any
	 */
	public abstract InputStream getXsamsStream(String query) throws XSAMSSourceException;
	
	/**
	 * Get supported restrictables
	 * @return collection of restrictables, empty collection if none defined.
	 */
	public abstract Collection<String> getRestrictables();
	
	/**
	 * Get sample queries
	 * @return collection of sample queries: simple, fast and valid
	 */
	public abstract Collection<String> getSampleQueries();
	
	/**
	 * Get estimated result document metrics
	 * @param query VSS query to give to plugin
	 * @return map of header keywords and their integer values
	 * @throws XSAMSSourceException 
	 */
	public abstract Map<HeaderMetrics,String> getMetrics(String query) throws XSAMSSourceException;
	
}
