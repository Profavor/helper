package com.favorsoft.shared.model;

public class DropdownModel {
	
	public DropdownModel() {
		
	}
	
	public DropdownModel(String name, String value, boolean selected) {
		this.name = name;
		this.value = value;
		this.selected  = selected ;
	}
	
	private String name;
	
	private String value;
	
	private boolean selected ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}		
	
	public static class Values{
		public Values(String name, String value, boolean selected ) {
			
		}		
		
	}
}
