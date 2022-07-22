package com.facilio.wmsv2.handler;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

@TopicHandler(
        topic = Topics.System.pmPlanner,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        sendToAllWorkers = false
)

@Log4j
public class PlannedMaintenanceHandler extends BaseHandler {
    @Override
    public void processIncomingMessage(Message message) {
        LOGGER.error(message.toString());
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        try {
            LOGGER.error(message.toString());
            long plannerId = getPlannerId(message);
            ModuleCRUDBean moduleCRUD = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", message.getOrgId());
            moduleCRUD.schedulePM(plannerId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private long getPlannerId(Message message) {
        String topic = message.getTopic();
        String[] split = StringUtils.split(topic, "/");
        long plannerId = Long.parseLong(split[1]);
        return plannerId;
    }
}
