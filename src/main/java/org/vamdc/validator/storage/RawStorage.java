package org.vamdc.validator.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.vamdc.validator.Settings;
import org.vamdc.validator.interfaces.DocumentStorage;
import org.vamdc.validator.interfaces.ProgressMonitor;

public class RawStorage extends OutputStream{


	public RawStorage(DocumentStorage backend){
		super();
		if (backend!=null)
			this.out = backend;
		else
			initStorage();
	}

	public RawStorage() {
		super();
		initStorage();
	}



	public RawStorage(ProgressMonitor monitor) {
		super();
		this.monitor=monitor;
		initStorage();
	}

	@Override
	public void write(int b) throws IOException{
		out.write(b);
		checkByte((byte)b,bytes);
		bytes++;
	}

	@Override
	public void write(byte[] b) throws IOException{
		out.write(b);
		//Look for EOLs
		for(int i=0;i<b.length;i++){
			checkByte(b[i],bytes+i);
		}
		//Set new out size
		bytes+=b.length;
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
			checkByte(b[i+off],bytes+i);
			out.write(b[i+off]);
		}
		//Set new out size
		bytes+=len;
	}

	@Override
	public void flush() throws IOException{
		out.flush();
	}

	@Override
	public void close() throws IOException{
		out.flush();
	}

	public String getBlock(long startByte, int length) {
		return out.getBlock(startByte, length);
	}

	public String getLine(long lineIndex) {
		if (lineIndex<0|| lineIndex>=LinePos.size()) return "";
		long curLine = LinePos.get((int)lineIndex);
		long nextLine = -1L;
		if (lineIndex+1<LinePos.size())
			nextLine = LinePos.get((int)lineIndex+1);
		int length = (int)(nextLine-curLine);
		return out.getBlock(curLine, length);
	}

	public String getLines(long lineIndex, int lineCount){
		if (LinePos==null || (LinePos!=null && LinePos.size()<0))
			return "";
		else {
			if (lineCount<=0) lineCount=1;
			lineIndex--;//In LinePos lines are stored starting from 0
			if (lineIndex<0 || lineIndex>=LinePos.size()) 
				lineIndex=0;
			long curLinePos = LinePos.get((int)lineIndex);
			int length =0;
			if (lineIndex+lineCount<LinePos.size())
				length = (int)(LinePos.get((int)lineIndex+lineCount)-curLinePos);
			else
				length = (int) (bytes - curLinePos - 1);
			return out.getBlock(curLinePos, length);
		}
	}

	public long getLineCount() {
		return LinePos.size();
	}

	public void saveFile(File filename)throws IOException {
		out.saveFile(filename);
	}

	public long getSizeBytes() {
		return out.getSize();
	}

	/*
	 * Private block
	 */
	/**
	 * Setup storage, first try disk storage, then if nothing helps, create memory one.
	 */
	private void initStorage() {
		try {
			File tempdir = null;//Temporary directory path specified by user
			File storage = null;//Storage temporary file
			//Try to find directory specified for temp files
			String tempdirpath=Settings.get(Settings.StorageTempPath);
			if (tempdirpath!="" 
				&& (tempdir = new File(tempdirpath))!=null 
				&& tempdir.isDirectory() 
				&& tempdir.canWrite())
				storage = File.createTempFile("xsams", ".xml",tempdir);//Create temp file in specified place
			else
				storage = File.createTempFile("xsams", ".xml");//Create temp file in system default temp 
			storage.deleteOnExit();
			this.out = new FileStorage(storage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//If somehow we haven't got a file storage, keep all in memory.
		if (this.out==null)
			this.out = new MemoryStorage();
	}

	/**
	 * OutputStream to which we are bound
	 */
	private DocumentStorage out;

	/**
	 * Monitor to report to GUI the progress of xsams processing
	 */
	private ProgressMonitor monitor;

	/**
	 * Line start positions in byte array
	 */
	private ArrayList<Long> LinePos = new ArrayList<Long>();

	/**
	 * Total bytes saved
	 */
	private Long bytes = 0L;
	private Long lastEOL = -1L;//-1 here allows us to save 0 as start of first line :)
	private int reportIndex = 0;
	private final static int REPORT_INTERVAL = 5000;
	private byte prevsymbol = 0;

	private void checkByte(byte data, Long position){
		//Check if we had eol before and this symbol is not eol or we have sequental EOLs
		if (lastEOL+1L==position && ((data!= '\n' && data!='\r') || data==prevsymbol || (data=='\r' && prevsymbol=='\n'))){
			//Got new symbol after EOL
			LinePos.add(position);
			//Check reporting
			reportIndex++;
			if (monitor!=null && reportIndex == REPORT_INTERVAL){
				reportIndex=0;
				monitor.tick();
			}
		}
		if (data=='\n' || data=='\r'){
			//Got EOL, move pointer to last EOL
			lastEOL=position;
		}
		if (position == 0 && monitor!=null){
			monitor.started();
		}
		
		prevsymbol = data;
	}

}
