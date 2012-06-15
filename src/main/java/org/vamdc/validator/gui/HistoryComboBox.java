package org.vamdc.validator.gui;

import javax.swing.JComboBox;

public abstract class HistoryComboBox extends JComboBox{

	private static final long serialVersionUID = 7778562723435344834L;
	private String separator;
	private int historyDepth;


	public HistoryComboBox(String separator,int historyDepth){
		super();
		this.separator = separator;
		this.historyDepth=historyDepth;
		this.setEditable(true);
		loadValues();
	}

	protected abstract String getSavedString();
	protected abstract void saveString(String value);

	public String getText() {
		return this.getEditor().getItem().toString();
	}
	
	public void setText(String text) {
		this.getEditor().setItem(text);
	}

	protected void loadValues(){
		if (getSavedString()==null)
			return;
		String[] values = getSavedString().split(separator);
		for (String value:values){
			if (value.length()>1)
				this.addItem(value);
		}

	}
	
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
