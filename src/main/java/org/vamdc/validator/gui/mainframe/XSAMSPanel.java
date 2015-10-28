package org.vamdc.validator.gui.mainframe;

import java.awt.Color;
import java.util.HashMap;

import org.vamdc.validator.gui.textpanel.TextSearchPanel;
import org.vamdc.validator.interfaces.XSAMSIOModel;

import org.vamdc.validator.interfaces.DocumentElement;

/**
 * Panel for XSAMS displaying
 * @author Misha Doronin
 */
public class XSAMSPanel extends TextSearchPanel implements ComponentUpdateInterface{

	private static final long serialVersionUID = 3718826116022244080L;
	private XSAMSIOModel xsamsDoc=null;
	
	//XML Elements to highlight
	private HashMap<DocumentElement,Color> highlight=new HashMap<DocumentElement,Color>();
	
	@Override
	public void resetComponent() {
		//Reset me
		this.setDocEnd(1);
		this.resetHighlight();
		this.setText("<XSAMSData/>");
	}

	@Override
	public void setModel(XSAMSIOModel data) {
		xsamsDoc = data;
	}

	@Override
	public void updateFromModel(boolean isFinal) {
		if (xsamsDoc!=null && xsamsDoc.getLineCount() >0){
			int length=(int) xsamsDoc.getLineCount();
			if (length!=this.getDocEnd())//Document changed, update scrollbar
				this.setDocEnd(length);
			if (length<this.getDocPosition()){//Document shrinked, reset to start
				this.setDocPosition(1);
			};
		}
		updateText();
	}

	@Override
	public void updateText() {
		if (xsamsDoc!=null && xsamsDoc.getLineCount() >0){
			//Model has some document data to display, so do it
			this.setText(xsamsDoc.getBlock(this.getDocPosition(), this.getWindowRows()));
		}else
			this.resetComponent();
	}

	@Override
	public int searchString(String text, int startLine, boolean ignoreCase) {
		if (xsamsDoc!=null)
			return xsamsDoc.searchString(text, startLine, ignoreCase);
		return -1;
	}

	/**
	 * Replace all highlights with new one
	 * @param e 
	 * @param c
	 */
	public void setHighlight(DocumentElement e, Color c){
		highlight.clear();
		highlight.put(e,c);
		updateHighlight();
	}

	public void resetHighlight(){
		highlight.clear();
		updateHighlight();
	}

	/**
	 * Add element to highlight
	 * @param e DocumentElement structure
	 * @param c Color to use
	 */
	public void addHighlight(DocumentElement e, Color c){
		highlight.put(e, c);
		updateHighlight();
	}
	
	/**
	 * Update highlight
	 */
	@Override
	protected void updateHighlight(){
		super.updateHighlight();
		
		//Highlight elements
		for (DocumentElement element:highlight.keySet()){
			highlight(element,highlight.get(element));
		}
	}
	
	/**
	 * try to highlight specific document element in current displayable part
	 * @param element
	 * @param color
	 */
	private void highlight(DocumentElement element,Color color){
		if (this.getDocEnd()<=2) return;//Return if the text is not loaded yet for some reason.
		//Check if we have any line to highlight
		if (this.blockIsDisplayed(element.getFirstLine(), element.getLastLine())){
			this.highlightBlock(
				element.getFirstLine(),
				element.getFirstCol(),
				element.getLastLine(),
				element.getLastCol(), 
				color);
		}
	}


	
	
}
