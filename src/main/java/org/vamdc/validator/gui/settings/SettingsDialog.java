package org.vamdc.validator.gui.settings;

import java.awt.Frame;

import javax.swing.JPanel;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;
import org.vamdc.validator.gui.mainframe.MainFrameController;

public class SettingsDialog extends PositionMemoryDialog{

	private static final long serialVersionUID = -6235066841441399853L;

	private MainFrameController mainController;
	
	public SettingsDialog(Frame owner,MainFrameController controller) {
		super(owner,"Settings", Setting.GUISettingsDim);
		this.setModal(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		mainController=controller;
	}

	@Override
	protected JPanel lazyInitLayout() {
		SettingsPanel innerPanel = new SettingsPanel(mainController,this);
		this.addWindowListener(innerPanel.getController());
		return innerPanel;
	}

}
