package org.vamdc.validator.gui.processors;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.ivoa.xml.voresource.v1.Resource;

import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.Registry.Service;

public class ProcessorsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1459287023329522337L;

	private Registry registryClient;
	
	private ArrayList<Processor> processors=new ArrayList<Processor>();
	
	final static int COL_IVOAID=1;
	final static int COL_TITLE=0;
	
	private class Processor{
		URI IVOAID;
		String title;
		
		Processor(Resource res){
			try {
				this.IVOAID	= new URI(res.getIdentifier());
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e);
			}
			this.title 	= res.getTitle();
		}
		
	}
	
	public ProcessorsTableModel(Registry regclient){
		this.registryClient=regclient;
		for (String civoaid:registryClient.getIVOAIDs(Service.CONSUMER)){
			processors.add(
					new Processor(
							registryClient.getResourceMetadata(civoaid)));
		}
	}
	
	@Override
	public int getRowCount() {
		return processors.size();
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
			return "Title";
		}
		return "";
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex<processors.size()){
			Processor proc = processors.get(rowIndex);
			switch(columnIndex){
			case COL_IVOAID:
				return proc.IVOAID;
			case COL_TITLE:
				return proc.title;
			}
		}
		return null;
	}


}
