package org.vamdc.validator.gui.mainframe;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.vamdc.validator.interfaces.XSAMSIOModel;

public class XsamsTransferHandler extends TransferHandler{

	private static final long serialVersionUID = -547699830730722754L;

	private XSAMSIOModel xsamsDoc=null;
	
	public XsamsTransferHandler(){
		
	}
	
	
	public void setModel(XSAMSIOModel data) {
		xsamsDoc = data;
	}

	@Override
	public boolean canImport(TransferSupport input) {
		if (!importUrlList(input) && !importFileList(input)) {
			
			StringBuilder log = new StringBuilder();
			String eol = System.getProperty("line.separator");
			for (DataFlavor flavor:input.getDataFlavors()){
				log.append(flavor.getMimeType()).append(eol);
			}

			System.out.println("input "+log.toString());
			return false;
		}

		return true;
	}
	
	private boolean importFileList(TransferSupport input){
		return input.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	}
	private boolean importUrlList(TransferSupport input){
		return input.isDataFlavorSupported(DataFlavor.stringFlavor);
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!support.isDrop())
			return false;
		
		Transferable t = support.getTransferable();

		if (importFileList(support)){
		try {
			@SuppressWarnings("unchecked")
			java.util.List<File> l =
					(java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

			File iFile=null;
			if (l.size()>1){
				showMultiFileError(support);
				return false;
			}
			if (l.size()>0){
				iFile = l.get(0);
			}
			if (iFile!=null && iFile.exists() && iFile.canRead()){
				xsamsDoc.loadFile(iFile);
			}

			return true;
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		}else if (importUrlList(support)){
			try {
				String urlList = (String) t.getTransferData(DataFlavor.stringFlavor);
				String[] urls = urlList.split("[\r\n]");
				if (urls.length>1){
					showMultiFileError(support);
					return false;
				}
				if (urls.length>0){
					URL file = new URL(urls[0]);
					xsamsDoc.loadStream(file.openStream());
					xsamsDoc.setFilename(urls[0]);
				}
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return true;
		}
		

		return false;
	}


	private void showMultiFileError(final TransferHandler.TransferSupport support) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(support.getComponent(),"Drop of more than one file is not supported");
			}
		});
	}

}
