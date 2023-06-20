package com.facilio.fsm.commands;

import com.facilio.accounts.util.AccountUtil;
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
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DispatcherSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DispatcherSettingsContext dispatcherConfig = (DispatcherSettingsContext)context.get(FacilioConstants.Dispatcher.DISPATCHER_CONFIG);

        SharingContext<SingleSharingContext> dispatcherSharing = dispatcherConfig.getDispatcherSharing();
        SharingAPI.deleteSharingForParent(Collections.singletonList(dispatcherConfig.getId()), ModuleFactory.getDispatcherBoardSharingModule());

        if(dispatcherSharing == null) {
            dispatcherSharing = new SharingContext<>();
        }

        List<Long> peopleIds = dispatcherSharing.stream().filter(value -> value.getTypeEnum() == SingleSharingContext.SharingType.PEOPLE)
                .map(val -> val.getPeopleId()).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(peopleIds) && !peopleIds.contains(AccountUtil.getCurrentUser().getPeopleId())){
            SingleSharingContext newDispatcherSharing = new SingleSharingContext();
            newDispatcherSharing.setPeopleId(AccountUtil.getCurrentUser().getPeopleId());
            newDispatcherSharing.setType(SingleSharingContext.SharingType.PEOPLE);
            dispatcherSharing.add(newDispatcherSharing);
        }
        FacilioModule module = ModuleFactory.getDispatcherBoardSharingModule();
        List<FacilioField> dispatcherSharingFields = FieldFactory.getDispatcherBoardSharingFields(module);
        SharingAPI.addSharing(dispatcherSharing,dispatcherSharingFields,dispatcherConfig.getId(),module);


        return false;
    }
}
