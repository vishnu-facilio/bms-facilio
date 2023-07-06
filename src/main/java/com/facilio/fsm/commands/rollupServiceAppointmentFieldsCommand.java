package com.facilio.fsm.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.location.LocationContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rollupServiceAppointmentFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceAppointmentContext> serviceAppointments = (List<ServiceAppointmentContext>) recordMap.get(context.get("moduleName"));
        if(CollectionUtils.isNotEmpty(serviceAppointments)) {
            for (ServiceAppointmentContext serviceAppointment : serviceAppointments) {
                if (serviceAppointment.getAppointmentType() == ServiceAppointmentContext.AppointmentType.SERVICE_WORK_ORDER.getIndex()){
                    ServiceOrderContext serviceOrder = serviceAppointment.getServiceOrder();
                    if(serviceOrder != null){
                        serviceAppointment.setPriority(serviceOrder.getPriority());
                        V3SiteContext site = serviceOrder.getSite();
                        if(site != null) {
                            serviceAppointment.setSite(site);
                            LocationContext location = site.getLocation();
                            if(location != null) {
                                serviceAppointment.setLocation(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(location),LocationContextV3.class));
                            }
                            serviceAppointment.setTerritory(site.getTerritory());
                        }
                    }
                }
            }
        }
        return false;
    }
}
