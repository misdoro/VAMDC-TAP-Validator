package org.vamdc.validator.gui.settings;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.mainframe.MainFrameController;

public class SettingsPanelController extends WindowAdapter implements ActionListener{

	private MainFrameController main;
	private SettingsPanel panel;
	private Dialog settingsDialog;

	public SettingsPanelController(MainFrameController main,SettingsPanel panel,Dialog parentDialog){
		this.main  = main;
		this.panel = panel;
		
		this.settingsDialog = parentDialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == SettingsPanel.CMD_SAVE){
			handleSaveSettings();
		}else if (command == SettingsPanel.CMD_RESET){
			//Reload settings (clear changes)
			if (JOptionPane.showConfirmDialog(panel, "Revert all changes?", "Settings", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION){	
				panel.loadSetting();
			}
		}else if (command == SettingsPanel.CMD_DEFAULTS){
			//Reset settings to defaults
			if (JOptionPane.showConfirmDialog(panel, "Load defaults for all options?", "Settings", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION){	

				Setting.reset();
				panel.loadSetting();
			}
		}


	}



	@Override
	public void windowClosing(WindowEvent e) {
		if (!panel.verifySetting()){
			int confirm = JOptionPane.showOptionDialog(panel,
	                "Save and apply the new settings?",
	                "Closing the settings", JOptionPane.YES_NO_CANCEL_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, null, null);
	        	
			switch(confirm){
			case JOptionPane.YES_OPTION:
				handleSaveSettings();
				break;
			case JOptionPane.NO_OPTION:
				panel.loadSetting();
				hideParentDialog();
				break;
			}
		}else{
			//No settings were modified, just hide myself
			hideParentDialog();
		}
	}

	private void hideParentDialog(){
		if (settingsDialog!=null)
			settingsDialog.setVisible(false);
	}
	
	private void handleSaveSettings() {
		panel.saveSetting();
		panel.loadSetting();
		//reconfigure model
		try{
			main.asyncDocReconfigure();
			hideParentDialog();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(panel,"Exception while applying new settings: "+ex.getMessage(), "Settings",JOptionPane.ERROR_MESSAGE);
			System.out.println("Exception while applying new settings: "+ex.getMessage());
		}
	}

}
