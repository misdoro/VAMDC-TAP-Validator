package org.vamdc.validator.gui.mainframe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.vamdc.validator.interfaces.DocumentElement;
import org.vamdc.validator.interfaces.DocumentElementsLocator;
import org.vamdc.validator.interfaces.XSAMSIOModel;
import org.vamdc.validator.interfaces.DocumentElement.ElementTypes;

public class LocatorPanelController implements ActionListener{
	private XSAMSIOModel doc;
	private TextPanel xsamsPanel;

	public LocatorPanelController(XSAMSIOModel doc, TextPanel xsamsPanel){
		this.doc = doc;
		this.xsamsPanel = xsamsPanel;

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		//System.out.println(event.getActionCommand()+event.getSource().getClass().toString());
		DocumentElementsLocator loc = doc.getElementsLocator();
		LocatorRow row = (LocatorRow)event.getSource();
		int command = event.getID();
		if (loc!=null){
			String element = event.getActionCommand();
			try{
				ElementTypes el = ElementTypes.valueOf(element);
				List<DocumentElement> elements = loc.getElements(el);
				switch(command){
				case LocatorRow.SEARCH_NEXT:
					int location = findNextElement(elements,xsamsPanel.getDocCenter());
					if (location>=0){
						row.setValue(location);
						selectElement(elements.get(location));
					}
					break;
				case LocatorRow.SELECT_CURRENT:
					if (elements.size()>row.getValue() && row.getValue()>=0)
						selectElement(elements.get(row.getValue()));
					break;
				}
			}catch (IllegalArgumentException ex){
			}
		}
	}

	/**
	 * Find first element starting from current line position
	 * @param elements list of elements to search in
	 * @param currentLine current active line in document
	 * @return index of next element in a list
	 */
	private int findNextElement(List<DocumentElement> elements, int currentLine){
		//Let's be foolproof: return null if we don't have elements
		if (elements == null || elements.size()==0)
			return -1;
		
		//Current location in elements
		int loc = elements.size()/2;
		//Step to jump in guessing
		int step = elements.size()/4;
		int iter = elements.size();
		while (iter-->0){
			//If location is in the start or in the end, return it.
			if (loc==0 || loc == elements.size()-1) return loc;
			//Previous element start line
			int prevStart = (int)elements.get(loc-1).getFirstLine();
			//Current element start line
			int currStart = (int)elements.get(loc).getFirstLine();
			//Return current element if position is between it and preceding one
			if (prevStart<=currentLine && currentLine<=currStart)
				return loc;
			else if (currentLine<=prevStart)
				loc-=step;//Jump in direction of beginning if previous element starts after current line 
			else loc+=step;//Jump in direction of end
			//Check if we're not out of bounds
			loc=Math.min(loc, elements.size());
			loc=Math.max(loc,0);
			//Reduce step size
			step=Math.max(step/2, 1);	
		}
		return -1;
	}
	
	/**
	 * Jump to document element, highlight it
	 * @param docElement DocumentElement to go to
	 */
	private void selectElement(DocumentElement docElement) {
		if (docElement!=null){
			xsamsPanel.resetHighlight();
			xsamsPanel.addHighlight(docElement, Color.LIGHT_GRAY);
			xsamsPanel.centerLine(docElement.getFirstLine());
		}
	}

}
