package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class BaseMultiValueCRUDHandler<E> implements InsertSupplementHandler, UpdateSupplementHandler, DeleteSupplementHandler, FetchSupplementHandler {

    protected abstract FacilioModule getRelModule();

    protected abstract String getFieldName();

    protected abstract String getParentFieldName();

    protected abstract String getValueFieldName();

    protected abstract E parseValueField(Object value, String action) throws Exception;

    protected abstract void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField valueField) throws Exception;

    @Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            for (Map<String, Object> record : records) {
                List values = (List) record.get(getFieldName());
                if (CollectionUtils.isNotEmpty(values)) {
                    long id = (long) record.get("id");
                    processValueList(values, id, rels, "insert");
                }
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
            insertData(rels, fields);
        }
    }

    @Override
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            List values = (List) record.get(getFieldName());
            if (values != null) {//During update if null value is returned from map, no change will be made
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());

                deleteOldData(ids, fields);
                List<Map<String, Object>> rels = new ArrayList<>();
                for (Long id : ids) {
                    processValueList(values, id, rels, "update");
                }
                insertData(rels, fields);
            }
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

    private void deleteOldData(Collection<Long> ids, List<FacilioField> fields) throws Exception {
        LookupField parentField = (LookupField) FieldFactory.getAsMap(fields).get(getParentFieldName());
        int rows = new DeleteRecordBuilder<RelRecord>()
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                .delete();
    }

    private void insertData(List<Map<String, Object>> rels, List<FacilioField> fields) throws Exception {
        if (CollectionUtils.isNotEmpty(rels)) {
            new InsertRecordBuilder()
                    .fields(fields)
                    .module(getRelModule())
                    .addRecordProps(rels)
                    .save();
        }
    }

    private void processValueList(List values, long id, List<Map<String, Object>> rels, String action) throws Exception {
        for (Object value : values) {
            rels.add(createRelRecord(id, parseValueField(value, action)));
        }
    }

    protected Map<String, Object> createRelRecord(long parentId, E value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, Exception {
        Map<String, Object> relRecord = new HashMap<>();
        relRecord.put(getParentFieldName(), parentId);
        relRecord.put(getValueFieldName(), value);
        return relRecord;
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
        LookupField parentField = (LookupField) fieldMap.get(getParentFieldName());
        FacilioField valueField = fieldMap.get(getValueFieldName());

        SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> relBuilder = new SelectRecordsBuilder<>()
                .select(selectFields)
                .module(getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                ;

        fetchSupplements(isMap, relBuilder, valueField);
    }

    protected void addToRecordMap (long recordId, E value) {
        Map<Long, List<E>> recordMap = recordMap();
        List<E> recordList = recordMap.get(recordId);
        if (recordList == null) {
            recordList = new ArrayList<>();
            recordMap.put(recordId, recordList);
        }
        recordList.add(value);
    }

    private Map<Long, List<E>> recordMap = null;
    private Map<Long, List<E>> recordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
        return recordMap;
    }

    @Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        if (MapUtils.isNotEmpty(recordMap)) {
            Long recordId = (Long) record.get("id");
            List values = recordMap.get(recordId);
            if (CollectionUtils.isNotEmpty(values)) {
                record.put(getFieldName(), values);
            }
        }
    }
}
