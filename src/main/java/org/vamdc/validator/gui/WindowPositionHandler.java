package org.vamdc.validator.gui;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.vamdc.validator.Setting;

public class WindowPositionHandler {
	
	private Setting dimensionOption;
	private Window slaveWindow;
	
	public WindowPositionHandler(Window window, Setting dimensionStore){
		this.slaveWindow = window;
		this.dimensionOption = dimensionStore;
		
		initCloseEvent();
	}
	
	public void saveDimensions(){
		Rectangle pos = slaveWindow.getBounds();
		dimensionOption.saveObject(pos);
	}
	
	public void loadDimensions(){
		Object pos = dimensionOption.loadObject();
		if (pos instanceof Rectangle){
			slaveWindow.setBounds((Rectangle) pos);
		}else{
			slaveWindow.pack();
		}
	}
	
	protected void initCloseEvent() {
		slaveWindow.addWindowListener(
				new WindowAdapter(){
					@Override
					public void windowClosing(WindowEvent e){
						saveDimensions();
					}
					@Override
					public void windowLostFocus(WindowEvent e) {
						saveDimensions();
					}
				}
				);
	}
}
