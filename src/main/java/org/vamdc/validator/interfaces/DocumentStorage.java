package org.vamdc.validator.interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for document intermediate storage, implemented in FileStorage and MemoryStorage
 * @author doronin
 *
 */
public interface DocumentStorage {
	
	public void write(int b) throws IOException;
	public void write(byte[] b) throws IOException;
	public void write(byte[] b,int off, int len) throws IOException;
	public void flush() throws IOException;
	
	/**
	 * Get block of text starting from specified byte 
	 * @param startByte first byte, starting from 0
	 * @param length bytes to read
	 * @return String with block contents
	 */
	public String getBlock(long startByte, int length);
	
	/**
	 * Save stored XSAMS as file
	 * @param filename where to save XSAMS document
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void saveFile(File filename) throws FileNotFoundException, IOException;
	
	/**
	 * Get size of the stored document
	 * @return size of XSAMS document in bytes
	 */
	public long getSize();
	
	/**
	 * Provide an InputStream to read the document contents
	 * @return
	 */
	public InputStream getInputStream();
	

	
}
