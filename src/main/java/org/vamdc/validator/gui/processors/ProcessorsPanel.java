package org.vamdc.validator.gui.processors;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.RegistryFactory;
import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.settings.RegistryPanel;

public class ProcessorsPanel extends JPanel{
	private static final long serialVersionUID = 1631879366858257558L;
	
	private ProcessorsTableModel tablemod;
	private JTable procTable;
	private JDialog mydialog;
	private String registrySetting;
	
	
	public ProcessorsPanel(ProcessorsDialog processorsDialog){
		super(new BorderLayout());
		this.mydialog=processorsDialog;
		try {
			registrySetting = Setting.RegistryURL.getValue();
			URL regURL = RegistryPanel.getRegistryURL(registrySetting);
			Registry reg = RegistryFactory.getClient(regURL);
			
			tablemod=new ProcessorsTableModel(reg);
			procTable = new JTable(tablemod);
			JScrollPane tablePane = new JScrollPane(procTable);
			this.add(tablePane,BorderLayout.CENTER);
			
			this.add(new ProcessorsSavePanel(mydialog,procTable,tablemod),BorderLayout.SOUTH);
			
		} catch (RegistryCommunicationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			mydialog.setVisible(false);
		}
	}
	
	public String getRegistrySetting(){
		return registrySetting;
	}

	public int[] getSelectedRows(){
		return procTable.getSelectedRows();
	}

}
