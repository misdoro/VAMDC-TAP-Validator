package org.vamdc.validator.gui.processors;

import java.awt.Frame;

import javax.swing.JPanel;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;

public class ProcessorsDialog extends PositionMemoryDialog {

	public ProcessorsDialog(Frame owner) {
		super(owner, "Processors", Setting.GUIProcessorsDim);
		this.setModal(true);
	}

	private static final long serialVersionUID = 8918753044092864505L;

	@Override
	protected JPanel lazyInitLayout() {
		
		return new ProcessorsPanel(this);
	}

}
