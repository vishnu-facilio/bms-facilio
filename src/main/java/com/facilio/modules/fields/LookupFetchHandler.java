package com.facilio.modules.fields;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.SupplementsExtraMetaData;

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
        lookupObjects = FieldUtil.getLookupProps(field, lookupIds, isMap, true,null,null);
    }
    @Override
    public void fetchSupplements(boolean isMap, SupplementsExtraMetaData supplementsExtraMetaData) throws Exception {
        List<FacilioField> selectableFields = supplementsExtraMetaData.getSelectableFields(field);
        Map<BaseLookupField, List<FacilioField>> oneLevelSelectableFieldsMap = supplementsExtraMetaData.getOneLevelSelectableFieldsMap(field);
        lookupObjects = FieldUtil.getLookupProps(field, lookupIds, isMap, true,selectableFields,oneLevelSelectableFieldsMap);
    }

    @Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        Map<String, Object> lookupRecord = (Map<String, Object>) record.get(field.getName());
        if (lookupObjects != null && lookupRecord != null) {
            record.put(field.getName(), lookupObjects.get(lookupRecord.get("id")));
        }
    }
}
