package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.modules.ValueGenerator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.base.Objects;

public enum ScopeOperator implements Operator<Object> {

    SCOPING_IS(93, " scoping_is ") {
        @Override
        public String getWhereClause(String s, Object o) {
            return null;
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
            return new FacilioModulePredicate(fieldName, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return false;
                }
            });
        }
    }
    ;

    private static Logger log = LogManager.getLogger(ScopeOperator.class.getName());

    private ScopeOperator(int operatorId, String operator) {
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
        return operator;
    }

    @Override
    public boolean isPlaceHoldersMandatory() {
        return true;
    }

    @Override
    public boolean isDynamicOperator() {
        return false;
    }

    @Override
    public boolean isValueNeeded() {
        return true;
    }

    @Override
    public List<Object> computeValues(Object value) {
        return null;
    }

    private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
    private static Map<String, Operator> initOperatorMap() {
        Map<String, Operator> operatorMap = new HashMap<>();
        for(Operator operator : values()) {
            operatorMap.put(operator.getOperator().trim(), operator);
        }
        return operatorMap;
    }
    public static Map<String, Operator> getAllOperators() {
        return operatorMap;
    }

    public String getEvaluatedValues(ValueGenerator valueGenerator) {
        try {
            String values = null;
            if (AccountUtil.getCurrentUser().getAppDomain() != null) {
                values = (String) valueGenerator.generateValueForCondition(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType());
            }
            if (values != null && !values.isEmpty()) {
                return values;
            } else {
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
