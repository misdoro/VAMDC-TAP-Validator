package org.vamdc.validator.gui.settings;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.MouseInputListener;
import javax.swing.text.JTextComponent;

import org.vamdc.validator.gui.GuiSettings;
import org.vamdc.validator.gui.settings.FieldVerifier.Type;

/**
 * Action listener and mouse event listener for JTextField input fields, helps to fill-in the forms. 
 * @author doronin
 *
 */

public class FieldInputHelper implements ActionListener, MouseInputListener {
	private FieldVerifier.Type type;
	private JFileChooser chooser;
	
	public FieldInputHelper(FieldVerifier.Type myType){
		this.type = myType;
		if(this.type.equals(Type.DIR)||this.type.equals(Type.FILE)){
			chooser = new JFileChooser();
			File fodir = new File(GuiSettings.get(GuiSettings.FILE_OPEN_PATH));
			chooser.setCurrentDirectory(fodir);
			chooser.setMultiSelectionEnabled(false);
			if (this.type.equals(Type.DIR))
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			else
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Component comp = (Component)e.getSource();
		comp.getParent().requestFocusInWindow();
	}

	//Open file/directory dialogs for double click
	@Override
	public void mouseClicked(MouseEvent e) {
		JTextComponent component = (JTextComponent)e.getComponent();
		if (e.getClickCount()==2){
			switch (this.type){
			case FILE:
			case DIR:
				
				switch(chooser.showOpenDialog(component)){
				case JFileChooser.APPROVE_OPTION:
					component.setText(chooser.getSelectedFile().getAbsolutePath());
					break;
				default:
					break;
				}
				
			}
		}		
	}



	@Override
	public void mouseEntered(MouseEvent e) {}



	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

}
