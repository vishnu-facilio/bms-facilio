package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.readingrule.command.FetchReadingRuleSummaryCommand;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchAlarmRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id == null || id == -1) {
            throw new IllegalArgumentException("Invalid ID to fetch workflow");
        }

        boolean isNewReadingRule = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_READING_RULE);

        if (!isNewReadingRule) {
            List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(id);
            AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(id), readingAlarmRules);
            alarmRule.addAlarmRCARules(ReadingRuleAPI.getAlarmRCARules(id));
            alarmRule.getAlarmTriggerRule().setMatchedResources(null);
            alarmRule.getPreRequsite().setMatchedResources(null);
//		ReadingRuleAPI.setMatchedResources(alarmRule.getPreRequsite());
            context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
        } else {
            NewReadingRuleContext rule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(id)).get(0);
            rule.setId(id);
            AlarmRuleContext alarmRule = new AlarmRuleContext(rule);
            alarmRule.addAlarmRCARules(NewReadingRuleAPI.getRCARulesForReadingRule(id));
            context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
        }

        return false;
    }

}
