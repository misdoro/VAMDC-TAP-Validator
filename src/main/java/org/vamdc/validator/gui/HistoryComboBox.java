package org.vamdc.validator.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComboBox;

import org.vamdc.validator.Setting;

public class HistoryComboBox extends JComboBox<String>{

	private static final long serialVersionUID = 7778562723435344834L;
	private String separator;
	private int historyDepth;
	private Setting parameter;
	private Collection<Object> items;

	public HistoryComboBox(Setting parameter,String separator,int historyDepth){
		super();
		this.separator = separator;
		this.historyDepth=historyDepth;
		this.setEditable(true);
		this.parameter = parameter;
		this.items=new ArrayList<Object>();
		loadValues();
		new TextPopup().add(this);//TODO: fix adding HistoryComboBox
	}

	public String getText() {
		return this.getEditor().getItem().toString();
	}
	
	public void setText(String text) {
		this.getEditor().setItem(text);
	}

	protected void loadValues(){
		this.removeAllItems();
		items.clear();
		if (getSavedString()==null)
			return;
		String[] values = getSavedString().split(separator);
		for (String value:values){
			if (value.length()>1)
				this.addItem(value);
		}
	}
	
	/**
	 * Verify if an object is not yet in the list of elements
	 */
	@Override
	public void addItem(String anObject) {
		for (Object item:items){
			if (item.equals(anObject))
				return;
		}
		super.addItem(anObject);
		items.add(anObject);
	}
	
	protected String getSavedString() { return parameter.getValue(); }
	protected void saveString(String value){ parameter.saveValue(value); }

	protected void saveValue(String newValue){
		if (newValue == null || newValue.trim().length() == 0)
			return;
		
		StringBuilder serial = new StringBuilder();
		newValue=newValue.trim();
		
		newValue = newValue.replaceAll(separator, "");
		serial.append(newValue).append(separator);
		serial.append(cutHistory(getSavedString(),historyDepth));
		
		saveString(serial.toString());
		
	}

	private String cutHistory(String savedString, int historyLength) {
		int length = savedString.length();
		int curHistIndex = 0;
		int position=0;
		while(curHistIndex<historyLength && position<length){
			position=savedString.indexOf(separator, position+1);
			
			if (position==-1)
				return savedString;
			
			curHistIndex++;
		}
		return savedString.substring(0, position);
	}
	

}
