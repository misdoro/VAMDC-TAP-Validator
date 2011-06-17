package org.vamdc.validator.cli;

import java.util.ArrayList;
import java.util.List;

import jargs.gnu.CmdLineParser;

/**
 * Verbose jargs CmdLineParser.
 * Taken from examples.
 * @author doronin
 */
public class CommandOptions extends CmdLineParser{
	List<String> optionHelpStrings = new ArrayList<String>();

	public Option addHelp(Option option, String helpString) {
		String helpBlock = "";
		helpBlock+=" -" + option.shortForm() + ", --" + option.longForm() + " ";
		if (option.wantsValue())
			helpBlock+=" \"value\" ";
		helpBlock+=": "+ helpString+ "\n";
		
		optionHelpStrings.add(helpBlock);
		return option;
	}
	
	public void printUsage() {
        System.err.println("Usage: prog [options]");
        System.err.println("Available options:");
        for (String row:optionHelpStrings) {
        	System.err.println(row);
        }
    }
}
