package org.vamdc.validator.gui.mainframe;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.interfaces.DocumentElement.ElementTypes;

public class LocatorPanel extends JPanel implements ComponentUpdateInterface{
	private static final long serialVersionUID = -3917839864166563952L;
	
	/**
	 * Data container for locatorRow
	 * @author doronin
	 */
	public static class LocatorData{
		public final JPanel panel;
		public final ActionListener controller;
		public final ButtonGroup group;
		public LocatorData(JPanel panel, ActionListener controller, ButtonGroup group){
			this.panel = panel;
			this.controller = controller;
			this.group = group;
			
		}
	}
	
	private List<LocatorRow> locators = new ArrayList<LocatorRow>();
	private XSAMSIOModel doc;
	private LocatorData data;
	
	public LocatorPanel(ActionListener controller,XSAMSIOModel doc){
		super();
		
		data = new LocatorData(this,controller,new ButtonGroup());
		this.doc = doc;
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = LocatorRow.GRID_CELLS;
		int row=0;
		this.add(new JLabel("Locator panel"),constraints);
		
		
		locators.add(
				new LocatorRow("Atom",ElementTypes.Atom.name(),data,++row,0));
		locators.add(
				new LocatorRow("State",ElementTypes.AtomicState.name(),data,row,1));
		locators.add(
				new LocatorRow("Molecule",ElementTypes.Molecule.name(),data,++row,0));
		locators.add(
				new LocatorRow("State",ElementTypes.MolecularState.name(),data,row,1));
		locators.add(
				new LocatorRow("Particle",ElementTypes.Particle.name(),data,++row,0));
		locators.add(
				new LocatorRow("Solid",ElementTypes.Solid.name(),data,++row,0));
		locators.add(
				new LocatorRow("Radiative",ElementTypes.RadiativeTransition.name(),data,++row,0));
		locators.add(
				new LocatorRow("NonRadiative",ElementTypes.NonRadiativeTransition.name(),data,++row,0));
		locators.add(
				new LocatorRow("Collision",ElementTypes.CollisionalTransition.name(),data,++row,0));
		locators.add(
				new LocatorRow("AbsorptionCS",ElementTypes.AbsorptionCrossSection.name(),data,++row,0));
		locators.add(
				new LocatorRow("Source",ElementTypes.Source.name(),data,++row,0));
		locators.add(
				new LocatorRow("Method",ElementTypes.Method.name(),data,++row,0));
		locators.add(
				new LocatorRow("Function",ElementTypes.Function.name(),data,++row,0));
		locators.add(
				new LocatorRow("Environment",ElementTypes.Environment.name(),data,++row,0));
		
		this.setMaximumSize(this.getMinimumSize());
		this.setPreferredSize(this.getMinimumSize());
		
	}
	
	
	/**
	 * Update data from xsamsio model
	 */
	@Override
	public void resetComponent() {
		//Reset all rows
		for (LocatorRow row:locators)
			row.reset();
	}


	@Override
	public void setModel(XSAMSIOModel data) {
		this.doc = data;
	}


	@Override
	public void updateFromModel(boolean isFinal) {
		if (doc==null)
			return;
		DocumentElementsLocator loc = doc.getElementsLocator();
		if (loc==null)
			return;
		for (LocatorRow row:locators){
			row.setMaximum(loc.getElements(ElementTypes.valueOf(row.getCommand())).size());
			if (isFinal)
				row.setActionListener(data.controller);
		}
	}
	
}
