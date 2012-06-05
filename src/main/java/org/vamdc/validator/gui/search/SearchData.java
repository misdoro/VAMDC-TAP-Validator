package org.vamdc.validator.gui.search;

public class SearchData {

	private boolean ignoreCase;
	private String searchText;
	
	public void setData(String pattern,boolean ignoreCase){
		if (pattern==null || pattern.length()==0) return;
		else {
			this.searchText=pattern;
			this.ignoreCase = ignoreCase;
			
		}
	}
	
	public String getSearchText(){ return this.searchText; }
	public boolean ignoreCase(){ return ignoreCase; }
	
	

	
}
