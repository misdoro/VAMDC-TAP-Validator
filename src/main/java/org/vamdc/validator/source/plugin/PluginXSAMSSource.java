package org.vamdc.validator.source.plugin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.validator.Setting;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.source.XSAMSSource;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.xsams.io.IOSettings;
import org.vamdc.xsams.io.Input;
import org.vamdc.xsams.util.XSAMSSettings;

public class PluginXSAMSSource implements XSAMSSource{

	private PlugTalker talker = null;
	
	public PluginXSAMSSource(String pluginClass) throws XSAMSSourceException{
		try{
			talker = new PlugTalker(pluginClass);
		}catch (ClassNotFoundException e){
			throw new XSAMSSourceException("Class "+e.getMessage()+" not found");
		} catch (Exception e) {
			e.printStackTrace();
		}
		XSAMSSettings.idPrefix.setStrValue(Setting.PluginIDPrefix.getValue());
		XSAMSSettings.processesLimit.setIntValue(Setting.PluginLimitProcesses.getInt());
		XSAMSSettings.statesLimit.setIntValue(Setting.PluginLimitStates.getInt());
		
	}

	@Override
	public InputStream getXsamsStream(String query,XSAMSIOModel document) throws XSAMSSourceException {
		RequestProcess myrequest = prepareRequest(query);
		talker.buildXSAMS(myrequest);
		String filename = XSAMSSettings.idPrefix.getStrValue()+ (new Date().toString().replace(" ", "_")) + ".xsams";
		document.setFilename(filename);
		IOSettings.prettyprint.setIntValue(1);
		return Input.getXSAMSAsInputStream(myrequest.getJaxbXSAMSData());
	}

	private RequestProcess prepareRequest(String query)
			throws XSAMSSourceException {
		RequestProcess myrequest = new RequestProcess(query,talker.getRestrictables());
		if (!myrequest.isValid())
			throw new XSAMSSourceException("invalid request"+myrequest.toString());
		return myrequest;
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
		//Plugin is not responsible for sample queries generation
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getPreferredProcessors() {
		return Collections.emptyList();
	}
	
	@Override
	public Map<HeaderMetrics, String> getMetrics(String query) throws XSAMSSourceException {
		RequestProcess myrequest = prepareRequest(query);
		
		return processMetrics(talker.getMetrics(myrequest));
	}

	private Map<HeaderMetrics, String> processMetrics(
			Map<HeaderMetrics, Object> metrics) {
		Map<HeaderMetrics,String> result = new TreeMap<HeaderMetrics,String>();
		for (HeaderMetrics header:HeaderMetrics.values()){
			Object val = metrics.get(header);
			if (val!=null)
				result.put(header, val.toString());
		}
		return result;
	}


}
