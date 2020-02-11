package com.facilio.modules.fields;

import java.util.Map;

public interface FetchSupplementHandler {
    public void processRecord (Map<String,Object> record);

    public void fetchSupplements(boolean isMap) throws Exception;

    public void updateRecordWithSupplement(Map<String,Object> record);
}
