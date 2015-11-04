package org.vamdc.validator.gui.mainframe;

import java.awt.Color;

import javax.swing.TransferHandler;

import org.vamdc.validator.gui.textpanel.TextPanel;
import org.vamdc.validator.gui.textpanel.TextPanelController;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.DocumentError;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.interfaces.DocumentError.Type;

public class ValidationPanelController extends TextPanelController{

	private XSAMSIOModel xsamsdoc;
	private XSAMSPanel xsamsPanel;
	private ErrorTransferHandler eth;

	public ValidationPanelController(TextPanel valPanel,
			XSAMSIOModel document, XSAMSPanel xsamsPanel) {
		super(valPanel);
		this.xsamsdoc = document;
		this.xsamsPanel = xsamsPanel;
		eth = new ErrorTransferHandler(document);
		valPanel.setTransferHandler(eth);
	}

	@Override
	public void clickedLine(int lineNum) {
		DocumentElementsLocator el=xsamsdoc.getElementsLocator();
		if (el==null || el!=null && el.getErrors().size()<=lineNum)
			return;
		DocumentError clickedError = el.getErrors().get((int) lineNum);
		panel.highlightClear();
		panel.highlightLine(lineNum, new Color(0.9f,0.6f,0.6f));
		if (clickedError.getType()==Type.element){
			xsamsPanel.addHighlight(clickedError.getElement(), Color.RED);
			xsamsPanel.centerLine((int)clickedError.getElement().getFirstLine());
			eth.setError(clickedError);
			eth.exportToClipboard(panel, panel.getToolkit().getSystemClipboard(),
					TransferHandler.COPY);
		}else if (clickedError.getType()==Type.search){
			xsamsPanel.searchString(clickedError.getSearchString(), false,true);
		}

	}

}