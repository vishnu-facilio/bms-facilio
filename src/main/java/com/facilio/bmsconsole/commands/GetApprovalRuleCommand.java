package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ApprovalRuleMetaContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetApprovalRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if (id > 0) {
            ApprovalStateFlowRuleContext stateFlowContext = (ApprovalStateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(id);

            ApprovalRuleMetaContext approvalMeta = new ApprovalRuleMetaContext();
            approvalMeta.setName(stateFlowContext.getName());
            approvalMeta.setDescription(stateFlowContext.getDescription());
            approvalMeta.setCriteria(stateFlowContext.getCriteria());
            approvalMeta.setEventType(stateFlowContext.getActivityTypeEnum());
            approvalMeta.setConfigJson(stateFlowContext.getConfigJson());
            if (stateFlowContext.getActions() == null) {
                List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateFlowContext.getId());
                stateFlowContext.setActions(actions);
            }
            approvalMeta.setApprovalEntryActions(stateFlowContext.getActions());

            if (CollectionUtils.isNotEmpty(stateFlowContext.getFields())) {
                List<Long> fieldIds = stateFlowContext.getFields().stream().map(FieldChangeFieldContext::getFieldId).collect(Collectors.toList());
                approvalMeta.setFieldIds(fieldIds);
            }
            approvalMeta.setId(stateFlowContext.getId());
            String configJson = stateFlowContext.getConfigJson();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(stateFlowContext.getModuleId());

            if (StringUtils.isNotEmpty(configJson)) {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject parse = (JSONObject) parser.parse(configJson);
                    JSONArray relatedModules = (JSONArray) parse.get("relatedModules");
                    List<Long> extendedModuleIds = module.getExtendedModuleIds();
                    Map<Long,Object> relatedMap = new HashMap<>();
                    for (Object relatedModuleId: relatedModules) {
                        if (relatedModuleId instanceof Long) {
                            FacilioModule subModule = modBean.getModule((Long) relatedModuleId);
                            List<FacilioField> allFields = modBean.getAllFields(subModule.getName());
                            List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && (extendedModuleIds.contains(((LookupField) field).getLookupModuleId())))).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(fields)) {
                                for (FacilioField field : fields) {
                                    JSONObject relatedJson = new JSONObject();
                                    relatedJson.put("module", subModule);
                                    relatedJson.put("field", field);
                                    relatedMap.put(subModule.getModuleId(),relatedJson);
                                }
                            }
                        }
                    }
                    approvalMeta.setRelatedList(relatedMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext.getId());
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                for (WorkflowRuleContext rule : allStateTransitionList) {
                    ApprovalStateTransitionRuleContext stateTransitionRule = (ApprovalStateTransitionRuleContext) rule;
                    if (rule.getName().equals("Approve")) {
                        approvalMeta.setApprovers(stateTransitionRule.getApprovers());
                        approvalMeta.setApprovalOrder(stateTransitionRule.getApprovalOrder());
                        approvalMeta.setAllApprovalRequired(stateTransitionRule.getAllApprovalRequired());
                        approvalMeta.setApprovalForm(stateTransitionRule.getForm());
                        approvalMeta.setApprovalFormId(stateTransitionRule.getFormId());
                        if (stateTransitionRule.getActions() == null) {
                            List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateTransitionRule.getId());
                            stateTransitionRule.setActions(actions);
                        }
                        approvalMeta.setApproveActions(stateTransitionRule.getActions());
                    }
                    else if (rule.getName().equals("Reject")) {
                        approvalMeta.setRejectForm(stateTransitionRule.getForm());
                        approvalMeta.setRejectFormId(stateTransitionRule.getFormId());
                        if (stateTransitionRule.getActions() == null) {
                            List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateTransitionRule.getId());
                            stateTransitionRule.setActions(actions);
                        }
                        approvalMeta.setRejectActions(stateTransitionRule.getActions());
                    }
                    else if (rule.getName().equals("Re-Send")) {
                        approvalMeta.setResendApprovers(stateTransitionRule.getApprovers());
                        approvalMeta.setResendFormId(stateTransitionRule.getFormId());
                        if (stateTransitionRule.getActions() == null) {
                            List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(stateTransitionRule.getId());
                            stateTransitionRule.setActions(actions);
                        }
                        approvalMeta.setResendActions(stateTransitionRule.getActions());
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.APPROVAL_RULE, approvalMeta);
        }
        return false;
    }
}
