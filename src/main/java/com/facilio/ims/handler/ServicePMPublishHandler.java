package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fms.message.Message;
import com.facilio.fsm.util.ServicePlannedMaintenanceAPI;
import com.facilio.fw.BeanFactory;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@Log4j
public class ServicePMPublishHandler extends ImsHandler{
    @Override
    public void processMessage(Message message) {
        try{
            Long servicePMId = getServicePMId(message);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",message.getOrgId());
            ServicePlannedMaintenanceAPI.ScheduleOperation operation = getOperation(message);
            moduleCRUD.scheduleServicePM(servicePMId,operation);
        }
        catch(Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }
    private ServicePlannedMaintenanceAPI.ScheduleOperation getOperation(Message message) {
        JSONObject content = message.getContent();
        return ServicePlannedMaintenanceAPI.ScheduleOperation.valueOf((String) content.get("operation"));
    }
    private long getServicePMId(Message message) {
        String key = message.getKey();
        String[] split = StringUtils.split(key, "/");
        long pmId = Long.parseLong(split[1]);
        return pmId;
    }
}
