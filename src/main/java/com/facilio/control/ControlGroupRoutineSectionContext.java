package com.facilio.control;

import java.util.ArrayList;
import java.util.List;

import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControlGroupRoutineSectionContext extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String name;
	ControlGroupRoutineContext routine;
	ControlGroupSection section;
	ControlGroupAssetCategory category;
	
	List<ControlGroupFieldContext> fields;
	
	public void addField(ControlGroupFieldContext field) {
		this.fields = this.fields == null ? new ArrayList<ControlGroupFieldContext>() : this.fields; 
		
		this.fields.add(field);
	}
}
