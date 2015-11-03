package org.vamdc.validator.gui.processors;

import javax.swing.table.AbstractTableModel;

public class WaitingTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1459287023329522338L;
	private static final int COL_IVOAID = 1;
	private static final int COL_TITLE = 0;
	
	private String message;
	
	public WaitingTableModel(String message){
		this.message = message;
	}
	
	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		switch(column){
		case COL_IVOAID:
			return "IVOA ID";
		case COL_TITLE:
			return "Processor title";
		}
		return "";
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex==0){
			switch(columnIndex){
			case COL_IVOAID:
				return "";
			case COL_TITLE:
				return message;
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return message;
	}


}
