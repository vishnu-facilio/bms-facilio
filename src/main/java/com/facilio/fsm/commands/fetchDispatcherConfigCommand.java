package com.facilio.fsm.commands;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class fetchDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long)context.get(FacilioConstants.Dispatcher.DISPATCHER_ID);
        DispatcherSettingsContext dispatcherConfig = DispatcherUtil.getDispatcher(id);
        context.put(FacilioConstants.Dispatcher.DISPATCHER_CONFIG, dispatcherConfig);

        return false;
    }
}
