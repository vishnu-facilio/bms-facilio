package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class MultiLookupWriteHandler implements InsertSupplementHandler, UpdateSupplementHandler, DeleteSupplementHandler {

    private MultiLookupField field = null;
    MultiLookupWriteHandler(MultiLookupField field) {
        this.field = field;
    }

    @Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            for (Map<String, Object> record : records) {
                List lookups = (List) record.get(field.getName());
                if (lookups != null) { //During update if null value is returned from map, no change will be made
                    long id = (long) record.get("id");
                    processLookupList(lookups, id, rels);
                }
            }
            insertData(rels);
        }
    }


    @Override
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            List lookups = (List) record.get(field.getName());
            if (lookups != null) {
                deleteSupplements(ids);
                List<Map<String, Object>> rels = new ArrayList<>();
                for (Long id : ids) {
                    processLookupList(lookups, id, rels);
                }
                insertData(rels);
            }
        }
    }

    private void processLookupList(List lookups, long id, List<Map<String, Object>> rels) throws Exception {
        for (Object lookup : lookups) {
            rels.add(createRelRecord(id, field.parseLookupRecordId(lookup, "insert")));
        }
    }

    private void insertData(List<Map<String, Object>> rels) throws Exception {
        if (CollectionUtils.isNotEmpty(rels)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(field.getRelModule().getName());
            new InsertRecordBuilder<RelRecord>()
                    .fields(fields)
                    .module(field.getRelModule())
                    .addRecordProps(rels)
                    .save();
        }
    }

    private Map<String, Object> createRelRecord (long primarId, long secondaryId) {
        Map<String, Object> relRecord = new HashMap<>();
        relRecord.put(field.parentFieldName(), primarId);
        relRecord.put(field.childFieldName(), secondaryId);
        return relRecord;
    }

    @Override
    public void deleteSupplements(Collection<Long> ids) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(field.getRelModule().getName());
            deleteOldData(ids, fields);
        }
    }

    private void deleteOldData(Collection<Long> ids, List<FacilioField> fields) throws Exception {
        LookupField parentField = (LookupField) FieldFactory.getAsMap(fields).get(field.parentFieldName());
        int rows = new DeleteRecordBuilder<RelRecord>()
                .module(field.getRelModule())
                .andCondition(CriteriaAPI.getCondition(parentField, ids, PickListOperators.IS))
                .delete();
    }
}
