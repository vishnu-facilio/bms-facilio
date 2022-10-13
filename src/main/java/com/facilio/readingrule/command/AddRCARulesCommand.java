package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRCARulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        List<Long> alarmRCARules = rule.getAlarmRCARules();
        Long ruleId = rule.getId();

        if (CollectionUtils.isEmpty(alarmRCARules)) {
            List<Long> rcaRuleIds = NewReadingRuleAPI.getRCARulesForReadingRule(ruleId);
            if (!rcaRuleIds.isEmpty()) {
                deleteIfAlreadyExists(rule);
            }
        } else {
            for (Long rcaID : alarmRCARules) {
                if (ruleId == rcaID) {
                    throw new IllegalArgumentException("Same Rule cannot be added as RCA");
                }
            }

            deleteIfAlreadyExists(rule);

            GenericInsertRecordBuilder mappingInsert = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getReadingRuleRCAMapping().getTableName())
                    .fields(FieldFactory.getReadingRuleRCAMapping());
            for (Long alarmRCARule : alarmRCARules) {
                Map<String, Object> value = new HashMap<>();
                value.put("rule", ruleId);
                value.put("rcaRule", alarmRCARule);
                mappingInsert.addRecord(value);
            }
            mappingInsert.save();
        }

        return false;
    }

    private void deleteIfAlreadyExists(NewReadingRuleContext rule) throws Exception {
        GenericDeleteRecordBuilder delBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getReadingRuleRCAMapping().getTableName())
                .andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", String.valueOf(rule.getId()), NumberOperators.EQUALS));
        delBuilder.delete();
    }

}
