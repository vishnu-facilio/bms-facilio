package com.facilio.modules.fields;

public interface SupplementRecord {
    public FacilioField selectField();

    public FetchSupplementHandler newFetchHandler();

    public InsertSupplementHandler newInsertHandler();

    public UpdateSupplementHandler newUpdateHandler();

    public DeleteSupplementHandler newDeleteHandler();
}
