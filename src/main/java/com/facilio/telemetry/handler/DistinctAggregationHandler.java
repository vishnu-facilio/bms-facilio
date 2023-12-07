package com.facilio.telemetry.handler;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NameSpaceField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DistinctAggregationHandler implements AggregationHandler {
    @Override
    public Double aggregate(NameSpaceField nameSpaceField) throws Exception {

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = bean.getField(nameSpaceField.getFieldId());
        FacilioModule module = field.getModule();

        List<FacilioField> allFields = bean.getAllFields(field.getModule().getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        FacilioField parentField = fieldMap.get("parentId");
        FacilioField ttimeField = fieldMap.get("ttime");
        FacilioField moduleIdField = FieldFactory.getField("moduleId", "MODULEID", FieldType.NUMBER);

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(field);
        selectFields.add(parentField);
        selectFields.add(ttimeField);

        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - nameSpaceField.getDataInterval();

        FacilioField selectDistinctField = new FacilioField();
        selectDistinctField.setName(field.getName()+"distinct");
        selectDistinctField.setDisplayName(field.getDisplayName());
        selectDistinctField.setColumnName("COUNT(DISTINCT("+field.getCompleteColumnName()+"))");

        SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                .select(Collections.singletonList(selectDistinctField))
                .module(module)
                .beanClass(ReadingContext.class)
                .andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(nameSpaceField.getResourceId()), PickListOperators.IS))
                .andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(ttimeField, startTime + "," + endTime, DateOperators.BETWEEN))
                .andCondition(CriteriaAPI.getCondition(field, CommonOperators.IS_NOT_EMPTY))
                .orderBy("TTIME");


        List<Map<String, Object>> props = selectBuilder.getAsProps();

        Double value = null;

        if (props != null && !props.isEmpty() ) {
            value = (Double) props.get(0).get(field.getName());
        }

        System.out.println(value);
        nameSpaceField.setField(field);
        return value;
    }
}