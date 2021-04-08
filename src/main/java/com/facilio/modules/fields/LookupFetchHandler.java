package com.facilio.modules.fields;

import com.facilio.modules.FieldUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class LookupFetchHandler implements FetchSupplementHandler {

    private LookupField field = null;
    LookupFetchHandler(LookupField field) {
        this.field = field;
    }

    private List<Long> lookupIds = new ArrayList<>();
    @Override
    public void processRecord(Map<String, Object> record) {
        Map<String, Object> lookupRecord = (Map<String, Object>) record.get(field.getName());
        if (lookupRecord != null) {
            lookupIds.add((Long) lookupRecord.get("id"));
        }
    }


    Map<Long, ? extends Object> lookupObjects = null;
    @Override
    public void fetchSupplements(boolean isMap) throws Exception {
        lookupObjects = FieldUtil.getLookupProps(field, lookupIds, isMap);
    }

    @Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        Map<String, Object> lookupRecord = (Map<String, Object>) record.get(field.getName());
        if (lookupObjects != null && lookupRecord != null) {
            record.put(field.getName(), lookupObjects.get(lookupRecord.get("id")));
        }
    }
}
