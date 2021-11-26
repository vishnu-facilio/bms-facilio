package com.facilio.modules;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class CommonRelRecordCRUDHandler implements InsertSupplementHandler, UpdateSupplementHandler, DeleteSupplementHandler, FetchSupplementHandler {

    protected abstract FacilioModule getRelModule();

    protected abstract String getParentFieldName();

    protected abstract String getFieldName();

    protected abstract void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, List<FacilioField> relFields) throws Exception;

    protected Criteria additionalCriteriaOtherThanParentId(List<FacilioField> fields) throws Exception {
        return null;
    }

    private List<Long> ids = new ArrayList<>();

    @Override
    public void processRecord(Map<String, Object> record) {
        ids.add((Long) record.get("id"));
    }

    @Override
    public void fetchSupplements(boolean isMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> selectFields = modBean.getAllFields(getRelModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
        FacilioField parentField = fieldMap.get(getParentFieldName());

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> relBuilder = new SelectRecordsBuilder<>()
                .select(selectFields)
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                ;

        fetchSupplements(isMap, relBuilder, selectFields);
    }

    protected void insertData(List<Map<String, Object>> rels, List<FacilioField> fields) throws Exception {
        if (CollectionUtils.isNotEmpty(rels)) {
            new InsertRecordBuilder()
                    .fields(fields)
                    .module(getRelModule())
                    .addRecordProps(rels)
                    .save();
        }
    }

    @Override
    public void deleteSupplements(Collection<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
            deleteOldData(ids, fields);
        }
    }

    protected void deleteOldData(Collection<Long> ids, List<FacilioField> fields) throws Exception {
        FacilioField parentField = FieldFactory.getAsMap(fields).get(getParentFieldName());
        DeleteRecordBuilder builder = new DeleteRecordBuilder()
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS));
        Criteria additionalCriteria = additionalCriteriaOtherThanParentId(fields);
        if (additionalCriteria != null) {
            builder.andCriteria(additionalCriteria);
        }
        int rows = builder.delete();
    }
}
