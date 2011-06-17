package org.vamdc.validator.cli;

import org.vamdc.validator.Settings;

/**
 * Init options, parse, override settings
 * @author doronin
 *
 */
public class OptionsParser extends CommandOptions{

	public OptionsParser(){
		super();
		
		//Init options
		serviceTap = this.addHelp(this.addStringOption('u', "tap_url"), 
				"TAP-VAMDC service TAP endpoint URL\n " +
				"	e.g. -u http://service:port/TAP/\n" +
				"	-u http://service:port/tap/");
		
		serviceCaps = this.addHelp(this.addStringOption('c', "capabilities_url"), 
				"TAP-VAMDC service capabilities endpoint URL, \n" +
				"	e.g. -c \"http://service:port/VOSI/capabilities\"\n" +
				"	or -c \"http://service:port/tap/capabilities/\"\n" +
				"	Only first specified service will be used.");
		
		servicePrettyOut = this.addHelp(this.addBooleanOption('p', "prettyprint"),
				"Re-format input XML");	
		
		tempPath = this.addHelp(this.addStringOption('t', "temp_dir"),
				"Directory for temporary files.\n" +
				"	Usually, system default is used. \n" +
				"	If this directory is not writeable, using memory storage");
		
		schemaLocation = this.addHelp(this.addStringOption('s', "schemalocation"), 
				"No namespace Schema location to validate output against");
		
		namespaceLocation = this.addHelp(this.addStringOption('n', "nsschemalocation"), 
				"Space-separated list of pairs of namespace url and \n" +
				"relevant schema location to validate output against");
		
		queryString = this.addHelp(this.addStringOption('q',"query"), 
				" VSS1/VSS2 Query to send to service. \n" +
				"	Can be specified multiple times to do multiple queries on the same tapservice");
		
		outputPath = this.addHelp(this.addStringOption('o', "output_dir"),
				"Folder where to save result files. \n" +
				"	Needs to be specified to initiate command-line mode");
	
		printUsage = this.addHelp(this.addBooleanOption('h', "help"), 
				"Print usage and exit");
	} 
	
	/**
	 * Print usage and exit;
	 */
	public final Option printUsage;
	
	/**
	 * Tap service TAP endpoint URL
	 * !not yet implemented!
	 */
	public final Option serviceTap;
	
	/**
	 * Tap service VOSI capabilities endpoint URL
	 */
	public final Option serviceCaps;
	
	/**
	 * Whether do pretty-printing of input XML or not
	 */
	public final Option servicePrettyOut;
	
	/**
	 * Temporary file location
	 */
	public final Option tempPath;
	
	/**
	 * Schema location to validate against
	 */
	public final Option schemaLocation;
	
	/**
	 * per-namespace schema locations
	 */
	public final Option namespaceLocation;
	
	/**
	 * Query string, can be specified multiple times. 
	 */
	public final Option queryString;
	
	/**
	 * Output data path
	 */
	public final Option outputPath;
	
	
	
	/**
	 * Override some settings based on passed options:
	 */
	public void overrideSettings(){
		//String value = null;
		//Service URL		
		Settings.override(Settings.ServiceTAPURL,this.getOptionValue(serviceTap));
		Settings.override(Settings.ServiceVOSIURL,this.getOptionValue(serviceCaps));
		Settings.override(Settings.SchemaFile, this.getOptionValue(schemaLocation));
		Settings.override(Settings.SchemaLocations, this.getOptionValue(namespaceLocation));
		Settings.override(Settings.ServicePrettyOut, this.getOptionValue(servicePrettyOut));
		Settings.override(Settings.StorageTempPath, this.getOptionValue(tempPath));

	}
	
}
