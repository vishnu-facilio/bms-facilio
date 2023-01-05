package com.facilio.db.criteria.operators;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.Logger;

import java.util.List;

public enum CurrencyOperator implements Operator<String> {
    EQUALS(116, "=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "=", true, false);
            } else {
                return null;
            }
        }
    },
    NOT_EQUALS(117, "!=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "=", true, true);
            } else {
                return null;
            }
        }
    },
    LESS_THAN(118, "<") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "<", true, false);
            } else {
                return null;
            }
        }
    },
    LESS_THAN_EQUALS(119, "<=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "<=", true, false);
            } else {
                return null;
            }
        }
    },
    GREATER_THAN(120, ">") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, ">", true, false);
            } else {
                return null;
            }
        }
    },
    GREATER_THAN_EQUALS(121, ">=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, ">=", true, false);
            } else {
                return null;
            }
        }
    },
    BETWEEN(122, "between") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String baseCondition = getBaseCondition(fieldName, null, null, false, false);
                String betweenCondition = getBetweenCondition(value);
                return baseCondition + betweenCondition;
            } else {
                return null;
            }
        }
    },
    NOT_BETWEEN(123, "not between") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String baseCondition = getBaseCondition(fieldName, null, null, false, true);
                String betweenCondition = getBetweenCondition(value);
                return baseCondition + betweenCondition;
            } else {
                return null;
            }
        }
    }
    ;

    private static Logger log = LogManager.getLogger(CurrencyOperator.class.getName());

    private CurrencyOperator(int operatorId, String operator) {
        this.operatorId = operatorId;
        this.operator = operator;
    }

    private int operatorId;
    private String operator;

    @Override
    public int getOperatorId() {
        return this.operatorId;
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public String getWhereClause(String s, String s2) {
        return null;
    }

    @Override
    public FacilioModulePredicate getPredicate(String s, String s2) {
        return null;
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
    public boolean useFieldName() {
        return true;
    }

    @Override
    public boolean updateFieldNameWithModule() {
        return true;
    }

    @Override
    public List<Object> computeValues(String s) {
        return null;
    }

    public static String getBaseCondition(String fieldName, String value, String operator, boolean fetchValue, boolean isNot) throws Exception {
        String[] fieldNameSplit = fieldName.split("\\.");
        if (fieldNameSplit.length > 1) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule currModule = modBean.getModule(FacilioConstants.SystemLookup.CURRENCY_RECORD);
            FacilioField field = modBean.getField(fieldNameSplit[1], fieldNameSplit[0]);

            StringBuilder builder = new StringBuilder();
            if (field != null) {
                FacilioField idField = FieldFactory.getIdField(field.getModule());

                builder.append(idField.getCompleteColumnName());
                if (isNot) {
                    builder.append(" NOT");
                }
                builder.append(" IN (SELECT PARENT_ID FROM ")
                        .append(currModule.getTableName())
                        .append(" WHERE ")
                        .append(currModule.getTableName()).append(".ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND ")
                        .append(currModule.getTableName()).append(".MODULEID = ").append(currModule.getModuleId()).append(" AND ")
                        .append(currModule.getTableName()).append(".FIELD_ID = ").append(field.getFieldId())
                        .append(" AND ");

                if (fetchValue) {
                    FacilioField valueField = FieldFactory.getField("baseCurrencyValue", "BaseCurrency Value", "BASECURRENCY_VALUE", currModule, FieldType.DECIMAL);
                    if (value.contains(",")) {
                        builder.append(valueField.getCompleteColumnName()).append(" IN (").append(value).append(")");
                    } else {
                        builder.append(valueField.getCompleteColumnName()).append(operator).append(value);
                    }
                    builder.append(")");
                }
            }
            return builder.toString();
        }
        return null;
    }

    public static String getBetweenCondition (String value) throws Exception {
        String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule currModule = modBean.getModule(FacilioConstants.SystemLookup.CURRENCY_RECORD);
        FacilioField valueField = FieldFactory.getField("baseCurrencyValue", "BaseCurrency Value", "BASECURRENCY_VALUE", currModule, FieldType.DECIMAL);

        StringBuilder builder = new StringBuilder();
        builder.append(valueField.getCompleteColumnName());
        builder.append(" BETWEEN ").append(values[0]).append(" AND ").append(values[1]);
        builder.append(")");
        return builder.toString();
    }
}
