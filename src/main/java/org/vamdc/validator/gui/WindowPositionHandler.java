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
		System.out.println("Saving dimensions for "+slaveWindow.getClass());
		Rectangle pos = slaveWindow.getBounds();
		dimensionOption.saveObject(pos);
	}
	
	public void loadDimensions(){
		System.out.println("Loading dimensions for "+slaveWindow.getClass());
		Object pos = dimensionOption.loadObject();
		slaveWindow.pack();
		if (pos instanceof Rectangle){
			Rectangle position = fitInScreen((Rectangle) pos,slaveWindow.getBounds());
			
			slaveWindow.setBounds(position);
			slaveWindow.validate();
		}
	}

	private Rectangle fitInScreen(Rectangle position, Rectangle naturalPos) {
		Rectangle screen = new Rectangle(slaveWindow.getToolkit().getScreenSize());
		fitSize(position, screen);
		if (screen.contains(position)){//window fits the screen, do nothing
			return position;
		}else if (screen.intersects(position)){//window intersects the border of the screen
			Rectangle is = screen.intersection(position);
			int dx=position.width-is.width;
			if (position.x>0)
				dx=-dx;
			int dy=position.height-is.height;
			if(position.y>0)
				dy=-dy;
			position.translate(dx, dy);
			return position;
		}else
			return naturalPos;
	}

	private void fitSize(Rectangle position, Rectangle screen) {
		if (position.width>screen.width ||position.height>screen.height){
			//window is bigger than the screen
			position.width=Math.min(position.width, screen.width);
			position.height=Math.min(position.height, screen.height);
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
