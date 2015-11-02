package org.vamdc.validator.gui;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.vamdc.validator.Setting;

/**
 * JDialog that can remember it's position on screen
 */
public class PositionMemoryDialog extends JDialog{

	private static final long serialVersionUID = 862006088829415251L;

	protected WindowPositionHandler wph;
	
	public PositionMemoryDialog(Frame owner,String name, Setting dialogPosition){
		super(owner,name);
		wph = new WindowPositionHandler(this,dialogPosition);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	@Override
	public void setVisible(boolean b){
		if (!b) wph.saveDimensions();
		super.setVisible(b);
	}
	
	
}
