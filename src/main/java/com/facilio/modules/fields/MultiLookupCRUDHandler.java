package com.facilio.modules.fields;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultiLookupCRUDHandler extends BaseMultiValueCRUDHandler<Object> {

    private MultiLookupField field = null;
    MultiLookupCRUDHandler(MultiLookupField field) {
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
        return field.parentFieldName();
    }

    @Override
    protected String getValueFieldName() {
        return field.childFieldName();
    }

    @Override
    protected Long parseValueField(Object value, String action) throws Exception {
        return  field.parseLookupRecordId(value, action);
    }

    private LookupField constructValueSupplementRecord (LookupField valueField) throws Exception {
        if(LookupSpecialTypeUtil.isSpecialType(valueField.getSpecialType())) {
            return valueField;
        }
        else {
            LookupFieldMeta valueFieldMeta = null;
            if (field instanceof MultiLookupMeta) {
                MultiLookupMeta mlm = (MultiLookupMeta) field;
                if (CollectionUtils.isNotEmpty(mlm.getChildLookupFields()) || CollectionUtils.isNotEmpty(mlm.getSelectFields())) {
                    valueFieldMeta = new LookupFieldMeta(valueField, mlm.getSelectFields(), mlm.getChildLookupFields());
                }
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField primaryField = modBean.getPrimaryField(valueField.getLookupModule().getName());
            if (valueFieldMeta == null) {
                valueFieldMeta = new LookupFieldMeta(valueField, Collections.singletonList(primaryField), null);
            }
            else {
                if (CollectionUtils.isEmpty(valueFieldMeta.getSelectFields())) {
                    valueFieldMeta.setSelectFields(Collections.singletonList(primaryField));
                }
                else if (!valueFieldMeta.getSelectFields().contains(primaryField)) {
                    if (valueFieldMeta.getSelectFields() instanceof ArrayList) {
                        valueFieldMeta.getSelectFields().add(primaryField);
                    }
                    else {
                        List<FacilioField> selectFields = new ArrayList<>(valueFieldMeta.getSelectFields()); //To Handle immutable list
                        selectFields.add(primaryField);
                        valueFieldMeta.setSelectFields(selectFields);
                    }
                }
            }
            return valueFieldMeta;
        }
    }

    @Override
    protected void fetchSupplements(boolean isMap, SelectRecordsBuilder relBuilder, FacilioField parentField, FacilioField valueField) throws Exception {
        // Handling child multi lookups
        if (field instanceof MultiLookupMeta) {
            MultiLookupMeta mlm = (MultiLookupMeta) field;
            if (CollectionUtils.isNotEmpty(mlm.getChildMultiLookupFields())) {
                relBuilder.fetchSupplements(mlm.getChildMultiLookupFields());
            }
        }

        relBuilder.fetchSupplement(constructValueSupplementRecord((LookupField) valueField));

        if (isMap) {
            List<Map<String, Object>> props = relBuilder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> record : props) {
                    Long recordId = (Long) ((Map<String, Object>)record.get(parentField.getName())).get("id");
                    Object value = record.get(valueField.getName());
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


}
