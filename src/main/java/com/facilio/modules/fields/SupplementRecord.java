package com.facilio.modules.fields;

import com.facilio.util.FacilioStreamUtil;

import java.util.function.Predicate;

public interface SupplementRecord {
    public String linkName(); // Unique name used to avoid error when same supplement record is added twice or something

    public FacilioField selectField();

    public FetchSupplementHandler newFetchHandler();

    public InsertSupplementHandler newInsertHandler();

    public UpdateSupplementHandler newUpdateHandler();

    public DeleteSupplementHandler newDeleteHandler();

    public static Predicate<SupplementRecord> distinctSupplementRecord() {
        return FacilioStreamUtil.distinctByKey(SupplementRecord::linkName);
    }
}
