package org.vamdc.validator.gui;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistoryComboBoxTest {

	@Test
	public void testSaveString() {
		HistoryComboBoxImpl cbox = new HistoryComboBoxImpl("#",5);
		cbox.setSaved("1#2#3#4#5#6#");
		cbox.saveValue("0");
		System.out.println(cbox.getSaved());
		assertTrue(cbox.getSaved().equals("0#1#2#3#4#5"));
		
	}

}
