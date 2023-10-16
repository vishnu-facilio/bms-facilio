package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class UpdateRuleRCACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext readingRule = NewReadingRuleAPI.destructureRuleFromRecordMap(context).get(0);
        NewReadingRuleContext oldReadingRule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);
        if (readingRule.getRca() != null && oldReadingRule.getRca() == null) {
            ReadingRuleRcaAPI.createRCA(readingRule);
        }

        ReadingRuleRCAContext rcaContext = readingRule.getRca();
        if (rcaContext == null) {
            return true;
        }
        ReadingRuleRcaAPI.updateReadingRuleRca(rcaContext);

        return false;
    }
}
