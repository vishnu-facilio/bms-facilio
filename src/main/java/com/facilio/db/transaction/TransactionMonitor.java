package com.facilio.db.transaction;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.transaction.SystemException;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionMonitor extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(TransactionMonitor.class.getName());

    public void run() {
        ConcurrentHashMap<FacilioTransaction, Long> transactionMap = FTransactionManager.getTransactionTimeoutMap();
        transactionMap.entrySet().forEach(this::markRolledBack);
    }

    private void markRolledBack(Map.Entry<FacilioTransaction, Long> entry){
        long timeTaken = (System.currentTimeMillis()- entry.getValue());
        if(timeTaken > entry.getKey().getTransactionTimeout()){
            try {
                LOGGER.debug("Rolling back Transaction for " + entry.getKey().getTransactionId());
                LOGGER.info("Rolling back Transaction for " + entry.getKey().getTransactionId() +" time taken : " + timeTaken + " timeout : " + entry.getKey().getTransactionTimeout());
                entry.getKey().rollback(false);
                LOGGER.debug("Rolled back Transaction for " + entry.getKey().getTransactionId());
            } catch (SystemException e) {
                LOGGER.info("Exception while setting transaction as rolled back. ", e);
            }
        }
    }
}
