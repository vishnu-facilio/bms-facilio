package com.facilio.transaction;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.SystemException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TransactionMonitor extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(TransactionMonitor.class.getName());

    public void run() {
        ConcurrentHashMap<FacilioTransaction, Long> transactionMap = FTransactionManager.getTransactionTimeoutMap();
        transactionMap.entrySet().forEach(this::markRolledBack);
    }

    private void markRolledBack(Map.Entry<FacilioTransaction, Long> entry){
        if((System.currentTimeMillis()-entry.getValue()) > entry.getKey().getTransactionTimeout()){
            try {
                LOGGER.info("Rolling back Transaction for " + entry.getKey().getTransactionId());
                entry.getKey().rollback(false);
                LOGGER.info("Rolled back Transaction for " + entry.getKey().getTransactionId());
            } catch (SystemException e) {
                LOGGER.info("Exception while setting transaction as rolled back. ", e);
            }
        }
    }
}
