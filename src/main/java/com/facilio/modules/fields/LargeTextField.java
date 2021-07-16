package com.facilio.modules.fields;

import com.facilio.modules.FacilioModule;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class LargeTextField extends FacilioField implements SupplementRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PARENT_FIELD_NAME = "parentId";
    public static final String FILE_FIELD_NAME = "fileId";

	private FacilioModule relModule;
	private long relModuleId = -1;
	
	public LargeTextField() {
        // TODO Auto-generated constructor stub
        super();
    }

    protected LargeTextField(LargeTextField field) { // Do not forget to Handle here if new property is added
        super(field);
        this.relModule = field.relModule;
        this.relModuleId = field.relModuleId;
    }

	@Override
	public String linkName() {
		return super.getName();
	}

	@Override
	public FacilioField selectField() {
		return null;
	}

	@Override
	public FetchSupplementHandler newFetchHandler() {
		// TODO Auto-generated method stub
		return new LargeTextCRUDHandler(this);
	}

	@Override
	public InsertSupplementHandler newInsertHandler() {
		// TODO Auto-generated method stub
		return new LargeTextCRUDHandler(this);
	}

	@Override
	public UpdateSupplementHandler newUpdateHandler() {
		// TODO Auto-generated method stub
		return new LargeTextCRUDHandler(this);
	}

	@Override
	public DeleteSupplementHandler newDeleteHandler() {
		// TODO Auto-generated method stub
		return new LargeTextCRUDHandler(this);
	}

}
