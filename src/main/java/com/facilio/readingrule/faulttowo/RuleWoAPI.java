package com.facilio.readingrule.faulttowo;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RuleWoAPI {
    public static ReadingRuleWorkOrderRelContext getRuleWoDetails(Long workflowId,Long ruleId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("WORKFLOW_RULE_ID", "workFlowRuleId", String.valueOf(workflowId), NumberOperators.EQUALS))
        .andCondition(CriteriaAPI.getCondition("RULEID","ruleId",String.valueOf(ruleId),NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            ReadingRuleWorkOrderRelContext ruleWoCtx = FieldUtil.getAsBeanFromMap(prop.get(0), ReadingRuleWorkOrderRelContext.class);
            return ruleWoCtx;
        }
        return null;
    }
    public static void updateRuleWoDependencies(ReadingRuleWorkOrderRelContext ruleWo, Long ruleId) throws Exception {
        if(ruleWo.getWoCriteria()!=null && MapUtils.isNotEmpty(ruleWo.getWoCriteria().getConditions())){
            NewReadingRuleAPI.setModuleNameForCriteria(ruleWo.getWoCriteria(), FacilioConstants.ContextNames.WORK_ORDER);
            Long criteriaId=CriteriaAPI.addCriteria(ruleWo.getWoCriteria());
            ruleWo.setWoCriteriaId(criteriaId);
        }
        ruleWo.setRuleId(ruleId);
    }
    public static void addRuleWoDetails(ReadingRuleWorkOrderRelContext ruleWo,Long ruleId) throws Exception {
        updateRuleWoDependencies(ruleWo,ruleId);
        Map<String,Object> prop= FieldUtil.getAsProperties(ruleWo);
        GenericInsertRecordBuilder selectRecordBuilder =new GenericInsertRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .fields(FieldFactory.getReadingRuleWorkOrderFields());
        selectRecordBuilder.insert(prop);

    }

    public static ReadingRuleWorkOrderRelContext updateWoDetails(ReadingRuleWorkOrderRelContext rule,ReadingRuleWorkOrderRelContext oldRule) throws Exception{
        WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule,oldRule);
        WorkflowRuleAPI.updateExtendedRule(rule,ModuleFactory.getReadingRuleWorkOrderModule(),FieldFactory.getReadingRuleWorkOrderFields());
        return rule;
    }

    public static List<ReadingRuleWorkOrderRelContext> getRuleWoDetailsFromId(Long ruleWoId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("ID","ID",String.valueOf(ruleWoId),NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            List<ReadingRuleWorkOrderRelContext> ruleWoCtx = FieldUtil.getAsBeanListFromMapList(prop, ReadingRuleWorkOrderRelContext.class);
            return ruleWoCtx;
        }
        return null;
    }

    public static Map<String,Object> getRuleWoDetails(Long workflowId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(workflowId), NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            return prop.get(0);
        }
        return new HashMap<>();
    }
    public static List<Long> getRuleWoDetailsFromRuleId(Long ruleWoId) throws Exception {
        List<FacilioField> fields = FieldFactory.getReadingRuleWorkOrderFields();
        List<Long> workflowIds;
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("ruleId","RULEID",String.valueOf(ruleWoId),NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            List<ReadingRuleWorkOrderRelContext> ruleWoCtx = FieldUtil.getAsBeanListFromMapList(prop, ReadingRuleWorkOrderRelContext.class);
            workflowIds=ruleWoCtx.stream().map(m->m.getId()).collect(Collectors.toList());
            return workflowIds;
        }
        return null;
    }
    public static void addWorkOrderNotesFromAlarms(NoteContext note, Context ctx) throws Exception {
        FacilioChain addNote = TransactionChainFactory.getAddNotesChain();
        FacilioContext noteContext = addNote.getContext();
        WorkOrderContext workOrder=(WorkOrderContext) ctx.get(FacilioConstants.ContextNames.WORK_ORDER);
        noteContext.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TICKET_NOTES);
        noteContext.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, FacilioConstants.ContextNames.WORK_ORDER);
        noteContext.put(FacilioConstants.ContextNames.NOTE, note);

        noteContext.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);

        addNote.execute();
    }
    public static String getPlaceholderForRuleAndOccurrence(String placeholderString, BaseAlarmContext baseAlarmContext) throws Exception{
        Long ruleId=((ReadingAlarm)baseAlarmContext).getRule().getId();
        NewReadingRuleContext readingRule= NewReadingRuleAPI.getReadingRules(Collections.singletonList(ruleId)).get(0);
        placeholderString= WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ReadingRules.NEW_READING_RULE,readingRule,placeholderString);
        placeholderString= WorkflowRuleAPI.replacePlaceholders(FacilioConstants.ContextNames.NEW_READING_ALARM,baseAlarmContext,placeholderString);
        return placeholderString;
    }

}
