package org.vamdc.validator.gui.settings;

import javax.swing.JTextField;

import org.vamdc.validator.Setting;

public class SettingField extends JTextField implements SettingControl{

	private static final long serialVersionUID = -9021148873833748715L;
	
	private final Setting option;
	
	public SettingField(Setting option){
		super();
		this.option = option;
		load();
	}
	
	public void load(){
		this.setText(option.getValue());
	}
	
	public void save(){
		option.setValue(this.getText());
	}
	
}
