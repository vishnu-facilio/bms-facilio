package com.facilio.modules.fields;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;

public class LookupField extends BaseLookupField implements SupplementRecord {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public LookupField() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public LookupField(FacilioModule module, String name, String displayName, FieldDisplayType displayType,
			String columnName, FieldType dataType, Boolean required, Boolean disabled, Boolean isDefault,
			Boolean isMainField, String relatedListDisplayName, FacilioModule lookupModule) {
		super(module, name, displayName, displayType, columnName, dataType, required, disabled, isDefault, isMainField, relatedListDisplayName, lookupModule);
	}

	public LookupField(FacilioModule module, String name, String displayName, FieldDisplayType displayType,
			String columnName, FieldType dataType, Boolean required, Boolean disabled, Boolean isDefault,
			Boolean isMainField, String specialType) {
		super(module, name, displayName, displayType, columnName, dataType, required, disabled, isDefault, isMainField, specialType);
	}

	protected LookupField(LookupField field) { // Do not forget to Handle here if new property is added
		super(field);
	}
	
	@Override
	public LookupField clone() {
		// TODO Auto-generated method stub
		return new LookupField(this);
	}

	@Override
	public String linkName() {
		return super.getName();
	}

	@Override
	public FacilioField selectField() {
		return this;
	}

	@Override
	public FetchSupplementHandler newFetchHandler() {
		return new LookupFetchHandler(this);
	}

	@Override
	public InsertSupplementHandler newInsertHandler() {
		return null;
	}

	@Override
	public UpdateSupplementHandler newUpdateHandler() {
		return null;
	}

	@Override
	public DeleteSupplementHandler newDeleteHandler() {
		return null;
	}
}
