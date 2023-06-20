package com.facilio.fsm.commands;

import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;




public class AddDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DispatcherSettingsContext dispatcherConfig = (DispatcherSettingsContext)context.get(FacilioConstants.Dispatcher.DISPATCHER_CONFIG);
        DispatcherUtil.addDispatcher(dispatcherConfig);

        return false;
    }
}
