package org.vamdc.validator.gui.settings;

import javax.swing.JCheckBox;

import org.vamdc.validator.Setting;

public class SettingCheckbox extends JCheckBox implements SettingControl{

	private static final long serialVersionUID = -6087260304099747736L;
	
	private Setting option;

	public SettingCheckbox(Setting option,String label){
		super(label);
		this.option = option;
		loadSetting();
	}
	
	public void loadSetting(){
		this.setSelected(option.getBool());
	}
	
	public void saveSetting(){
		option.setValue(this.isSelected());
	}

	@Override
	public boolean verifySetting() {
		return this.isSelected() == option.getBool();
	}
	
}
