package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.FlaggedEventRuleAlarmTypeRel;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.telemetry.context.TelemetryCriteriaContext;
import com.facilio.telemetry.signup.AddTelemetryCriteriaModule;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.action.WorkflowAction;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetFlaggedEventRuleRelatedRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                Long flaggedEventRuleId = flaggedEventRule.getId();
                Long controllerCriteriaId = flaggedEventRule.getControllerCriteriaId();
                Long siteCriteriaId = flaggedEventRule.getSiteCriteriaId();
                if (controllerCriteriaId != null && controllerCriteriaId > 0) {
                    flaggedEventRule.setControllerCriteria(CriteriaAPI.getCriteria(controllerCriteriaId));
                }
                if (siteCriteriaId != null && siteCriteriaId > 0) {
                    flaggedEventRule.setSiteCriteria(CriteriaAPI.getCriteria(siteCriteriaId));
                }
                flaggedEventRule.setTicketModuleName(RemoteMonitorUtils.getTicketModuleName(flaggedEventRule));
                List<FlaggedEventRuleAlarmTypeRel> flaggedEventRuleAlarmTypeRels = RemoteMonitorUtils.getFlaggedEventRuleAlarmTypeRel(flaggedEventRuleId);
                flaggedEventRule.setFlaggedEventRuleAlarmTypeRel(flaggedEventRuleAlarmTypeRels);
                flaggedEventRule.setFlaggedEventRuleBureauEvaluationContexts(RemoteMonitorUtils.getFlaggedEventBureauEval(flaggedEventRule.getId()));
                flaggedEventRule.setFieldMapping(RemoteMonitorUtils.getFlaggedEventRuleWOFieldMapping(flaggedEventRule.getId(), RemoteMonitorUtils.getTicketModuleName(flaggedEventRule)));
                flaggedEventRule.setDelayedEmailRule(RemoteMonitorUtils.getEmailRule(flaggedEventRule.getDelayedEmailRuleOneId()));
                flaggedEventRule.setFiles(AttachmentsAPI.getAttachments(RemoteMonitorConstants.FLAGGED_EVENT_RULE_ATTACHMENT_MOD_NAME, flaggedEventRuleId, false, null));
                flaggedEventRule.setFlaggedEventRuleClosureConfig(RemoteMonitorUtils.getFlaggedEventRuleClosureConfig(flaggedEventRuleId));
                flaggedEventRule.setEmailRule(RemoteMonitorUtils.getEmailRule(flaggedEventRule.getEmailNotificationRuleId()));
                if (flaggedEventRule.getWorkflowId() != null && flaggedEventRule.getWorkflowId() > 0) {
                    flaggedEventRule.setWorkflowContext(WorkflowUtil.getWorkflowContext(flaggedEventRule.getWorkflowId()));
                }
                fetchControlActionTemplatesForFlaggedRule(flaggedEventRule);
                fetchTelemetryCriteriaTemplatesForFlaggedRuleOnCreate(flaggedEventRule);
            }
        }
        return false;
    }

    public static void fetchTelemetryCriteriaTemplatesForFlaggedRuleOnCreate(FlaggedEventRuleContext rule) throws Exception {
        if(CollectionUtils.isNotEmpty(rule.getOnCreateTelemetryCriteria())) {
            List<Long> ids = rule.getOnCreateTelemetryCriteria().stream().map(TelemetryCriteriaContext::getId).collect(Collectors.toList());
            List<TelemetryCriteriaContext> telemetryCriteriaContexts = V3RecordAPI.getRecordsList(AddTelemetryCriteriaModule.MODULE_NAME,ids,TelemetryCriteriaContext.class);
            rule.setOnCreateTelemetryCriteria(telemetryCriteriaContexts);
        }
    }
    public static void fetchControlActionTemplatesForFlaggedRule(FlaggedEventRuleContext rule) throws Exception {
        if(CollectionUtils.isNotEmpty(rule.getControlActionTemplate())) {
            List<Long> ids = rule.getControlActionTemplate().stream().map(V3ControlActionTemplateContext::getId).collect(Collectors.toList());
            List<V3ControlActionTemplateContext> caTemplates = V3RecordAPI.getRecordsList(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME,ids,V3ControlActionTemplateContext.class);
            rule.setControlActionTemplate(caTemplates);
        }
    }
}
