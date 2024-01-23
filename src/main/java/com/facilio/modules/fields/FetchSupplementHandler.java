package com.facilio.modules.fields;

import com.facilio.modules.SupplementsExtraMetaData;

import java.util.Map;

public interface FetchSupplementHandler {
    public void processRecord (Map<String,Object> record);

    public void fetchSupplements(boolean isMap) throws Exception;

    public default void fetchSupplements(boolean isMap, SupplementsExtraMetaData supplementsExtraMetaData) throws Exception {
        fetchSupplements(isMap);
    }

    public void updateRecordWithSupplement(Map<String,Object> record);
}
