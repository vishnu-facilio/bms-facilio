package com.facilio.db.criteria.operators;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.LogManager;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        @SneakyThrows @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String[] module = fieldName.split("\\.");
                if(module.length > 1) {
                    fieldName = module[1];
                }
                return new FacilioModulePredicate(fieldName, computeEqualPredicate(value));
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

        @SneakyThrows @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String[] module = fieldName.split("\\.");
                if(module.length > 1) {
                    fieldName = module[1];
                }
                return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeEqualPredicate(value)));
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

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, "<");
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

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, "<=");
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

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, ">");
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

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, ">=");
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

        @SneakyThrows @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String[] module = fieldName.split("\\.");
                if(module.length > 1) {
                    fieldName = module[1];
                }
                return new FacilioModulePredicate(fieldName, getBetweenPredicate(value));
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

        @SneakyThrows @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                String[] module = fieldName.split("\\.");
                if(module.length > 1) {
                    fieldName = module[1];
                }
                return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(getBetweenPredicate(value)));
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
    public abstract FacilioModulePredicate getPredicate(String fieldName, String value);

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

    private static Double getBaseCurrencyValue(Map<String, Object> currObj) {
        Double baseCurrencyValue = null;
        String currencyValueStr = String.valueOf(currObj.get("currencyValue"));
        Double currencyValue = Double.parseDouble(currencyValueStr);
        if (currObj.containsKey("currencyCode")) {
            String currencyCode = String.valueOf(currObj.get("currencyCode"));
            try {
                CurrencyContext currency  = CurrencyUtil.getCurrencyFromCode(currencyCode);
                if (currency != null) {
                    double exchangeRate = currency.getExchangeRate();
                    baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, exchangeRate);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            baseCurrencyValue = currencyValue;
        }
        return baseCurrencyValue;
    }

    private static Predicate computeEqualPredicate(String value) {
        if(value.contains(",")) {
            List<Predicate> equalsPredicates = new ArrayList<>();
            String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
            for(String val : values) {
                equalsPredicates.add(getEqualPredicate(val));
            }
            return PredicateUtils.anyPredicate(equalsPredicates);
        }
        else {
            return getEqualPredicate(value);
        }
    }

    private static Predicate getEqualPredicate(final String value) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                if (object instanceof Map && MapUtils.isNotEmpty((Map<String, Object>) object)) {
                    Double currentVal = getBaseCurrencyValue((Map<String, Object>) object);
                    Double doubleVal = Double.valueOf(value);

                    return compareDoubles(currentVal, doubleVal, "==");
                } else {
                    return false;
                }
            }
        };
    }

    public static Predicate getBetweenPredicate(final String value) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                if (object instanceof Map && MapUtils.isNotEmpty((Map<String, Object>) object)) {
                    String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
                    Double currentVal = getBaseCurrencyValue((Map<String, Object>) object);
                    Double minVal = Double.valueOf(values[0]);
                    Double maxVal = Double.valueOf(values[1]);

                    return compareDoubles(currentVal, minVal, ">=") && compareDoubles(currentVal, maxVal, "<=");
                } else {
                    return false;
                }
            }
        };
    }

    public static FacilioModulePredicate getPredicate(String fieldName, final String value, String operator) {
        if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
            String[] module = fieldName.split("\\.");
            if(module.length > 1) {
                fieldName = module[1];
            }
            return new FacilioModulePredicate(fieldName, new Predicate() {
                public boolean evaluate(Object object) {
                    if (object instanceof Map && MapUtils.isNotEmpty((Map<String, Object>) object)) {
                        Double currentVal = getBaseCurrencyValue((Map<String, Object>) object);
                        Double doubleVal = Double.valueOf(value);

                        return compareDoubles(currentVal, doubleVal, operator);
                    } else {
                        return false;
                    }
                }
            });
        } else {
            return null;
        }
    }

    public static boolean compareDoubles(Double a, Double b, String operator) {
        int result = a.compareTo(b);
        switch (operator) {
            case "<":
                return result < 0;
            case "<=":
                return result <= 0;
            case ">":
                return result > 0;
            case ">=":
                return result >= 0;
            case "==":
                return result == 0;
            case "!=":
                return result != 0;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

}
