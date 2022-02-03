package com.facilio.modules.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LookupFieldMeta extends LookupField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LookupFieldMeta(LookupField field) {
		// TODO Auto-generated constructor stub
		super(field);
	}

	public LookupFieldMeta(LookupField field, List<FacilioField> selectFields, Collection<SupplementRecord> chileSupplements) {
		// TODO Auto-generated constructor stub
		this(field);
		this.selectFields = selectFields;
		this.childSupplements = chileSupplements;
	}

	private List<FacilioField> selectFields;
	public List<FacilioField> getSelectFields() {
		return selectFields;
	}
	public void setSelectFields(List<FacilioField> selectFields) {
		this.selectFields = selectFields;
	}

	private Collection<SupplementRecord> childSupplements;
	public Collection<SupplementRecord> getChildSupplements() {
		return childSupplements;
	}
	public void setChildSupplements(Collection<SupplementRecord> childSupplements) {
		this.childSupplements = childSupplements;
	}
	public void addChildSupplement(SupplementRecord childSupplement) {
		if (this.childSupplements == null) {
			childSupplements = new ArrayList<>();
		}
		childSupplements.add(childSupplement);
	}
}
