package org.vamdc.validator.gui.settings;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;


import javax.swing.table.AbstractTableModel;

/**
 * Table model for namespaces table
 * @author doronin
 *
 */
public class NamespaceTableModel extends AbstractTableModel{

	/**
	 * Constructor that accepts string which is a space-separated list of namespace URL's and their schemalocations.
	 * That list must have even number of members.
	 * @param namespaces
	 */
	public NamespaceTableModel(String namespaces){
		super();
		setNSString(namespaces);
	}

	/**
	 * Default constructor
	 */
	public NamespaceTableModel(){
		super();
	}

	/**
	 * Get space separated namespace URLs and schemalocations
	 * @return space separated namespace URLs and schemalocations as accepted by xerces-j schema locations attribute
	 */
	public String getNSString(){
		StringBuilder result = new StringBuilder();
		for (String[] row:nslist){
			if (row[0].length()>0 && row[1].length()>0)
				result.append(quote(row[0])).append(" ").append(quote(row[1])).append(" ");
		}
		return result.toString();
		//return (String)Settings.defaults.get(Settings.SchemaLocations);
	}

	/**
	 * Quote string if it contains spaces
	 * @param in input string
	 * @return output string
	 */
	private String quote(String in){
		try{
			new URL(in);
			return in.replace(" ", "%20");
		}catch (MalformedURLException e) {
			return in.replace(" ", "\\ ");
		}

	}

	/**
	 * Update table contents with new schema locations
	 * @param schemalocations space separated namespace URLs and schemalocations as accepted by xerces-j schema locations attribute
	 */
	public void setNSString(String schemalocations){
		String[] nsarray = schemalocations.trim().split(" ");
		for(int i=0;i<nsarray.length;i++){
			String row = nsarray[i]; 
			if (row.endsWith("'") && row.startsWith("'") && row.length()>=2)
				nsarray[i]=row.substring(1, -1);
		}
		nslist.clear();
		if (nsarray.length%2 == 0){
			for (int i=0;i<nsarray.length/2;i++){
				nslist.add(new String[]{nsarray[i*2],nsarray[i*2+1]});
			}
		}else{
		}
		fireTableStructureChanged();
	}


	private static final long serialVersionUID = -1812053956894535267L;
	//Column names
	private String[] columnNames = new String[]{"Namespace URI","Schema location"};
	//Row values
	private ArrayList<String[]> nslist = new ArrayList<String[]>();


	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return nslist.size()+1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex<nslist.size())
			return nslist.get(rowIndex)[columnIndex];			
		return "";
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return true; //Cells are always editable
	}

	/**
	 * Save row
	 */
	public void setValueAt(Object value, int row, int col) {
		String val = (String)value;
		Boolean addedRow = false;
		//Delete row in case entered empty value
		if (val.trim().length()==0){
			deleteRow(row);
			fireTableRowsDeleted(row,row);
		}
		switch(col){
		case 0:
			try {//Saving namespace url, must be normal URL.
				URL namespace = new URL(val);
				addedRow=saveRow(namespace.toString(),"",row);
			} catch (MalformedURLException e) {	
			}
			break;
		case 1:
			try{//Saving location, may be either existing file or normal URL.
				URL location = new URL(val);
				//If protocol is file, check file and save only path
				if (location.getProtocol().equalsIgnoreCase("file"))
					if (checkFileAccess(location.getPath()))//Check if file is accessible
						addedRow = saveRow("",location.getPath(),row);
					else;
				// TODO: add exception
				else//else save full URL without checking
					addedRow = saveRow("",location.toString(),row);
			}catch (MalformedURLException e) {
				if (checkFileAccess(val))
					addedRow = saveRow("",val,row);
			}
			break;
		default:
			break;
		}
		if (addedRow)
			fireTableRowsInserted(row+1, row+1);
		fireTableRowsUpdated(row, row);
	}


	/**
	 * Delete row from arrays
	 * @param row index
	 */
	private void deleteRow(int row){
		if (row<nslist.size())
			nslist.remove(row);
	}

	/**
	 * Check if file exists and is readable and is file :)
	 * @param val path to file
	 * @return true if path is readable existing file.
	 */
	private boolean checkFileAccess(String val){
		File filename = new File(val);
		return (filename.exists()&& filename.canRead() && filename.isFile());
	}

	/**
	 * Save location cell 
	 * @param location
	 * @param index
	 * @return
	 */
	private boolean saveRow(String namespace,String location, int row){
		if (row>=nslist.size()){
			nslist.add(new String[]{namespace,location});
			return true;
		}else
			if (namespace!=null && namespace.length()>0)
				nslist.get(row)[0]=namespace;
			else if (location!=null && location.length()>0)
				nslist.get(row)[1]=location;
		return false;
	}


}
