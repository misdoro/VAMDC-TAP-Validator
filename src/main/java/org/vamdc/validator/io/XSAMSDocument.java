package org.vamdc.validator.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.input.TeeInputStream;
import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.OperationModes;
import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.ProgressMonitor;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.source.XSAMSSource;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.validator.source.http.HttpXSAMSSource;
import org.vamdc.validator.source.plugin.PluginXSAMSSource;
import org.vamdc.validator.storage.RawStorage;
import org.vamdc.validator.validation.Validator;
import org.vamdc.validator.validation.XSAMSValidatorException;
import org.vamdc.xsams.io.PrettyPrint;

public class XSAMSDocument implements XSAMSIOModel{

	@Override
	public long doQuery(String query) throws XSAMSSourceException {
		//Save query string
		this.query = query.trim().replaceAll(";", "");

		if (source==null)
			throw new XSAMSSourceException("XSAMS source is not available");
		//Setup xsams source
		InputStream stream = source.getXsamsStream(this.query,this);
		//Process it
		try {
			return processStream(stream, this.query);
		} catch (IOException e) {
			throw new XSAMSSourceException(e);
		}
	}

	@Override
	public Map<HeaderMetrics, String> previewQuery(String query)
			throws XSAMSSourceException {
		this.query = query.trim().replaceAll(";", "");

		if (source==null)
			throw new XSAMSSourceException("XSAMS source is not available");

		return source.getMetrics(this.query);
	}
	
	@Override
	public String getBlock(long lineIndex, int lineCount) {
		if (storage!=null)
			return storage.getLines((int) lineIndex, lineCount);
		else return "";
	}
	
	@Override
	public InputStream getInputStream() {
		if (storage!=null)
			return storage.getInputStream();
		return null;
	}

	@Override
	public long getLineCount() {
		if (storage!=null)
			return storage.getLineCount();
		else 
			return 0;
	}

	@Override
	public long getSize() {
		if (storage!=null) 
			return storage.getSizeBytes();
		else 
			return 0;
	}

	@Override
	public long loadFile(File xsamsDocument) throws IOException {
		this.setFilename(xsamsDocument.getAbsolutePath());
		return loadStream(new FileInputStream(xsamsDocument));

	}

	@Override
	public long loadStream(InputStream stream) throws IOException {
		//Process the stream
		return processStream(stream, "");
		
	}

	@Override
	public long saveFile(File xsamsDocument)throws IOException {
		storage.saveFile(xsamsDocument);
		return storage.getSizeBytes();
	}

	@Override
	public int searchString(String word, int lineOffset,boolean ignoreCase) {
		if (ignoreCase)
			word = word.toLowerCase();
		if (storage!=null)
			for (int line = lineOffset;line<storage.getLineCount();line++){
				String textLine = storage.getLine(line);
				if (ignoreCase)
					textLine = textLine.toLowerCase();
				if (textLine.contains(word)) 
					return line;
			}
		return -1;
	}

	@Override
	public void setProgressMonitor(ProgressMonitor mon) {
		this.monitor = mon;
	}

	@Override
	public String getErrorInfo() {
		if (errorMsg!=null)
			return errorMsg;
		return "";
	}


	/**
	 * Default constructor
	 */
	public XSAMSDocument(){
		
	}

	private XSAMSSource source;
	private RawStorage storage;
	private Validator validator;
	private ProgressMonitor monitor;
	private InputStream xsamsStream;
	private String errorMsg;
	private String query;
	private String filename;
	
	
	/**
	 * Process xsams stream: copy to temp storage, validate
	 * @param XSAMSStream stream containing XSAMS data
	 * @return
	 * @throws IOException 
	 */
	private long processStream(InputStream incomingStream,String query) throws IOException{
		//Always reset the storage
		storage = new RawStorage(monitor);
		//Reset errors
		errorMsg=null;
		
		//Check if we have an input stream
		if (incomingStream==null){
			monitor.tick();
			monitor.done(0,query);
			errorMsg="Incoming stream is null, please report a bug.";
			xsamsStream = null;
			return 0;
		}
		
		xsamsStream = new BufferedInputStream(incomingStream,4096);
		
		//Pretty-print the stream
		PrettyPrint pretty=null;
		if (Setting.PrettyPrint.getBool()){
			pretty=new PrettyPrint();
			xsamsStream = pretty.transform(xsamsStream);
		}

		//Setup a copying stream to save the document for future navigation
		xsamsStream = new TeeInputStream(xsamsStream,storage);
		
		//Parse stream to find block positions and errors
		try{
			validator.parse(xsamsStream);
			//Parsing is complete, say that we're done.
			if (monitor!=null)
				monitor.done(storage.getLineCount(),query);
		}catch (IOException e){
			System.out.println("Got IOException");
			e.printStackTrace();
			if (monitor!=null)
				monitor.done(-1,query);
			errorMsg = e.getMessage();
		}finally{
			if (xsamsStream!=null){
				xsamsStream.close();
				xsamsStream=null;
			}

			if (storage!=null)
				storage.flush();
		}

		if (pretty!=null && pretty.getTransformException()!=null){
			//If there were exceptions in the prettyprinter, make them pop up
			throw new IOException(pretty.getTransformException());
		}
		//Return document size in lines
		return storage.getLineCount();
	}

	private XSAMSSource setupSource() throws XSAMSSourceException{
		XSAMSSource src = null;
		switch( OperationModes.valueOf(Setting.OperationMode.getValue())){
		case network:
			src = new HttpXSAMSSource();
			break;
		case plugin:
			String pluginClass = Setting.PluginClass.getValue();
			src = new PluginXSAMSSource(pluginClass);
			break;
		default:
			break;
		}
		return src;
	}

	@Override
	public DocumentElementsLocator getElementsLocator() {
		if (validator!=null)
			return validator.getElements();
		return null;
	}

	@Override
	public void close() {

	}

	@Override
	public void stopQuery() {
		if (xsamsStream!=null)
			try {
				xsamsStream.close();
			} catch (IOException e) {
			}
	}

	@Override
	public void reconfigure() throws XSAMSSourceException, XSAMSValidatorException {
		//Stop running queries if any
		stopQuery();
		//Remove source&validator
		source = null;
		validator=null;

		//Setup XSAMS validator
		validator = new Validator(
				Setting.SchemaFile.getValue(),
				Setting.SchemaLocations.getValue());

		//Setup XSAMS source
		source = setupSource();

		monitor.init();
	}

	@Override
	public Collection<String> getRestrictables() {
		if (source!=null)
			return Collections.unmodifiableCollection(source.getRestrictables());
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getSampleQueries() {
		if (source!=null)
			return Collections.unmodifiableCollection(source.getSampleQueries());
		return Collections.emptyList();
	}
	
	@Override
	public Collection<String> getPreferredProcessors() {
		if (source!=null)
			return Collections.unmodifiableCollection(source.getPreferredProcessors());
		return Collections.emptyList();
	}
	

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getSourceStatus() {
		if (this.source!=null)
			return this.source.getStatus();
		return "Source not initialized";
	}












}
