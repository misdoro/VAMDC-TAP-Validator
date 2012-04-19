package org.vamdc.validator.gui.mainframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import org.vamdc.validator.ValidatorMain;
import org.vamdc.validator.gui.GuiSettings;
import org.vamdc.validator.gui.settings.SettingsPanel;
import org.vamdc.validator.interfaces.DocumentError;
import org.vamdc.validator.interfaces.XSAMSIOModel;

public class MainFrameController implements ActionListener {

	public static class XsamsPanelController extends TextPanelController {


		public XsamsPanelController(TextPanel model, XSAMSIOModel xsamsdoc){
			super(model);
		}

		@Override
		public void clickedLine(long lineNum) {
			panel.centerLine(lineNum);
		}
	}

	public static class ValidationPanelController extends TextPanelController{

		private XSAMSIOModel xsamsdoc;
		private TextPanel xsamsPanel;

		public ValidationPanelController(TextPanel valPanel,
				XSAMSIOModel document, TextPanel xsamsPanel) {
			super(valPanel);
			this.xsamsdoc = document;
			this.xsamsPanel = xsamsPanel;
		}

		@Override
		public void clickedLine(long lineNum) {
			DocumentError clickedError = xsamsdoc.getElementsLocator().getErrors().get((int) lineNum);
			xsamsPanel.setHighlight(clickedError.getElement(), Color.RED);
			xsamsPanel.centerLine(clickedError.getElement().getFirstLine());
		}

		/*@Override
		public void update() {
			if (xsamsdoc!=null && xsamsdoc.getElementsLocator()!=null && xsamsdoc.getElementsLocator().getErrors()!=null){
				//Get count of errors:
				List<DocumentError> errors = xsamsdoc.getElementsLocator().getErrors();
				int length = errors.size();
				if (length <=0 ){//No errors, reset me to empty.
					this.reset();
				}else{//Model has some errors data to display


					//Check if XSAMS window tracks me, re-highlight it and move
					if (GuiSettings.getBoolean(GuiSettings.TRACK_ERRORS, false)){
						//Highlight active line in me
						panel.resetHighlight();
						panel.highlightLine(panel.getScrollBar().getValue(), Color.BLUE);

						xscontroller.panel.resetHighlight();
						int start = panel.getScrollBar().getValue()-panel.getWindowRows()/2;
						if (start<0) start=0;
						int count = panel.getWindowRows();
						for (int i=start;(i<start+count)&&i<errors.size();i++){
							xscontroller.panel.addHighlight(errors.get(i).getElement(), Color.RED);
						}
						int center = panel.getScrollBar().getValue()-1;
						xscontroller.panel.centerLine(errors.get(center).getElement().getFirstLine());
					}

				}

			}
		}*/

	}

	private static class RestrictablesController extends TextPanelController{
		private JTextComponent query;
		private RestrictsPanel model;


		public RestrictablesController(RestrictsPanel model, JTextComponent query) {
			super(model);
			this.query = query;
			this.model = model;
		}

		@Override
		public void clickedLine(long lineNum) {
			//Add selected restrictable to the end of query string.
			if (this.query==null || this.query.getText()==null)
				return;
			String text = this.query.getText().trim();

			if (text.endsWith(";"))
				text = text.substring(0, text.length()-1);

			String restr = model.getRestrictable((int)lineNum);

			text+=" "+restr;
			this.query.setText(text);
			int len = text.length();
			query.setCaretPosition(len);
			query.requestFocusInWindow();

		}

	}



	private XSAMSIOModel doc;
	private MainFrame frame;
	private Thread inputThread; //Thread for xsams input

	public final LocatorPanelController locController; 
	private JFrame settingsFrame;
	private SettingsPanel settingsPanel;
	private String searchPattern="";
	private final JFileChooser saveChooser;
	private final JFileChooser loadChooser;

	public MainFrameController(XSAMSIOModel doc,MainFrame frame){
		this.doc=doc;
		this.frame=frame;

		settingsPanel = new SettingsPanel(this);
		settingsFrame = new JFrame("Settings");
		settingsFrame.setContentPane(settingsPanel);
		settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		//Init text panel controllers
		new XsamsPanelController(frame.xsamsPanel,this.doc);
		new ValidationPanelController(frame.valPanel,this.doc,frame.xsamsPanel);
		new RestrictablesController(frame.restrictPanel,frame.getQueryField());

		saveChooser = new JFileChooser();
		File cdir = new File(GuiSettings.get(GuiSettings.FILE_SAVE_PATH));
		saveChooser.setCurrentDirectory(cdir);

		loadChooser = new JFileChooser();
		File fodir = new File(GuiSettings.get(GuiSettings.FILE_OPEN_PATH));
		loadChooser.setCurrentDirectory(fodir);

		locController = new LocatorPanelController(doc,frame.xsamsPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		System.out.println(command);

		if (command == MainFrame.DO_QUERY){
			handleDoQuery();

		}else if (command == MainFrame.STOP_QUERY){
			if (inputThread!=null){
				doc.stopQuery();
				//this.done(0,);
			}
		}else if (command == MenuBar.CMD_FIND){
			//Display search dialog, start search on specified string:
			String newSearchPattern = JOptionPane.showInputDialog(frame,"Find:",searchPattern);
			if (newSearchPattern==null) return;
			else {
				searchPattern=newSearchPattern;
				frame.xsamsPanel.setSearchString(searchPattern);
			}
			searchNext();
		}else if (command == MenuBar.CMD_EXIT){
			if (JOptionPane.showConfirmDialog(frame, "Do you really want to quit?", "Quit", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION)
				System.exit(0);
		}else if (command == MenuBar.CMD_FINDNEXT){
			searchNext();
		}else if (command == MenuBar.CMD_CONFIG){
			settingsFrame.pack();
			settingsFrame.setVisible(true);
		}else if (command == MenuBar.CMD_OPEN){
			handleFileOpen();
		}else if (command == MenuBar.CMD_SAVE){
			handleFileSave();
		}else if (command == MenuBar.CMD_ABOUT){
			JOptionPane.showMessageDialog(frame, ValidatorMain.ABOUT_MESSAGE);
		}
	}


	/**
	 * Handle query action
	 */
	private void handleDoQuery(){
		//Save query
		final String query = frame.getQuery();
		if (inputThread==null){
			//Create separate thread for query execution
			inputThread = new Thread( new Runnable(){
				public void run() {
					try{
						frame.progress.setIndeterminate(true);
						doc.doQuery(query);
					}catch (Exception e){
						JOptionPane.showMessageDialog(frame, "Exception during query: "+e.getMessage(),"Query",JOptionPane.ERROR_MESSAGE);
						frame.progress.setIndeterminate(false);
						e.printStackTrace();
					}finally{
						inputThread = null;
					}
				}
			});
			
			inputThread.start();
			
		}
	}
	/**
	 * Handle file open action
	 */
	private void handleFileOpen(){
		//Show open dialog
		switch(loadChooser.showOpenDialog(frame)){
		case JFileChooser.APPROVE_OPTION:
			final File filename = loadChooser.getSelectedFile();
			if (filename.exists() && filename.canRead()&& inputThread==null){
				//Save new file path
				GuiSettings.put(GuiSettings.FILE_OPEN_PATH, filename.getPath());
				//Create a thread processing file
				inputThread = new Thread( new Runnable(){
					@Override
					public void run() {
						try{
							doc.loadFile(filename);
						}catch (Exception ex){
							JOptionPane.showMessageDialog(frame, "Exception during open: "+ex.getMessage(),"Open",JOptionPane.ERROR_MESSAGE);
						}finally{
							inputThread=null;
						}
					}
				});
				inputThread.start();


			}
		}
	}
	/**
	 * Handle file save action
	 */
	private void handleFileSave() {
		//Show save dialog
		switch(saveChooser.showSaveDialog(frame)){
		case JFileChooser.APPROVE_OPTION://If selected file
			File filename = saveChooser.getSelectedFile();
			//Check if file exists, ask user to overwrite
			if (!filename.exists() || (filename.exists() && JOptionPane.showConfirmDialog(
					frame,
					"File "+filename.getAbsolutePath()+" already exists! Overwrite?",
					"Save",
					JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)){

				//Save path in preferences for future use
				GuiSettings.put(GuiSettings.FILE_SAVE_PATH, filename.getPath());
				//Tell storage to save file
				try{
					doc.saveFile(filename);
					JOptionPane.showMessageDialog(frame, "File "+filename.getAbsolutePath()+" written successfully.","Save",JOptionPane.INFORMATION_MESSAGE);
				}catch (Exception ex){
					JOptionPane.showMessageDialog(frame, "Exception during save: "+ex.getMessage(),"Save",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Handle search
	 */
	private void searchNext(){
		if (searchPattern==null || searchPattern.equals("")) return;
		long foundLine = doc.searchString(searchPattern, frame.xsamsPanel.getDocCenter());
		if (foundLine==-1){
			switch (JOptionPane.showConfirmDialog(
					frame,
					"String "+searchPattern+" not found, start from the beginning?",
					"Search",
					JOptionPane.YES_NO_OPTION))
					{
					case JOptionPane.OK_OPTION:
						foundLine = doc.searchString(searchPattern,0);
						break;
					case JOptionPane.NO_OPTION:
						return;
					}
		}
		if (foundLine==-1){
			JOptionPane.showMessageDialog(frame, "String "+searchPattern+" not found.","Search",JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		frame.xsamsPanel.centerLine(foundLine);
	}

	/**
	 * Update child components, reload settings.
	 */
	public void reloadDocument(){
		try{
			doc.reconfigure();
			settingsFrame.setVisible(false);
			frame.updateFromModel(true);
		}catch (Exception e){
			JOptionPane.showMessageDialog(settingsFrame, "Exception while applying new settings: "+e.getMessage(),"Settings",JOptionPane.ERROR_MESSAGE);
		}
		
	}







}
