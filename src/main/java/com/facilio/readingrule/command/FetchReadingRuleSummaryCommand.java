package com.facilio.readingrule.command;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import lombok.NonNull;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchReadingRuleSummaryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        @NonNull Long id = (Long) context.get(FacilioConstants.ContextNames.ID);

//        List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRules(id);
        AlarmRuleContext alarmRule = new AlarmRuleContext(NewReadingRuleAPI.getRule(id));
//        AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getAlarmRulesList(id), null);
        Map<String, Object> resourcesWithCount = NewReadingRuleAPI.getMatchedResourcesWithCount((NewReadingRuleContext) alarmRule.getAlarmTriggerRule());
        context.put(FacilioConstants.ContextNames.RULE_ASSET_COUNT, resourcesWithCount.get("count"));
        context.put(FacilioConstants.ContextNames.ASSET_LIST, resourcesWithCount.get("resourceIds"));
        alarmRule.addAlarmRCARules(NewReadingRuleAPI.getRCARulesForReadingRule(id));
        context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
        context.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.NEW_READING_RULE_MODULE);

        return false;
    }
}
