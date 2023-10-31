package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringSystemEnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.AddFlaggedEventClosureConfigModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;

public class AddFlaggedEventClosureConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                FlaggedEventRuleClosureConfigContext oldClosureConfig = RemoteMonitorUtils.getFlaggedEventRuleClosureConfig(flaggedEventRule.getId());
                if(oldClosureConfig != null ){
                    deleteCloseEmailRule(oldClosureConfig.getCloseEmailNotificationRuleId());
                }
                Criteria delCriteria = new Criteria();
                delCriteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE_ID","flaggedEventRuleId",String.valueOf(flaggedEventRule.getId()),NumberOperators.EQUALS));
                V3RecordAPI.deleteRecords(AddFlaggedEventClosureConfigModule.MODULE_NAME,delCriteria,false);
                FlaggedEventRuleClosureConfigContext closureConfig = flaggedEventRule.getFlaggedEventRuleClosureConfig();
                if(closureConfig != null) {
                    closureConfig.setCloseEmailNotificationRuleId(null);
                    if(closureConfig.getCloseEmailRule() != null) {
                        Criteria criteria = new Criteria();
                        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField(FlaggedEventModule.FLAGGED_EVENT_RULE_FIELD_NAME, FlaggedEventModule.MODULE_NAME),String.valueOf(flaggedEventRule.getId()), PickListOperators.IS));
                        criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("status",FlaggedEventModule.MODULE_NAME), StringUtils.join(Arrays.asList(FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED.getIndex(),FlaggedEventContext.FlaggedEventStatus.CLEARED.getIndex()),","), StringSystemEnumOperators.IS));
                        closureConfig.setCloseEmailNotificationRuleId(FlaggedEventUtil.addEmailRule(closureConfig.getCloseEmailRule(),"Email Notification Rule for Flagged Event Close - " + flaggedEventRule.getId(),criteria, EventType.CREATE_OR_EDIT));
                    }
                    FlaggedEventRuleContext rule = new FlaggedEventRuleContext();
                    rule.setId(flaggedEventRule.getId());
                    closureConfig.setFlaggedEventRule(rule);
                    closureConfig.setCloseEmailRule(null);
                    FacilioContext closureContext = V3Util.createRecord(modBean.getModule(AddFlaggedEventClosureConfigModule.MODULE_NAME), FieldUtil.getAsProperties(closureConfig));
                    Map<String, List> dataMap = (Map<String, List>) closureContext.get(Constants.RECORD_MAP);
                    List list = dataMap.get(AddFlaggedEventClosureConfigModule.MODULE_NAME);
                    ModuleBaseWithCustomFields data = (ModuleBaseWithCustomFields) list.get(0);
                    Long flaggedEventClosureConfigId = data.getId();
                    deleteExistingClosureStatuses(flaggedEventClosureConfigId);
                    if(flaggedEventClosureConfigId != null && flaggedEventClosureConfigId > -1) {
                        List<FlaggedEventContext.FlaggedEventStatus> flaggedEventStatuses = closureConfig.getFlaggedEventStatuses();
                        addClosureStatuses(flaggedEventClosureConfigId, flaggedEventStatuses);
                    }
                }
            }
        }

        return false;
    }

    private static void deleteCloseEmailRule(Long ruleId) throws Exception {
        if(ruleId != null && ruleId > -1) {
            FacilioContext ruleDeleteContext = new FacilioContext();
            ruleDeleteContext.put(FacilioConstants.ContextNames.ID, Collections.singletonList(ruleId));
            FacilioChain deleteRule = TransactionChainFactory.getDeleteWorkflowRuleChain();
            deleteRule.execute(ruleDeleteContext);
        }
    }

    private static void deleteExistingClosureStatuses(Long closureConfigId) throws Exception {
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getFlaggedEventClosureStatusModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(closureConfigId), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }

    private static void addClosureStatuses(Long closureConfigId, List<FlaggedEventContext.FlaggedEventStatus> flaggedEventStatuses) throws SQLException {
        List<Map<String,Object>> propsList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(flaggedEventStatuses)) {
            for(FlaggedEventContext.FlaggedEventStatus flaggedEventStatus : flaggedEventStatuses) {
                Map<String,Object> prop = new HashMap<>();
                prop.put("parentId",closureConfigId);
                prop.put("value",flaggedEventStatus.getIndex());
                propsList.add(prop);
            }
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getFlaggedEventClosureStatusModule().getTableName())
                    .fields(FieldFactory.getFlaggedEventClosureStatusFields());
            insertRecordBuilder.addRecords(propsList);
            insertRecordBuilder.save();
        }
    }
}
