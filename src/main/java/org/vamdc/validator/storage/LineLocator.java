package org.vamdc.validator.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Line start position locator. 
 */
public class LineLocator {
	
	private ArrayList<Long> lineStartPositions = new ArrayList<Long>();
	private byte lastByte = '\n';
	

	public List<Long> getLinePositions(){
		return Collections.unmodifiableList(lineStartPositions);
	}
	
	public boolean validIndex(int index){
		return index>=0 && index<lineStartPositions.size();
	}
	
	public Long getPosition(int lineIndex){
		return lineStartPositions.get(lineIndex);
		
	}
	
	public int getLineCount(){
		return lineStartPositions.size();
	}
	
	public void checkByte(byte data,long position){
		if (isStartOfLine(data,position)){
			lineStartPositions.add(new Long(position));
		}
		lastByte=data;
	}
	
	private boolean isStartOfLine(byte data, long position){
		return (lastByte=='\n' || lastByte=='\r') && //Was a new line 
				((data!= '\n' && data!='\r') //non-eol symbol
						|| data==lastByte //sequental \n or \r 
						|| (data=='\r' && lastByte=='\n')); //sequental \r\n
	}
	
}
