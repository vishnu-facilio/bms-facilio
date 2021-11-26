package com.facilio.modules.fields;

import com.facilio.modules.BaseSystemLookupRecord;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class BaseSystemLookupField<LookupClass extends BaseSystemLookupRecord> extends BaseLookupField implements SupplementRecord {
    private String lookupModuleName;
    private Class<LookupClass> lookupRecordClass;
    public BaseSystemLookupField(String lookupModuleName, Class<LookupClass> lookupRecordClass) {
        this.lookupModuleName = lookupModuleName;
        this.lookupRecordClass = lookupRecordClass;
    }

    public abstract void addDefaultPropsToLookupRecordDuringFetch(Map<String, Object> lookupRecord);

    public abstract void addDefaultPropsToLookupRecordDuringFetch(LookupClass lookupRecord);

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
        return new SystemLookupCRUDHandler(this);
    }

    @Override
    public InsertSupplementHandler newInsertHandler() {
        return new SystemLookupCRUDHandler(this);
    }

    @Override
    public UpdateSupplementHandler newUpdateHandler() {
        return new SystemLookupCRUDHandler(this);
    }

    @Override
    public DeleteSupplementHandler newDeleteHandler() {
        return new SystemLookupCRUDHandler(this);
    }
}
