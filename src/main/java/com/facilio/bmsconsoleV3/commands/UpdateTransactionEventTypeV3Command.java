package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;


public class UpdateTransactionEventTypeV3Command extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        CommonCommandUtil.addEventType(EventType.TRANSACTION, (FacilioContext) context);
        return false;
    }
}
