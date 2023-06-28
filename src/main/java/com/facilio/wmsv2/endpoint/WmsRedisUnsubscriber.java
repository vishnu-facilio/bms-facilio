package com.facilio.wmsv2.endpoint;

import com.facilio.db.util.DBConf;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class WmsRedisUnsubscriber implements Runnable{

    private int timeInterval = 10;

    WmsRedisUnsubscriber(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(DBConf.getInstance().getEnvironment() + "wms-redis-unsubscribe-thread");
        long allowedTimeLimit = System.currentTimeMillis() - this.timeInterval * 60 * 1000;
        List<LiveSession> idleSessions = SessionManager.getInstance().getLiveSessions().stream()
                .filter(ls -> ls.getLastPingTime() < allowedTimeLimit) //less than last ping time
                .filter(ls -> !ls.getTopics().isEmpty()) //no need to unsubscribe for empty topics
                .collect(Collectors.toList());
        if(idleSessions.size()!=0) {
            for (LiveSession session : idleSessions) {
                SessionManager.getInstance().unsubscribeEligibleTopics(session);
                LOGGER.info("WMS_LOG_TEMP :: Session topics :: "+session.getId()+" :: "+session.getTopics() + " :: "+session);
                session.flushTopics();
            }
            LOGGER.info("WMS_LOG :: WMS-REDIS-BULK-UNSUBSCRIBE done for " + idleSessions.size() + " livesessions");
        }
    }
}
