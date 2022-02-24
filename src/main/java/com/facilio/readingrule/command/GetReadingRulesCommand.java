package com.facilio.readingrule.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class GetReadingRulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.NEW_READING_RULE, NewReadingRuleAPI.getRules());
        return false;
    }
}
