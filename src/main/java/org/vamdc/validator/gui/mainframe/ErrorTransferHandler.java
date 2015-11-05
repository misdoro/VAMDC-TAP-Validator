package org.vamdc.validator.gui.mainframe;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.vamdc.validator.interfaces.DocumentError;
import org.vamdc.validator.interfaces.XSAMSIOModel;

public class ErrorTransferHandler extends TransferHandler{
	private static final long serialVersionUID = 5474341752088819868L;

	private DocumentError error = null;
	private XSAMSIOModel document;
	
	public ErrorTransferHandler(XSAMSIOModel doc){
		this.document = doc;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		return new XsamsErrorTransferable(document,error);
	}
	
	@Override 
	public int getSourceActions(JComponent c){
		return COPY;
	}
	
	public static class XsamsErrorTransferable implements Transferable{
		private static final DataFlavor flavors[] = { DataFlavor.stringFlavor};
		
		private DocumentError error = null;
		private XSAMSIOModel document;
		
		
		public XsamsErrorTransferable(XSAMSIOModel document, DocumentError error) {
			this.error = error;
			this.document = document;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return flavors.clone();
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.stringFlavor.equals(flavor);
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (error==null || document==null) 
				return "";
			if ( isDataFlavorSupported(flavor)){
				String eol = System.getProperty("line.separator");
				
				StringBuilder result = new StringBuilder();
				result.append(error.getMessage());
				result.append(eol);
				
				long count = error.getElement().getLastLine()-error.getElement().getFirstLine();
				String block = document.getBlock(error.getElement().getFirstLine(), (int) count+1);
				String prevLine=null;
				for (String line:block.split("[\r\n]")){
					if (prevLine==null){
						//Treat first line
						prevLine = line.substring(error.getElement().getFirstCol()-1);
					}else{
						result.append(prevLine).append(eol);
						prevLine = line;
					}
				}
				result.append(prevLine).append(eol);
				return result.toString();
			}else{
				throw new UnsupportedFlavorException(flavor);
			}
		}
		
	}

	public void setError(DocumentError clickedError) {
		this.error = clickedError;
	}
	
}
