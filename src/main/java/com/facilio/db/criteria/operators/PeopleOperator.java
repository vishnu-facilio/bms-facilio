package com.facilio.db.criteria.operators;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.util.LookupCriteriaUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.LogManager;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.Logger;

import java.util.List;

@Log4j
public enum PeopleOperator implements Operator<String> {
    CURRENT_USER(128, "Logged In User") {
        @Override
        public String getWhereClause(String fieldName, String value) {
            return getWhereClauseForType(fieldName, false);
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicateForType(fieldName, false);
        }
    },
    NOT_CURRENT_USER(129, "Not Logged In User") {
        @Override
        public String getWhereClause(String fieldName, String value) {
            return getWhereClauseForType(fieldName, true);
        }

        @Override
        public FacilioModulePredicate getPredicate(String fieldName, String value) {
            return getPredicateForType(fieldName, true);
        }
    };

    private static String getWhereClauseForType(String fieldName, boolean isNot) {
        try {
            Long peopleOrUserId = getPeopleOrUserId(fieldName);
            FacilioField field = getField(fieldName);
            if (field != null && peopleOrUserId != null) {
                Operator op = getFieldOperator(field, isNot);
                if (op != null) {
                    if (op instanceof PickListOperators) {
                        return op.getWhereClause(field.getCompleteColumnName(), String.valueOf(peopleOrUserId));
                    } else {
                        return op.getWhereClause(fieldName, String.valueOf(peopleOrUserId));
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.info("Exception occurred ", e);
        }
        return null;
    }

    private static FacilioModulePredicate getPredicateForType(String fieldName, boolean isNot) {
        try {
            Long peopleOrUserId = getPeopleOrUserId(fieldName);
            FacilioField field = getField(fieldName);
            if (field != null && peopleOrUserId != null) {
                Operator op = getFieldOperator(field, isNot);
                if (op instanceof PickListOperators) {
                    return op.getPredicate(field.getName(), String.valueOf(peopleOrUserId));
                } else {
                    return op.getPredicate(fieldName, String.valueOf(peopleOrUserId));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.info("Exception occurred ", e);
        }
        return null;
    }

    private static Operator getFieldOperator(FacilioField field, boolean isNot) {
        if (field instanceof LookupField) {
            if (isNot) {
                return PickListOperators.ISN_T;
            } else {
                return PickListOperators.IS;
            }
        } else if (field instanceof MultiLookupField) {
            if (isNot) {
                return MultiFieldOperators.NOT_CONTAINS;
            } else {
                return MultiFieldOperators.CONTAINS;
            }
        }
        return null;
    }

    private static Long getPeopleOrUserId(String fieldName) throws Exception {
        FacilioField field = getField(fieldName);
        Long peopleOrUserId = null;
        if (field != null) {
            if (field instanceof BaseLookupField) {
                BaseLookupField baseLookupField = (BaseLookupField) field;
                if (baseLookupField != null) {
                    FacilioModule lookupModule = baseLookupField.getLookupModule();
                    if (lookupModule != null && lookupModule.getName() != null &&  AccountUtil.getCurrentUser() != null) {
                        if (lookupModule.getName().equals(FacilioConstants.ContextNames.USERS)) {
                            peopleOrUserId = AccountUtil.getCurrentUser().getId();
                        } else if (lookupModule.getName().equals(FacilioConstants.ContextNames.PEOPLE)) {
                            peopleOrUserId = AccountUtil.getCurrentUser().getPeopleId();
                        }
                    }
                }
            }
        }
        return peopleOrUserId;
    }

    private static FacilioField getField(String fieldName) throws Exception {
        if (fieldName != null && !fieldName.isEmpty()) {
            String[] module = fieldName.split("\\.");
            if (module.length > 1) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField field = modBean.getField(module[1], module[0]);
                return field;
            }
        }
        return null;
    }

    private static Logger log = LogManager.getLogger(PeopleOperator.class.getName());

    private PeopleOperator(int operatorId, String operator) {
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
    public boolean isDynamicOperator() {
        return true;
    }

    @Override
    public boolean isValueNeeded() {
        return false;
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

}
