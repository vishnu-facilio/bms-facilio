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
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.BooleanUtils;
@Log4j
public class AddFaultToWorkOrderSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ReadingRuleWorkOrderRelContext workflowRuleContext = (ReadingRuleWorkOrderRelContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        BaseAlarmContext baseAlarmContext = (BaseAlarmContext) context.get(FacilioConstants.ContextNames.BASE_ALARM);
        AlarmOccurrenceContext alarmOccurrence = baseAlarmContext.getLastOccurrence();
        Boolean createNewWo = false;

        if (baseAlarmContext != null) {
            Long ruleId = ((ReadingAlarm) baseAlarmContext).getRule().getId();
            LOGGER.info("workflowRuleContext "+workflowRuleContext.getRuleId()+"ruleId "+ruleId);
            if (ruleId == workflowRuleContext.getRuleId()) {
                if (alarmOccurrence != null) {
                    Long woId = alarmOccurrence.getWoId();
                    createNewWo = (woId == -1);
                }

                Long lastWoId = baseAlarmContext.getLastWoId();
                boolean isSkip = false;
                if (BooleanUtils.isTrue(workflowRuleContext.getIsSkip()) && lastWoId != -1) {
                    WorkOrderContext workOrder = WorkOrderAPI.getWorkOrder(lastWoId);
                    FacilioStatus moduleState = workOrder.getModuleState();
                    FacilioStatus status = TicketAPI.getStatus(moduleState.getId());
                    if (workOrder != null) {
                        if (status.getType() == FacilioStatus.StatusType.CLOSED) {
                            createNewWo = true;
                        } else {
                            workflowRuleContext.setWoCriteria(CriteriaAPI.getCriteria(workflowRuleContext.getWoCriteriaId()));
                            createNewWo = evaluateRecordForSkip(workflowRuleContext, workOrder.getId());
                            isSkip = true;
                        }
                    }
                }
                context.put(FacilioConstants.ContextNames.FOR_CREATE, createNewWo);
                context.put(FacilioConstants.ContextNames.SKIP_WO_CREATION, isSkip);
            }
        }
        return false;
    }

    private boolean evaluateRecordForSkip(ReadingRuleWorkOrderRelContext ruleToWo, Long woId) throws Exception {
        WorkOrderContext workOrder = WorkOrderAPI.getWorkOrderByCriteria(woId, ruleToWo.getWoCriteria());
        return (workOrder != null) ? true : false;
    }

}
