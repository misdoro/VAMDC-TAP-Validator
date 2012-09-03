package org.vamdc.validator.gui.settings;

import java.awt.Frame;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;
import org.vamdc.validator.gui.mainframe.MainFrameController;

public class SettingsDialog extends PositionMemoryDialog{

	private static final long serialVersionUID = -6235066841441399853L;

	public SettingsDialog(Frame owner,MainFrameController controller) {
		super("Settings", owner, Setting.GUISettingsDim);
		this.setContentPane(new SettingsPanel(controller));
		this.setModal(true);
		wph.loadDimensions();
	}

}
