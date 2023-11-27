package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkflowRuleActionLogContext;
import com.facilio.bmsconsole.context.WorkflowRuleLogContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ims.handler.WorkFlowRuleLogHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorkflowRuleLogUtil {

    public static final Logger LOGGER = LogManager.getLogger(WorkflowRuleLogUtil.class.getName());
    public static void insertBulkWorkflowRuleLog(List<WorkflowRuleLogContext> workflowRuleLogList) throws Exception{
        if (CollectionUtils.isEmpty(workflowRuleLogList))
            return;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        InsertRecordBuilder<WorkflowRuleLogContext> insertRecordBuilder = new InsertRecordBuilder<WorkflowRuleLogContext>()
                .module(module)
                .fields(fields)
                .addRecords(workflowRuleLogList);
        insertRecordBuilder.save();

        List<WorkflowRuleActionLogContext> actionLogList=new LinkedList<>();
        for(WorkflowRuleLogContext log:workflowRuleLogList){
            List<WorkflowRuleActionLogContext> actionLog=log.getActions();
            if(CollectionUtils.isNotEmpty(actionLog)) {
                actionLog.forEach(i -> i.setWorkflowRuleLogId(log.getId()));
                actionLogList.addAll(actionLog);
            }
        }
        insertBulkWorkflowRuleActionLog(actionLogList);
    }
    public static void insertWorkflowRuleLog(WorkflowRuleLogContext workflowRuleLog,List<WorkflowRuleActionLogContext> workflowRuleActionLogContext) throws Exception{
        if (workflowRuleLog == null)
            return;

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String,Object> props=FieldUtil.getAsProperties(workflowRuleLog);
        InsertRecordBuilder<WorkflowRuleLogContext> insertRecordBuilder = new InsertRecordBuilder<WorkflowRuleLogContext>()
                .module(module)
                .fields(fields)
                .addRecordProp(props);
        insertRecordBuilder.save();
        if(CollectionUtils.isNotEmpty(workflowRuleActionLogContext)) {
            insertWorkflowRuleActionLog((long) props.get("id"), workflowRuleActionLogContext);
        }
    }
    public static void insertBulkWorkflowRuleActionLog(List<WorkflowRuleActionLogContext> actionLogList) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_ACTION_LOGS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        InsertRecordBuilder<WorkflowRuleActionLogContext> builder = new InsertRecordBuilder<WorkflowRuleActionLogContext>()
                .module(module)
                .fields(fields)
                .addRecords(actionLogList);
        builder.save();
    }
    public static void insertWorkflowRuleActionLog(long workflowRuleLogId,List<WorkflowRuleActionLogContext> workflowRuleActionLogContext) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_ACTION_LOGS);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        workflowRuleActionLogContext.forEach(i->i.setWorkflowRuleLogId(workflowRuleLogId));
        List<Map<String, Object>> props = FieldUtil.getAsMapList(workflowRuleActionLogContext,WorkflowRuleActionLogContext.class);
        InsertRecordBuilder<WorkflowRuleActionLogContext> builder = new InsertRecordBuilder<WorkflowRuleActionLogContext>()
                .module(module)
                .fields(fields)
                .addRecordProps(props);
        builder.save();
    }

    public static void sendWorkflowRuleLogs(WorkflowRuleLogContext workflowRuleLog)  throws Exception {
        if (workflowRuleLog == null)
            return;
        try {
            long orgId = AccountUtil.getCurrentOrg() == null? workflowRuleLog.getOrgId() : AccountUtil.getCurrentOrg().getOrgId();

            if (orgId > 0L) {
                Messenger.getMessenger().sendMessage(new Message()
                        .setKey(WorkFlowRuleLogHandler.KEY+"/"+orgId+"/"+workflowRuleLog.getRecordModuleId()+"/"+workflowRuleLog.getWorkflowRuleId())
                        .setOrgId(orgId)
                        .setContent(workflowRuleLog.toJson()));
            }
        }catch (Exception e){
            LOGGER.error(e);
        }
    }
    public static List<WorkflowRuleActionLogContext> getActionsForRuleId(List<Long> workflowRuleId) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule actionLogsModule = modBean.getModule(FacilioConstants.ContextNames.WORKFLOW_RULE_ACTION_LOGS);
        List<FacilioField> actionLogsModuleFields= modBean.getAllFields(actionLogsModule.getName());
        SelectRecordsBuilder builder =new SelectRecordsBuilder()
                .module(actionLogsModule)
                .beanClass(WorkflowRuleActionLogContext.class)
                .select(actionLogsModuleFields)
                .andCondition(CriteriaAPI.getConditionFromList("WORKFLOW_RULE_LOG_ID","workflowRuleLogId",workflowRuleId,NumberOperators.EQUALS));
        return builder.get();

    }
    public  static String linkConfigForRuleType(WorkflowRuleContext workflowRuleContext)
    {
        if(workflowRuleContext==null)
            return null;
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            switch (workflowRuleContext.getRuleTypeEnum()) {
                case STATE_RULE:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("stateFlowId", ((StateflowTransitionContext) workflowRuleContext).getStateFlowId());
                    json.put("navigateTo", "STATEFLOWS");
                    json.put("enumType",workflowRuleContext.getRuleTypeEnum().toString());
                    json.put("setupType","SETUP_OVERVIEW");
                    array.add(json);
                    break;
                case APPROVAL_STATE_TRANSITION:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "APPROVALS");
                    json.put("approvalId", ((ApprovalStateTransitionRuleContext) workflowRuleContext).getStateFlowId());
                    json.put("enumType",workflowRuleContext.getRuleTypeEnum().toString());
                    json.put("queryParam","NewApprovalRule");
                    array.add(json);
                    break;
                case STATE_FLOW:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "STATEFLOWS");
                    json.put("queryParam","StateFlowList");
                    array.add(json);
                    break;
                case APPROVAL_STATE_FLOW:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "APPROVALS");
                    json.put("queryParam","ApprovalRulesList");
                    array.add(json);
                    break;
                case CUSTOM_BUTTON:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("moduleId", workflowRuleContext.getModuleId());
                    json.put("ruleType", workflowRuleContext.getRuleType());
                    json.put("setupType","SETUP_OVERVIEW");
                    json.put("enumType",workflowRuleContext.getRuleTypeEnum().toString());
                    json.put("navigateTo", "MODULES");
                    array.add(json);
                    break;
                case MODULE_RULE:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "WORKFLOWS");
                    array.add(json);
                    break;
                case MODULE_RULE_NOTIFICATION:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "NOTIFICATIONS");
                    array.add(json);
                    break;
                case SLA_WORKFLOW_RULE:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "SLA_POLICIES");
                    json.put("slaParentRuleId",workflowRuleContext.getParentRuleId());
                    json.put("enumType",workflowRuleContext.getRuleTypeEnum().toString());
                    json.put("queryParam","NewSLA");
                    array.add(json);
                    break;
                case SLA_POLICY_RULE:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "SLA_POLICIES");
                    json.put("queryParam","SLAList");
                    array.add(json);
                    break;
                case ASSIGNMENT_RULE:
                    json.put("id", workflowRuleContext.getId());
                    json.put("moduleName", workflowRuleContext.getModuleName());
                    json.put("navigateTo", "ASSIGNMENT_RULES");
                    array.add(json);
                default:
                    break;
            }

        } catch (Exception e) {
            LOGGER.info("error occured while getting the linkconfig");
        }
        return array.toJSONString();
    }
}