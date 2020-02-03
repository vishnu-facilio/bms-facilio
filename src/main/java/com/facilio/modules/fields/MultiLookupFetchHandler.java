package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.RelModule;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiLookupFetchHandler implements FetchSupplementHandler {

    private MultiLookup field = null;
    public MultiLookupFetchHandler(MultiLookup multiLookup) {
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
        if (CollectionUtils.isNotEmpty(ids)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            List<FacilioField> selectFields = modBean.getAllFields(field.getLookupModule().getName());
            LookupField parentField = null;
            LookupField childField = null;
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
            switch (field.getParentFieldPositionEnum()) {
                case LEFT:
                    parentField = (LookupField) fieldMap.get("left");
                    childField = (LookupField) fieldMap.get("right");
                    break;
                case RIGHT:
                    parentField = (LookupField) fieldMap.get("right");
                    childField = (LookupField) fieldMap.get("left");
                    break;
            }

            SelectRecordsBuilder<RelModule> relBuilder = new SelectRecordsBuilder<RelModule>()
                                                            .select(selectFields)
                                                            .module(field.getLookupModule())
                                                            .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                                                            ;

            if (field instanceof MultiLookupMeta) {
                MultiLookupMeta mlm = (MultiLookupMeta) field;

                if (CollectionUtils.isNotEmpty(mlm.getChildMultiLookupFields())) {
//                    relBuilder.fetchExtras(mlm.getChildMultiLookupFields());
                }

                if (CollectionUtils.isNotEmpty(mlm.getChildLookupFields()) || CollectionUtils.isNotEmpty(mlm.getSelectFields())) {
                    LookupFieldMeta meta = new LookupFieldMeta(childField);
                    meta.setSelectFields(mlm.getSelectFields());
                    meta.setChildLookupFields(mlm.getChildLookupFields());
//                    relBuilder.fetchExtra(meta);
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
                List<RelModule> props = relBuilder.get();
                for (RelModule rel : props) {
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

                    long recordId = -1;
                    if (LookupSpecialTypeUtil.isSpecialType(field.getSpecialType())) {
                        recordId = LookupSpecialTypeUtil.getLookupObjectId(field.getSpecialType(), parentObject);
                    }
                    else {
                        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) parentObject;
                        recordId = record.getId();
                    }
                    addToRecordMap(recordId, value);
                }
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
    public void updateRecord(Map<String, Object> record) {

    }
}
