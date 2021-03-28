package com.facilio.modules.fields;

import com.facilio.apiv3.APIv3Config;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LineItemCRUDHandler extends BaseMultiValueCRUDHandler<Object> {

    private LineItemField field;

    @Override
    protected FacilioModule getRelModule() {
        return field.getChildModule();
    }

    @Override
    protected String getFieldName() {
        return field.getName();
    }

    @Override
    protected String getParentFieldName() {
        return field.getChildLookupField().getName();
    }

    @Override
    protected String getValueFieldName() {
        return null;
    }

    @Override
    protected Object parseValueField(Object value, String action) throws Exception {
        return value;
    }

    private Class<ModuleBaseWithCustomFields> fetchClassObj () throws Exception {
        V3Config config = ChainUtil.getV3Config(field.getChildModule().getName());
        return config == null? FacilioConstants.ContextNames.getClassFromModule(field.getChildModule()) : config.getBeanClass();
    }

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField valueField) throws Exception {
        List<Map<String, Object>> props = relBuilder.getAsProps();
        Class<ModuleBaseWithCustomFields> classObj = isMap ? null : fetchClassObj();
        for (Map<String, Object> record : props) {
            Long recordId = (Long) ((Map<String, Object>)record.remove(getParentFieldName())).get("id");
            if (isMap) {
                addToRecordMap(recordId, record);
            }
            else {
                addToRecordMap(recordId, FieldUtil.getAsBeanFromMap(record, classObj));
            }
        }
    }

    @Override
    protected Map<String, Object> createRelRecord(long parentId, Object value) throws Exception {
        Map<String, Object> prop = FieldUtil.getAsProperties(value);
        prop.put(getParentFieldName(), parentId);
        return prop;
    }
}
