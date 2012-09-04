package org.vamdc.validator.gui;

import org.vamdc.validator.Setting;

public class HistoryComboBoxImpl extends HistoryComboBox{

	public HistoryComboBoxImpl(String separator, int historyDepth) {
		super(Setting.GUIVOSIHistory,separator, historyDepth);
		savedString=separator;
	}

	private static final long serialVersionUID = -6571160259774371145L;

	private String savedString;
	
	public String getSaved(){
		return savedString;
	}
	
	public void setSaved(String value){
		this.savedString=value;
	}
	
	@Override
	protected String getSavedString() {
		return savedString;
	}

	@Override
	protected void saveString(String value) {
		this.savedString=value;
	}

}
