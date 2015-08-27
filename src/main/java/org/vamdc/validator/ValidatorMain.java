package org.vamdc.validator;

import java.io.File;

import jargs.gnu.CmdLineParser;

import org.vamdc.validator.cli.CLIProcess;
import org.vamdc.validator.cli.OptionsParser;
import org.vamdc.validator.gui.mainframe.MainFrame;



public class ValidatorMain{

	/**
	 * Description for about dialog
	 */
	public final static String ABOUT_MESSAGE=
		"(C)VAMDC Project, 2011-2015\n" +
		"http://www.vamdc.eu/\n" +
		"\n" +
		"Author: Misha Doronin\n" +
		"misha@doronin.org\n" +
		"support@vamdc.org\n"
		;
	
	public final static String VERSION = "12.07r1";
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		
		Setting.load();

		OptionsParser parser = new OptionsParser();

		CLIProcess proc=null;

		try {
			parser.parse(args);
		}
		catch ( CmdLineParser.OptionException e ) {
			System.err.println(e.getMessage());
			parser.printUsage();
			System.exit(2);
		}finally {
			//Override some system settings
			parser.overrideSettings();

			//Try CLI processing
			proc = new CLIProcess(parser);
		}

		final String[] remainder = parser.getRemainingArgs();
		
		if (proc.getStatus() == CLIProcess.STATUS_DONE_NOTHING){
			System.out.println("Starting GUI");
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					MainFrame frame = new MainFrame();
					
					if (Setting.GUILogConsole.getBool())
						frame.controller.showLogPanel();
					if (remainder!=null && remainder.length>0)
						frame.controller.asyncLoadFile(new File(remainder[0]));
				}
			});
		}
	}
}
