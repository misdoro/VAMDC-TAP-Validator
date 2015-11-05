package org.vamdc.validator.gui.textpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.vamdc.validator.gui.TextPopup;



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
	protected Highlighter hl;




	private static class JIndexTextArea extends JTextPane implements ChangeListener{
		private static final long serialVersionUID = 4499919184133885310L;
		/**
		 * Displayable area height
		 */
		private int viewHeight = 1;
		/**
		 * Displayable lines count
		 */
		private int maxLines = 7;
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
		
		private JIndexTextArea(){
			SimpleAttributeSet attribs = new SimpleAttributeSet();
			StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_RIGHT);
			this.setParagraphAttributes(attribs,true);
		}

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
			StringBuilder text = new StringBuilder();
			for(long i = startLine; (i < startLine+maxLines)&&i<=docEndLine; i++)
				text.append(i).append("\n");
			if (docEndLine>maxLines && startLine+maxLines<docEndLine)
				text.append("…\n").append(docEndLine).append("\n");
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
		new TextPopup().add(textArea);
		hl = textArea.getHighlighter();
	}

	public void setText(String text) {
		textArea.setText(text);
		lineidx.updateIndex();
		scrollBar.setVisibleAmount(this.getWindowRows()-1);
		highlightUpdate();
	}
	
	@Override
	public void setTransferHandler(TransferHandler hdl){
		super.setTransferHandler(hdl);
		textArea.setTransferHandler(hdl);
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
	public void centerLine(int lineIndex){
		if (lineIndex<0 || lineIndex>this.getDocEnd())
			return;
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
	public int getCenterLine(){
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


	public boolean blockIsDisplayed(long firstLine, long lastLine){
		return (lastLine>=(lineidx.startLine)&&firstLine<lineidx.startLine+lineidx.maxLines);
	}
	


	/*
	 * Methods required for controllers
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	public Component getScroll() {
		return scroll;
	}

	public Component getScrollBar() {
		return scrollBar;
	}

	public Component getIndexArea(){
		return lineidx;
	}

	public int getWindowRows(){
		return lineidx.getDisplayableRows();
	}
	
	public void highlightClear(){
		hl.removeAllHighlights();
	}

	/**
	 * Highlight line index i
	 * @param lineNumber line number to highlight
	 */
	public void highlightLine(long lineNumber,Color c) {
		if (lineidx.isDisplayed(lineNumber+1)) //Check if line is really displayed :)
			try {
				int windowline = (int)(lineNumber+1-getDocPosition());
				hl.addHighlight(textArea.getLineStartOffset(windowline),
						textArea.getLineEndOffset(windowline),
						new DefaultHighlighter.DefaultHighlightPainter(c));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Highlight block from line ls column cs to line le column ce with the color c
	 */
	public void highlightBlock(long startLine,int startCol,long endLine, int endCol, Color color){
		//Dimensions
		//Visible lines of the document
		long wFirstLine = this.getDocPosition();
		
		try {
			//Start with highlighting all the displayed window
			int hlStart = 0;
			int hlEnd=textArea.getLineEndOffset(Math.min(lineidx.maxLines-1,(int)((lineidx.docEndLine-wFirstLine)-1)));
			
			int firstLinePos = (int)(startLine - wFirstLine);
			int lastLinePos = (int)(endLine - wFirstLine);
			
			if (firstLinePos >= 0) //Position of highlighed part 
				hlStart = textArea.getLineStartOffset(firstLinePos)+startCol-1;
	
			if (lineidx.isDisplayed(endLine)){	
				hlEnd = textArea.getLineStartOffset(lastLinePos)+endCol-1;
			}
			hl.addHighlight(hlStart,hlEnd,new DefaultHighlighter.DefaultHighlightPainter(color));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Update highlight
	 */
	protected void highlightUpdate(){
		hl.removeAllHighlights();
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
	 * Redraw text, for example, if window if resized
	 */
	public abstract void updateText();

}
