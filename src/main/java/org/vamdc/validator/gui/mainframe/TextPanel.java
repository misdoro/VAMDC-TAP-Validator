package org.vamdc.validator.gui.mainframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.vamdc.validator.interfaces.DocumentElement;


/**
 * Text panel for displaying XSAMS document.
 * Self-handles scrolling, implements moving inside document.
 * 
 * @author doronin
 *
 */
public abstract class TextPanel extends JPanel  {
	private static final long serialVersionUID = -2938873545728595404L;

	private JScrollPane scroll;


	private JScrollBar scrollBar;
	private JTextArea textArea;
	private JIndexTextArea lineidx;
	private Highlighter hl;
	private String searchText;//Text to highlight in gray colour
	private Color searchColor=Color.GRAY;

	//Elements to highlight
	private HashMap<DocumentElement,Color> highlight;


	private static class JIndexTextArea extends JTextArea implements ChangeListener{
		private static final long serialVersionUID = 4499919184133885310L;
		/**
		 * Displayable area height
		 */
		private int viewHeight = 1;
		/**
		 * Displayable lines count
		 */
		private int maxLines = 10;
		/**
		 * Start line
		 */
		private int startLine = 1;
		/**
		 * Document last line, don't add line numbers after it.
		 */
		private int docEndLine = 1;

		/**
		 * Line height
		 */
		private int lineHeight=0;

		@Override  
		public void paint(Graphics g) {  
			super.paint(g);
			//Recalculate capacity if stored height differs from actual
			if (this.getHeight()!=viewHeight ){
				viewHeight = this.getHeight();
				final FontMetrics fontMetrics = g.getFontMetrics();  
				lineHeight = fontMetrics.getHeight();
				maxLines = (viewHeight/lineHeight)-2;//Leave two lines for horizontal scroller
				if (maxLines <1) maxLines=1;
			}
		}

		/**
		 * Get 
		 * @return
		 */
		public int getDisplayableRows(){
			return maxLines;
		}

		private String genLineNums(){
			int lines = this.getDisplayableRows();
			StringBuilder text = new StringBuilder();
			for(long i = startLine; (i < startLine+lines)&&i<=docEndLine; i++)
				text.append(i).append("\n");
			return text.toString();
		}

		public void updateIndex(){
			this.setText(genLineNums());
		}


		/**
		 * Check if line lineNumber is currently displayed 
		 * @param lineNumber line concerned, first line is 1
		 * @return true if line is currently displayed
		 */
		public boolean isDisplayed(long lineNumber){
			return (lineNumber >= startLine && (lineNumber < startLine+maxLines) && lineNumber<=docEndLine);
		}
		
		/**
		 * Check if any of the text block's lines is displayed
		 * @param firstLine
		 * @param lastLine
		 * @return true if any of the lines between the first and the last is in the display area.
		 */
		public boolean blockIsDisplayed(long firstLine, long lastLine){
			return (lastLine>=(startLine)&&firstLine<startLine+maxLines);
		}

		/**
		 * Get line height
		 * @return line height in pixels
		 */
		public int getLineHeight() {
			return this.lineHeight;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			//ScrollBar state has changed
			BoundedRangeModel model = (BoundedRangeModel) e.getSource();
			this.startLine = model.getValue();
			this.docEndLine = model.getMaximum();
			updateIndex();
		}
	}

	public TextPanel(){
		super();
		buildPanel();

		hl = textArea.getHighlighter();
		highlight = new HashMap<DocumentElement,Color>();
	}

	public void setText(String text) {
		textArea.setText(text);
		lineidx.updateIndex();
		scrollBar.setVisibleAmount(this.getWindowRows()-1);
		updateHighlight();
	}

	public void setSearchString(String text){
		searchText=text;
		updateHighlight();
	}

	/**
	 * Set view position in document to docIndex
	 * Text is then updated through call to scrollbar change event monitor.
	 * @param docIndex first line to display.
	 */
	public void setDocPosition(long docIndex){
		if (lineidx.docEndLine <= docIndex){
			scrollBar.setValue((int)lineidx.docEndLine);
		}else{
			scrollBar.setValue((int)docIndex);
		}
	}

	/**
	 * Center on certain line
	 * @param lineIndex line index to center on
	 */
	public void centerLine(long lineIndex){
		long startLine = lineIndex - this.getWindowRows()/2;
		if (startLine<=0) startLine=1;
		this.setDocPosition(startLine);
	}

	/**
	 * Get view start line in document
	 * @return index of first displayed line
	 */
	public int getDocPosition(){
		return scrollBar.getValue();
	}

	/**
	 * Get view center line (or last doc line)
	 * @return index of line displayed in the center of window
	 */
	public int getDocCenter(){
		return getDocPosition()+getWindowRows()/2+1;
	}

	/**
	 * Set last displayable line index in document
	 * @param docEnd last line index
	 */
	public void setDocEnd(long docEnd){
		scrollBar.setMaximum((int)docEnd);
	}

	/**
	 * Get stored document size
	 * @return last line of document index
	 */
	public long getDocEnd(){
		return scrollBar.getMaximum();
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



	/*
	 * Methods required for controllers
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	public JScrollPane getScroll() {
		return scroll;
	}

	public JScrollBar getScrollBar() {
		return scrollBar;
	}

	public JTextArea getIndexArea(){
		return lineidx;
	}

	public int getWindowRows(){
		return lineidx.getDisplayableRows();
	}

	/**
	 * Highlight line index i
	 * @param lineNumber line number to highlight
	 */
	public void highlightLine(long lineNumber,Color c) {
		if (lineidx.isDisplayed(lineNumber)) //Check if line is really displayed :)
			try {
				int windowline = (int)(lineNumber-getDocPosition());
				hl.addHighlight(textArea.getLineStartOffset(windowline),
						textArea.getLineEndOffset(windowline),
						new DefaultHighlighter.DefaultHighlightPainter(c));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Update highlight
	 */
	private void updateHighlight(){
		hl.removeAllHighlights();
		
		//Highlight search results
		if (searchText!=null && !searchText.isEmpty()){
			String text = this.getTextArea().getText();
			int start=0,position = 0;

			while((position=text.indexOf(searchText, start))>=0){
				try {
					hl.addHighlight(position,position+searchText.length(), new DefaultHighlighter.DefaultHighlightPainter(searchColor));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				start=position+searchText.length();
			}
		}
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
		if (textArea.getLineCount()<=2) return;//Return if text is not loaded yet for some reason.
		//Dimensions
		//First visible line in document
		long wFirstLine = lineidx.startLine;
		//Check if we have any line to highlight
		if (lineidx.blockIsDisplayed(element.getFirstLine(), element.getLastLine()))
			try {
				int hlStart = 0;
				int hlEnd=textArea.getLineEndOffset(Math.min(lineidx.maxLines-1,(int)((lineidx.docEndLine-wFirstLine)-1)));
				int firstLinePos = (int)(element.getFirstLine() - wFirstLine);
				int lastLinePos = (int)(element.getLastLine() - wFirstLine);
				if (firstLinePos >= 0) //Position of highlighed part 
					hlStart = textArea.getLineStartOffset(firstLinePos)+element.getFirstCol()-1;
				if (lineidx.isDisplayed(element.getLastLine())){	
					hlEnd = textArea.getLineStartOffset(lastLinePos)+element.getLastCol()-1;
				}
				hl.addHighlight(hlStart,hlEnd,new DefaultHighlighter.DefaultHighlightPainter(color));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
	}
	

	/**
	 * Build panel look
	 */
	private void buildPanel(){
		lineidx = new JIndexTextArea();
		lineidx.setEditable(false);
		lineidx.setBackground(Color.LIGHT_GRAY);
		lineidx.setMinimumSize(lineidx.getPreferredSize());


		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setTabSize(2);

		scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollBar = new JScrollBar();
		scrollBar.setMinimum(1);
		scrollBar.getModel().addChangeListener(lineidx);

		this.setLayout(new BorderLayout());
		this.add(lineidx,BorderLayout.WEST);
		this.add(scroll,BorderLayout.CENTER);
		this.add(scrollBar,BorderLayout.EAST);		
	}


	/**
	 * Set minimum size so at least count lines could be displayed.
	 * @param count
	 */
	public void setDisplayableLines(int count){
		int lineHeight = lineidx.getLineHeight();
		if (lineHeight>0){
			int minHeight = lineHeight*(count+1)+scrollBar.getPreferredSize().width;//Width since scrollbar is vertical and we need height of scrollpane's scrollbar.
			this.setMinimumSize(new Dimension(100,minHeight));
		}
	}
	
	/**
	 * Redraw text, for example, if window if resized
	 */
	public abstract void updateText();

}
