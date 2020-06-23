package com.facilio.bmsconsole.commands;

import com.facilio.activity.ActivityContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ApprovalModuleActivityListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> subModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.ACTIVITY);
            if (CollectionUtils.isEmpty(subModules)) {
                throw new IllegalArgumentException("No Activity module found");
            }
            FacilioModule activityModule = subModules.get(0);

            List<FacilioField> fields = modBean.getAllFields(activityModule.getName());
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            SelectRecordsBuilder<ActivityContext> builder = new SelectRecordsBuilder<ActivityContext>()
                    .module(activityModule)
                    .select(fields)
                    .beanClass(ActivityContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("type"),
                            CommonActivityType.APPROVAL.getValue() + "," + CommonActivityType.APPROVAL_ENTRY.getValue(),
                            NumberOperators.EQUALS))
                    .orderBy(fieldMap.get("ttime").getCompleteColumnName() + " DESC ");
            List<ActivityContext> activityContexts = builder.get();

            if (CollectionUtils.isNotEmpty(activityContexts)) {
                List<Long> parentIds = new ArrayList<>();
                List<Long> ruleIds = new ArrayList<>();
                for (ActivityContext activityContext : activityContexts) {
                    parentIds.add(activityContext.getParentId());
                    JSONObject info = activityContext.getInfo();
                    if (info != null) {
                        if (info.containsKey("ruleId")) {
                            ruleIds.add((Long) info.get("ruleId"));
                        }
                    }
                }

                FacilioModule module = modBean.getModule(moduleName);
                FacilioField primaryField = modBean.getPrimaryField(moduleName);
                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                        .module(module)
                        .select(Collections.singletonList(primaryField))
                        .andCondition(CriteriaAPI.getIdCondition(parentIds, module));

                List<Map<String, Object>> records = selectRecordsBuilder.getAsProps();
                Map<Long, String> pickList = new HashMap<>();
                if(records != null && records.size() > 0) {
                    for(Map<String, Object> record : records) {
                        pickList.put((Long) record.get("id"), record.get(primaryField.getName()).toString());
                    }
                }

                context.put(FacilioConstants.ContextNames.PICKLIST, pickList);

                List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules(ruleIds, false, false);
                if (CollectionUtils.isNotEmpty(workflowRules)) {
                    Map<Long, String> workflowMap = workflowRules.stream().collect(Collectors.toMap(WorkflowRuleContext::getId, WorkflowRuleContext::getName));
                    context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, workflowMap);
                }
            }

            context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, activityContexts);
        }
        return false;
    }
}
