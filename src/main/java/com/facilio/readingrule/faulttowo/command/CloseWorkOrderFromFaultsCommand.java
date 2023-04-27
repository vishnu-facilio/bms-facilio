package com.facilio.readingrule.faulttowo.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class CloseWorkOrderFromFaultsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReadingRuleWorkOrderRelContext workflowRuleContext = (ReadingRuleWorkOrderRelContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        BaseAlarmContext baseAlarmContext = (BaseAlarmContext) context.get(FacilioConstants.ContextNames.BASE_ALARM);
        if (baseAlarmContext != null && workflowRuleContext != null) {
            Long ruleId = ((ReadingAlarm) baseAlarmContext).getRule().getId();
            if (ruleId == workflowRuleContext.getRuleId()) {
                JSONObject template = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
                workflowRuleContext.setCriteria(CriteriaAPI.getCriteria(workflowRuleContext.getWoCriteriaId()));
                WorkOrderContext workOrderContext = WorkOrderAPI.getWorkOrderByCriteria(baseAlarmContext.getLastWoId(), workflowRuleContext.getCriteria());
                if (workOrderContext != null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET);
                    context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrderContext);
                    Object obj = template.get("new_state");
                    Long statusVal = Long.parseLong((String) obj);
                    FacilioStatus status = TicketAPI.getStatus(statusVal);
                    if (workOrderContext.getStatus() != status) {
                        StateFlowRulesAPI.updateState(workOrderContext, module, status, false, context);
                        NoteContext note = addWoStateChangeNotes(workflowRuleContext, baseAlarmContext, workOrderContext.getId());
                        RuleWoAPI.addWorkOrderNotesFromAlarms(note, context);
                    }
                }
            }
        }
        return false;
    }

    private NoteContext addWoStateChangeNotes(ReadingRuleWorkOrderRelContext readingRuleWo, BaseAlarmContext baseAlarm, Long woId) throws Exception {
        JSONObject comments = readingRuleWo.getComments();
        NoteContext note = new NoteContext();
        String closeComment = (String) comments.get("close");
        if (closeComment != null) {
            closeComment = RuleWoAPI.getPlaceholderForRuleAndOccurrence(closeComment, baseAlarm);
            note.setBody(closeComment);
            note.setParentId(woId);
        }
        return (note.getBody() != null) ? note : null;
    }
}
