package org.vamdc.validator.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.DocumentStorage;
import org.vamdc.validator.interfaces.ProgressMonitor;

/**
 * Storage controller, able to retrieve document lines
 * 
 */
public class RawStorage extends OutputStream{

	public RawStorage(ProgressMonitor monitor) {
		super();
		this.monitor=monitor;
		initStorage();
	}

	@Override
	public void write(int b) throws IOException{
		storageStream.write(b);
		locator.checkByte((byte)b,savedBytes);
		monitor(savedBytes);
		savedBytes++;
	}

	@Override
	public void write(byte[] b) throws IOException{
		storageStream.write(b);
		//Look for EOLs
		for(int i=0;i<b.length;i++){
			locator.checkByte(b[i],savedBytes+i);
			monitor(savedBytes+i);
		}
		//Set new out size
		savedBytes+=b.length;
	}

	@Override
	public void write(byte[] b,int off, int len) throws IOException{
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		//Look for EOLs
		for(int i=0;i<len;i++){
			locator.checkByte(b[i+off],savedBytes+i);
			monitor(savedBytes+i);
			storageStream.write(b[i+off]);
		}
		//Set new out size
		savedBytes+=len;
	}

	@Override
	public void flush() throws IOException{
		storageStream.flush();
	}

	@Override
	public void close() throws IOException{
		storageStream.flush();
	}

	public String getBlock(long startByte, int length) {
		return storageStream.getBlock(startByte, length);
	}

	public String getLine(int lineIndex) {
		if (!locator.validIndex(lineIndex)) return "";
		long curLine = locator.getPosition(lineIndex);
		long nextLine = -1L;
		int length=0;
		if (locator.validIndex(lineIndex+1)){
			nextLine = locator.getPosition(lineIndex+1);
			length = (int) (nextLine-curLine);
		}else 
			length = (int) (storageStream.getSize()-curLine);
		return storageStream.getBlock(curLine, length);
	}

	/**
	 * Get few lines from the document
	 * @param lineIndex line index,starting from 1
	 * @param lineCount number of lines to return
	 * @return String containing text lines
	 */
	public String getLines(int lineIndex, int lineCount){
		lineIndex--;//In LinePos lines are stored starting from 0
		if (!locator.validIndex(lineIndex)) return "";
		else {
			if (lineCount<=0) lineCount=1;
			int lastLine=lineIndex+lineCount;
			
			long curLinePos = locator.getPosition(lineIndex);
			
			int length =0;
			if (locator.validIndex(lastLine))
				length = (int) (locator.getPosition(lastLine)-curLinePos);
			else
				length = (int) (savedBytes - curLinePos - 1);
			
			return storageStream.getBlock(curLinePos, length);
		}
	}

	public int getLineCount() {
		return locator.getLineCount();
	}

	public void saveFile(File filename)throws IOException {
		storageStream.saveFile(filename);
	}

	public long getSizeBytes() {
		return storageStream.getSize();
	}


	/**
	 * Setup storage, first try disk storage, then if nothing helps, create memory one.
	 */
	private void initStorage() {
		try {
			File tempdir = null;//Temporary directory path specified by user
			File storage = null;//Storage temporary file
			//Try to find directory specified for temp files
			String tempdirpath=Setting.StorageTempPath.getValue();
			if (tempdirpath!="" 
					&& (tempdir = new File(tempdirpath))!=null 
					&& tempdir.isDirectory() 
					&& tempdir.canWrite())
				storage = File.createTempFile("xsams", ".xml",tempdir);//Create temp file in specified place
			else
				storage = File.createTempFile("xsams", ".xml");//Create temp file in system default temp 
			storage.deleteOnExit();
			this.storageStream = new FileStorage(storage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//If somehow we haven't got a file storage, keep all in memory.
		if (this.storageStream==null)
			this.storageStream = new MemoryStorage();
	}

	private LineLocator locator = new LineLocator();
	private DocumentStorage storageStream;
	private ProgressMonitor monitor;
	private Long savedBytes = 0L;

	
	private final static int REPORT_INTERVAL_BYTES = 1048576;


	private void monitor(long position){
		if (monitor==null)
			return;
		if (position == 0)
			monitor.started();
		else if (position%REPORT_INTERVAL_BYTES == 0)
			monitor.tick();
	}

	public InputStream getInputStream() {
		if (this.storageStream!=null)
			return this.storageStream.getInputStream();
		return null;
		
	}

}
