package org.vamdc.validator.gui.processors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

public class ProcessorsSavePanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 6583140509900207013L;

	private JTable proctable;
	private ProcessorsTableModel procmodel;
	private JDialog myDialog;
	
	private JButton saveButton = new JButton("Save as CSV");
	private JButton copySelectedButton = new JButton("Copy Selected");
	
	public ProcessorsSavePanel(JDialog dialog,JTable table,ProcessorsTableModel model){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(saveButton);
		this.add(copySelectedButton);
		saveButton.addActionListener(this);
		copySelectedButton.addActionListener(this);
		
		this.proctable = table;
		this.procmodel = model;
		this.myDialog=dialog;
	}
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source==saveButton){
			//TODO: implement
			
		}else if (source==copySelectedButton){
			int[] selectedRows = proctable.getSelectedRows();
			if (selectedRows!=null && selectedRows.length>0){
				copySelected(selectedRows);
				myDialog.setVisible(false);
			}else{
				
			}
			
			
		}
		
	}
	
	public void copySelected(int[] selectedRows) {
		StringBuilder result = new StringBuilder();
		result.append("processors=");
		for (int i:selectedRows){
			result.append(procmodel.getValueAt(i,ProcessorsTableModel.COL_IVOAID)).append("#");
		}
		result.deleteCharAt(result.length()-1);
		writeToClipboard(result.toString());
	}
	
	public static void writeToClipboard(String s)
	{
	  Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	  Transferable transferable = new StringSelection(s);
	  clipboard.setContents(transferable, null);
	}
}
