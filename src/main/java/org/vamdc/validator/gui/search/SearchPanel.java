package org.vamdc.validator.gui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vamdc.validator.Setting;
import org.vamdc.validator.gui.PositionMemoryDialog;
import org.vamdc.validator.gui.mainframe.MainFrame;
import org.vamdc.validator.gui.mainframe.MainFrameController;

public class SearchPanel extends PositionMemoryDialog implements ActionListener{
	private static final long serialVersionUID = 2147287084424028910L;
	
	private JTextField search = new JTextField();
	private JCheckBox ignoreCase = new JCheckBox("Ignore case");
	private JButton ok = new JButton("OK"),cancel = new JButton("cancel");
	private SearchData searchData=new SearchData();
	private MainFrameController control;
	
	public SearchPanel(MainFrame frame,MainFrameController control) {
		super("Search Panel",frame,Setting.GUISearchDim);
		init();
		wph.loadDimensions();
		this.control = control;
	}
	
	private void init(){
		this.setModal(true);
		this.setContentPane(initLayout());
		this.setResizable(false);
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	private JPanel initLayout() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(search);
		panel.add(ignoreCase);
		panel.add(getButtonsPanel());
		return panel;
	}
	
	private JPanel getButtonsPanel(){
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result,BoxLayout.X_AXIS));
		result.add(ok);
		result.add(cancel);
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.hideDialog();
		if (e.getSource()==ok){
			searchData.setData(search.getText(), ignoreCase.isSelected());
			control.search();
		}
	}

	public SearchData getSearch() {
		return searchData;
	}

}
