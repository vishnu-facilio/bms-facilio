package com.facilio.readingrule.faulttowo.command;

import com.facilio.activity.AlarmActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faulttowo.FaultToWoActionsUtil;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.BooleanUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class WorkOrderCreationFromFaultCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean createWo = (Boolean) context.get(FacilioConstants.ContextNames.FOR_CREATE);
        ReadingRuleWorkOrderRelContext workflowRule = (ReadingRuleWorkOrderRelContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        JSONObject template = (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        BaseAlarmContext baseAlarm = (BaseAlarmContext) context.get(FacilioConstants.ContextNames.BASE_ALARM);
        Boolean isSkip = (Boolean) context.get(FacilioConstants.ContextNames.SKIP_WO_CREATION);

        if (createWo != null && createWo) {
            AlarmOccurrenceContext lastOccurrence = baseAlarm.getLastOccurrence();
            NoteContext note = constructNote(baseAlarm, workflowRule, isSkip);
            if (BooleanUtils.isTrue(workflowRule.getIsSkip()) && isSkip) {
                RuleWoAPI.addWorkOrderNotesFromAlarms(note, context);
            } else {
                FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
                Context woContext = c.getContext();
                WorkOrderContext workOrder = getContextFromFieldMatcher(template);
                performOtherWoAction(workOrder, workflowRule);
                CommonCommandUtil.addAlarmActivityToContext(baseAlarm.getId(), -1, AlarmActivityType.CREATE_WORKORDER, template, (FacilioContext) context, lastOccurrence.getId());
                woContext.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
                woContext.put(FacilioConstants.ContextNames.TASK_MAP, workOrder.getTaskList());
                woContext.put(FacilioConstants.ContextNames.NOTE, note);
                woContext.put(FacilioConstants.ContextNames.RECORD_ID, lastOccurrence.getId());
                c.execute();
            }
        }
        return false;
    }

    private static WorkOrderContext getContextFromFieldMatcher(JSONObject obj) throws Exception {
        if (obj != null) {
            Map<String, Object> contentMap = new HashMap<>();
            Long formId = (Long) obj.get(FacilioConstants.ContextNames.FORM_ID);
            List<Map<String, Object>> fieldMatcher = (List<Map<String, Object>>) obj.get(FacilioConstants.ContextNames.FIELD_MATCHER);
            for (Map field : fieldMatcher) {
                if (field.get(FacilioConstants.ContextNames.FIELD).toString().equals("siteId")) {
                    Map<String, Object> valueMap = (Map<String, Object>) field.get("value");
                    contentMap.put(field.get(FacilioConstants.ContextNames.FIELD).toString(), valueMap.get("id"));
                } else {
                    contentMap.put(field.get(FacilioConstants.ContextNames.FIELD).toString(), field.get("value"));
                }
            }
            WorkOrderContext woCtx = FieldUtil.getAsBeanFromMap(contentMap, WorkOrderContext.class);
            woCtx.setFormId(formId);
            return woCtx;
        }
        return null;
    }

    private NoteContext constructNote(BaseAlarmContext baseAlarmContext, ReadingRuleWorkOrderRelContext woRelContext, Boolean isSkip) throws Exception {
        JSONObject comments = woRelContext.getComments();
        NoteContext note = new NoteContext();
        if (BooleanUtils.isTrue(woRelContext.getIsSkip()) && isSkip) {
            String skipComment = (String) comments.get("skip");
            if (skipComment != null) {
                skipComment = RuleWoAPI.getPlaceholderForRuleAndOccurrence(skipComment, baseAlarmContext);
                note.setBody(skipComment);
                note.setParentId(baseAlarmContext.getLastWoId());
            }
        } else {
            String createComment = (String) comments.get("create");
            if (createComment != null) {
                createComment = RuleWoAPI.getPlaceholderForRuleAndOccurrence(createComment, baseAlarmContext);
                note.setBody(createComment);
            }
        }
        return (note.getBody() != null) ? note : null;
    }

    private void performOtherWoAction(WorkOrderContext workOrder, ReadingRuleWorkOrderRelContext workflow) throws Exception {
        NewReadingRuleContext readingRuleContext = new NewReadingRuleContext();
        readingRuleContext.setId(workflow.getRuleId());
        NewReadingRuleAPI.fetchAndUpdateAlarmDetails(readingRuleContext);

        if (BooleanUtils.isTrue(workflow.getIsRecommendationAsTask())) {
            FaultToWoActionsUtil.addWoTaskToContext(workOrder, readingRuleContext.getAlarmDetails());
        }
        if (BooleanUtils.isTrue(workflow.getIsPossibleCauseAsDesc())) {
            FaultToWoActionsUtil.addWODescFromRulePossibleCauses(workOrder, readingRuleContext.getAlarmDetails());
        }
    }

}
