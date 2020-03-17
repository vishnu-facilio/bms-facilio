package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchRuleSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id == null || id == -1) {
            throw new IllegalArgumentException("Invalid ID to fetch workflow");
        }
        List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRules(id);
        AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getAlarmRulesList(id),readingAlarmRules);
        long count=ReadingRuleAPI.getMatchedResourcesCount(alarmRule.getPreRequsite());
		context.put(FacilioConstants.ContextNames.RULE_ASSET_COUNT,count);
        alarmRule.addAlarmRCARules(ReadingRuleAPI.getAlarmRCARules(id));
        context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);

        return false;
    }
}
