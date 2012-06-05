package org.vamdc.validator.gui.mainframe;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.vamdc.validator.interfaces.ProgressMonitor;
import org.vamdc.validator.interfaces.XSAMSIOModel;

/**
 * Main frame of TAPValidator
 * @author doronin
 */
public class MainFrame extends JFrame implements ComponentUpdateInterface, ProgressMonitor{
	private static final long serialVersionUID = 4739426234268388161L;

	public final static String PRE_QUERY = "Preview";
	public final static String DO_QUERY = "Query";
	public final static String STOP_QUERY = "Stop";
		
	public final XSAMSPanel xsamsPanel = new XSAMSPanel();
	public final ValidationPanel valPanel = new ValidationPanel();
	public final RestrictsPanel restrictPanel = new RestrictsPanel();
	//public final LocatorPanel locator = new LocatorPanel();
	public final MainFrame frame = this;
	
	private final QueryField query = new QueryField();
	private XSAMSIOModel document;
	public final JProgressBar progress = new JProgressBar();
	private StatusBar status = new StatusBar();
	
	public List<ComponentUpdateInterface> childComponents = new ArrayList<ComponentUpdateInterface>();
	
	private MainFrameController controller;
	
	public MainFrame(XSAMSIOModel doc){
		super("VAMDC-TAP service validation GUI");
		this.document = doc;
		
		//Set controller
		this.controller = new MainFrameController(document,this);
		
		//Set monitor
		this.document.setProgressMonitor(this);
		
		this.setJMenuBar(new MenuBar(controller));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setContentPane(initGUI());
		
		//Set model for all child components that need it
		this.setModel(doc);
		//Reset all child components
		this.resetComponent();
		
		this.setPreferredSize(new Dimension(800, 600));
		this.pack();
		this.setVisible(true);
		
		
	}
	

	
	public JTextComponent getQueryField(){
		if (query!=null)
			return query.getTextComponent();
		return null;
	}
	
	public String getQuery(){
		return query.getText();
	}
	
	
	
	
	private JPanel getQueryPanel(){
		JPanel queryPanel = new JPanel();
		queryPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		
		queryPanel.add(query,constraints);
		childComponents.add(query);
		constraints.weightx = 0;
		
		addActionButton(queryPanel, constraints,MainFrame.PRE_QUERY);
		addActionButton(queryPanel, constraints,MainFrame.DO_QUERY);
		addActionButton(queryPanel, constraints,MainFrame.STOP_QUERY);
		
		constraints.gridx++;
		queryPanel.add(progress,constraints);
		
		return queryPanel;
	}



	private void addActionButton(JPanel queryPanel,
			GridBagConstraints constraints,String action) {
		JButton stopQuery = new JButton(action);
		stopQuery.setActionCommand(action);
		stopQuery.addActionListener(controller);
		constraints.gridx++;
		queryPanel.add(stopQuery,constraints);
	}
	
	private Component getCenterPanel(){
		JSplitPane xsamsPane = new JSplitPane();
		xsamsPane.setResizeWeight(1);
		xsamsPane.setLeftComponent(xsamsPanel);
		childComponents.add(xsamsPanel);
		xsamsPane.setRightComponent(getRightPanel());
		return xsamsPane;
	}
	
	private Component getRightPanel(){
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		

		
		restrictPanel.setPreferredSize(new Dimension (200,160));
		restrictPanel.setMinimumSize(restrictPanel.getPreferredSize());
		rightPanel.add(restrictPanel,constraints);
		childComponents.add(restrictPanel);
		constraints.gridy++;
		
		constraints.weighty = 0.0;
		LocatorPanel locpanel = new LocatorPanel(controller.locController,document);
		rightPanel.add(locpanel,constraints);
		childComponents.add(locpanel);
		constraints.gridy++;
		
		constraints.weighty = 1.0;
		rightPanel.add(valPanel,constraints);
		childComponents.add(valPanel);
		constraints.gridy++;
		return rightPanel;
	}
	
	private JPanel initGUI(){
		JPanel windowPanel = new JPanel();
		windowPanel.setLayout(new BorderLayout());
		
		windowPanel.add(getQueryPanel(),BorderLayout.NORTH);
		windowPanel.add(getCenterPanel(),BorderLayout.CENTER);
		windowPanel.add(status,BorderLayout.SOUTH);
		childComponents.add(status);
		return windowPanel;
	}

	//Distribute updateInterface events to child components 
	@Override
	public void resetComponent() {
		for(ComponentUpdateInterface comp:childComponents)
			comp.resetComponent();
	}

	@Override
	public void setModel(XSAMSIOModel data) {
		for(ComponentUpdateInterface comp:childComponents)
			comp.setModel(data);
	}

	@Override
	public void updateFromModel(boolean isFinal) {
		for(ComponentUpdateInterface comp:childComponents)
			comp.updateFromModel(isFinal);
	}

	@Override
	public void started() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.resetComponent();
				frame.progress.setIndeterminate(true);
				frame.progress.setValue(1);
				
			}}
		);
	}

	@Override
	public void tick() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.updateFromModel(false);
				frame.progress.setIndeterminate(false);
				if (frame.progress.getValue()>=100)
					frame.progress.setValue(1);
				frame.progress.setValue(frame.progress.getValue()+1);
			}}
		);
	}
	
	@Override
	public void done(final long documentLines, final String query) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.updateFromModel(true);
				frame.progress.setIndeterminate(false);
				frame.progress.setValue(100);
			}}
		);
	}
	
}
