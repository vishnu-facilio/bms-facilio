package com.facilio.classification.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.JoinContext;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidateListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Condition condition;
        Criteria criteria = new Criteria();
        Object filters = context.get(FacilioConstants.ContextNames.FILTERS);
        if (filters == null && !context.containsKey(FacilioConstants.ContextNames.SEARCH)) {

            if (Constants.containsQueryParam(context, "fromModuleName")) {
                this.addFromModuleCondition(context, criteria);
            }
            // add parent classification
            if (Constants.containsQueryParam(context, "parentClassificationId")) {
                Long parentClassificationId = FacilioUtil.parseLong(Constants.getQueryParam(context, "parentClassificationId"));
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        String.valueOf(parentClassificationId), NumberOperators.EQUALS);
                criteria.addAndCondition(condition);

            } else {
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        null, CommonOperators.IS_EMPTY);
                criteria.addAndCondition(condition);
            }
        }
        addFetchAllCondition(context, criteria);
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria.isEmpty() ? null : criteria);
        return false;
    }

    private void addFetchAllCondition(Context context, Criteria criteria) {
        Boolean fetchAll = false;
        if (Constants.containsQueryParam(context, "fetchAll")) {
            fetchAll = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "fetchAll"));
        }
        if (!fetchAll) {
            Condition condition = CriteriaAPI.getCondition("STATUS", "status",
                    String.valueOf(true), BooleanOperators.IS);
            criteria.addAndCondition(condition);
        }
    }

    private Condition getFromModuleIdCondition(Long fromModuleId, Map<String, FacilioField> appliedModulesFiledsMap) {
        Condition fromModuleIdCondition = CriteriaAPI.getCondition(appliedModulesFiledsMap.get("moduleId").getCompleteColumnName(), "moduleId", String.valueOf(fromModuleId), NumberOperators.EQUALS);
        return fromModuleIdCondition;
    }

    private Condition getParentClassificationIdCondition(Long parentClassificationId) {
        Condition parentClassificationIdCondition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId", String.valueOf(parentClassificationId), NumberOperators.EQUALS);
        return parentClassificationIdCondition;
    }

    private void addFromModuleCondition(Context context, Criteria criteria) throws Exception {
        String fromModuleName = (String) (Constants.getQueryParam(context, "fromModuleName"));
        FacilioUtil.throwIllegalArgumentException(StringUtils.isBlank(fromModuleName), "fromModuleName cannot be empty");

        ModuleBean modBean = Constants.getModBean();
        FacilioModule fromModule = modBean.getModule(fromModuleName);
        FacilioUtil.throwIllegalArgumentException(fromModule == null, "Invalid fromModuleName");

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getClassificationAppliedModulesFields());

        Long fromModuleId = fromModule.getModuleId();

        FacilioModule classificationAppliedModules = ModuleFactory.getClassificationAppliedModules();
        FacilioModule classificationModule = modBean.getModule(Constants.getModuleName(context));

        List<JoinContext> joinContextList = new ArrayList<>();
        JoinContext join = new JoinContext(
                classificationAppliedModules,
                FieldFactory.getIdField(classificationModule),
                fieldsMap.get("classificationId"),
                JoinContext.JoinType.INNER_JOIN
        );
        Condition condition = this.getFromModuleIdCondition(fromModuleId, fieldsMap);
        Criteria joinCriteria = new Criteria();
        joinCriteria.addAndCondition(condition);
        join.setCriteria(joinCriteria);
        joinContextList.add(join);
        context.put(Constants.JOINS, joinContextList);
    }
}
