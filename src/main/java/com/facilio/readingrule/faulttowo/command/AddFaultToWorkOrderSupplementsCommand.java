package com.facilio.readingrule.faulttowo.command;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioStatus;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import org.apache.commons.chain.Context;

public class AddFaultToWorkOrderSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        WorkflowRuleContext workflowRuleContext=(WorkflowRuleContext)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        BaseAlarmContext baseAlarmContext=(BaseAlarmContext)context.get(FacilioConstants.ContextNames.BASE_ALARM);
        AlarmOccurrenceContext alarmOccurrence=baseAlarmContext.getLastOccurrence();
        Boolean createNewWo = false;

        if(baseAlarmContext!=null) {
            if(alarmOccurrence!=null) {
                Long woId = alarmOccurrence.getWoId();
                createNewWo = (woId == -1);
            }
            ReadingRuleWorkOrderRelContext ruleWorkOrderContext = RuleWoAPI.getRuleWoDetails(workflowRuleContext.getId(), ((ReadingAlarm) baseAlarmContext).getRule().getId());
            if (ruleWorkOrderContext == null) {
                return false;
            }

            Long lastWoId = baseAlarmContext.getLastWoId();
            boolean isSkip=false;
            if (ruleWorkOrderContext.getIsSkip() && lastWoId!=-1) {
                    WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(lastWoId);
                    FacilioStatus moduleState = workOrder.getModuleState();
                    FacilioStatus status = TicketAPI.getStatus(moduleState.getId());
                    if (workOrder != null) {
                        if(status.getType() == FacilioStatus.StatusType.CLOSED){
                            createNewWo=true;
                        }
                        else {
                            ruleWorkOrderContext.setCriteria(CriteriaAPI.getCriteria(ruleWorkOrderContext.getCriteriaId()));
                            createNewWo = evaluateRecordForSkip(ruleWorkOrderContext, workOrder.getId());
                            isSkip=true;
                        }
                    }
            }
            context.put(FacilioConstants.ContextNames.FOR_CREATE, createNewWo);
            context.put(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER, ruleWorkOrderContext);
            context.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,isSkip);
        }
        return false;
    }

    private boolean evaluateRecordForSkip(ReadingRuleWorkOrderRelContext ruleToWo, Long woId) throws Exception {
        WorkOrderContext workOrder = WorkOrderAPI.getWorkOrderByCriteria(woId, ruleToWo.getCriteria());
        return (workOrder!=null)?true:false;
    }

}
