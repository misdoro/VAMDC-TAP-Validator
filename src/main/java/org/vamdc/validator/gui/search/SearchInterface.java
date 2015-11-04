package org.vamdc.validator.gui.search;

public interface SearchInterface {

	public void searchString(String text,boolean ignoreCase, boolean searchNext);
	
	public void searchHere();
	
	public void searchNext();
	
}
