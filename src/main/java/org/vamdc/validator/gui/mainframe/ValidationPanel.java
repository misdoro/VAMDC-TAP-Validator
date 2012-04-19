package org.vamdc.validator.gui.mainframe;

import java.util.List;

import org.vamdc.validator.interfaces.DocumentError;
import org.vamdc.validator.interfaces.XSAMSIOModel;

/**
 * Panel for validation errors
 * @author doronin
 *
 */
public class ValidationPanel extends TextPanel implements ComponentUpdateInterface{

	private static final long serialVersionUID = -1834226957651637105L;
	private XSAMSIOModel xsamsDoc;

	@Override
	public void updateText() {
		if (xsamsDoc!=null && xsamsDoc.getElementsLocator()!=null && xsamsDoc.getElementsLocator().getErrors()!=null){
			List<DocumentError> errors = xsamsDoc.getElementsLocator().getErrors();
			this.setText( getTextInfo(errors,this.getScrollBar().getValue(),this.getWindowRows()));
		}else
			this.resetComponent();
	}

	@Override
	public void resetComponent() {
		this.setDocEnd(1);
		this.resetHighlight();
		this.setText("Validation errors");		
	}

	@Override
	public void setModel(XSAMSIOModel data) {
		this.xsamsDoc = data;
	}

	@Override
	public void updateFromModel(boolean isFinal) {
		if (xsamsDoc!=null && xsamsDoc.getElementsLocator()!=null && xsamsDoc.getElementsLocator().getErrors()!=null){
			List<DocumentError> errors = xsamsDoc.getElementsLocator().getErrors();
			this.setDocEnd(Math.max(1,errors.size()));
		}
		updateText();
	}
	
	private String getTextInfo(List<DocumentError> errors, int start,int count){
		StringBuilder result = new StringBuilder();
		start--;
		if (start<0) start=0;
		for (int i=start;(i<start+count)&&i<errors.size();i++){
			DocumentError error = errors.get(i);
			result
				.append(error.getElement().getFirstLine())
				.append(":")
				.append(error.getElement().getFirstCol())
				.append("\t")
				.append(error.getMessage())
				.append("\n");
		}
		return result.toString();
	}
	
}
