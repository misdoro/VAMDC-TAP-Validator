package org.vamdc.validator.gui.mainframe;

import javax.swing.JLabel;

import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.DocumentElement.ElementTypes;

/**
 * Statusbar, it knows what to display and does so on update() :)
 * @author doronin
 *
 */
public class StatusBar extends JLabel implements ComponentUpdateInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6594708041533612581L;
	private XSAMSIOModel model;

	public StatusBar(){
		super("Status");
	}

	/**
	 * Set model
	 */
	@Override
	public void setModel(XSAMSIOModel model){
		this.model = model;
	}
	
	/**
	 * Reset to initial status
	 */
	@Override
	public void resetComponent() {
		this.setText("Status");
	}

	/**
	 * Update display data from model
	 */
	@Override
	public void updateFromModel(boolean isFinal) {
		if (model!=null){
			if (model.getErrorInfo()==null || model.getErrorInfo().equals("")){
				String statusText = "File size: "+model.getSize()+"; "+
				"Lines count: "+model.getLineCount()+"; ";
				DocumentElementsLocator loc = model.getElementsLocator();
				if (loc !=null){
					statusText+="Errors: "+loc.getErrors().size()+"; ";
					statusText+="Sources: "+loc.getElements(ElementTypes.Source).size()+"; ";
					statusText+="States: "+(loc.getElements(ElementTypes.AtomicState).size()+loc.getElements(ElementTypes.MolecularState).size())+"; ";
					statusText+="Collisions: "+loc.getElements(ElementTypes.CollisionalTransition).size()+"; ";
					statusText+="Transitions: "+loc.getElements(ElementTypes.RadiativeTransition).size()+"; ";
				}
				this.setText(statusText);
			}else if (model.getErrorInfo()!=null)
				this.setText(model.getErrorInfo());
		}else this.setText("Model not initialized");
	}

}
