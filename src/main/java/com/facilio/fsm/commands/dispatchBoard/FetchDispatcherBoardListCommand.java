package com.facilio.fsm.commands.dispatchBoard;


import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchDispatcherBoardListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<DispatcherSettingsContext>dispatcherSettingsContextList = DispatcherUtil.getDispatcherList();
        context.put(FacilioConstants.Dispatcher.DISPATCHER_LIST,dispatcherSettingsContextList);
        return false;
    }


}
