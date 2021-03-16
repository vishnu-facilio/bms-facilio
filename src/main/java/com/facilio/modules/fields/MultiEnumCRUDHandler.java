package com.facilio.modules.fields;

import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class MultiEnumCRUDHandler extends BaseMultiValueCRUDHandler<Integer> {

    private MultiEnumField field;

    MultiEnumCRUDHandler(MultiEnumField field) {
        this.field = field;
    }

    @Override
    protected FacilioModule getRelModule() {
        return field.getRelModule();
    }

    @Override
    protected String getFieldName() {
        return field.getName();
    }

    @Override
    protected String getParentFieldName() {
        return MultiEnumField.PARENT_FIELD_NAME;
    }

    @Override
    protected String getValueFieldName() {
        return MultiEnumField.VALUE_FIELD_NAME;
    }

    @Override
    protected Integer parseValueField(Object value, String action) throws Exception {
        return FacilioUtil.parseInt(value);
    }

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField valueField) throws Exception {
        List<Map<String, Object>> props = relBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> record : props) {
                Long recordId = (Long) ((Map<String, Object>)record.get(getParentFieldName())).get("id");
                Integer value = (Integer) record.get(valueField.getName());
                addToRecordMap(recordId, value);
            }
        }
    }
}
