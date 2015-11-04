package org.vamdc.validator.gui.textpanel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import org.vamdc.validator.gui.search.SearchInterface;
import org.vamdc.validator.gui.search.SearchPanel;

/**
 * A TextPanel extension adding the search panel
 * @author doronin
 *
 */
public abstract class TextSearchPanel extends TextPanel implements SearchInterface{
	
	private static final long serialVersionUID = 2535428433264950651L;

	private String searchString=null;
	private String searchStringLowCase=null;
	private boolean searchIgnoreCase=false;
	
	private SearchPanel searchPanel=new SearchPanel(this);
	
	public TextSearchPanel(){
		super();
		this.add(searchPanel,BorderLayout.SOUTH);	
	}
	
	public abstract int searchString(String text,int startLine, boolean ignoreCase);
	
	@Override
	protected void highlightUpdate(){
		super.highlightUpdate();
		if (searchString!=null)
			highlightSearchResult();
	}
	
	@Override
	public void searchString(String search,boolean ignoreCase,boolean searchNext){
		this.searchString=search;
		if (ignoreCase)
			this.searchStringLowCase=search.toLowerCase();
		this.searchIgnoreCase=ignoreCase;
		this.highlightUpdate();
		if (searchNext)
			this.searchNext();
		else
			this.searchHere();
	}
	
	private void highlightSearchResult() {
		if (this.searchString!=null && !this.searchString.isEmpty()){
			String searchText=this.searchString;
		 	String text = this.getTextArea().getText();
			if (this.searchIgnoreCase){
				searchText=this.searchStringLowCase;
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
	
	public void searchHere(){
		this.centerLine(this.searchNext(this.getCenterLine()-1,true));
	}
	
	@Override
	public void searchNext(){
		this.centerLine(this.searchNext(this.getCenterLine(),false));
	}
	
	private int searchNext(int startLine,boolean silent){
		if (this.searchString==null || this.searchString.isEmpty()) return -1;
		int foundLine = this.searchString(this.searchString, startLine,this.searchIgnoreCase);
		if (foundLine==-1 && startLine>1 && !silent){
			switch (JOptionPane.showConfirmDialog(
					this,
					"String \""+this.searchString+"\" not found, start from the beginning?",
					"Search",
					JOptionPane.YES_NO_OPTION))
					{
					case JOptionPane.OK_OPTION:
						foundLine = this.searchString(this.searchString,0,this.searchIgnoreCase);
						break;
					case JOptionPane.NO_OPTION:
						return -1;
					}
		}
		if (foundLine==-1 && !silent){
			final String message="String \""+this.searchString+"\" not found.";
			JOptionPane.showMessageDialog(this, message,"Search",JOptionPane.ERROR_MESSAGE);
			return -1;
		}

		if (foundLine==-1 && silent){
			return startLine;
		}
		return foundLine;
	}
	
}
