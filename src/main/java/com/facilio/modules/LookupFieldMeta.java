package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collection;

public class LookupFieldMeta extends LookupField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LookupFieldMeta(LookupField field) {
		// TODO Auto-generated constructor stub
		super(field);
	}
	
	private Collection<LookupField> childLookupFields;
	public Collection<LookupField> getChildLookupFields() {
		return childLookupFields;
	}
	public void setChildLookupFields(Collection<LookupField> childLookupFields) {
		this.childLookupFields = childLookupFields;
	}
	public void addChildLookupFIeld (LookupField childLookupField) {
		if (this.childLookupFields == null) {
			childLookupFields = new ArrayList<>();
		}
		childLookupFields.add(childLookupField);
	}
}
