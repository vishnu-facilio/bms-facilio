package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.FacilioField;

import java.util.ArrayList;

public class NewPanel {
public enum Type {
		
		QUARTER(3),
		ONE_THIRD(4),
		HALF(6),
		TWO_THIRD(8),
		THREE_FOURTH(9),
		FULL(12);
		
		int value;
		Type(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
	public Type getDisplay() {
		return display;
	}
	public void setDisplay(Type display) {
		this.display = display;
	}
	Type display;
	public NewPanel(Type display)
	{
		this.display =display;
	}
	
	String title;
	public NewPanel setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	ArrayList<FacilioField> fields;
	public NewPanel setFields(ArrayList<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public ArrayList<FacilioField> getFields() {
		return this.fields;
	}
	
	public NewPanel addField(FacilioField field) {
		if (this.fields == null) {
			this.fields = new ArrayList<>();
		}
		this.fields.add(field);
		return this;
	}
}