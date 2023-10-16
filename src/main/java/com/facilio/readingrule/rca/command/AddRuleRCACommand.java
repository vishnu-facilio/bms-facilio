package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import org.apache.commons.chain.Context;

public class AddRuleRCACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext rule = (NewReadingRuleContext) context.get(FacilioConstants.ReadingRules.NEW_READING_RULE);
        ReadingRuleRcaAPI.createRCA(rule);
        return false;
    }
}
