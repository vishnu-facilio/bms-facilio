package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

import java.util.Map;

public class FetchImpactFieldsForRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId = (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
        Map<String, Long> impactFieldIds = NewReadingRuleAPI.getImpactFieldIdsForRule(ruleId);
        context.put(FacilioConstants.ContextNames.RECORD, impactFieldIds);
        return false;
    }
}
