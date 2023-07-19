package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class rollUpServiceTaskCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> serviceTaskIds = (List<Long>) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK_IDS);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        if(CollectionUtils.isNotEmpty(serviceTaskIds) && CollectionUtils.isNotEmpty(serviceAppointments)) {

            ServiceAppointmentContext serviceAppointment = serviceAppointments.get(0);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField serviceAppointmentId = modBean.getField("serviceAppointment", "serviceTask");
            FacilioModule serviceTaskModule = Constants.getModBean().getModule("serviceTask");
            for (Long serviceTaskId : serviceTaskIds) {
                ServiceTaskContext serviceTask = new ServiceTaskContext();
                serviceTask.setId(serviceTaskId);
                serviceTask.setServiceAppointment(serviceAppointment);
                V3RecordAPI.updateRecord(serviceTask, serviceTaskModule, Collections.singletonList(serviceAppointmentId));
            }
        }

        return false;
        }

}
