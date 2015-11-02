package org.vamdc.validator.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;

import org.vamdc.validator.Setting;

public class LazyFileChooser {
	
	private Setting pathSetting;
	private JFileChooser chooser;
	
	public LazyFileChooser(Setting setting){
		
		this.pathSetting = setting;
		
	}
	
	
	public int showOpenDialog(Component parent) throws HeadlessException{
		verifyBackingChooser();
		return chooser.showOpenDialog(parent);
	}
	
	public int showSaveDialog(Component parent) {
		verifyBackingChooser();
		return chooser.showSaveDialog(parent);
	}
	
	private void verifyBackingChooser(){
		if (chooser==null){
			chooser=new JFileChooser();
			chooser.setCurrentDirectory(new File(pathSetting.getValue()));
		}
	}

	public File getSelectedFile() {
		verifyBackingChooser();
		return chooser.getSelectedFile();
	}

	public void setSelectedFile(File file) {
		verifyBackingChooser();
		chooser.setSelectedFile(file);
	}


	public void savePath() {
		File sfile = this.getSelectedFile();
		if (sfile!=null && sfile.getPath()!=null)
			pathSetting.saveValue(sfile.getPath());
	}



}
