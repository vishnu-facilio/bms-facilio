package com.facilio.readingrule.faulttowo.command;

import com.facilio.activity.AlarmActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderCreationFromFaultCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean createWo=(Boolean) context.get(FacilioConstants.ContextNames.FOR_CREATE);
        JSONObject template= (JSONObject) context.get(FacilioConstants.ContextNames.TEMPLATE_JSON);
        ReadingRuleWorkOrderRelContext ruleWoRel= (ReadingRuleWorkOrderRelContext) context.get(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER);
        BaseAlarmContext baseAlarm=(BaseAlarmContext) context.get(FacilioConstants.ContextNames.BASE_ALARM);
        Boolean isSkip=(Boolean) context.get(FacilioConstants.ContextNames.SKIP_WO_CREATION);
        if(createWo!=null && createWo) {
            AlarmOccurrenceContext lastOccurrence=baseAlarm.getLastOccurrence();
            NoteContext note = constructNote(baseAlarm,ruleWoRel,isSkip);
            if(ruleWoRel.getIsSkip() && isSkip){
                RuleWoAPI.addWorkOrderNotesFromAlarms(note,context);
            }else {
                FacilioChain c = TransactionChainFactory.getV2AlarmOccurrenceCreateWO();
                Context woContext = c.getContext();
                WorkOrderContext workOrder = getContextFromFieldMatcher(template);
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
        if(obj!=null) {
            Map<String, Object> contentMap = new HashMap<>();
            Long formId= (Long) obj.get(FacilioConstants.ContextNames.FORM_ID);
            List<Map<String, Object>> fieldMatcher = (List<Map<String, Object>>) obj.get(FacilioConstants.ContextNames.FIELD_MATCHER);
            for (Map field : fieldMatcher) {
                if(field.get(FacilioConstants.ContextNames.FIELD).toString().equals("siteId")){
                    Map<String,Object> valueMap= (Map<String, Object>) field.get("value");
                    contentMap.put(field.get(FacilioConstants.ContextNames.FIELD).toString(), valueMap.get("id"));
                }
                else {
                    contentMap.put(field.get(FacilioConstants.ContextNames.FIELD).toString(), field.get("value"));
                }
            }
            WorkOrderContext woCtx = FieldUtil.getAsBeanFromMap(contentMap, WorkOrderContext.class);
            woCtx.setFormId(formId);
            return woCtx;
        }
        return null;
    }
    private  NoteContext constructNote(BaseAlarmContext baseAlarmContext, ReadingRuleWorkOrderRelContext woRelContext,Boolean isSkip) throws Exception {
        JSONObject comments=woRelContext.getComments();
        NoteContext note =new NoteContext();
        if(woRelContext.getIsSkip() && isSkip){
            String skipComment= (String) comments.get("skip");
            if(skipComment!=null){
                skipComment=getPlaceholderForRuleAndOccurrence(skipComment,baseAlarmContext);
                note.setBody(skipComment);
                note.setParentId(baseAlarmContext.getLastWoId());
            }
        }
        else{
            String createComment= (String) comments.get("create");
            if(createComment!=null){
                createComment=getPlaceholderForRuleAndOccurrence(createComment,baseAlarmContext);
                note.setBody(createComment);
            }
        }

        return (note.getBody()!=null)?note:null;
    }
    private String getPlaceholderForRuleAndOccurrence(String placeholderString,BaseAlarmContext baseAlarmContext) throws Exception{
        Long ruleId=((ReadingAlarm)baseAlarmContext).getRule().getId();
        NewReadingRuleContext readingRule= NewReadingRuleAPI.getReadingRules(Collections.singletonList(ruleId)).get(0);
        placeholderString= WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ReadingRules.NEW_READING_RULE,readingRule,placeholderString);
        placeholderString= WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ContextNames.ALARM_OCCURRENCE,baseAlarmContext.getLastOccurrence(),placeholderString);
        return placeholderString;
    }
}