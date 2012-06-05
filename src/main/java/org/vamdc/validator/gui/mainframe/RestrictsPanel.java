package org.vamdc.validator.gui.mainframe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.vamdc.dictionary.Restrictable;
import org.vamdc.validator.interfaces.XSAMSIOModel;

public class RestrictsPanel extends TextPanel implements ComponentUpdateInterface{

	private static final long serialVersionUID = 5551792529563406363L;
	private XSAMSIOModel doc;
	private List<String> restricts = new ArrayList<String>();
	
	@Override
	public void updateText() {
		if (restricts!=null && restricts.size()>0){
			int length = restricts.size();
			StringBuilder text = new StringBuilder();
			int start = Math.max(this.getDocPosition()-1,0);
			int size = this.getWindowRows();
			int end = Math.min(start+size,length);
			for (int i=start;i<end;i++){
				text.append(restricts.get(i)).append("\n");
			}
			this.setText(text.toString());
			for (int i=start;i<end;i++){
				String restrict = restricts.get(i);
				Restrictable keyword=null;
				try{
					keyword=Restrictable.valueOfIgnoreCase(restrict);
				}catch (IllegalArgumentException e){}
				if (keyword==null)
					highlightLine(i+1, Color.LIGHT_GRAY);
			}
		}else{
			reset();
		}
	}

	private void reset(){
		this.setDocEnd(1);
		this.setText("Restrictables keywords");
	}
	
	@Override
	public void resetComponent() {
		updateFromModel(true);
	}

	@Override
	public void setModel(XSAMSIOModel data) {
		this.doc=data;
		updateFromModel(true);
	}

	@Override
	public void updateFromModel(boolean isFinal) {
		if (restricts!=null && doc!=null){
			restricts.clear();
			restricts.addAll(doc.getRestrictables());
			this.setDocEnd(Math.max(1,restricts.size()));
		}
		updateText();
	}

	/**
	 * Get restrictable keyword from the line N
	 * @param line index of restrictable
	 * @return String containing restrictable name
	 */
	public String getRestrictable (int line){
		if (restricts!=null)
			return restricts.get(line);
		else 
			return "";
	}
	
	
}
