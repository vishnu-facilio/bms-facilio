package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;

import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AutoCreateSA extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = (List<ServiceOrderContext>) recordMap.get(context.get("moduleName"));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceAppointmentModule = modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
        List<ModuleBaseWithCustomFields> recordList = new ArrayList<>();
        List<ServiceAppointmentContext> serviceAppointmentList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(serviceOrders)){
        for(ServiceOrderContext order : serviceOrders) {

            if(order.isAutoCreateSa()){
//                List<ServiceTaskContext> serviceTaskList = (List<ServiceTaskContext>) order.getRelations().get("serviceTask").get(0).getData();
                ServiceAppointmentContext sa = new ServiceAppointmentContext();
                sa.setAppointmentType(ServiceAppointmentContext.AppointmentType.SERVICE_WORK_ORDER);
                sa.setName(order.getName());
                sa.setDescription(order.getDescription());
                sa.setScheduledStartTime(order.getPreferredStartTime());
                sa.setScheduledEndTime(order.getPreferredEndTime());
                sa.setServiceOrder(order);
//                sa.setActualStartTime(order.getActualStartTime());
//                sa.setActualEndTime(order.getActualEndTime());
                V3SiteContext site = order.getSite();
                if (site != null) {
                    V3SiteContext siteObj = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SITE,site.getId(), V3SiteContext.class);
                    sa.setSite(siteObj);
                    LocationContext location = siteObj.getLocation();
                    if (location != null) {
                        sa.setLocation(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(location), LocationContextV3.class));
                    }
                    sa.setTerritory(siteObj.getTerritory());
                }
//                List<ServiceTaskContext> serviceTaskList= order.getServiceTask();
                //fetch the list of all tasks against the service order

                FacilioContext socontext = V3Util.getSummary(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER, Collections.singletonList(order.getId()));
                ServiceOrderContext so = (ServiceOrderContext) Constants.getRecordList(socontext).get(0);

                List<ServiceAppointmentTaskContext> serviceAppointmentTaskList=new ArrayList<>();

                if(CollectionUtils.isNotEmpty(so.getServiceTask())) {
                    for ( ServiceTaskContext serviceTask : so.getServiceTask()) {
                        ServiceAppointmentTaskContext satask = new ServiceAppointmentTaskContext();
                        satask.setId(serviceTask.getId());
                        serviceAppointmentTaskList.add(satask);
                    }
                }
                sa.setServiceTasks(serviceAppointmentTaskList);

                serviceAppointmentList.add(sa);


            }
        }
            recordList.addAll(serviceAppointmentList);
        //getting null pointer error here after status change
            V3Util.createRecord(serviceAppointmentModule,recordList);
        }

        return false;
    }
}
