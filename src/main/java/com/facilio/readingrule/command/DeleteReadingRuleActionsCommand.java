package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteReadingRuleActionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long ruleId = (long) context.get(FacilioConstants.ContextNames.ID);
        if (ruleId != -1) {

            FacilioModule alarmWorkflowRuleModule = ModuleFactory.getAlarmWorkflowRuleModule();
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getAlarmWorkflowRuleFields())
                    .table(alarmWorkflowRuleModule.getTableName())
                    .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(ruleId), NumberOperators.EQUALS));
            List<Map<String, Object>> result = selectBuilder.get();

            List<Long> actionRuleIds = new ArrayList<>();
            for(Map<String, Object> resRow : result) {
                Long wfRuleId = (Long) resRow.get("id");
                actionRuleIds.add(wfRuleId);
            }

            if(CollectionUtils.isNotEmpty(actionRuleIds)) {
                FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();

                GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                        .table(ModuleFactory.getWorkflowRuleModule().getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(actionRuleIds, workflowModule));
                deleteBuilder.delete();
            }
        }

        return false;
    }
}
