package org.vamdc.validator.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.vamdc.validator.Setting;

public class LazyFileChooser {

	private Setting pathSetting;
	private JFileChooser chooser;

	public LazyFileChooser(Setting setting) {

		this.pathSetting = setting;

	}

	public int showOpenDialog(Component parent) throws HeadlessException {
		verifyBackingChooser();
		return chooser.showOpenDialog(parent);
	}

	public int showSaveDialog(Component parent) {
		verifyBackingChooser();
		return chooser.showSaveDialog(parent);
	}

	private void verifyBackingChooser() {
		if (chooser == null) {
			chooser = new JFileChooser();
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
		if (sfile != null && sfile.getPath() != null)
			pathSetting.saveValue(sfile.getPath());
	}

	public File pickAFileName(Component frame, String nameSuggestion) {
		verifyBackingChooser();

		File selectedFile = null;

		this.setSelectedFile(new File(nameSuggestion));

		// Show save dialog
		if (this.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			// If selected file
			selectedFile = this.getSelectedFile();
			// Check if file exists, ask user to overwrite
			if (!selectedFile.exists()
					|| (selectedFile.exists() && JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
							frame, 
							"File " + selectedFile.getAbsolutePath() + " already exists! Overwrite?", 
							"Save",
							JOptionPane.YES_NO_OPTION)
						)
				) {
				this.savePath();
				return selectedFile;
			}
		}
		return null;
	}

}
