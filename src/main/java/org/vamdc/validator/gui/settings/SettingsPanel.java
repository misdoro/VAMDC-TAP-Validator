package org.vamdc.validator.gui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.vamdc.validator.Settings;
import org.vamdc.validator.Settings.OperationModes;
import org.vamdc.validator.gui.mainframe.MainFrameController;
import org.vamdc.validator.gui.settings.FieldVerifier.Type;

/**
 * Settings main panel
 * @author doronin
 *
 */
public class SettingsPanel extends JPanel{
	private static final long serialVersionUID = -6257407488101227895L;

	public static final String CMD_SAVE="Save";
	public static final String CMD_RESET="Reset";
	public static final String CMD_DEFAULTS="Defaults";

	private MainFrameController main;
	private SettingsPanelController control;
	
	private JTextField xsamsPath,tapURL,tapSuffix,vosiURL,tempPath,
	pluginClass,pluginPrefix,pluginMaxProc,pluginMaxStates,httpConnTimeout,httpDataTimeout;
	private JRadioButton useNetMode,usePlugMode;
	private JCheckBox prettyInput;
	private JTable schemaTable;
	private NamespaceTableModel nsTableModel;

	//Button group for operation mode chooser
	private ButtonGroup opModeGroup= new ButtonGroup();

	public SettingsPanel(){
		super();
		init();
	}
	
	public SettingsPanel(MainFrameController main){
		super();
		this.main = main;
		init();
	}
	
	private void init(){
		control = new SettingsPanelController(main, this);
		initLayout();
		loadSettings();
		
	}
	
	private void initLayout(){
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		this.add(getSchemaPanel());
		this.add(getNetworkPanel());
		this.add(getPluginPanel());
		this.add(getControlPanel());
		this.setPreferredSize(new Dimension(640,480));

	}

	/**
	 * @return panel with schemaLocation input fields
	 */
	private JPanel getSchemaPanel(){
		JPanel schemaPanel = new JPanel();
		schemaPanel.setLayout(new BoxLayout(schemaPanel,BoxLayout.Y_AXIS));

		setTitle(schemaPanel,"Local settings");

		JPanel fieldPanel = new JPanel(new BorderLayout());
		fieldPanel.add(new JLabel("noNamespace schema location"),BorderLayout.WEST);
		fieldPanel.add(xsamsPath = getTextField("NoNamespaceSchema",Type.FILE),BorderLayout.CENTER);

		schemaPanel.add(fieldPanel);

		nsTableModel = new NamespaceTableModel();
		schemaTable = new JTable(nsTableModel);
		JScrollPane tablePane = new JScrollPane(schemaTable);
		schemaPanel.add(tablePane);

		JPanel tempPanel = new JPanel(new BorderLayout());
		tempPanel.add(new JLabel("temp files location"),BorderLayout.WEST);
		tempPanel.add(tempPath = getTextField("TempPath",Type.DIR),BorderLayout.CENTER);
		schemaPanel.add(tempPanel);

		return schemaPanel;
	}

	/**
	 * 
	 * @return panel with network settings
	 */
	private JPanel getNetworkPanel(){
		JPanel netPanel = new JPanel(new GridBagLayout());
		setTitle(netPanel,"Network mode settings");

		GridBagConstraints grid = new GridBagConstraints();
		grid.fill = GridBagConstraints.HORIZONTAL;

		//Mode selector
		gridNextLabel(grid);
		netPanel.add(useNetMode = new JRadioButton("Use Network Mode"),grid);
		opModeGroup.add(useNetMode);
		
		//Capabilities URL
		gridNextLabel(grid);
		netPanel.add(new JLabel("VAMDC-TAP Capabilities endpoint"),grid);
		gridItem(grid);
		netPanel.add(vosiURL = getTextField("VOSIUrl",Type.HTTPURL),grid);

		//TAP URL
		gridNextLabel(grid);
		netPanel.add(new JLabel("VAMDC-TAP sync endpoint"),grid);
		gridItem(grid);
		netPanel.add(tapURL = getTextField("TAPUrl",Type.HTTPURL),grid);
		
		//TAP suffix
		gridNextLabel(grid);
		netPanel.add(new JLabel("TAP url suffix (EXPERT OPTION! :) )"),grid);
		gridItem(grid);
		netPanel.add(tapSuffix = new JTextField(),grid);
		
		//Pretty-printing switch
		gridNextLabel(grid);
		gridItem(grid);
		netPanel.add(prettyInput=new JCheckBox("Input pretty-printing"),grid);

		//HTTP connect timeout
		gridNextLabel(grid);
		netPanel.add(new JLabel("HTTP CONNECT timeout"),grid);
		gridItem(grid);
		netPanel.add(httpConnTimeout = getTextField("ConnectTimeout",Type.INT),grid);

		//HTTP connect timeout
		gridNextLabel(grid);
		netPanel.add(new JLabel("HTTP Data timeout"),grid);
		gridItem(grid);
		netPanel.add(httpDataTimeout = getTextField("DataTimeout",Type.INT),grid);

		return netPanel;
	}

	private JPanel getPluginPanel(){
		JPanel plugPanel = new JPanel(new GridBagLayout());
		setTitle(plugPanel,"Plugin mode settings");

		GridBagConstraints grid = new GridBagConstraints();
		grid.fill = GridBagConstraints.HORIZONTAL;

		//Mode selector
		gridNextLabel(grid);
		plugPanel.add(usePlugMode = new JRadioButton("Use Plugin Mode"),grid);
		opModeGroup.add(usePlugMode);

		//Plugin class
		gridNextLabel(grid);
		plugPanel.add(new JLabel("Plugin class name"),grid);
		gridItem(grid);
		plugPanel.add(pluginClass = new JTextField(),grid);

		//Outpit ID prefix
		gridNextLabel(grid);
		plugPanel.add(new JLabel("Unique ID prefix"),grid);
		gridItem(grid);
		plugPanel.add(pluginPrefix = new JTextField(),grid);

		//Outpit states limit
		gridNextLabel(grid);
		plugPanel.add(new JLabel("States limit"),grid);
		gridItem(grid);
		plugPanel.add(pluginMaxStates = getTextField("MaxStates",Type.INT),grid);

		//Outpit states limit
		gridNextLabel(grid);
		plugPanel.add(new JLabel("Processes limit"),grid);
		gridItem(grid);
		plugPanel.add(pluginMaxProc = getTextField("MaxProcesses",Type.INT),grid);

		return plugPanel;
	}

	private JTextField getTextField(String name,Type type){
		JTextField field = new JTextField();
		FieldInputHelper helper = new FieldInputHelper(type);
		field.addActionListener(helper);
		field.addMouseListener(helper);
		field.setActionCommand(type.name());
		field.setInputVerifier(new FieldVerifier(type));
		field.setName(name);
		return field;
	}
	
	/**
	 * Set grid to next line's title
	 * @param grid
	 */
	private void gridNextLabel(GridBagConstraints grid){
		grid.gridx=0;
		grid.weightx = 0.0;
		grid.gridy++;
	}

	/**
	 * Set grid to line's item
	 * @param grid
	 */
	private void gridItem(GridBagConstraints grid){
		grid.gridx++;
		grid.weightx = 1.0;
	}

	/**
	 * Add border and title to panel
	 * @param panel 
	 * @param name Title string
	 */
	private void setTitle(JPanel panel,String name){
		TitledBorder title;
		title = BorderFactory.createTitledBorder(name);
		title.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title);
	}

	private JPanel getControlPanel(){
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));
		addButton(cPanel,SettingsPanel.CMD_DEFAULTS);
		addButton(cPanel,SettingsPanel.CMD_RESET);
		addButton(cPanel,SettingsPanel.CMD_SAVE);
		return cPanel;
	}

	private void addButton(JPanel buttonPanel, String command){
		JButton newBtn = new JButton(command);
		newBtn.addActionListener(control);
		buttonPanel.add(newBtn);
	}

	private int getInt(String value){
		try{
			return Integer.valueOf(value);
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * Save settings from all fields into application-wide preferences
	 */
	public void saveSettings(){

		Settings.putInt(Settings.HTTP_CONNECT_TIMEOUT, getInt(httpConnTimeout.getText()));
		Settings.putInt(Settings.HTTP_DATA_TIMEOUT, getInt(httpDataTimeout.getText()));

		String opMode = OperationModes.network.name();
		if(usePlugMode.isSelected())
			opMode = OperationModes.plugin.name();
		Settings.put(Settings.OperationMode,opMode);

		Settings.put(Settings.PluginClass, pluginClass.getText());
		Settings.put(Settings.PluginIDPrefix, pluginPrefix.getText());
		Settings.putInt(Settings.PluginLimitProcesses, getInt(pluginMaxProc.getText()));
		Settings.putInt(Settings.PluginLimitStates,getInt(pluginMaxStates.getText()));
		Settings.put(Settings.SchemaFile, xsamsPath.getText());
		
		//Don't save schema locations if they haven't changed
		String sl = nsTableModel.getNSString();
		if (sl!=null && !sl.equals(Settings.getDefault(Settings.SchemaLocations))){
			Settings.put(Settings.SchemaLocations,sl);
		}
		Settings.putBoolean(Settings.ServicePrettyOut, prettyInput.isSelected());
		Settings.put(Settings.ServiceTAPURL,tapURL.getText());
		Settings.put(Settings.ServiceTAPSuffix, tapSuffix.getText());
		Settings.put(Settings.ServiceVOSIURL,vosiURL.getText());
		Settings.put(Settings.StorageTempPath, tempPath.getText());
		Settings.sync();
		
	}

	/**
	 * Load all field values from properties
	 */
	public void loadSettings(){
		httpConnTimeout.setText(Settings.get(Settings.HTTP_CONNECT_TIMEOUT));
		httpDataTimeout.setText(Settings.get(Settings.HTTP_DATA_TIMEOUT));
		switch(OperationModes.valueOf(Settings.get(Settings.OperationMode))){
		case network:
			useNetMode.setSelected(true);
			break;
		case plugin:
			usePlugMode.setSelected(true);
			break;
		case file:
			useNetMode.setSelected(false);
			usePlugMode.setSelected(false);
			break;
		}
		pluginClass.setText(Settings.get(Settings.PluginClass));
		pluginPrefix.setText(Settings.get(Settings.PluginIDPrefix));
		pluginMaxProc.setText(Settings.get(Settings.PluginLimitProcesses));
		pluginMaxStates.setText(Settings.get(Settings.PluginLimitStates));
		
		xsamsPath.setText(Settings.get(Settings.SchemaFile));
		nsTableModel.setNSString(Settings.get(Settings.SchemaLocations));
		
		prettyInput.setSelected(Settings.getBoolean(Settings.ServicePrettyOut));
		tapURL.setText(Settings.get(Settings.ServiceTAPURL));
		tapSuffix.setText(Settings.get(Settings.ServiceTAPSuffix));
		vosiURL.setText(Settings.get(Settings.ServiceVOSIURL));
		tempPath.setText(Settings.get(Settings.StorageTempPath));
		
	}

}
