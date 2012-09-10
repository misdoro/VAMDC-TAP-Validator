package org.vamdc.validator.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.validator.OperationModes;
import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.io.Input;
import org.vamdc.validator.io.XSAMSDocument;
import org.vamdc.validator.report.XMLReport;
import org.vamdc.validator.validator.XSAMSValidatorException;

/**
 * Command-line interface process.
 * In command-line mode Java TAP-VAMDC plugin testing is not supported(yet)
 * @author doronin
 *
 */
public class CLIProcess {

	public static final int STATUS_ERROR=1;
	public static final int STATUS_PROCESSED=0;
	public static final int STATUS_DONE_NOTHING=-1;

	private int status=STATUS_DONE_NOTHING;

	private int fileCounter=0;
	private File outputDir;
	private final OptionsParser parser;

	public CLIProcess(OptionsParser parser){
		//Check if we are asked to print usage?
		if ( Boolean.TRUE.equals(parser.getOptionValue(parser.printUsage))) {
			parser.printUsage();
			System.exit(status);
		}

		this.parser = parser;
		//If we have output path defined, work in CLI mode
		String outputPath = (String)parser.getOptionValue(parser.outputPath);
		if (outputPath!=null){
			outputDir = new File(outputPath);
			if (outputDir.isDirectory() && outputDir.canWrite()){
				//Force network mode
				Setting.OperationMode.setValue(OperationModes.network.name());
				//Set status
				status = STATUS_PROCESSED;

				XSAMSIOModel doc = new XSAMSDocument();

				try{
					if (loadFiles(doc)==0);

					executeQueries(doc);
				}catch (IOException e){
					e.printStackTrace();
					status = STATUS_ERROR;

				}finally{
					doc.close();
					if (status>0){
						System.exit(status);
					}
				}
			}else{
				status = STATUS_ERROR;
			}

		}
	}

	private int loadFiles(XSAMSIOModel doc) throws IOException {
		final String[] remainder = parser.getRemainingArgs();
		if (remainder.length>0){
			for (String fileName:remainder){
				File file = new File(fileName);
				if (file.exists()&&file.canRead()){
					doc.loadFile(file);
				}else{
					URL loc = new URL(fileName);
					InputStream stream = Input.openStream(loc);
					doc.loadStream(stream);
				}
				status+=saveOutput(doc, fileCounter++);
			}
		}
		return fileCounter;
	}



	private void executeQueries(XSAMSIOModel doc) throws IOException, XSAMSValidatorException {
		Collection<String> queries = getQueries(parser, doc);
		//For each query execute it, validate output and save both document and errors in output path.
		for (String query:queries){

			System.out.println(query);
			doc.doQuery(query);

			status+=saveOutput(doc, fileCounter++);
		}


	}

	private Collection<String> getQueries(OptionsParser parser, XSAMSIOModel doc) {
		Collection<String> queries = new ArrayList<String>();
		String query=null;
		while ((query=(String)parser.getOptionValue(parser.queryString))!=null)
			queries.add(query);
		if (queries.size()==0 && doc.getSampleQueries()!=null)
			queries.addAll(doc.getSampleQueries());
		return queries;
	}

	public int getStatus(){
		return status;
	}

	/**
	 * Save output file and report file to specified output folder
	 */
	private int saveOutput(XSAMSIOModel doc, int index) throws IOException{
		//Save XSAMS document
		File xsamsDocument = new File(outputDir.getAbsolutePath()+File.separator+"xsams"+index+".xml");
		if (!xsamsDocument.exists()){
			System.out.print("Writing "+xsamsDocument.getAbsolutePath()+" ...");
			doc.saveFile(xsamsDocument);
			System.out.println("Done");
		}
		else
			throw new IOException("File"+xsamsDocument.getAbsolutePath()+" already exists!");

		//Save status
		File statusFile = new File(outputDir.getAbsolutePath()+File.separator+"report"+index+".xml");
		if (!statusFile.exists()){
			System.out.print("Writing "+statusFile.getAbsolutePath()+" ...");
			new XMLReport(doc,statusFile,xsamsDocument.getName()).write();
			System.out.println("Done ");
			return doc.getElementsLocator().getErrors().size();
		}
		else
			throw new IOException("File"+statusFile.getAbsolutePath()+" already exists!");
	}

}
