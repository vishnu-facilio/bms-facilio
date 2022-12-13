package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.util.ReadingRuleRcaAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import org.apache.commons.chain.Context;

public class DeleteReadingRuleRcaCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        NewReadingRuleContext newReadingRule = NewReadingRuleAPI.destructureRuleFromRecordMap(context).get(0);
        Long ruleId = newReadingRule.getId();
        ReadingRuleRcaAPI.deleteReadingRuleRca(ruleId);
        return false;
    }
}
