package org.vamdc.validator.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.validator.validation.XSAMSValidatorException;

public interface XSAMSIOModel{
	
	/**
	 * Send VSS query to XSAMSSource
	 * @param query query string
	 * @return output document lines count
	 * @throws XSAMSSourceException exception, if any, that occurred during the query
	 */
	public long doQuery(String query) throws XSAMSSourceException,XSAMSValidatorException;
	
	/**
	 * Sends a HEAD request with VSS query to estimate the data
	 * @param query query string
	 * @return map with estimation headers
	 * @throws XSAMSSourceException
	 */
	public Map<HeaderMetrics,String> previewQuery(String query) throws XSAMSSourceException;
	
	/**
	 * Get error info.
	 * Contents depends on source type, starting from 'file not found' or 'plugin not loaded' to all kinds of tapservice errors.
	 * @return string representation of error, null if everything's fine.
	 */
	public String getErrorInfo();
	
	/**
	 * 
	 * @return document size in bytes
	 */
	public long getSize();
	
	/**
	 * 
	 * @return document lines count, 0 if document is not ready
	 */
	public long getLineCount();
	
	/**
	 * Get position of some elements in xsams
	 * @return elements locator that can be used to get position of validation errors, 
	 */
	public DocumentElementsLocator getElementsLocator();
	
	/**
	 * Get block of XSAMS document for display
	 * @param lineIndex starting line, first line is 1
	 * @param lineCount how many lines to return
	 * @return block of XSAMS document
	 */
	public String getBlock(long lineIndex, int lineCount);
	
	/**
	 * Search for string word, starting from line lineOffset
	 * @param word
	 * @param lineOffset
	 * @return line index of found string, 0 if not found.
	 */
	public int searchString(String word, int lineOffset, boolean ignoreCase);
	
	/**
	 * Get an input stream allowing to read the temporary document.
	 * @return the input stream allowing to read the entire document.
	 */
	public InputStream getInputStream();
	

	/**
	 * Close everything
	 */
	public void close();

	/**
	 * Terminate running query
	 */
	public void stopQuery();
	
	/**
	 * Reconfigure
	 * @throws XSAMSSourceException 
	 */
	public void reconfigure() throws XSAMSSourceException,XSAMSValidatorException;
	
	/**
	 * Set progress monitor of Model to ProgressMonitor. Sadly, we can't define limits, so just calling it continuosly.
	 * @param mon
	 */
	public void setProgressMonitor(ProgressMonitor mon);
	
	/**
	 * Get restrictables supported by an xsams source
	 * @return
	 */
	public Collection<String> getRestrictables();
	
	/**
	 * Get sample queries defined by an xsams source
	 * @return
	 */
	public Collection<String> getSampleQueries();
	
	/**
	 * Get preferred processors defined by an xsams source
	 * @return collection of the preferred processor IVOAIDs
	 */
	public abstract Collection<String> getPreferredProcessors();
	
	/**
	 * Load XSAMS document from disk 
	 * @param xsamsDocument file containing XSAMS document
	 * @return loaded lines count
	 * @throws IOException 
	 */
	public long loadFile(File xsamsDocument) throws IOException;
	
	/**
	 * Load XSAMS document from stream
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public long loadStream(InputStream stream) throws IOException;
	
	/**
	 * Save XSAMS document to disk
	 * @param xsamsDocument
	 * @return
	 * @throws IOException 
	 */
	public long saveFile(File xsamsDocument) throws IOException;
	
	/**
	 * Get active query
	 * @return query string as it was defined in doQuery
	 */
	public String getQuery();
	
	/**
	 * Get current filename
	 * @return current filename of the document
	 */
	public String getFilename();

	/**
	 * Set current filename
	 * @param filename current filename of the document (override automatically recognized)
	 */
	public void setFilename(String filename);
	
}
