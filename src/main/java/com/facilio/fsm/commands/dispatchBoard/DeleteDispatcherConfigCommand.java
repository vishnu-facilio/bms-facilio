package com.facilio.fsm.commands.dispatchBoard;

import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class DeleteDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long dispatcherId = (long) context.get(FacilioConstants.Dispatcher.DISPATCHER_ID);
        DispatcherUtil.deleteDispatcher(dispatcherId);
        return false;
    }
}
