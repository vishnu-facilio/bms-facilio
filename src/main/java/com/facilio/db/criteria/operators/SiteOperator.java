package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.util.LookupCriteriaUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

@Log4j
public enum SiteOperator implements Operator<Criteria> {

    SITE(126, "site");

    private SiteOperator(int operatorId, String operator) {
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
    public String getWhereClause(String fieldName, Criteria value) {
        // TODO Auto-generated method stub
        try {
            if(fieldName != null && !fieldName.isEmpty() && value != null) {
                String[] module = fieldName.split("\\.");
                if(module.length > 1) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    if(!module[1].equals(FacilioConstants.ContextNames.SITE_ID)) {
                        throw new RuntimeException("No siteId field found");
                    }
                    FacilioField siteField = modBean.getField(module[1],module[0]);
                    FacilioModule lookupModule = modBean.getModule(FacilioConstants.ContextNames.SITE);

                    if(module != null) {
                        return LookupCriteriaUtil.constructLookupQuery(siteField,FieldFactory.getIdField(lookupModule),lookupModule,value);
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
        if(value != null) {
            return value.getComputedValues();
        }
        return null;
    }

    @Override
    public FacilioModulePredicate getPredicate(String fieldName, Criteria value) {
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
            throw new IllegalArgumentException("Criteria Value cannot be null in Condition with LOOKUP operator");
        }
    }

    private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
    private static Map<String, Operator> initOperatorMap() {
        Map<String, Operator> operatorMap = new HashMap<>();
        operatorMap.putAll(PickListOperators.getAllOperators());
        operatorMap.putAll(BuildingOperator.getAllOperators());
        operatorMap.putAll(UserOperators.getAllOperators());
        for(Operator operator : values()) {
            operatorMap.put(operator.getOperator(), operator);
        }
        operatorMap.putAll(CommonOperators.getAllOperators());
        return operatorMap;
    }
    public static Map<String, Operator> getAllOperators() {
        return operatorMap;
    }
}
