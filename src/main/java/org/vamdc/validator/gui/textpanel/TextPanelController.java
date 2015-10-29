package org.vamdc.validator.gui.textpanel;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;

public abstract class TextPanelController implements ComponentListener,AdjustmentListener, MouseWheelListener,MouseInputListener{
	
	protected TextPanel panel;
	
	
	/**
	 * It is called when we had double click on certain line of document
	 * @param lineNum
	 */
	public abstract void clickedLine(int lineNum);
	
	public TextPanelController(TextPanel model){
		this.panel = model;
		setupEventListeners();
	}

	private void setupEventListeners() {
		panel.getIndexArea().addMouseWheelListener(this);
		panel.getIndexArea().addComponentListener(this);
		if (panel.getScroll().getMouseWheelListeners() != null)
			for(MouseWheelListener i :panel.getScroll().getMouseWheelListeners())
				panel.getScroll().removeMouseWheelListener(i);
		panel.getScroll().addMouseWheelListener(this);
		panel.getScrollBar().addMouseWheelListener(this);
		panel.getScrollBar().addAdjustmentListener(this);
		panel.getTextArea().addMouseListener(this);
	}
	
	/*
	 * Resize handler
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	private int oldPanelRows=0;//Old panel rows count. Upon resizing, update the text only if the current value does not match this.
	@Override
	public void componentResized(ComponentEvent e) {
		int newPanelRows=panel.getWindowRows();
		if (oldPanelRows!=newPanelRows){
			panel.updateText();
			oldPanelRows = newPanelRows;
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		panel.updateText();
	}


	/**
	 * Scrollbar adjustment, move in document
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		panel.updateText();
	}

	/**
	 * Scrolling handler
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		e.consume();
		int scrolled = e.getScrollAmount()*e.getWheelRotation();
		long pos = panel.getDocPosition()+scrolled;
		if (pos<=0) pos = 1;
		panel.setDocPosition(pos);
	}


	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount()==2 ){
			try {
				JTextArea text = panel.getTextArea();
				int caretPos = text.getCaretPosition();
				int lineNum = text.getLineOfOffset(caretPos);
				String panelText = text.getText();
				if(panelText.length()>caretPos && panelText.charAt(caretPos-1) == '\n')
					lineNum--;
				lineNum += panel.getDocPosition();
				lineNum--;
				if (lineNum>=0) clickedLine(lineNum);	
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//unused
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//unused
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//unused
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//unused
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//unused
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//unused
	}
	
}
