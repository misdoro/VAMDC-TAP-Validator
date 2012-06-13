package org.vamdc.validator.gui.mainframe;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Locator panel row with all it's controls
 * @author doronin
 */
public final class LocatorRow implements ChangeListener,ActionListener,MouseWheelListener{

	
	public static final int GRID_CELLS=6; //Cells that single locatorRow takes
	
	//Event identifiers
	public static final int SELECT_CURRENT=0;//Select current element
	public static final int SEARCH_NEXT=1;//Search for next element starting at this position
	
	private JRadioButton isActive;
	private JSpinner spinner;
	private SpinnerNumberModel model;
	private JLabel maximum;
	private JButton next;
	
	//Command to initiate (relevant block name, in fact)
	private String command;
	//Controller to call
	private ActionListener controller;
	
	
	public LocatorRow(String label, String command, LocatorPanel.LocatorData data, int row, int column){
		this.command = command;
		this.controller = data.controller;
		
		GridBagConstraints constr = new GridBagConstraints();
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.gridy = row;
		constr.gridx = column*GRID_CELLS;
		
		//Radiobutton
		isActive = new JRadioButton(label);
		data.panel.add(isActive,constr);
		constr.gridx++;
		data.group.add(isActive);
		isActive.addActionListener(this);
		
		//Some blank space
		data.panel.add(new JPanel(),constr);
		constr.gridx++;
		//Spinner with index
		model = new SpinnerNumberModel();
		
		spinner = new JSpinner();
		data.panel.add(spinner,constr);
		constr.gridx++;
		spinner.setModel(model);
		spinner.addChangeListener(this);
		spinner.addMouseWheelListener(this);
		getTextField(spinner).setColumns(5);
		
		data.panel.add(new JLabel(" of "),constr);
		constr.gridx++;
		//Label with maximum
		maximum = new JLabel();
		data.panel.add(maximum,constr);
		constr.gridx++;
		next = new JButton("->");
		next.setMargin(new Insets(1,1,1,1));
		next.setMaximumSize(next.getMinimumSize());
		next.setPreferredSize(next.getMinimumSize());
		data.panel.add(next,constr);
		constr.gridx++;
		next.addActionListener(this);
		
		this.setMaximum(0);
		
	}
	
	private JFormattedTextField getTextField(JSpinner spinner) {
	    JComponent editor = spinner.getEditor();
	    if (editor instanceof JSpinner.DefaultEditor) {
	        return ((JSpinner.DefaultEditor)editor).getTextField();
	    } else {
	        System.err.println("Unexpected editor type: "
	                           + spinner.getEditor().getClass()
	                           + " isn't a descendant of DefaultEditor");
	        return null;
	    }
	}

	/**
	 * Set maximum value, 0 to disable row (like we don't have data of this type)
	 * @param count
	 */
	public void setMaximum(int count){
		//Set minimum to correct value
		if (count>0 && model.getMinimum().equals(0)){
			model.setMinimum(1);
			model.setValue(1);
		}else if (count==0){
			model.setMinimum(0);
			model.setValue(0);
		}
		model.setMaximum(count);
		maximum.setText(String.valueOf(count));
	}
	
	/**
	 * Set current value, accepts values in range 0<=value<maximum,
	 * displays value+1
	 * @param value value to set.
	 */
	public void setValue(int value){
		value = Math.min(value,(Integer)model.getMaximum()-1);
		value = Math.max(value,(Integer)model.getMinimum()-1);
		spinner.setValue(value+1);
	}
	
	/**
	 * Reset component to default state
	 */
	public void reset(){
		this.setMaximum(0);
		this.setActionListener(null);
	}
	
	/**
	 * Set action listener for component.
	 * @param controller action listener
	 */
	public void setActionListener(ActionListener controller){
		this.controller = controller;
	}
	
	/**
	 * Get command (xsams block element type name)
	 * @return xsams block element type name
	 */
	public String getCommand(){
		return command;
	}
	
	/**
	 * Get current index, returns displayed value-1
	 * so return value is in range of 0<=getValue()<maximum
	 * @return index of current location in array
	 */
	public int getValue(){
		return (Integer)spinner.getValue()-1;
	}
	
	private void increment(int change){
		int newValue = this.getValue()+change;
		setValue(newValue);
	}
	
	private void doAction(int event){
		if (controller!=null){
			ActionEvent actionEvent = new ActionEvent(this, event, command);
			controller.actionPerformed(actionEvent);
		}
	}
	
	//Wrappers for events, call model event with this panel and command as an event
	@Override
	public void stateChanged(ChangeEvent e) {
		
		//System.out.println("state"+this.getCommand()+spinner.getValue());
		this.isActive.setSelected(true);
		
		doAction(LocatorRow.SELECT_CURRENT);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(next))
			doAction(LocatorRow.SEARCH_NEXT);
		else
			doAction(LocatorRow.SELECT_CURRENT);
		this.isActive.setSelected(true);
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.increment(-e.getWheelRotation());
		this.isActive.setSelected(true);
	}
	
}
