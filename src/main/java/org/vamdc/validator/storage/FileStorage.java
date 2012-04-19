package org.vamdc.validator.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.vamdc.validator.interfaces.DocumentStorage;

public class FileStorage extends OutputStream implements DocumentStorage{

	private RandomAccessFile rafile;
	private FileOutputStream out;
	private BufferedOutputStream buf;
	private File file;
	
	
	public FileStorage(File file) throws FileNotFoundException {
		rafile = new RandomAccessFile(file,"r");
		out = new FileOutputStream(file);
		buf = new BufferedOutputStream(out,4096);
		this.file = file;
	}

	@Override
	public String getBlock(long startByte, int length) {
		if (length<=1) return "";
		byte[] block = new byte[length];
		
		try {
			
			rafile.seek(startByte);
			int readLength = rafile.read(block);
			if (readLength<length)
				length=readLength;
			block = Arrays.copyOf(block,length);
			return new String(block,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public void saveFile(File filename) throws IOException {
		FileUtils.copyFile(file, filename);
	}

	@Override
	public long getSize() {
		try {
			return rafile.length();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void write(int b) throws IOException {
		buf.write(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException{
		buf.write(b);
	}
	
	@Override
	public void write(byte[] b,int off, int len) throws IOException{
		buf.write(b, off, len);
	}
	
	@Override
	public void flush() throws IOException{
		buf.flush();
		out.flush();
	}
	
	@Override
	public void close() throws IOException{
		buf.flush();
		out.flush();
		buf.close();
		out.close();
	}

		

}
