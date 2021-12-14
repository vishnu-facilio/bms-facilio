package com.facilio.modules.fields;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BaseSystemLookupRecord;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public class SystemLookupCRUDHandler<SystemLookupRecord extends BaseSystemLookupRecord, SystemLookupField extends BaseSystemLookupField<SystemLookupRecord>> extends BaseSingleRelRecordCRUDHandler<Map<String, Object>> {

    public SystemLookupCRUDHandler(SystemLookupField field) {
        super(field);
    }

    @Override
    protected FacilioModule getRelModule() {
        return (( BaseSystemLookupField) getField()).getLookupModule();
    }

    private <Type> void iterateAndAddToRecordMap (List<Type> lookupRecords, ToLongFunction<Type> getParentId, Consumer<Type> addDefaultProps) {
        if (CollectionUtils.isNotEmpty(lookupRecords)) {
            for (Type record : lookupRecords) {
                long parentId = getParentId.applyAsLong(record);
                addDefaultProps.accept(record);
                addToRecordMap(parentId, record);
            }
        }
    }

    @Override
    protected Criteria additionalCriteriaOtherThanParentId(List<FacilioField> fields) throws Exception {
        Criteria criteria = new Criteria();
        FacilioField fieldIdField = FieldFactory.getAsMap(fields).get(FIELD_ID_FIELD_NAME);
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(getField().getFieldId()), NumberOperators.EQUALS));
        return criteria;
    }

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, List<FacilioField> relFields) throws Exception {
        // Doing all these separate handling because essentially these will be custom fields and if we give them as props, it'll not be converted to their POJOs
        // It'll be converted if there's proper getter/ setter and in that case it'll not be custom field
        BaseSystemLookupField field = (BaseSystemLookupField) getField();
        relBuilder.andCriteria(additionalCriteriaOtherThanParentId(relFields));
        if (isMap) {
            List<Map<String, Object>> lookupRecords = relBuilder.getAsProps();
            iterateAndAddToRecordMap(lookupRecords, r -> (long) r.get(getParentFieldName()), field::addDefaultPropsToLookupRecordDuringFetch); // Assuming parent id cannot be null which it shouldn't be
        }
        else {
            relBuilder.beanClass(((SystemLookupField)getField()).getLookupRecordClass());
            List<SystemLookupRecord> lookupRecords = relBuilder.get();
            iterateAndAddToRecordMap(lookupRecords, r -> r.getParentId(), field::addDefaultPropsToLookupRecordDuringFetch);
        }
    }

    @Override
    protected boolean isNull(Map<String, Object> prop) throws Exception {
        return MapUtils.isEmpty(prop);
    }

    public static final String FIELD_ID_FIELD_NAME = "fieldId";
    @Override
    protected Map<String, Object> createRelRecord(long parentId, Map<String, Object> prop) throws Exception {
        ((SystemLookupField) getField()).validateRecord(prop);
        prop.put(getParentFieldName(), parentId);
        prop.put(FIELD_ID_FIELD_NAME, getField().getFieldId());
        return prop;
    }
}
