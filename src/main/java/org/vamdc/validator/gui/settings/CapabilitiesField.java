package org.vamdc.validator.gui.settings;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.HistoryComboBox;

public class CapabilitiesField extends HistoryComboBox{

	private static final long serialVersionUID = 7778562723435344834L;

	public CapabilitiesField(){
		super("#",10);	
	}

	@Override
	protected String getSavedString() {
		return Setting.GUICapsURLHistory.getValue();
	}

	@Override
	protected void saveString(String value) {
		Setting.GUICapsURLHistory.setValue(value,true);
	}
	
	
	

}
