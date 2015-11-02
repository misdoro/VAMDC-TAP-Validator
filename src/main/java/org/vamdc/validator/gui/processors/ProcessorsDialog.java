package org.vamdc.validator.gui.processors;

import java.awt.Frame;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;

public class ProcessorsDialog extends PositionMemoryDialog {
	
	private ProcessorsPanel panel = new ProcessorsPanel(this);
	
	public ProcessorsDialog(Frame owner) {
		super(owner,"Processors", Setting.GUIProcessorsDim);
		this.setContentPane(panel);
		this.setModal(true);
		wph.loadDimensions();
	}

	private static final long serialVersionUID = 8918753044092864505L;

	public String getRegistrySetting() {
		return panel.getRegistrySetting();
	}

}
