package org.vamdc.validator.gui.mainframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.vamdc.dictionary.HeaderMetrics;
import org.vamdc.validator.Setting;
import org.vamdc.validator.ValidatorMain;
import org.vamdc.validator.gui.LazyFileChooser;
import org.vamdc.validator.gui.PositionMemoryDialog;
import org.vamdc.validator.gui.console.ConsolePanel;
import org.vamdc.validator.gui.processors.ProcessorsDialog;
import org.vamdc.validator.gui.settings.SettingsDialog;
import org.vamdc.validator.gui.textpanel.TextPanel;
import org.vamdc.validator.gui.textpanel.TextPanelController;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.io.Input;
import org.vamdc.validator.report.XMLReport;
import org.vamdc.validator.source.XSAMSSourceException;
import org.vamdc.validator.transform.GetReturnables;


public class MainFrameController implements ActionListener {

	public static class XsamsPanelController extends TextPanelController {


		public XsamsPanelController(TextPanel model, XSAMSIOModel xsamsdoc){
			super(model);
		}

		@Override
		public void clickedLine(int lineNum) {
			panel.centerLine(lineNum);
		}
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
		public void clickedLine(int lineNum) {
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
	private ConsolePanel logPanel;
	private PositionMemoryDialog settingsDialog;
	private ProcessorsDialog procs;
	private LazyFileChooser saveChooser = new LazyFileChooser(Setting.GUIFileSavePath);
	private LazyFileChooser loadChooser = new LazyFileChooser(Setting.GUIFileOpenPath);

	public MainFrameController(XSAMSIOModel doc,MainFrame frame){
		this.doc=doc;
		this.frame=frame;

		logPanel = new ConsolePanel(frame);
		settingsDialog = new SettingsDialog(frame,this);
		procs = new ProcessorsDialog(frame);
		
		


		initCloseEvent();

		//Init text panel controllers
		new XsamsPanelController(frame.xsamsPanel,this.doc);
		new ValidationPanelController(frame.valPanel,this.doc,frame.xsamsPanel);
		new RestrictablesController(frame.restrictPanel,frame.getQueryField());

		locController = new LocatorPanelController(doc,frame.xsamsPanel);

		asyncReconfigure();
	}
	
	private void asyncReconfigure() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					doc.reconfigure();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command == MainFrame.DO_QUERY){
			handleDoQuery(false);
		}else if (command == MainFrame.PRE_QUERY){
			handleDoQuery(true);
		}else if (command == MainFrame.STOP_QUERY){
			if (inputThread!=null){
				doc.stopQuery();
			}
		}else if (command == MenuBar.CMD_EXIT){
			if (JOptionPane.showConfirmDialog(frame, "Do you really want to quit?", "Quit", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION){
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		}else if (command == MenuBar.CMD_FINDNEXT){
			frame.xsamsPanel.searchNext();
		}else if (command == MenuBar.CMD_CONFIG){
			settingsDialog.setVisible(true);
		}else if (command == MenuBar.CMD_LOG){
			Setting.GUILogConsole.saveValue(true);
			logPanel.setVisible(true);
		}else if (command == MenuBar.CMD_OPEN){
			handleFileOpen();
		}else if (command == MenuBar.CMD_OPENURL){
			handleUrlLoad();
		}else if (command == MenuBar.CMD_RELOAD){
			handleFileReload();
		}else if (command == MenuBar.CMD_SAVE){
			handleFileSave();
		}else if (command == MenuBar.CMD_REPORT){
			handleSaveReport();
		}else if (command == MenuBar.CMD_ABOUT){
			JOptionPane.showMessageDialog(frame, ValidatorMain.ABOUT_MESSAGE);
		}else if (command == MenuBar.CMD_RETURNABLES){
			GetReturnables.process(doc.getInputStream());
		}else if (command == MenuBar.CMD_PROCESSORS){
			procs.setVisible(true);
		}
		
	}

	public void showLogPanel() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				logPanel.setVisible(true);
			}}
				);
	}

	
	/**
	 * Handle query action
	 */
	private void handleDoQuery(final boolean isPreview){
		//Save query
		final String query = frame.getQuery();
		System.out.println("Performing query "+query);
		if (isPreview)
			System.out.println("in preview mode");
		QueryField.validateQuery(query);
		if (inputThread==null){
			//Create separate thread for query execution
			inputThread = new Thread( new Runnable(){
				public void run() {
					try{
						frame.progress.setIndeterminate(true);
						if (isPreview){
							processHeaders(doc.previewQuery(query));
							frame.progress.setIndeterminate(false);
						}
						else{
							doc.doQuery(query);
						}
					}catch (XSAMSSourceException e){
						showError(e.getMessage(),"Query");
						System.out.println(e.getMessage());
					}catch (Exception e){
						showError(e.getMessage(),"Query");
						e.printStackTrace();
					}finally{
						frame.progress.setIndeterminate(false);
						inputThread = null;
					}
				}
			});

			inputThread.start();

		}
	}
	
	protected void processHeaders(Map<HeaderMetrics, String> previewQuery) {
		StringBuilder message=new StringBuilder();
		for (Entry<HeaderMetrics,String> metric:previewQuery.entrySet()){
			message.append(metric.getKey().name().replace("_", "-")).append(":");
			message.append(metric.getValue()).append("\n");
		}
		String result = message.toString();
		System.out.println(result);
		JOptionPane.showMessageDialog(frame,result);
	}

	/**
	 * Handle file open action
	 */
	private void handleFileOpen(){
		//Show open dialog
		if (loadChooser.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION){

			File filename = loadChooser.getSelectedFile();
			if (filename.exists() && filename.canRead()&& inputThread==null){
				loadChooser.savePath();
				asyncLoadFile(filename);
				
			}
		}
	}

	private void handleUrlLoad(){
		String url = JOptionPane.showInputDialog(frame, "Enter URL", "Open", JOptionPane.DEFAULT_OPTION);
		if (url==null)
			return;
		try {
			asyncLoadURL(new URL(url));
		} catch (MalformedURLException e) {
			showError("Malformed url\n"+e.getMessage(), "Open URL");
		}
	}

	public void asyncLoadFile(final File filename) {
		System.out.println("Loading file "+filename);
		//Create a thread processing file
		inputThread = new Thread( new Runnable(){
			@Override
			public void run() {
				try{
					doc.loadFile(filename);
				}catch (Exception ex){
					showError("Exception during open: "+ex.getMessage(),"Open");
					ex.printStackTrace();
				}finally{
					if (doc.getErrorInfo()!=null && !doc.getErrorInfo().isEmpty())
						showError("There was a problem reading the file \n"+doc.getErrorInfo(),"Open");
					inputThread=null;
				}
			}
		});
		inputThread.start();
	}

	private void handleFileReload(){
		String filename = doc.getFilename();

		if (filename!=null && filename.length()>0){
			File file = new File (filename);
			if (file.exists() && file.canRead()){
				asyncLoadFile(file);
				return;
			}else{
				try{
					final URL fileUrl = new URL(filename);
					asyncLoadURL(fileUrl);
					return;
				}catch (MalformedURLException e){
					showError("Exception during open: "+e.getMessage(),"Reload");
				};
			}
			showError("Unable to reload file "+doc.getFilename(),"Reload");
		}else{
			showError("No file was loaded","Reload");
		}
	}

	public void asyncLoadURL(final URL fileUrl) {
		System.out.println("Loading URL "+fileUrl);
		inputThread = new Thread( new Runnable(){
			@Override
			public void run() {
				try{
					doc.loadStream(Input.openStream(fileUrl));
					doc.setFilename(fileUrl.toString());
				}catch (Exception ex){
					showError("Exception during open: "+ex.getMessage(),"Open");
					ex.printStackTrace();
				}finally{
					inputThread=null;
				}
			}
		});
		inputThread.start();
	}

	/**
	 * Handle file save action
	 */
	private void handleFileSave() {
		File filename= saveChooser.pickAFileName(frame,doc.getFilename());
		if (filename!=null){
			//Tell storage to save file
			try{
				doc.saveFile(filename);
				System.out.println("File "+filename.getAbsolutePath()+" written successfully.");
			}catch (Exception ex){
				ex.printStackTrace();

			}
		}

	}

	private void showError(final String message,final String title){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(frame, message,title,JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	

	/**
	 * Handle save validation report 
	 */
	private void handleSaveReport(){
		File filename = saveChooser.pickAFileName(frame, doc.getFilename()+".report.xml");
		if (filename!=null){
			new XMLReport(doc,filename,doc.getFilename()).write();
		}
	}

	/**
	 * Update child components, reload settings.
	 */
	public void reloadComponents() throws Exception{
		doc.reconfigure();
		frame.resetComponent();
	}



	private void initCloseEvent() {
		frame.addWindowListener(
				new WindowAdapter(){
					@Override
					public void windowActivated(WindowEvent arg0) {
						frame.updateFromModel(false);//Needed for correct line count in restrictables
					}
					@Override
					public void windowClosing(WindowEvent e){
						logPanel.setVisible(false);
						settingsDialog.setVisible(false);
					}
				}
				);
	}




}
