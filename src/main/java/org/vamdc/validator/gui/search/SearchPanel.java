package org.vamdc.validator.gui.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.vamdc.validator.Setting;

public class SearchPanel extends JPanel implements ActionListener, CaretListener{
	
	private static final long serialVersionUID = 4894504594389348097L;

	private JTextField search = new JTextField();
	private JCheckBox ignoreCase = new JCheckBox("Ignore case");
	private JCheckBox interactive = new JCheckBox("Interactive");
	private JButton doSearch = new JButton("Search"),doClear = new JButton("Clear");
	private SearchInterface searcher;
	private String oldSearch="";
	
	public SearchPanel(SearchInterface searcher){
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(search);
		this.add(ignoreCase);
		this.add(doSearch);
		
		this.add(interactive);
		this.add(doClear);
		this.searcher=searcher;
		this.ignoreCase.setSelected(Setting.GUISearchIgnoreCase.getBool());
		this.interactive.setSelected(Setting.GUISearchInteractive.getBool());
		ignoreCase.addActionListener(this);
		doSearch.addActionListener(this);
		doClear.addActionListener(this);
		search.addActionListener(this);
		search.addCaretListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==doSearch||e.getSource()==search){
			searcher.searchString(search.getText(), ignoreCase.isSelected(),true);
		}else if (e.getSource()==doClear){
			searcher.searchString("", ignoreCase.isSelected(),false);
		}else if (e.getSource()==ignoreCase){
			Setting.GUISearchIgnoreCase.saveValue(ignoreCase.isSelected());
		}else if (e.getSource()==interactive){
			Setting.GUISearchInteractive.saveValue(interactive.isSelected());
		}
	}


	@Override
	public void caretUpdate(CaretEvent e) {
		String newSearch = search.getText();
		if (!newSearch.equals(oldSearch)&& interactive.isSelected()){
			oldSearch=newSearch;
			searcher.searchString(newSearch, ignoreCase.isSelected(), false);
		}
		System.out.println(e.toString()+newSearch);
	}

}
