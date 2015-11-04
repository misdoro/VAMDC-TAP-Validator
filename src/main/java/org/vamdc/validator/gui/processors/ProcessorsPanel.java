package org.vamdc.validator.gui.processors;


import java.awt.BorderLayout;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


public class ProcessorsPanel extends JPanel{
	private static final long serialVersionUID = 1631879366858257558L;
	
	private AbstractTableModel tablemod;
	private ProcessorsController controller;
	private JTable procTable;
	private JLabel registryLabel;
	private Set<String> nodeProcessors;
	
	public ProcessorsPanel(ProcessorsDialog processorsDialog){
		super();
		this.setLayout(new BorderLayout());
		
		this.controller = new ProcessorsController(this);
		this.procTable = new JTable(new WaitingTableModel("Waiting for the registry..."));
		this.registryLabel = new JLabel("Registry: ");
		
		this.add(new JScrollPane(this.procTable),BorderLayout.CENTER);
		this.add(this.registryLabel,BorderLayout.NORTH);
		this.add(new ProcessorsSavePanel(this.controller),BorderLayout.SOUTH);
		
	}
	
	public int[] getSelectedRows(){
		return procTable.getSelectedRows();
	}
	
	public AbstractTableModel getTableModel(){
		return tablemod;
	}

	public void setModel(AbstractTableModel tablemodel) {
		this.procTable.setModel(tablemodel);
		this.tablemod = tablemodel;
		this.procTable.validate();
		updateSelectedProcessors();
	}
	
	public void setRegistryLabel(String label){
		this.registryLabel.setText("Registry: "+label);
	}
	
	public void updateSelectedProcessors(){

		if (nodeProcessors!=null && this.tablemod instanceof ProcessorsTableModel){
			procTable.clearSelection();
			int length=tablemod.getRowCount();
			for (int i=0;i<length;i++){
				String value = tablemod.getValueAt(i,ProcessorsTableModel.COL_IVOAID).toString();
				if (nodeProcessors.contains(value)){
					procTable.addRowSelectionInterval(i, i);
				}
			}
		}
	}

	public void setNodeProcessors(Collection<String> nodeProcessors) {
		this.nodeProcessors = new TreeSet<String>(nodeProcessors);
		updateSelectedProcessors();
	}

}
