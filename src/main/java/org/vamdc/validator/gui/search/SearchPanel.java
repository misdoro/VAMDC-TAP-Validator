package org.vamdc.validator.gui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vamdc.validator.Setting;

public class SearchPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 4894504594389348097L;

	private JTextField search = new JTextField();
	private JCheckBox ignoreCase = new JCheckBox("Ignore case");
	private JButton doSearch = new JButton("Search"),doClear = new JButton("Clear");
	private SearchInterface searcher;
	
	public SearchPanel(SearchInterface searcher){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(search);
		this.add(ignoreCase);
		this.add(doSearch);
		this.add(doClear);
		this.searcher=searcher;
		this.ignoreCase.setSelected(Setting.GUISearchIgnoreCase.getBool());
		ignoreCase.addActionListener(this);
		doSearch.addActionListener(this);
		doClear.addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==doSearch){
			searcher.searchString(search.getText(), ignoreCase.isSelected());
		}else if (e.getSource()==doClear){
			searcher.searchString("", ignoreCase.isSelected());
		}else if (e.getSource()==ignoreCase){
			Setting.GUISearchIgnoreCase.saveValue(ignoreCase.isSelected());
		}
	}

}
