package org.vamdc.validator.source.plugin;

import java.util.Collection;
import java.util.Map;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.tapservice.api.DatabasePlugin;
import org.vamdc.tapservice.api.RequestInterface;

public class PlugTalker {
	private DatabasePlugin plugInst;

	/**
	 * constructor
	 * @param plugClass class name for tested plug
	 */
	public PlugTalker(String plugClass) throws Exception{
		setPlugInst((DatabasePlugin) Class.forName(plugClass).newInstance());
	}

	public void setPlugInst(DatabasePlugin plugInst) {
		this.plugInst = plugInst;
	}

	public DatabasePlugin getPlugInst() {
		return plugInst;
	}
	
	public void buildXSAMS(RequestInterface userRequest) {
		if (getPlugInst() != null)
		getPlugInst().buildXSAMS(userRequest);
	}

	public Collection<Restrictable> getRestrictables() {
		if (getPlugInst() == null) return null;
		return getPlugInst().getRestrictables();
	}
	
	public Map<HeaderMetrics, Object> getMetrics(RequestInterface userRequest){
		if (getPlugInst() == null) return null;
		return getPlugInst().getMetrics(userRequest);
	}
	
}
