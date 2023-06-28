package com.facilio.ims.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Duration;

@Log4j
public class PMPlannerHandler extends ImsHandler {

    @Override
    public void processMessage(Message message) {
        try {
            LOGGER.error("PM Planner handler entry");
            LOGGER.error(message.toString());
            long plannerId = getPlannerId(message);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD",message.getOrgId());
            //ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            PlannedMaintenanceAPI.ScheduleOperation operation = getOperation(message);
            switch (operation) {
                case REINIT:
                    moduleCRUD.schedulePM(plannerId,operation);
                    break;
                case EXTEND:
                    // moduleCRUD.extendPlanner(plannerId, getDuration(message));
                    break;
                case NIGHTLY:
                	moduleCRUD.schedulePM(plannerId,operation);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private Duration getDuration(Message message) {
        JSONObject content = message.getContent();
        return Duration.parse((String) content.get("duration"));
    }

    private PlannedMaintenanceAPI.ScheduleOperation getOperation(Message message) {
        JSONObject content = message.getContent();
        return PlannedMaintenanceAPI.ScheduleOperation.valueOf((String) content.get("operation"));
    }

    private long getPlannerId(Message message) {
        String key = message.getKey();
        String[] split = StringUtils.split(key, "/");
        long plannerId = Long.parseLong(split[1]);
        return plannerId;
    }
}
