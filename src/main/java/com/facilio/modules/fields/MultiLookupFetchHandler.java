package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.RelRecord;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiLookupFetchHandler implements FetchSupplementHandler {

    private MultiLookupField field = null;
    public MultiLookupFetchHandler(MultiLookupField multiLookup) {
        this.field = multiLookup;
    }

    private List<Long> ids = new ArrayList<>();
    @Override
    public void processRecord(Map<String, Object> record) {
        ids.add((Long) record.get("id"));
    }

    Map<Long, List<Object>> recordMap = null;
    @Override
    public void fetchSupplements(boolean isMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> selectFields = modBean.getAllFields(field.getRelModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
        LookupField parentField = (LookupField) fieldMap.get(field.parentFieldName());
        LookupField childField = (LookupField) fieldMap.get(field.childFieldName());

        SelectRecordsBuilder<RelRecord> relBuilder = new SelectRecordsBuilder<RelRecord>()
                                                        .select(selectFields)
                                                        .module(field.getRelModule())
                                                        .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                                                        ;

        if (field instanceof MultiLookupMeta) {
            MultiLookupMeta mlm = (MultiLookupMeta) field;

            if (CollectionUtils.isNotEmpty(mlm.getChildMultiLookupFields())) {
                relBuilder.fetchSupplements(mlm.getChildMultiLookupFields());
            }

            if (CollectionUtils.isNotEmpty(mlm.getChildLookupFields()) || CollectionUtils.isNotEmpty(mlm.getSelectFields())) {
                LookupFieldMeta meta = new LookupFieldMeta(childField);
                meta.setSelectFields(mlm.getSelectFields());
                meta.setChildLookupFields(mlm.getChildLookupFields());
                relBuilder.fetchSupplement(meta);
            }
        }

        if (isMap) {
            List<Map<String, Object>> props = relBuilder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> record : props) {
                    Long recordId = (Long) ((Map<String, Object>)record.get(parentField.getName())).get("id");
                    Object value = record.get(childField.getName());
                    addToRecordMap(recordId, value);
                }
            }
        }
        else {
            List<RelRecord> props = relBuilder.beanClass(RelRecord.class).get();
            for (RelRecord rel : props) {
                Object parentObject = null;
                Object value = null;

                switch (field.getParentFieldPositionEnum()) {
                    case LEFT:
                        parentObject = rel.getLeft();
                        value = rel.getRight();
                        break;
                    case RIGHT:
                        parentObject = rel.getRight();
                        value = rel.getRight();
                        break;
                }

                long recordId = field.parseLookupRecordId(parentObject, "fetch");
                addToRecordMap(recordId, value);
            }
        }
    }

    private void addToRecordMap (long recordId, Object value) {
        Map<Long, List<Object>> recordMap = recordMap();
        List<Object> recordList = recordMap.get(recordId);
        if (recordList == null) {
            recordList = new ArrayList<>();
            recordMap.put(recordId, recordList);
        }
        recordList.add(value);
    }

    private Map<Long, List<Object>> recordMap() {
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
                record.put(field.getName(), values);
            }
        }
    }
}
