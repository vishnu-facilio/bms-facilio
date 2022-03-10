package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;


public class UpdateTransactionEventTypeV3Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Constants.setEventType(context,EventType.TRANSACTION);
        return false;
    }
}
