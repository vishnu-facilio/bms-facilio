package com.facilio.db.criteria.operators;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.util.CurrencyUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import lombok.SneakyThrows;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum MultiCurrencyOperator implements Operator<String> {
    EQUALS(134, "=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return value.contains(",") ? getBaseCondition(fieldName, value, "IN (")+")" : getBaseCondition(fieldName, value, "=");
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
    NOT_EQUALS(135, "!=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return value.contains(",") ? getBaseCondition(fieldName, value, " NOT IN (") + ")" : getBaseCondition(fieldName, value, "!=");
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
    LESS_THAN(136, "<") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "<");
            } else {
                return null;
            }
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, "<");
        }
    },
    LESS_THAN_EQUALS(137, "<=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, "<=");
            } else {
                return null;
            }
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, "<=");
        }
    },
    GREATER_THAN(138, ">") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, ">");
            } else {
                return null;
            }
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, ">");
        }
    },
    GREATER_THAN_EQUALS(139, ">=") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBaseCondition(fieldName, value, ">=");
            } else {
                return null;
            }
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicate(fieldName, value, ">=");
        }
    },
    BETWEEN(140, "between") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBetweenCondition(fieldName, value, false);
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
    NOT_BETWEEN(141, "not between") {
        @SneakyThrows @Override
        public String getWhereClause(String fieldName, String value) {
            if (fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
                return getBetweenCondition(fieldName, value, true);
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

    private static Logger log = LogManager.getLogger(MultiCurrencyOperator.class.getName());

    private MultiCurrencyOperator(int operatorId, String operator) {
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

    public static String getBaseCondition(String fieldName, String value, String operator) throws Exception {
        String[] fieldNameSplit = fieldName.split("\\.");
        if (fieldNameSplit.length > 1) {
            ModuleBean modBean = Constants.getModBean();
            MultiCurrencyField field = (MultiCurrencyField)modBean.getField(fieldNameSplit[1], fieldNameSplit[0]);
            FacilioModule module = field.getModule();

            StringBuilder builder = new StringBuilder();
            String baseCurrencyValueColumnName = field.getBaseCurrencyValueColumnName();
            if(StringUtils.isNotEmpty(baseCurrencyValueColumnName)){
                FacilioField valueField = FieldFactory.getField("baseCurrencyValue", "BaseCurrency Value", baseCurrencyValueColumnName, module, FieldType.DECIMAL);
                builder.append(valueField.getCompleteColumnName());
                builder.append(operator).append(value);
                return builder.toString();
            }
        }
        return null;
    }

    public static String getBetweenCondition (String fieldName, String value, boolean isNot) throws Exception {
        String[] fieldNameSplit = fieldName.split("\\.");
        String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        MultiCurrencyField field = (MultiCurrencyField)modBean.getField(fieldNameSplit[1], fieldNameSplit[0]);
        String baseCurrencyValueColumnName = field.getBaseCurrencyValueColumnName();
        FacilioModule module = field.getModule();
        FacilioField valueField = FieldFactory.getField("baseCurrencyValue", "BaseCurrency Value", baseCurrencyValueColumnName, module, FieldType.DECIMAL);

        StringBuilder builder = new StringBuilder();
        builder.append(valueField.getCompleteColumnName());
        if (isNot) {
            builder.append(" NOT");
        }
        builder.append(" BETWEEN ").append(values[0]).append(" AND ").append(values[1]);
        return builder.toString();
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
                if(object != null) {
                    double doubleVal = Double.parseDouble(value);
                    double currentVal = Double.parseDouble(object.toString());
                    return compareDoubles(currentVal, doubleVal, "==");
                }
                return false;
            }
        };
    }

    public static Predicate getBetweenPredicate(final String value) {
        return new Predicate() {
            public boolean evaluate(Object object) {
                if(object != null) {
                    String[] values = value.trim().split(FacilioUtil.COMMA_SPLIT_REGEX);
                    double minVal = Double.parseDouble(values[0]);
                    double maxVal = Double.parseDouble(values[1]);
                    double currentVal = Double.parseDouble(object.toString());

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
                    if(object != null) {
                        double doubleVal = Double.parseDouble(value);
                        double currentVal = Double.parseDouble(object.toString());
                        return compareDoubles(currentVal, doubleVal, operator);
                    }
                    return false;
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

