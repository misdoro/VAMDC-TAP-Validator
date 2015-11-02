package org.vamdc.validator.gui.settings;

public interface SettingControl {

	/**
	 * Load the element backing value
	 */
	public void loadSetting();
	
	/**
	 * Verify if the setting value has changed
	 * @return true if the component state is in sync with the backing @{Link org.vamdc.validator.Setting#getValue()}
	 */
	public boolean verifySetting();
	
	/**
	 * Save the value to the backing setting
	 */
	public void saveSetting();
}
