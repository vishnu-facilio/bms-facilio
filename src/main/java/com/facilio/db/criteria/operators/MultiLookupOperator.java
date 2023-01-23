package com.facilio.db.criteria.operators;

import java.util.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.util.LookupCriteriaUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

@Log4j
public enum MultiLookupOperator implements Operator<Criteria> {

    MULTI_LOOKUP(124, "multi_lookup") {

        @Override
        public String getWhereClause(String fieldName, Criteria value) {
            // TODO Auto-generated method stub
            try {
                if (fieldName != null && !fieldName.isEmpty() && value != null) {
                    String[] module = fieldName.split("\\.");
                    if (module.length > 1) {
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        MultiLookupField lookupField = (MultiLookupField) modBean.getField(module[1], module[0]);
                        FacilioModule relModule = lookupField.getRelModule();
                        FacilioField parentField = modBean.getField(lookupField.parentFieldName(), relModule.getName());
                        FacilioField childField = modBean.getField(lookupField.childFieldName(), relModule.getName());
                        FacilioModule currentModule = modBean.getModule(module[0]);
                        FacilioModule lookupModule = lookupField.getLookupModule();
                        if (lookupModule == null && lookupField.getSpecialType() != null) {
                            lookupModule = modBean.getModule(lookupField.getSpecialType());
                        }
                        if (module != null) {
                            return LookupCriteriaUtil.constructMultiLookupQuery(currentModule, relModule, lookupModule, parentField, childField, value,"ID");
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LOGGER.info("Exception occurred ", e);
            }
            return null;
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, Criteria value) {
            // TODO Auto-generated method stub
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String[] module = fieldName.split("\\.");
                if (module.length > 1) {
                    fieldName = module[1];
                }
                return new FacilioModulePredicate(fieldName, computePredicate(value));
            }
            return null;
        }
    },
    MULTI_LOOKUP_USER(125, "multi_lookup_user") {

        @Override
        public String getWhereClause(String fieldName, Criteria value) {
            // TODO Auto-generated method stub
            try {
                if (fieldName != null && !fieldName.isEmpty() && value != null) {
                    String[] module = fieldName.split("\\.");
                    if (module.length > 1) {
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        MultiLookupField lookupField = (MultiLookupField) modBean.getField(module[1], module[0]);
                        FacilioModule relModule = lookupField.getRelModule();
                        FacilioField parentField = modBean.getField(lookupField.parentFieldName(), relModule.getName());
                        FacilioField childField = modBean.getField(lookupField.childFieldName(), relModule.getName());
                        FacilioModule currentModule = modBean.getModule(module[0]);
                        FacilioModule lookupModule = lookupField.getLookupModule();
                        if (lookupModule == null && lookupField.getSpecialType() != null) {
                            lookupModule = modBean.getModule(lookupField.getSpecialType());
                        }
                        if (module != null) {
                            return LookupCriteriaUtil.constructMultiLookupQuery(currentModule, relModule, lookupModule, parentField, childField, value,"ORG_USERID");
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LOGGER.info("Exception occurred ", e);
            }
            return null;
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, Criteria value) {
            return null;
        }
    };


    private MultiLookupOperator(int operatorId, String operator) {
        this.operatorId = operatorId;
        this.operator = operator;
    }

    private int operatorId;

    @Override
    public int getOperatorId() {
        return operatorId;
    }

    private String operator;

    @Override
    public String getOperator() {
        // TODO Auto-generated method stub
        return operator;
    }

    @Override
    public boolean isDynamicOperator() {
        return false;
    }

    @Override
    public boolean isValueNeeded() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public List<Object> computeValues(Criteria value) {
        // TODO Auto-generated method stub
        if (value != null) {
            return value.getComputedValues();
        }
        return null;
    }

    @Override
    public boolean updateFieldNameWithModule() {
        return true;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.CRITERIA;
    }

    @Override
    public void validateValue(Condition condition, Criteria value) {
        if (value == null && condition.getCriteriaValueId() == -1) {
            throw new IllegalArgumentException("Criteria Value cannot be null in Condition with MULTI_LOOKUP operator");
        }
    }

    private static Predicate computePredicate(Criteria value) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                if (object != null && object instanceof List) {
                    Predicate predicate = value.computePredicate();
                    List<Object> recordList = (List<Object>) object;
                    if (CollectionUtils.isNotEmpty(recordList)) {
                        for (Object record : recordList) {
                            if (predicate.evaluate(record)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());

    private static Map<String, Operator> initOperatorMap() {
        Map<String, Operator> operatorMap = new HashMap<>();
        operatorMap.putAll(PickListOperators.getAllOperators());
        operatorMap.putAll(BuildingOperator.getAllOperators());
        operatorMap.putAll(UserOperators.getAllOperators());
        for (Operator operator : values()) {
            operatorMap.put(operator.getOperator(), operator);
        }
        operatorMap.putAll(CommonOperators.getAllOperators());
        return operatorMap;
    }

    public static Map<String, Operator> getAllOperators() {
        return operatorMap;
    }
}
