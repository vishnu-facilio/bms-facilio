package com.facilio.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class LookupFieldMeta extends LookupField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LookupFieldMeta(LookupField field) {
		// TODO Auto-generated constructor stub
		super(field);
	}

	private List<FacilioField> selectFields;
	public List<FacilioField> getSelectFields() {
		return selectFields;
	}
	public void setSelectFields(List<FacilioField> selectFields) {
		this.selectFields = selectFields;
	}

	private Collection<LookupField> childLookupFields;
	public Collection<LookupField> getChildLookupFields() {
		return childLookupFields;
	}
	public void setChildLookupFields(Collection<LookupField> childLookupFields) {
		this.childLookupFields = childLookupFields;
	}
	public void addChildLookupField(LookupField childLookupField) {
		if (this.childLookupFields == null) {
			childLookupFields = new ArrayList<>();
		}
		childLookupFields.add(childLookupField);
	}
}
