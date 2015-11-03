package org.vamdc.validator.gui.processors;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ProcessorsSavePanel extends JPanel{
	private static final long serialVersionUID = 6583140509900207013L;
	

	
	private JButton saveButton = new JButton(ProcessorsController.CMD_SAVECSV);
	private JButton reloadButton = new JButton(ProcessorsController.CMD_RELOAD);
	private JButton copySelectedButton = new JButton(ProcessorsController.CMD_COPYSEL);
	
	public ProcessorsSavePanel(ActionListener alistener){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(reloadButton);
		this.add(saveButton);
		this.add(copySelectedButton);
		reloadButton.addActionListener(alistener);
		saveButton.addActionListener(alistener);
		copySelectedButton.addActionListener(alistener);
		
		this.setEnabled(false);
	}
	
	
	
}
