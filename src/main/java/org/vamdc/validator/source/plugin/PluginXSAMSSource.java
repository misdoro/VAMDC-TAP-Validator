package org.vamdc.validator.source.plugin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.vamdc.validator.Settings;
import org.vamdc.validator.interfaces.XSAMSSource;
import org.vamdc.validator.interfaces.XSAMSSourceException;

public class PluginXSAMSSource extends XSAMSSource{

	private PlugTalker talker = null;
	private XsamsIO io = null;
	
	public PluginXSAMSSource(String pluginClass) throws XSAMSSourceException{
		try{
			talker = new PlugTalker(pluginClass);
		}catch (ClassNotFoundException e){
			throw new XSAMSSourceException("Class "+e.getMessage()+" not found");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.vamdc.xsams.util.Settings xsamsSettings = org.vamdc.xsams.util.Settings.getSettings();
		xsamsSettings.setIdPrefix(Settings.get(Settings.PluginIDPrefix));
		xsamsSettings.setProcessesLimit(Settings.getInt(Settings.PluginLimitProcesses));
		xsamsSettings.setStatesLimit(Settings.getInt(Settings.PluginLimitStates));
		
		io = new XsamsIO();
	}

	@Override
	public InputStream getXsamsStream(String query) throws XSAMSSourceException {
		RequestProcess myrequest = new RequestProcess(query,talker.getRestrictables());
		if (!myrequest.isValid())
			throw new XSAMSSourceException("invalid request"+myrequest.toString());
		talker.buildXSAMS(myrequest);
		return io.getXSAMSStream(myrequest.getJaxbXSAMSData());
	}

	@Override
	public Collection<String> getRestrictables() {
		if (talker!=null && talker.getRestrictables()!=null && talker.getRestrictables().keySet()!=null)
			return talker.getRestrictables().keySet();
		else
			return new ArrayList<String>();
	}
	
}
