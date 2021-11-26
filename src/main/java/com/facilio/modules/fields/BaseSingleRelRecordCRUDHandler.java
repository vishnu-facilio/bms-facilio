package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.CommonRelRecordCRUDHandler;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.*;

public abstract class BaseSingleRelRecordCRUDHandler<Value> extends CommonRelRecordCRUDHandler {

    protected abstract boolean isNull (Value value) throws Exception;

    protected abstract Map<String, Object> createRelRecord(long parentId, Value value) throws Exception;

    @Getter
    private FacilioField field;
    public BaseSingleRelRecordCRUDHandler(FacilioField field) {
        this.field = field;
    }

    @Override
    protected String getFieldName() {
        // TODO Auto-generated method stub
        return field.getName();
    }

    public static final String PARENT_ID_FIELD_NAME = "parentId";
    @Override
    protected String getParentFieldName() {
        // TODO Auto-generated method stub
        return PARENT_ID_FIELD_NAME;
    }

    private Map<Long, Object> recordMap = null;
    private Map<Long, Object> recordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
        return recordMap;
    }

    protected void addToRecordMap (long recordId, Object value) {
        Map<Long, Object> recordMap = recordMap();
        FacilioUtil.throwRunTimeException(recordMap.containsKey(recordId), MessageFormat.format("This shouldn't be possible. There are two rel records with field id {0} for id {1}", field.getFieldId(), recordId));
        recordMap.put(recordId, value);
    }

    @Override
    public void updateRecordWithSupplement(Map<String, Object> record) {
        if (MapUtils.isNotEmpty(recordMap)) {
            Long recordId = (Long) record.get("id");
            Object value = recordMap.get(recordId);
            record.put(getFieldName(), value);
        }
    }

    @Override
    public void insertSupplements(List<Map<String, Object>> records) throws Exception {
        if (CollectionUtils.isNotEmpty(records)) {
            List<Map<String, Object>> rels = new ArrayList<>();
            for (Map<String, Object> record : records) {
                Value val = (Value) record.get(getFieldName());
                if (!isNull(val)) {
                    long parentId = (long) record.get("id");
                    Map<String, Object> relRecord = createRelRecord(parentId,val);
                    rels.add(relRecord);
                }
            }
            addData(rels);
        }
    }

    private void addData(List<Map<String, Object>> rels) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(getRelModule().getName());
        insertData(rels, fields);
    }

    @Override
    public void updateSupplements(Map<String, Object> record, Collection<Long> ids, boolean ignoreSplNullHandling) throws Exception {
        if (CollectionUtils.isNotEmpty(ids)) {
            Value value = (Value) record.get(getFieldName());
            if (value != null) { // Not updating anything if value is null and ignore null is false
                deleteSupplements(ids);
                List<Map<String, Object>> rels = new ArrayList<>();
                for(Long id : ids) {
                    Map<String, Object> relRecord = createRelRecord(id, value);
                    rels.add(relRecord);
                }
                addData(rels);
            }
            else if (ignoreSplNullHandling) {  // Deleting older data if value is null and ignore null is true
                deleteSupplements(ids);
            }
        }
    }
}
