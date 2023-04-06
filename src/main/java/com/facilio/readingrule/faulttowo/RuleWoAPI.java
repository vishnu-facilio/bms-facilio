package com.facilio.readingrule.faulttowo;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

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
        if(ruleWo.getCriteria()!=null){
            NewReadingRuleAPI.setModuleNameForCriteria(ruleWo.getCriteria(), FacilioConstants.ContextNames.WORK_ORDER);
            Long criteriaId=CriteriaAPI.addCriteria(ruleWo.getCriteria());
            ruleWo.setCriteriaId(criteriaId);
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

    public static void updateWoDetails(ReadingRuleWorkOrderRelContext ruleWoCtx) throws Exception{

        Map<String,Object> ruleWoProp=FieldUtil.getAsProperties(ruleWoCtx);
        GenericUpdateRecordBuilder updateRecordBuilder=new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .fields(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("ID","id",String.valueOf(ruleWoCtx.getId()),NumberOperators.EQUALS));
        updateRecordBuilder.update(ruleWoProp);
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

    public static void checkAndUpdateRuleWoDetails(ReadingRuleWorkOrderRelContext record,ReadingRuleWorkOrderRelContext oldRecord) throws Exception {
        if(oldRecord.getCriteria()!=record.getCriteria()){
            if(oldRecord.getCriteriaId()!=null) {
                CriteriaAPI.deleteCriteria(oldRecord.getCriteriaId());
            }
            updateRuleWoDependencies(record,record.ruleId);
        }
        updateWoDetails(record);
    }
    public static ReadingRuleWorkOrderRelContext getRuleWoDetails(Long workflowId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("WORKFLOW_RULE_ID", "workFlowRuleId", String.valueOf(workflowId), NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            ReadingRuleWorkOrderRelContext ruleWoCtx = FieldUtil.getAsBeanFromMap(prop.get(0), ReadingRuleWorkOrderRelContext.class);
            return ruleWoCtx;
        }
        return null;
    }
    public static List<ReadingRuleWorkOrderRelContext> getRuleWoDetailsFromRuleId(Long ruleWoId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getReadingRuleWorkOrderModule().getTableName())
                .select(FieldFactory.getReadingRuleWorkOrderFields())
                .andCondition(CriteriaAPI.getCondition("ruleId","RULEID",String.valueOf(ruleWoId),NumberOperators.EQUALS));
        List<Map<String, Object>> prop= selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(prop)) {
            List<ReadingRuleWorkOrderRelContext> ruleWoCtx = FieldUtil.getAsBeanListFromMapList(prop, ReadingRuleWorkOrderRelContext.class);
            return ruleWoCtx;
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
}
