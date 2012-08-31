package org.vamdc.validator.gui.mainframe;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.vamdc.validator.gui.search.SearchData;
import org.vamdc.validator.interfaces.XSAMSIOModel;

/**
 * Panel for XSAMS displaying
 * @author Misha Doronin
 */
public class XSAMSPanel extends TextPanel implements ComponentUpdateInterface{

	private static final long serialVersionUID = 3718826116022244080L;
	private XSAMSIOModel xsamsDoc=null;
	private SearchData search=null;
	
	
	public XSAMSPanel(){
		super();
	}
	
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
	protected void updateHighlight(){
		super.updateHighlight();
		if (search!=null)
			highlightSearchResult();
	}
	
	public void setSearch(SearchData search){
		this.search=search;
	}
	
	private void highlightSearchResult() {
		String searchText = search.getSearchText();
		if (searchText!=null && !searchText.isEmpty()){

		 	String text = this.getTextArea().getText();
			if (search.ignoreCase()){
				searchText=searchText.toLowerCase();
				text=text.toLowerCase();
			}
			int start=0,position = 0;

			while((position=text.indexOf(searchText, start))>=0){
				try {
					hl.addHighlight(position,position+searchText.length(), 
							new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				start=position+searchText.length();
			}
		}
	}

}
