package com.facilio.bmsconsoleV3.signup.fsmApp;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.DispatcherUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddDefaultDispatchConsoleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        String serviceAppointmentModuleName = FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
        List<FacilioField> saFields = moduleBean.getAllFields(serviceAppointmentModuleName);
        Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);
        FacilioField scheduledStartTimeField = saFieldMap.get("scheduledStartTime");
        FacilioField scheduledEndTime = saFieldMap.get("scheduledEndTime");
        DispatcherSettingsContext dispatcherConfig = new DispatcherSettingsContext();
        dispatcherConfig.setName(FacilioConstants.Dispatcher.MY_DISPATCHER_CONSOLE);
        dispatcherConfig.setStartTimeFieldId(scheduledStartTimeField.getFieldId());
        dispatcherConfig.setEndTimeFieldId(scheduledEndTime.getFieldId());

        DispatcherUtil.addDispatcher(dispatcherConfig);

        return false;
    }
}
