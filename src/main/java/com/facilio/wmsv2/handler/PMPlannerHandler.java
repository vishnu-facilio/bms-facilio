package com.facilio.wmsv2.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Duration;

@TopicHandler(
        topic = Topics.System.pmPlanner,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        sendToAllWorkers = false
)

@Log4j
public class PMPlannerHandler extends BaseHandler {
    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        // TODO(1): After publishing
        try {
            LOGGER.error(message.toString());
            long plannerId = getPlannerId(message);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            PlannedMaintenanceAPI.ScheduleOperation operation = getOperation(message);
            switch (operation) {
                case REINIT:
                    moduleCRUD.schedulePM(plannerId);
                    break;
                case EXTEND:
                    moduleCRUD.extendPlanner(plannerId, getDuration(message));
                    break;
            }
            moduleCRUD.schedulePM(plannerId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
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
        String topic = message.getTopic();
        String[] split = StringUtils.split(topic, "/");
        long plannerId = Long.parseLong(split[1]);
        return plannerId;
    }
}
