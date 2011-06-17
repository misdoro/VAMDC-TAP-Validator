package org.vamdc.validator.source.plugin;

import java.util.Map;

import org.vamdc.tapservice.api.DatabasePlug;
import org.vamdc.tapservice.api.RequestInterface;

public class PlugTalker {
	private DatabasePlug plugInst;

	/**
	 * constructor
	 * @param plugClass class name for tested plug
	 */
	public PlugTalker(String plugClass) throws Exception{
		setPlugInst((DatabasePlug) Class.forName(plugClass).newInstance());
	}

	public void setPlugInst(DatabasePlug plugInst) {
		this.plugInst = plugInst;
	}

	public DatabasePlug getPlugInst() {
		return plugInst;
	}
	
	public void buildXSAMS(RequestInterface userRequest) {
		if (getPlugInst() != null)
		getPlugInst().buildXSAMS(userRequest);
	}

	public Map<String,Integer> getRestrictables() {
		if (getPlugInst() == null) return null;
		return getPlugInst().getRestrictables();
	}
	
}
