package org.vamdc.validator;

import java.io.File;
import java.io.IOException;

import jargs.gnu.CmdLineParser;

import org.vamdc.validator.cli.CLIProcess;
import org.vamdc.validator.cli.OptionsParser;
import org.vamdc.validator.gui.mainframe.MainFrame;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.iocontroller.XSAMSDocument;



public class ValidatorMain{

	/**
	 * Description for about dialog
	 */
	public final static String ABOUT_MESSAGE=
		"(C)VAMDC Project, 2011-2012\n" +
		"http://www.vamdc.eu/\n" +
		"\n" +
		"Author: Misha Doronin\n" +
		"misha@doronin.org\n" +
		"support@vamdc.org"
		;
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
					XSAMSIOModel doc = new XSAMSDocument();
					MainFrame frame = new MainFrame(doc);
					frame.setVisible(true);
					if (Setting.GUILogConsole.getBool())
						frame.controller.showLogPanel();
					if (remainder!=null && remainder.length>0)
						try {
							doc.loadFile(new File(remainder[0]));
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			});
		}
	}
}
