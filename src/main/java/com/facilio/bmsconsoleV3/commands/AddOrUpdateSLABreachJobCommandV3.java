package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import io.opentelemetry.extension.annotations.WithSpan;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddOrUpdateSLABreachJobCommandV3 extends FacilioCommand {
    private boolean addMode;

    public AddOrUpdateSLABreachJobCommandV3(boolean addMode) {
        this.addMode = addMode;
    }

    @Override
    @WithSpan
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
        if (MapUtils.isNotEmpty(recordMap)) {
            for (String moduleName : recordMap.keySet()) {
                List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);

                if (!addMode) {
                    deleteAllExistingSLASingleRecordJob(records, "_Breach", StringOperators.ENDS_WITH, module);
                }

                FacilioChain slaEntityChain = ReadOnlyChainFactory.getAllSLAEntityChain();
                FacilioContext slaEntityContext = slaEntityChain.getContext();
                slaEntityContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                slaEntityContext.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, true);
                slaEntityChain.execute();

                List<SLAEntityContext> slaEntityList = (List<SLAEntityContext>) slaEntityContext.get(FacilioConstants.ContextNames.SLA_ENTITY_LIST);
                if (CollectionUtils.isNotEmpty(slaEntityList)) {
                    for (ModuleBaseWithCustomFields record : records) {
                        for (SLAEntityContext entity : slaEntityList) {
                            long dueFieldId = entity.getDueFieldId();
                            FacilioField field = modBean.getField(dueFieldId, moduleName);
                            Object value;
                            if (field.isDefault()) {
                                value = PropertyUtils.getProperty(record, field.getName());
                            } else {
                                value = record.getDatum(field.getName());
                            }
                            if (value instanceof Long && !FacilioUtil.isEmptyOrNull(value) && ((Long) value) > System.currentTimeMillis()) {
                                addSLAEntityBreachJob(entity.getName() + "_" + record.getId() + "_Breach", module, record, entity.getCriteria(),
                                        field, entity);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void deleteAllExistingSLASingleRecordJob(List<ModuleBaseWithCustomFields> records,
                                                           String ruleName, Operator operator, FacilioModule module) throws Exception {
        for (ModuleBaseWithCustomFields record : records) {
            long parentId = record.getId();

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", ruleName, operator));
            criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            List<WorkflowRuleContext> workflowRules = WorkflowRuleAPI.getWorkflowRules(WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE, false, criteria, null, null);
            if (CollectionUtils.isNotEmpty(workflowRules)) {
                List<Long> workFlowIds = workflowRules.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList());
                WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
            }
        }
    }

    private void addSLAEntityBreachJob(String name, FacilioModule module, ModuleBaseWithCustomFields moduleRecord, Criteria criteria, FacilioField dueField, SLAEntityContext entity) throws Exception {
        WorkflowRuleContext workflowRuleContext = new WorkflowRuleContext();
        workflowRuleContext.setName(name);
        workflowRuleContext.setRuleType(WorkflowRuleContext.RuleType.RECORD_SPECIFIC_RULE);
        workflowRuleContext.setActivityType(EventType.SCHEDULED);
        workflowRuleContext.setModule(module);
        workflowRuleContext.setParentId(moduleRecord.getId());

        workflowRuleContext.setCriteria(criteria);
        workflowRuleContext.setDateFieldId(dueField.getFieldId());

        workflowRuleContext.setInterval(0);
        workflowRuleContext.setScheduleType(WorkflowRuleContext.ScheduledRuleType.ON);

        FacilioChain recordRuleChain = TransactionChainFactory.getAddOrUpdateRecordRuleChain();
        FacilioContext recordRuleContext = recordRuleChain.getContext();
        recordRuleContext.put(FacilioConstants.ContextNames.RECORD, workflowRuleContext);

        ActionContext actionContext = getDefaultSLAMetAction(module, entity);

        recordRuleContext.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, Collections.singletonList(actionContext));
        recordRuleChain.execute();
    }

    private ActionContext getDefaultSLAMetAction(FacilioModule module, SLAEntityContext entity) {
        ActionContext actionContext = new ActionContext();
        actionContext.setActionType(ActionType.ACTIVITY_FOR_MODULE_RECORD);
        JSONObject json = new JSONObject();
        json.put("activityType", WorkOrderActivityType.SLA_MEET.getValue());
        JSONObject infoJson = new JSONObject();
        infoJson.put("message", module.getDisplayName() + " overshot its " + entity.getName());
        json.put("info", infoJson);
        actionContext.setTemplateJson(json);
        actionContext.setStatus(true);
        return actionContext;
    }
}
