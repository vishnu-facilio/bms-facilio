package com.facilio.wmsv2.endpoint;

import com.facilio.db.util.DBConf;
import com.facilio.wmsv2.message.WebMessage;
import com.facilio.wmsv2.util.WmsUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class DefaultBroadcaster {

    public void sendMessage(WebMessage message) {
        try {
            if (message.getOrgId()==-1L && DBConf.getInstance().getCurrentOrg() != null) {
                message.setOrgId(DBConf.getInstance().getCurrentOrg().getId());
            }
            String redisTopic = WmsUtil.convertToRedisTopic(message, message.getTopic());
            if(redisTopic == null) {
                return;
            }
            message.setTopic(redisTopic);
            broadcast(message);
        } catch (Exception ex) {
            LOGGER.error("WMS_ERROR :: Send message failed: " + message.toString(), ex);
        }
    }

    protected abstract void broadcast(WebMessage message);

    public abstract void subscribe(String... topics);

    public abstract void unsubscribe(String... topics);
}
