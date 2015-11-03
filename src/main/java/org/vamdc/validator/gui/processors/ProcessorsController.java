package org.vamdc.validator.gui.processors;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.vamdc.registry.client.Registry;
import org.vamdc.registry.client.RegistryCommunicationException;
import org.vamdc.registry.client.RegistryFactory;
import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.LazyFileChooser;
import org.vamdc.validator.gui.settings.RegistryPanel;

public class ProcessorsController implements ActionListener {

	public final static String CMD_COPYSEL="Copy Selected";
	public final static String CMD_RELOAD="Reload";
	public final static String CMD_SAVECSV="Save as CSV";
	
	
	private ProcessorsPanel myPanel;
	private LazyFileChooser chooser = new LazyFileChooser(Setting.GUIFileProcessors);

	public ProcessorsController(ProcessorsPanel panel) {
		myPanel = panel;
		asyncLoadProcessors();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println(command);

		if (CMD_COPYSEL.equals(command)) {
			int[] selectedRows = myPanel.getSelectedRows();
			AbstractTableModel table = myPanel.getTableModel();
			if (table != null){
				copySelected(table,selectedRows);
			} else {
				asyncLoadProcessors();
			}
		}else if(CMD_RELOAD.equals(command)){
			sendTableModel(new WaitingTableModel("Reloading the registry...")," ");
			asyncLoadProcessors();
		}else if(CMD_SAVECSV.equals(command)){
			AbstractTableModel table = myPanel.getTableModel();
			saveCsv(table);
		}

	}

	private void saveCsv(AbstractTableModel table) {
		if (table == null)
			return;
		
		File saveFile = chooser.pickAFileName(myPanel, "processors.csv");
		if (saveFile!=null){
			FileWriter fw = null;
			try {
				fw = new FileWriter(saveFile);
				fw.write(table.toString());
				fw.close();
				
				
			} catch (IOException e) {
				if (fw!=null)
					try {
						fw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				e.printStackTrace();
			}
		}
	}

	public void copySelected(AbstractTableModel table,int[] selectedRows) {
		if (table==null || selectedRows==null || selectedRows.length ==0)
			return;
		StringBuilder result = new StringBuilder();
		result.append("processors=");
		for (int i : selectedRows) {
			result.append(table.getValueAt(i,ProcessorsTableModel.COL_IVOAID)).append("#");
		}
		result.deleteCharAt(result.length() - 1);
		writeToClipboard(result.toString());
	}

	public static void writeToClipboard(String s) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(s);
		clipboard.setContents(transferable, null);
	}

	public void asyncLoadProcessors() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				loadProcessors();
			}
		}).start();
	}

	private void loadProcessors() {
		String regBase = Setting.RegistryURL.getValue();
		try {
			URL regURL = RegistryPanel.getRegistryURL(regBase);
			Registry reg = RegistryFactory.getClient(regURL);

			final AbstractTableModel tablemod = new ProcessorsTableModel(reg);
			sendTableModel(tablemod,regBase);
		} catch (RegistryCommunicationException e) {
			sendTableModel(new WaitingTableModel(e.getMessage()),regBase);
			e.printStackTrace();
		} catch (MalformedURLException e) {
			sendTableModel(new WaitingTableModel(e.getMessage()),regBase);
			e.printStackTrace();
		}
	}

	private void sendTableModel(final AbstractTableModel tablemod,final String regLabel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				myPanel.setModel(tablemod);
				myPanel.setRegistryLabel(regLabel);
			}
		});
	}
}
