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
		if (model!=null){
			this.setText(model.getSourceStatus());
		}else{
			this.setText("Model not initialized");
		}
	}

	/**
	 * Update display data from model
	 */
	@Override
	public void updateFromModel(boolean isFinal) {
		if (model!=null){
			if (model.getErrorInfo()==null || model.getErrorInfo().equals("")){
				StringBuilder status=new StringBuilder();
				status.append("File size: "+model.getSize()+"; ")
				.append("Lines count: "+model.getLineCount()+"; ");
				DocumentElementsLocator loc = model.getElementsLocator();
				if (loc !=null){
					status.append("Errors: "+loc.getErrors().size()+"; ");
					status.append("Sources: "+loc.getElements(ElementTypes.Source).size()+"; ");
					status.append("States: "+(loc.getElements(ElementTypes.AtomicState).size()+loc.getElements(ElementTypes.MolecularState).size())+"; ");
					status.append("Collisions: "+loc.getElements(ElementTypes.CollisionalTransition).size()+"; ");
					status.append("Transitions: "+loc.getElements(ElementTypes.RadiativeTransition).size()+"; ");
				}
				String statusText = status.toString();
				
				this.setText(statusText);
				
				if (isFinal)
					System.out.println(statusText);
				
			}else if (model.getErrorInfo()!=null)
				this.setText(model.getErrorInfo());
		}else this.setText("Model not initialized");
	}

}
