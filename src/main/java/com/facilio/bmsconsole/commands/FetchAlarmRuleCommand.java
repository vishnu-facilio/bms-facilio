package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchAlarmRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id == null || id == -1) {
            throw new IllegalArgumentException("Invalid ID to fetch workflow");
        }

        boolean isNewReadingRule = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_NEW_READING_RULE, Boolean.FALSE);

        if (!isNewReadingRule) {
            List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(id);
            AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(id), readingAlarmRules);
            alarmRule.addAlarmRCARules(ReadingRuleAPI.getAlarmRCARules(id));
            alarmRule.getAlarmTriggerRule().setMatchedResources(null);
            alarmRule.getPreRequsite().setMatchedResources(null);
//		ReadingRuleAPI.setMatchedResources(alarmRule.getPreRequsite());
            context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
        } else {
            AlarmRuleContext alarmRule = new AlarmRuleContext(NewReadingRuleAPI.getRule(id));
            alarmRule.addAlarmRCARules(NewReadingRuleAPI.getRCARulesForReadingRule(id));
            context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
        }

        return false;
    }

}
