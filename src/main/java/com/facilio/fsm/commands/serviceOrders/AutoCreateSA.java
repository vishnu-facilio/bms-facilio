package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.fw.BeanFactory;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoCreateSA extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) recordMap.get(context.get("moduleName"));
        for(ServiceOrderContext order : serviceOrders) {
            if(order.getAutoCreateSa()){
                ServiceAppointmentContext sa = new ServiceAppointmentContext();
                sa.setAppointmentType(ServiceAppointmentContext.AppointmentType.SERVICE_WORK_ORDER);
                sa.setName("Service Appointment for "+ order.getName() + "(AC)");
                sa.setActualStartTime(order.getActualStartTime());
                sa.setActualEndTime(order.getActualEndTime());
                V3SiteContext site = order.getSite();
                if (site != null) {
                    sa.setSite(order.getSite());
                    LocationContext location = site.getLocation();
                    if (location != null) {
                        sa.setLocation(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(location), LocationContextV3.class));
                    }
                    sa.setTerritory(site.getTerritory());
                }
                List<ServiceTaskContext> serviceTaskList= order.getServiceTasks();
                //fetch the list of all tasks against the service order


                List<ServiceAppointmentTaskContext> serviceAppointmentTaskContextList = new ArrayList<>();

                if(CollectionUtils.isNotEmpty(serviceTaskList)) {
                    for (ServiceTaskContext serviceTask : serviceTaskList) {
                        ServiceAppointmentTaskContext task = new ServiceAppointmentTaskContext();
                        task.setId(serviceTask.getId());
                        serviceAppointmentTaskContextList.add(task);
                    }
                }
                sa.setServiceTasks(serviceAppointmentTaskContextList);


//
//                SelectRecordsBuilder<ServiceTaskContext> serviceTasksBuilder = new SelectRecordsBuilder<ServiceTaskContext>();
//                serviceTasksBuilder.select(serviceTaskFields)
//                        .module(serviceTaskModule)
//                        .beanClass(ServiceTaskContext.class)
//                        .andCondition(CriteriaAPI.getCondition(serviceTaskMap.get("serviceOrder"),String.valueOf(order.getId()), StringOperators.IS ));
//                //Need to add it against the service appointment
//                List<ServiceTaskContext> serviceTaskList = serviceTasksBuilder.get();
//                if(CollectionUtils.isNotEmpty(serviceTaskList)) {
//                    for (ServiceTaskContext st : serviceTaskList) {
//
//                    }
//                }
//                sa.setServiceTasks(order.getServiceTasks());
            }
        }

        return false;
    }
}
