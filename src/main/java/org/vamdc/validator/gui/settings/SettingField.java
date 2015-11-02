package org.vamdc.validator.gui.settings;

import javax.swing.JTextField;

import org.vamdc.validator.Setting;

public class SettingField extends JTextField implements SettingControl{

	private static final long serialVersionUID = -9021148873833748715L;
	
	private final Setting option;
	
	public SettingField(Setting option){
		super();
		this.option = option;
		loadSetting();
	}
	
	@Override
	public void loadSetting(){
		this.setText(option.getValue());
	}
	
	@Override
	public void saveSetting(){
		option.setValue(this.getText());
	}

	@Override
	public boolean verifySetting() {
		return this.getText().equals(option.getValue());
	}
	
}
