package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GenerateCriteriaForV4Command extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GenerateCriteriaForV4Command.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (!isV4) {
            return false;
        }

        Map<String, List<Object>> v4Filters = Constants.getQueryParams(context);
        String moduleName = Constants.getModuleName(context);
        if (MapUtils.isEmpty(v4Filters)) {
            return false;
        }

        Set<String> keys = v4Filters.keySet();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

        Criteria filterCriteria = new Criteria();

        for (String key: keys) {
            var operatorStr = StringUtils.substringAfterLast(key,"_");
            var fieldStr = StringUtils.substringBefore(key, "_");

            if (StringUtils.isEmpty(fieldStr)) {
                continue;
            }

            FacilioField field = fieldMap.get(fieldStr);
            if (field == null) {
                continue;
            }

            FieldType dataTypeEnum = field.getDataTypeEnum();
            Criteria criteria = null;

            switch (dataTypeEnum) {
                case STRING:
                    criteria = getStringFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case DECIMAL:
                case NUMBER:
                    criteria = getNumberFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case BOOLEAN:
                    criteria = getBooleanFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case DATE_TIME:
                    criteria = getDateTimeFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case DATE:
                    criteria = getDateFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case LOOKUP:
                    criteria = getLookupFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                    break;
                case ENUM:
                    criteria = getEnumFieldCriteria((String) v4Filters.get(key).get(0), operatorStr, field);
                default:

            }

            if (criteria != null) {
                filterCriteria.andCriteria(criteria);
                Constants.setFilterCriteria(context, criteria);
            }
        }

        return false;
    }

    private enum enumOperators {
        eq,
        neq
    }

    private Criteria getEnumFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        Condition condition;
        enumOperators operator;

        try {
            operator = enumOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        EnumField enumField = (EnumField) field;

        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, enumField.getIndex(val)+"", NumberOperators.EQUALS);
                break;
            case neq:
                condition = CriteriaAPI.getCondition(field, enumField.getIndex(val)+"", NumberOperators.NOT_EQUALS);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);
        return criteria;
    }

    private enum lookupOperators {
        eq,
        neq
    }

    private Criteria getLookupFieldCriteria(String val, String operatorStr, FacilioField field) throws Exception {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        Condition condition;
        lookupOperators operator;

        try {
            operator = lookupOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException exception) {
            return null;
        }

        long moduleId = field.getModuleId();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleId);

        FacilioModule.ModuleType moduleType = module.getTypeEnum();
        if (moduleType != FacilioModule.ModuleType.PICK_LIST) {
            return null;
        }

        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        FacilioField idField = fieldMap.get("id");
        FacilioField mainField = null;

        for (FacilioField f: allFields) {
            if (f.isMainField()) {
                mainField = f;
                break;
            }
        }

        if (mainField == null) {
            return null;
        }

        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder();
        selectRecordsBuilder
                .module(module)
                .select(Arrays.asList(mainField, idField));

        List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();

        Map<String, Long> lookupMap = new HashMap<>();
        for (Map<String, Object> prop: asProps) {
            long id = (long) prop.get("id");
            String mainVal = (String) prop.get(mainField.getName());
            lookupMap.put(mainVal, id);
        }

        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, lookupMap.get(val)+"", NumberOperators.EQUALS);
                break;
            case neq:
                condition = CriteriaAPI.getCondition(field, lookupMap.get(val)+"", NumberOperators.NOT_EQUALS);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);
        return criteria;
    }

    private enum dateTimeOperators {
        gt,
        lt,
        gte,
        lte,
        eq,
        neq
    }

    private Criteria getDateTimeFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        Condition condition;
        dateTimeOperators operator;
        try {
            operator = dateTimeOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, val, DateOperators.IS);
                break;
            case neq:
                condition = CriteriaAPI.getCondition(field, val, DateOperators.ISN_T);
                break;
            case lte:
            case lt:
                condition = CriteriaAPI.getCondition(field, val, DateOperators.IS_BEFORE);
                break;
            case gte:
            case gt:
                condition = CriteriaAPI.getCondition(field, val, DateOperators.IS_AFTER);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        if (operator == dateTimeOperators.lte || operator == dateTimeOperators.gte) {
            criteria.addOrCondition(CriteriaAPI.getCondition(field, val, DateOperators.IS));
        }

        return criteria;
    }


    private enum dateOperators {
        gt,
        lt,
        eq,
        gte,
        lte,
        neq
    }

    private Criteria getDateFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        Condition condition;
        dateOperators operator;
        try {
            operator = dateOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        String dayStartTime = DateTimeUtil.getDayStartTime(val, "yyyy-MM-dd", true) + "";
        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, dayStartTime, DateOperators.IS);
                break;
            case neq:
                condition = CriteriaAPI.getCondition(field, dayStartTime, DateOperators.ISN_T);
                break;
            case lte:
            case lt:
                condition = CriteriaAPI.getCondition(field, dayStartTime, DateOperators.IS_BEFORE);
                break;
            case gte:
            case gt:
                condition = CriteriaAPI.getCondition(field, dayStartTime, DateOperators.IS_AFTER);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        if (operator == dateOperators.lte || operator == dateOperators.gte) {
            criteria.addOrCondition(CriteriaAPI.getCondition(field, val, DateOperators.IS));
        }

        return criteria;
    }



    private enum numberOperators {
        gt,
        lt,
        eq,
        gte,
        lte
    }

    private Criteria getNumberFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        numberOperators operator;
        try {
            operator = numberOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        Condition condition = null;
        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, val, NumberOperators.EQUALS);
                break;
            case lt:
                condition = CriteriaAPI.getCondition(field, val, NumberOperators.LESS_THAN);
                break;
            case gt:
                condition = CriteriaAPI.getCondition(field, val, NumberOperators.GREATER_THAN);
                break;
            case gte:
                condition = CriteriaAPI.getCondition(field, val, NumberOperators.GREATER_THAN_EQUAL);
                break;
            case lte:
                condition = CriteriaAPI.getCondition(field, val, NumberOperators.LESS_THAN_EQUAL);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }

    private enum booleanOperators {
        eq
    }

    private Criteria getBooleanFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        booleanOperators operator;
        try {
            operator = booleanOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        Condition condition = null;
        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, val, BooleanOperators.IS);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }


    private enum stringOperators {
        eq,
        neq,
        con,
        sw,
        ew
    }

    private Criteria getStringFieldCriteria(String val, String operatorStr, FacilioField field) {
        if (StringUtils.isEmpty(operatorStr)) {
            operatorStr = "eq";
        }

        stringOperators operator;
        try {
            operator = stringOperators.valueOf(operatorStr);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        Condition condition;
        switch (operator) {
            case eq:
                condition = CriteriaAPI.getCondition(field, val, StringOperators.IS);
                break;
            case neq:
                condition = CriteriaAPI.getCondition(field, val, StringOperators.ISN_T);
                break;
            case con:
                condition = CriteriaAPI.getCondition(field, val, StringOperators.CONTAINS);
                break;
            case sw:
                condition = CriteriaAPI.getCondition(field, val, StringOperators.STARTS_WITH);
                break;
            case ew:
                condition = CriteriaAPI.getCondition(field, val, StringOperators.ENDS_WITH);
                break;
            default:
                return null;
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(condition);

        return criteria;
    }
}
