package com.facilio.db.criteria.operators;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.UrlField;
import com.facilio.util.FacilioUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Getter
@Log4j
@AllArgsConstructor
public enum UrlOperators implements Operator<String> {
    IS (95, "is", StringOperators.IS),
    ISN_T (96, "isn't", StringOperators.ISN_T),
    CONTAINS (97, "contains", StringOperators.CONTAINS),
    DOESNT_CONTAIN(98, "doesn't contain", StringOperators.DOESNT_CONTAIN),
    STARTS_WITH (99, "starts with", StringOperators.STARTS_WITH),
    ENDS_WITH (100, "ends with", StringOperators.ENDS_WITH)
    ;

    private int operatorId;
    private String operator;

    @NonNull
    private StringOperators stringOperator;

    @Override @SneakyThrows
    public String getWhereClause(String fieldName, String value) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fieldName), "Field Name cannot be empty for UrlOperator");
        UrlField urlField = (UrlField) CriteriaAPI.fetchFieldFromFQFieldName(fieldName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(urlField.getLookupModuleName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        return SystemLookupOperatorHelper.computeWhereClause(constructCriteria(value, fieldMap), urlField, fieldMap);
    }

    @Override @SneakyThrows
    public FacilioModulePredicate getPredicate(String fieldName, String value) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(fieldName), "Field Name cannot be empty for UrlOperator");
        UrlField urlField = (UrlField) CriteriaAPI.fetchFieldFromFQFieldName(fieldName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(urlField.getLookupModuleName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        return SystemLookupOperatorHelper.computePredicate(constructCriteria(value, fieldMap), urlField, fieldMap);
    }

    private Criteria constructCriteria(String value, Map<String, FacilioField> fieldMap) {
        Criteria criteria = new Criteria();
        criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("href"), value, stringOperator));
        criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("name"), value, stringOperator));

        return criteria;
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
    public List<Object> computeValues(String value) {
        List<Object> values = stringOperator.computeValues(value);
        values.addAll(stringOperator.computeValues(value)); // Adding twice because we'll apply string operator twice (one for href and another for name field)
        return values;
    }

    @Override
    public boolean useFieldName() {
        return true;
    }

    @Override
    public boolean updateFieldNameWithModule() {
        return true;
    }
}
