package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class BaseMultiValueCRUDHandler<E> extends CommonRelRecordCRUDHandler {

    protected abstract String getValueFieldName();

    protected abstract E parseValueField(Object value, String action) throws Exception;

    protected abstract void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField valueField) throws Exception;

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, List<FacilioField> relFields) throws Exception {
        FacilioField valueField = FieldFactory.getAsMap(relFields).get(getValueFieldName());
        fetchSupplements(isMap, relBuilder, valueField);
    }

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
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids, boolean ignoreSplNullHandling) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            List values = (List) record.get(getFieldName());
            if (values != null) {//During update if null value is returned from map, no change will be made if ignore null is false
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
                deleteOldData(ids, fields);

                List<Map<String, Object>> rels = new ArrayList<>();
                for (Long id : ids) {
                    processValueList(values, id, rels, "update");
                }
                insertData(rels, fields);
            }
            else if (ignoreSplNullHandling) { // If ignore null is true and if null is returned, older data are deleted
                deleteSupplements(ids);
            }
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

    protected void addToRecordMap (long recordId, E value) {
        Map<Long, List<E>> recordMap = recordMap();
        List<E> recordList = recordMap.get(recordId);
        if (recordList == null) {
            recordList = new ArrayList<>();
            recordMap.put(recordId, recordList);
        }
        if(value !=null){
            recordList.add(value);
        }
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
