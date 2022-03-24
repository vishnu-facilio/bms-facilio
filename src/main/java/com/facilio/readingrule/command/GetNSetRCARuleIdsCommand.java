package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetNSetRCARuleIdsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ContextNames.NEW_READING_RULE);
        List<Long> alarmRCARules = NewReadingRuleAPI.getRCARulesForReadingRule(rule.getId());
        rule.setRcaRules(alarmRCARules);
        return false;
    }
}
