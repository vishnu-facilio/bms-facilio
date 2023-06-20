package com.facilio.fsm.commands;

import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import java.util.List;

public class fetchDispatcherBoardSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long dispatcherId = (long) context.get(FacilioConstants.Dispatcher.DISPATCHER_ID);
        DispatcherSettingsContext dispatcherConfig = (DispatcherSettingsContext)context.get(FacilioConstants.Dispatcher.DISPATCHER_CONFIG);
        if(dispatcherId > 0 ) {
            FacilioModule dispatcherBoardSharingModule = ModuleFactory.getDispatcherBoardSharingModule();
            List<FacilioField> dispatcherBoardSharingFields = FieldFactory.getDispatcherBoardSharingFields(dispatcherBoardSharingModule);

            SharingContext<SingleSharingContext> dispatcherBoardSharing = SharingAPI.getSharing(dispatcherId, dispatcherBoardSharingModule, SingleSharingContext.class, dispatcherBoardSharingFields);
            dispatcherConfig.setDispatcherSharing(dispatcherBoardSharing);
        }
        return false;
    }
}
