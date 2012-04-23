package org.vamdc.validator.source.plugin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.validator.Settings;
import org.vamdc.validator.interfaces.XSAMSSource;
import org.vamdc.validator.interfaces.XSAMSSourceException;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.Input;
import org.vamdc.xsams.util.XSAMSSettings;

public class PluginXSAMSSource extends XSAMSSource{

	private PlugTalker talker = null;
	
	public PluginXSAMSSource(String pluginClass) throws XSAMSSourceException{
		try{
			talker = new PlugTalker(pluginClass);
		}catch (ClassNotFoundException e){
			throw new XSAMSSourceException("Class "+e.getMessage()+" not found");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XSAMSSettings.idPrefix.setStrValue(Settings.get(Settings.PluginIDPrefix));
		XSAMSSettings.processesLimit.setIntValue(Settings.getInt(Settings.PluginLimitProcesses));
		XSAMSSettings.statesLimit.setIntValue(Settings.getInt(Settings.PluginLimitStates));
		
	}

	@Override
	public InputStream getXsamsStream(String query) throws XSAMSSourceException {
		RequestProcess myrequest = new RequestProcess(query,talker.getRestrictables());
		if (!myrequest.isValid())
			throw new XSAMSSourceException("invalid request"+myrequest.toString());
		talker.buildXSAMS(myrequest);
		IOSettings.prettyprint.setIntValue(1);
		return Input.getXSAMSAsInputStream(myrequest.getJaxbXSAMSData());
	}

	@Override
	public Collection<String> getRestrictables() {
		Collection<String> restricts = new ArrayList<String>();
	
		if (talker!=null && talker.getRestrictables()!=null && talker.getRestrictables()!=null)
			for (Restrictable rest:talker.getRestrictables()){
				restricts.add(rest.toString());
			}
		return restricts;
	}

	@Override
	public Collection<String> getSampleQueries() {
		/*Plugin is not responsible for the sample queries generation,
		 * so return an empty ArrayList<String>
		 */
		return new ArrayList<String>();
	}
	
}
