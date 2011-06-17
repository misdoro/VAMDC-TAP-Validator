package org.vamdc.validator.storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.vamdc.validator.interfaces.DocumentStorage;

public class MemoryStorage extends ByteArrayOutputStream implements DocumentStorage{

	@Override
	public String getBlock(long startByte, int length) {
		byte[] block;
		if (startByte < 0 
				|| startByte > (this.buf.length) 
				|| length <=0 
				|| length > (this.buf.length - startByte)) 
			return "";
		
		block = Arrays.copyOfRange(this.buf, (int)startByte, (int)startByte+length);

		try {
			return new String(block,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String();
		}
	}

	@Override
	public void saveFile(File filename)throws FileNotFoundException, IOException {
		FileOutputStream fos=null;
		IOException ex=null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(this.buf);
		}catch (IOException e) {
			ex=e;
		}finally{
			try {
				if (fos!=null){
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				ex=e;
			}
		}
		if (ex!=null) throw ex;
	}

	@Override
	public long getSize() {
		return (long)this.buf.length;
	}

}
