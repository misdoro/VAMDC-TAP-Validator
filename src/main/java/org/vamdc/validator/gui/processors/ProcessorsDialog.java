package org.vamdc.validator.gui.processors;

import java.awt.Frame;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;

public class ProcessorsDialog extends PositionMemoryDialog {

	private ProcessorsPanel panel;

	public ProcessorsDialog(Frame owner) {
		super(owner, "Processors", Setting.GUIProcessorsDim);
		this.setModal(true);
		wph.loadDimensions();
	}

	private static final long serialVersionUID = 8918753044092864505L;

	@Override
	public void setVisible(boolean b) {
		if (b && (panel == null)) {
			panel = new ProcessorsPanel(this);
			this.setContentPane(panel);
		}

		super.setVisible(b);
	}

}
