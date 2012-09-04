package org.vamdc.validator.gui.settings;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.HistoryComboBox;

public class CapabilitiesField extends HistoryComboBox implements SettingControl{

	private static final long serialVersionUID = 7778562723435344834L;

	public CapabilitiesField(){
		super(Setting.GUICapsURLHistory,"#",10);	
	}

	@Override
	public void load() {
		super.loadValues();
		this.setText(Setting.ServiceVOSIURL.getValue());
	}

	@Override
	public void save() {
		Setting.ServiceVOSIURL.setValue(this.getText());
		super.saveValue(this.getText());
	}
	
	
	

}
