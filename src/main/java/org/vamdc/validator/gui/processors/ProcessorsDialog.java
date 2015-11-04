package org.vamdc.validator.gui.processors;

import javax.swing.JPanel;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;
import org.vamdc.validator.gui.mainframe.ComponentUpdateInterface;
import org.vamdc.validator.gui.mainframe.MainFrame;
import org.vamdc.validator.interfaces.XSAMSIOModel;

public class ProcessorsDialog extends PositionMemoryDialog implements ComponentUpdateInterface {

	XSAMSIOModel doc;
	ProcessorsPanel panel;
	
	public ProcessorsDialog(MainFrame owner) {
		super(owner, "Processors", Setting.GUIProcessorsDim);
	}

	private static final long serialVersionUID = 8918753044092864505L;

	@Override
	protected JPanel lazyInitLayout() {
		panel = new ProcessorsPanel(this);
		if (doc!=null)
			panel.setNodeProcessors(doc.getPreferredProcessors());
		return panel;
	}

	@Override
	public void setModel(XSAMSIOModel data) {
		this.doc=data;
		if (panel!=null){
			panel.setNodeProcessors(doc.getPreferredProcessors());
			panel.updateSelectedProcessors();
		}
	}

	@Override
	public void resetComponent() {
		if (this.panel!=null && this.doc!=null){
			panel.setNodeProcessors(doc.getPreferredProcessors());
			panel.updateSelectedProcessors();
		}
	}

	@Override
	public void updateFromModel(boolean isFinal) {
		if (isFinal && this.panel!=null)
			panel.updateSelectedProcessors();
	}

}
